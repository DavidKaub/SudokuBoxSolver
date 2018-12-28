package solver;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class NeighborConnection extends TCPConnection{


    private List<String> messageHistory;
    private NetworkHandler networkHandler;


    public NeighborConnection(Socket socket, NetworkHandler networkHandler){
        super(socket);
        this.messageHistory = new ArrayList<>();
        this.networkHandler = networkHandler;
    }

    @Override
    public boolean sendMessage(String message) throws IOException {
        if(messageHistory.contains(message)){
            return true;
        }
        if(super.sendMessage(message)){
            messageHistory.add(message);
            return true;
        }
        return false;
    }


    @Override
    public void run(){
         // receive messages from neighbor and write those to the incoming message list through the message handler object
        try {
            while(true) {
                String line = this.readLine();
                if (line != null) {
                    line = line.trim();
                    //System.out.println("Received message: " + line);
                    synchronized (networkHandler){
                        networkHandler.addIncomingMessage(line);
                    }
                } else {
                    break;
                }
            }
            this.closeConnection();
            System.out.println("Connection to neighbour closed!");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
}
