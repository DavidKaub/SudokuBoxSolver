package solver;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class NetworkHandler extends Thread{

    private SudokuBox sudokuBox;

    private String boxManagerUri;
    private int boxManagerPort;

    private ManagerConnection managerConnection;
    private Map<String, NeighborConnection> neighborConnections;
    private List<String> incommingMessages;
    private List<String> outgoingMessages;
    private List<String> sentMessages;




    public NetworkHandler(SudokuBox sudokuBox, String boxManagerUri, int boxManagerPort){
        this.sudokuBox = sudokuBox;
        try {
            System.out.println("establishing connection to boxManager: "+boxManagerUri+":"+boxManagerPort);
            this.managerConnection = new ManagerConnection(sudokuBox,boxManagerUri,boxManagerPort);
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
            for(String neighborName: sudokuBox.getNeighborNames()){
                System.out.println("Askibg for neighbor "+ neighborName);
                managerConnection.sendMessage(neighborName);
                //the neighbor connections should get established during the run method
                // receiving the data from the server
            }



        } catch (IOException e) {
            e.printStackTrace();
        }
        // managerConnection = ManagerConnection.getInstance(sudokuBox.getManagerUri(), sudokuBox.getManagerPort());
        //TODO

    }

    @Override
    public void run(){



        while (true){

        }



    }








    private void sentMessages(){
        for(String message: outgoingMessages){
            if(sentMessages.contains(message)){
                outgoingMessages.remove(message);
            }else{
                for(String neighborName: sudokuBox.getNeighborNames()){
                    NeighborConnection n = retrieveNeighborConnection(neighborName);
                    //TODO send messages
                }
                outgoingMessages.remove(message);
                sentMessages.add(message);
            }
        }
    }


    private NeighborConnection retrieveNeighborConnection(String neighborName){
        NeighborConnection neighborConnection = null;
        if (neighborConnections.containsKey(neighborName)){
            neighborConnection = neighborConnections.get(neighborName);
        }else{
            try {
                managerConnection.sendMessage(neighborName);
                //TODO PROBELM wegen drecks thread gedoens! hier muss die antwort ja irgendwie gefangen werden! d.h. sende wissen erst wenn alle nachbarn bekannt sind.==??
            } catch (IOException e) {
                e.printStackTrace();
            }
            //TODO create new neighborProy
            /**
             * TODO
             * 1. Beziehe Verbindung zum BoxManager
             * 2. Frage BoxManager nach der IP Adresse und dem Port des Nachbarn
             * 3. Baue eine verbindung zum Server des Nachbarn auf znd psiecher diese als neighborConneciton
             * 4. Speichere Verbindung in neighborConnections
             * 5. (neighborConneciton wird außerhalb des "else" zurück gegeben)
             */

        }
        return neighborConnection;
    }

}