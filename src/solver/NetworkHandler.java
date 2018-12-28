package solver;

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

    private List<String> incomingMessages;
    private List<String> outgoingMessages;
    private List<String> messageHistory;


    public NetworkHandler(SudokuBox sudokuBox, String boxManagerUri, int boxManagerPort) {
        this.sudokuBox = sudokuBox;
        this.boxName = sudokuBox.getBoxName();

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
    }

    private void startLokalServer() {

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
                System.out.println("Asking for neighbor " + neighborName);
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
        while (true) {

            /**
             * 1. Read messages from all incoming connections
             * 3. feed the box with the new knowledge
             *      b. this means also processing the boxes solving algorithm and retrieving new knowledge

             */

            synchronized (incomingMessages) {
                for (String message : incomingMessages) {
                    // give message to box
                    sudokuBox.receiveKnowledge(message);
                    incomingMessages.remove(message);
                }
            }
            synchronized (this) {
                sentPendingMessages();
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public void addIncomingMessage(String message) {
        synchronized (incomingMessages) {
            incomingMessages.add(message);
        }
    }

    public void addOutgoingMessage(String message) {
        synchronized (outgoingMessages) {
            outgoingMessages.add(message);
        }
    }


    private void sentPendingMessages() {
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
        synchronized (outgoingNeighborConnections) {
            if (!outgoingNeighborConnections.isEmpty()) {
                synchronized (outgoingMessages) {
                    for (String message : outgoingMessages) {
                        if (messageHistory.contains(message)) {
                            outgoingMessages.remove(message);
                        } else {
                            for (NeighborConnection neighbor : outgoingNeighborConnections) {
                                try {
                                    neighbor.sendMessage(message);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            outgoingMessages.remove(message);
                            messageHistory.add(message);
                        }
                    }
                }
            }
        }
    }

    public void addIncommingNeighborConnection(NeighborConnection neighborConnection) {
        synchronized (incomingNeighborConnections) {
            incomingNeighborConnections.add(neighborConnection);
        }
    }

    public void addOutgoingNeighborConnection(NeighborConnection neighborConnection) {
        synchronized (outgoingNeighborConnections) {
            outgoingNeighborConnections.add(neighborConnection);
        }
    }


    public String getBoxName() {
        return boxName;
    }

}
