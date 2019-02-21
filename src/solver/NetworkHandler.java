package solver;

import Initializer.Debugger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

abstract class NetworkHandler extends Thread {

    protected SudokuBox sudokuBox;
    protected String boxName;
    protected List<String> incomingMessages = new ArrayList<>();
    protected List<String> outgoingMessages = new ArrayList<>();
    protected int runCounter = 0;
    protected boolean sentSolvedMessage = false;
    protected int[][] sudokuSheet;

    protected Lock lockForIncomingMessages = new ReentrantLock();
    protected Lock lockForOutgoingMessages = new ReentrantLock();




    public NetworkHandler(SudokuBox sudokuBox) {
        this.sudokuBox = sudokuBox;
        this.boxName = sudokuBox.getBoxName();
        sudokuSheet = new int[10][10];
        establishConnectionToManager();
    }


    @Override
    public void run() {
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (true) {

            /**
             * 1. Read messages from all incoming connections
             * 3. feed the box with the new knowledge
             *      b. this means also processing the boxes solving algorithm and retrieving new knowledge

             */
            if (!sudokuBox.isSolved()) {
                if (lockForIncomingMessages.tryLock()) {
                    // Got the lock
                    try {
                        for (String message : incomingMessages) {
                            // give message to box
                            sudokuBox.receiveKnowledge(message);
                        }
                        incomingMessages.clear();

                    } finally {
                        // Make sure to unlock so that we don't cause a deadlock
                        lockForIncomingMessages.unlock();
                    }
                }else {
                    noLockNotification();
                }
            } else if (!sentSolvedMessage) {
                sendIsSolved();
            }
            sendPendingMessages();
            try {
                /**
                 * TODO why does the system not work if the thread is not sleeping here for a certain time (1 ms does not work)???
                 *
                 * NOTE: 10 ms seams to work but 100ms is much faster!!! probably gets even more important when using real
                 * network and not localhost
                 */
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }




    abstract void establishConnectionToManager();




    protected void addIncomingMessage(String message) {
        //Debugger.__("received incoming message: " + message + " from neighbor", this);
        if (!sudokuBox.isSolved()) {

            if (lockForIncomingMessages.tryLock()) {
                // Got the lock
                try {
                    incomingMessages.add(message);
                } finally {
                    // Make sure to unlock so that we don't cause a deadlock
                    lockForIncomingMessages.unlock();
                }
            } else {
                noLockNotification();
                //Someone else had the lock, abort
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //Risiko eines StackOverflow!
                addIncomingMessage(message);
            }
        }
        addOutgoingMessage(message);
    }

    protected void addOutgoingMessage(String message) {
        // Debugger.__("received outgoing message: " + message + " from box", this);


        if (lockForOutgoingMessages.tryLock()) {
            // Got the lock
            try {
                outgoingMessages.add(message);
            } finally {
                // Make sure to unlock so that we don't cause a deadlock
                lockForOutgoingMessages.unlock();
            }
        } else {
            // Someone else had the lock, abort
            noLockNotification();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            addOutgoingMessage(message);
        }
    }


    protected void addKnowledgeToSheet(String message) {
        char col = message.charAt(0);
        int column = 1 + (col - 'A');
        int row = Integer.parseInt("" + message.charAt(1));
        int value = Integer.parseInt("" + message.charAt(3));
        sudokuSheet[column][row] = value;
    }

    protected String sheetToString() {
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

    abstract void sendIsSolved();

    protected void noLockNotification(){
        Debugger.__("DIDNT GET THE LOCK!!!!!",this);
        Debugger.__("DIDNT GET THE LOCK!!!!!",this);
        Debugger.__("DIDNT GET THE LOCK!!!!!",this);
        Debugger.__("DIDNT GET THE LOCK!!!!!",this);
        Debugger.__("DIDNT GET THE LOCK!!!!!",this);
        Debugger.__("DIDNT GET THE LOCK!!!!!",this);
        Debugger.__("DIDNT GET THE LOCK!!!!!",this);
    }


    abstract void sendPendingMessages();





    public String getBoxName() {
        return boxName;
    }

}
