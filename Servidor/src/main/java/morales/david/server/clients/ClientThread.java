package morales.david.server.clients;

import morales.david.server.Server;
import morales.david.server.models.Packet;
import morales.david.server.utils.Constants;
import morales.david.server.utils.DBConnection;

import java.io.*;
import java.net.Socket;

public class ClientThread extends Thread {

    private Server server;

    private Socket socket;

    private BufferedReader input;
    private BufferedWriter output;

    private boolean connected;

    private Packet receivedPacket;

    private ClientSession clientSession;
    private ClientProtocol clientProtocol;

    private DBConnection dbConnection;

    public ClientThread(Server server, Socket socket) {

        this.server = server;
        this.socket = socket;
        this.connected = true;

        openIO();

        this.server.getClientRepository().addClient(this);

        this.clientSession = new ClientSession();
        this.clientProtocol = new ClientProtocol(this);
        this.dbConnection = new DBConnection();

    }

    public void openIO() {

        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            System.out.println(Constants.LOG_SERVER_ERROR_IO);
            e.printStackTrace();
        }

    }

    @Override
    public void run() {

        while(connected) {

            receivedPacket = clientProtocol.readPacketIO();

            if(receivedPacket == null)
                continue;

            clientProtocol.parseInput(receivedPacket);

        }

        System.out.println(Constants.LOG_SERVER_USER_DISCONNECTED);
        this.server.getClientRepository().removeClient(this);
        this.stop();

    }

    public Server getServer() { return server; }

    public Socket getSocket() { return socket; }

    public BufferedReader getInput() { return input; }

    public BufferedWriter getOutput() { return output; }

    public ClientSession getClientSession() { return clientSession; }

    public ClientProtocol getClientProtocol() { return clientProtocol; }

    public DBConnection getDbConnection() { return dbConnection; }

    public boolean isConnected() { return connected; }

    public void setConnected(boolean connected) { this.connected = connected; }

}
