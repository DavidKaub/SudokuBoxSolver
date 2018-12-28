package solver;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

public class TCPServer extends Thread {

    private ServerSocket serverSocket;
    private NetworkHandler networkHandler;
    private String localIp;
    private int localPort;

    public TCPServer(NetworkHandler networkHandler){
        this.networkHandler = networkHandler;
        try {
            this.serverSocket = new ServerSocket(0);
            this.localIp = InetAddress.getLocalHost().getHostAddress();
            this.localPort = serverSocket.getLocalPort();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(true) {
            try {
                NeighborConnection neighborConnection = new NeighborConnection(this.serverSocket.accept(), networkHandler);
                this.networkHandler.addIncomingNeighborConnection(neighborConnection);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getLocalIp() {
        return localIp;
    }

    public int getLocalPort() {
        return localPort;
    }
}
