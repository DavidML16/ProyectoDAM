package morales.david.server.clients;

import morales.david.server.Server;
import morales.david.server.models.packets.Packet;
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

    /**
     * Create a new instance of ClientThread with given server and socket
     * @param server
     * @param socket
     */
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

    /**
     * Create the input and output streams of the socket
     */
    public void openIO() {

        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF8"));
            output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));
        } catch (IOException e) {
            System.out.println(Constants.LOG_SERVER_ERROR_IO);
            e.printStackTrace();
        }

    }

    /**
     * Close the input and output streams
     */
    public void closeIO() {

        if(input != null) {
            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(output != null) {
            try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void run() {

        while(connected) {

            try {

                if(input != null && input.ready()) {

                    receivedPacket = clientProtocol.readPacketIO();

                    if (receivedPacket == null)
                        continue;

                    clientProtocol.parseInput(receivedPacket);

                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        System.out.println(Constants.LOG_SERVER_USER_DISCONNECTED);
        closeIO();
        this.server.getClientRepository().removeClient(this);
        this.stop();

    }

    /**
     * Get server of the client thread
     * @return server
     */
    public Server getServer() { return server; }

    /**
     * Get socket of the client thread
     * @return socket
     */
    public Socket getSocket() { return socket; }

    /**
     * Get input stream of the client thread
     * @return input
     */
    public BufferedReader getInput() { return input; }

    /**
     * Get output stream of the client thread
     * @return output
     */
    public BufferedWriter getOutput() { return output; }

    /**
     * Get session of the client thread
     * @return clientSession
     */
    public ClientSession getClientSession() { return clientSession; }

    /**
     * Get protocol of the client thread
     * @return clientProtocol
     */
    public ClientProtocol getClientProtocol() { return clientProtocol; }

    /**
     * Get db connection of the client thread
     * @return dbConnection
     */
    public DBConnection getDbConnection() { return dbConnection; }

    /**
     * Get if the client its connected to the server
     * @return connected
     */
    public boolean isConnected() { return connected; }

    /**
     * Set if the client is connected to the server
     * @param connected
     */
    public void setConnected(boolean connected) { this.connected = connected; }

}
