package solver;

import Initializer.Debugger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NetworkHandler extends Thread {

    private SudokuBox sudokuBox;
    private String boxName;
    private String boxUri;
    private int boxPort;
    private TCPServer tcpServer;

    private ManagerConnection managerConnection;
    private List<NeighborConnection> incomingNeighborConnections;
    private List<NeighborConnection> outgoingNeighborConnections;
    private int[][] sudokuSheet;

    private List<String> incomingMessages;
    private List<String> outgoingMessages;
    private List<String> messageHistory;
    private int runCounter = 0;
    private boolean sentSolvedMessage;

    public NetworkHandler(SudokuBox sudokuBox, String boxManagerUri, int boxManagerPort) {
        this.sudokuBox = sudokuBox;
        this.boxName = sudokuBox.getBoxName();
        sudokuSheet = new int[10][10];
        sentSolvedMessage = false;

        incomingNeighborConnections = new ArrayList<>();
        outgoingNeighborConnections = new ArrayList<>();

        incomingMessages = new ArrayList<>();

        outgoingMessages = new ArrayList<>();
        messageHistory = new ArrayList<>();

        //before establishing connection to remote server (manager) start local server to receive messages when registered
        tcpServer = new TCPServer(this);
        boxUri = tcpServer.getLocalIp();
        boxPort = tcpServer.getLocalPort();

        tcpServer.start();
        establishConnectionToManager(boxManagerUri, boxManagerPort);
        //sudokuBox.sendInitialState();
    }


    private void establishConnectionToManager(String boxManagerUri, int boxManagerPort) {
        try {
            System.out.println("establishing connection to boxManager: " + boxManagerUri + ":" + boxManagerPort);
            this.managerConnection = new ManagerConnection(this, boxManagerUri, boxManagerPort, boxUri, boxPort);
            /**
             * TODO Should logon server and ask for neighbor adresses! asap
             * when all neighbor connections are established the sudokubox is beeing
             * "started"! i.e. messages are sent and received
             *
             */
            managerConnection.start();
            managerConnection.registerOnManager();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (String neighborName : sudokuBox.getNeighborNames()) {
                managerConnection.sendMessage(neighborName);
                //the neighbor connections should get established during the run method
                // receiving the data from the server
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        System.out.println("NetworkHandler of " + sudokuBox.getBoxName() + "run Method!");
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (true) {
            System.out.println("NetworkHandler of " + sudokuBox.getBoxName() + "while true!");

            /**
             * 1. Read messages from all incoming connections
             * 3. feed the box with the new knowledge
             *      b. this means also processing the boxes solving algorithm and retrieving new knowledge

             */
            if (!sudokuBox.isSolved()) {
                synchronized (incomingMessages) {
                    for (String message : incomingMessages) {
                        // give message to box
                        sudokuBox.receiveKnowledge(message);
                    }
                    incomingMessages.clear();
                }
            }else if(!sentSolvedMessage){
                sendIsSolved();
            }
            sendPendingMessages();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    void addIncomingMessage(String message) {
        //Debugger.__("received incoming message: " + message + " from neighbor", this);
        if (!sudokuBox.isSolved()) {
            synchronized (incomingMessages) {
                incomingMessages.add(message);
            }
        }

        synchronized (outgoingMessages) {
            outgoingMessages.add(message);
        }
    }

    void addOutgoingMessage(String message) {
        // Debugger.__("received outgoing message: " + message + " from box", this);
        synchronized (outgoingMessages) {
            outgoingMessages.add(message);
        }
    }


    private void addKnowledgeToSheet(String message) {
        char col = message.charAt(0);
        int column = 1 + (col - 'A');
        int row = Integer.parseInt("" + message.charAt(1));
        int value = Integer.parseInt("" + message.charAt(3));
        sudokuSheet[column][row] = value;
    }

    private String sheetToString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SudokuSheet of " + sudokuBox.getBoxName() + ":\n");

        for (int r = 1; r <= 9; r++) {
            stringBuilder.append("\n");
            stringBuilder.append("  ------------------------------------");
            stringBuilder.append("\n");
            for (int c = 1; c <= 9; c++) {
                stringBuilder.append(" | ");
                if (sudokuSheet[c][r] != 0) {
                    stringBuilder.append(sudokuSheet[c][r]);
                } else stringBuilder.append(" ");
            }
            stringBuilder.append(" |");
        }
        stringBuilder.append("\n");
        stringBuilder.append("  ------------------------------------");
        stringBuilder.append("\n");

        return stringBuilder.toString();
    }

    private void sendIsSolved(){
        //TODO : RESULT,Boxname,1,4,3,2,6,7,5,9,8

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("RESULT,"+sudokuBox.getBoxName()+",");
        stringBuilder.append(sudokuBox.printResult().trim());
        try {
            managerConnection.sendMessage(stringBuilder.toString());
            sentSolvedMessage = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void sendPendingMessages() {
        //Debugger.__("SENDING PENDING MESSAGES\nSENDING PENDING MESSAGES\nSENDING PENDING MESSAGES\nSENDING PENDING MESSAGES\nSENDING PENDING MESSAGES", this);
/**
 * TODO Design
 * in den ausgehgenden Verbindungen wird nochmals überprüft ob die nachrtichte bereits versendet wurde
 * Wenn wir hier dieser Prüfung nicht stattfindet aber dafür in den einzelnen ist zwar der aufwand größer
 * aber es kann auch ermöglicht werden (eher jedenfalls) dass während des prozesses einzelne Boxen ausgetauscht werden
 *
 * edit: das zentrale speichern der versendeten nachrichten ist sinvoll bzw notwendig um ggf die nachrichten nochmals
 * zu versenden. Außerdem ist in der regal für die performance des systems ein lokaler zugriff deutlich günstiger
 * als das versenden einer nachricht. d.h. lieber lokal doppelt prüfen anstatt unnötige naschrichten senden
 *
 */
        try {
            Thread.sleep(100);
            synchronized (outgoingNeighborConnections) {
                System.out.println("locked neighborConnections waiting on messages");
                if (outgoingNeighborConnections.size() == sudokuBox.getNeighborNames().size()) {
                    synchronized (outgoingMessages) {
                        System.out.println("locked messages");
                        for (String message : outgoingMessages) {
                            addKnowledgeToSheet(message);
                            //Debugger.__("Sending Message: " + message, this);
                            for (NeighborConnection neighbor : outgoingNeighborConnections) {
                                try {
                                    neighbor.sendMessage(message);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        Debugger.__(sheetToString() + " \nRUN = " + (++runCounter), this);
                        outgoingMessages.clear();
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }


    public void addIncomingNeighborConnection(NeighborConnection neighborConnection) {
        synchronized (incomingNeighborConnections) {
            incomingNeighborConnections.add(neighborConnection);
            neighborConnection.start();
        }
    }

    public void addOutgoingNeighborConnection(NeighborConnection neighborConnection) {
        synchronized (outgoingNeighborConnections) {
            outgoingNeighborConnections.add(neighborConnection);
            neighborConnection.start();
        }
    }


    public String getBoxName() {
        return boxName;
    }

}
