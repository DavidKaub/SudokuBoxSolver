package solver;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class ManagerConnection extends TCPConnection {
    private SudokuBox sudokuBox;

    public ManagerConnection(SudokuBox sudokuBox, String uri, int port) throws IOException {
        super(new Socket(InetAddress.getByName(uri), port));

        //new Socket(InetAddress.getByName(uri),port);
        this.sudokuBox = sudokuBox;

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
                        //TODO
                        //Socket s = new Socket(address, port);
                        //BoxNeighbourSocket neighbour = new BoxNeighbourSocket(s, this.box);
                        //this.box.addNeighbour(neighbour);
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
        String registrationMessage = sudokuBox.getBoxName() + "," + sudokuBox.getBoxUri() + "," + sudokuBox.getBoxPort();
        System.out.println("Sending Message: "+registrationMessage);
        try {
            this.sendMessage(registrationMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





}
