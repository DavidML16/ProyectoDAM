package morales.david.server.clients;

import morales.david.server.Server;
import morales.david.server.models.Packet;
import morales.david.server.models.PacketBuilder;
import morales.david.server.utils.Constants;
import morales.david.server.utils.DBConnection;

import java.io.IOException;
import java.net.SocketException;

public class ClientProtocol {

    private ClientThread clientThread;

    private boolean logged;

    private Packet lastPacket;

    public ClientProtocol(ClientThread clientThread) {
        this.clientThread = clientThread;
        this.logged = false;
    }

    public void parseInput(Packet packet) {

        lastPacket = packet;

        switch (lastPacket.getType()) {

            case Constants.REQUEST_LOGIN:
                login();
                break;

            case Constants.REQUEST_DISCONNECT:
                disconnect();
                break;

        }

    }

    private void login() {

        final String username = (String) lastPacket.getArgument("username");
        final String password = (String) lastPacket.getArgument("password");

        ClientSession clientSession = clientThread.getClientSession();
        DBConnection dbConnection = clientThread.getDbConnection();

        if(dbConnection.existsCredential(username, password)) {

            dbConnection.getUserDetails(username, clientSession);

            Packet loginConfirmationPacket = new PacketBuilder()
                    .ofType(Constants.CONFIRMATION_LOGIN)
                    .addArgument("id", clientSession.getId())
                    .addArgument("name", clientSession.getName())
                    .addArgument("role", clientSession.getRole())
                    .build();

            sendPacketIO(loginConfirmationPacket);

            logged = true;

        } else {

            Packet loginErrorPacket = new PacketBuilder()
                    .ofType(Constants.ERROR_LOGIN)
                    .build();

            sendPacketIO(loginErrorPacket);

        }

    }

    private void disconnect() {

        if(logged) {

            Packet disconnectConfirmationPacket = new PacketBuilder()
                    .ofType(Constants.CONFIRMATION_DISCONNECT)
                    .build();

            sendPacketIO(disconnectConfirmationPacket);
            clientThread.setConnected(false);

        } else {

            Packet disconnectErrorPacket = new PacketBuilder()
                    .ofType(Constants.ERROR_DISCONNECT)
                    .build();

            sendPacketIO(disconnectErrorPacket);

        }

    }

    public void sendPacketIO(Packet packet) {
        try {
            clientThread.getOutput().write(packet.toString());
            clientThread.getOutput().newLine();
            clientThread.getOutput().flush();
        } catch (IOException e) {
            System.out.println(Constants.LOG_SERVER_ERROR_IO_SEND);
            e.printStackTrace();
        }
    }

    public Packet readPacketIO() {
        try {
            String json = clientThread.getInput().readLine();
            return Server.GSON.fromJson(json, Packet.class);
        } catch (SocketException e) {
            clientThread.setConnected(false);
        } catch (IOException e) {
            System.out.println(Constants.LOG_SERVER_ERROR_IO_READ);
            e.printStackTrace();
        }
        return null;
    }

}
