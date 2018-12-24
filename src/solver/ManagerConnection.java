package solver;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class ManagerConnection extends TCPConnection{
    private SudokuBox sudokuBox;

    public ManagerConnection(SudokuBox sudokuBox, String uri, int port) throws IOException {
        super(new Socket(InetAddress.getByName(uri),port));
        this.sudokuBox = sudokuBox;

    }

    @Override
    public void run(){
        try{
            if(registerOnManager()) {
                while (true) {
                    String line = this.readLine();
                    if (line != null) {
                        line = line.trim();
                        System.out.println("Server responded: " + line);


                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }






    private boolean registerOnManager(){
        System.out.println("registering on Manager!");
        try {

            String registrationMessage = sudokuBox.getBoxName()+","+sudokuBox.getBoxUri()+","+sudokuBox.getBoxPort();
           System.out.println("Sending Message: "+registrationMessage);
            this.sendMessage(registrationMessage);
            //this.bufferedWriter = new BufferedWriter(outputStreamWriter);
            String line = this.readLine();
            if (line != null) {
                line = line.trim();
                System.out.println("Server responded: " + line);
                if(line.equals("Someone else is responsible for this box name")) {
                    System.out.println("This box name is already assigned. Closing connection and terminating...");
                    this.closeConnection();
                    return false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
