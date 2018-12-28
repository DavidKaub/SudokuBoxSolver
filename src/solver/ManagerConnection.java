package solver;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class ManagerConnection extends TCPConnection {
    private NetworkHandler networkHandler;
    private String localUri;
    private int localPort;

    public ManagerConnection(NetworkHandler networkHandler, String remoteUri, int remotePort, String localUri, int localPort) throws IOException {
        super(new Socket(InetAddress.getByName(remoteUri), remotePort));
        this.localUri = localUri;
        this.localPort = localPort;

        //new Socket(InetAddress.getByName(uri),port);
        this.networkHandler = networkHandler;

    }

    @Override
    public void run() {
        try {
            while (true) {
                String line = this.readLine();
                if (line != null) {
                    line = line.trim();
                    System.out.println("Server responded: " + line);
                    if (line.equals("Someone else is responsible for this box name")) {
                        System.out.println("This box name is already assigned. Closing connection and terminating...");
                        break;
                    }
                    if (line.matches("^([12]?[0-9]?[0-9].){3}[12]?[0-9]?[0-9],\\s*[0-9]+$")) {
                        String[] parts = line.split(",");
                        String address = null;
                        int port = -1;
                        if (parts.length >= 2) {
                            address = parts[0].trim();
                            port = Integer.parseInt(parts[1].trim());
                        }
                        Socket socket = new Socket(address, port);
                        NeighborConnection neighborConnection = new NeighborConnection(socket, networkHandler);
                        networkHandler.addOutgoingNeighborConnection(neighborConnection);
                        System.out.println("Established connection with neighbour " + address + " on port " + port);
                    }

                } else {
                    break;
                }
            }
            this.closeConnection();
            System.out.println("Connection to manager closed!");
            System.exit(0);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(0);
        }
    }

    public void registerOnManager(){
        String registrationMessage = networkHandler.getBoxName() + "," + localUri + "," + localPort;
        System.out.println("Sending Message: "+registrationMessage);
        try {
            this.sendMessage(registrationMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





}
