package solver;

import Initializer.Debugger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TCPHandler extends NetworkHandler {

    private String boxUri;
    private int boxPort;
    private String boxManagerUri;
    private int boxManagerPort;
    private TCPServer tcpServer;

    private ManagerConnection managerConnection;
    private List<NeighborConnection> incomingNeighborConnections = new ArrayList<>();
    private List<NeighborConnection> outgoingNeighborConnections = new ArrayList<>();


    private Lock lockForIncomingNeighborConnections = new ReentrantLock();
    private Lock lockForOutgoingNeighborConnections = new ReentrantLock();



    public TCPHandler(SudokuBox sudokuBox, String boxManagerUri, int boxManagerPort){
        super(sudokuBox);
        this.boxManagerUri = boxManagerUri;
        this.boxManagerPort = boxManagerPort;

        //TODO
        //this.boxUri = sudokuBox.get
        //this.boxPort =

    }






    @Override
    void establishConnectionToManager() {
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

    public void sendIsSolved() {
        //TODO : RESULT,Boxname,1,4,3,2,6,7,5,9,8

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("RESULT," + sudokuBox.getBoxName() + ",");
        stringBuilder.append(sudokuBox.printResult().trim());
        try {
            managerConnection.sendMessage(stringBuilder.toString());
            sentSolvedMessage = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    protected void noLockNotification(){
        Debugger.__("DIDNT GET THE LOCK!!!!!",this);
        Debugger.__("DIDNT GET THE LOCK!!!!!",this);
        Debugger.__("DIDNT GET THE LOCK!!!!!",this);
        Debugger.__("DIDNT GET THE LOCK!!!!!",this);
        Debugger.__("DIDNT GET THE LOCK!!!!!",this);
        Debugger.__("DIDNT GET THE LOCK!!!!!",this);
        Debugger.__("DIDNT GET THE LOCK!!!!!",this);
    }


    public void sendPendingMessages() {
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


        if (lockForOutgoingNeighborConnections.tryLock()) {
            // Got the first lock
            try {
                // Process record
                if (outgoingNeighborConnections.size() == sudokuBox.getNeighborNames().size()) {

                    if (lockForOutgoingMessages.tryLock()) {
                        // Got the second lock -> start process
                        try {
                            // Process record
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
                        } finally {
                            // Make sure to unlock so that we don't cause a deadlock
                            lockForOutgoingMessages.unlock();
                        }
                    }else
                        noLockNotification();
                }
            } finally {
                // Make sure to unlock so that we don't cause a deadlock
                lockForOutgoingNeighborConnections.unlock();
            }
        }else
            noLockNotification();
    }


    public void addIncomingNeighborConnection(NeighborConnection neighborConnection) {
        if (lockForIncomingNeighborConnections.tryLock()) {
            // Got the lock
            try {
                incomingNeighborConnections.add(neighborConnection);
                neighborConnection.start();
            } finally {
                // Make sure to unlock so that we don't cause a deadlock
                lockForIncomingNeighborConnections.unlock();
            }
        } else {
            noLockNotification();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            addIncomingNeighborConnection(neighborConnection);
            // Someone else had the lock, abort
        }
    }

    public void addOutgoingNeighborConnection(NeighborConnection neighborConnection) {
        if (lockForOutgoingNeighborConnections.tryLock()) {
            // Got the lock
            try {
                outgoingNeighborConnections.add(neighborConnection);
                neighborConnection.start();
            } finally {
                // Make sure to unlock so that we don't cause a deadlock
                lockForOutgoingNeighborConnections.unlock();
            }
        } else {
            noLockNotification();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            addOutgoingNeighborConnection(neighborConnection);
        }
    }



}
