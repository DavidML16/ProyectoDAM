package morales.david.server.threads;

import morales.david.server.Server;
import morales.david.server.models.ClientSession;
import morales.david.server.utils.Constants;
import morales.david.server.utils.DBConnection;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class ClientThread extends Thread {

    private Server server;

    private Socket socket;

    private BufferedReader input;
    private BufferedWriter output;

    private boolean connected;

    private String receivedMessage;
    private String[] receivedArguments;

    private ClientSession clientSession;

    private DBConnection dbConnection;

    public ClientThread(Server server, Socket socket) {

        this.server = server;
        this.socket = socket;
        this.connected = true;

        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            System.out.println(Constants.LOG_SERVER_ERROR_IO);
            e.printStackTrace();
        }

        this.server.getClientRepository().addClient(this);

        this.clientSession = new ClientSession();
        this.dbConnection = new DBConnection(this);

    }

    @Override
    public void run() {

        while(connected) {

            receivedMessage = readMessageIO();
            receivedArguments = receivedMessage.split(Constants.ARGUMENT_DIVIDER);

            switch (receivedArguments[0]) {

                case Constants.REQUEST_LOGIN:
                    login();
                    break;

            }

        }

    }

    private void login() {

        final String username = receivedArguments[1];
        final String password = receivedArguments[2];

        if(dbConnection.existsCredential(username, password)) {

            sendMessageIO(Constants.CONFIRMATION_LOGIN);

            dbConnection.getUserDetails(username, clientSession);

            StringBuilder sb = new StringBuilder()
                    .append(Constants.CONFIRMATION_DETAILS)
                    .append(Constants.ARGUMENT_DIVIDER)
                    .append(clientSession.getId())
                    .append(Constants.ARGUMENT_DIVIDER)
                    .append(clientSession.getName())
                    .append(Constants.ARGUMENT_DIVIDER)
                    .append(clientSession.getRole());

            sendMessageIO(sb.toString());

        } else {

            sendMessageIO(Constants.ERROR_LOGIN);

        }

    }

    public void sendMessageIO(String message) {
        try {
            output.write(message);
            output.newLine();
            output.flush();
        } catch (IOException e) {
            System.out.println(Constants.LOG_SERVER_ERROR_IO_SEND);
            e.printStackTrace();
        }
    }

    public String readMessageIO() {
        try {
            return input.readLine();
        } catch (SocketException e) {

            connected = false;
            server.getClientRepository().removeClient(this);

            System.out.println(Constants.LOG_SERVER_USER_DISCONNECTED);

            this.stop();

        } catch (IOException e) {
            System.out.println(Constants.LOG_SERVER_ERROR_IO_READ);
            e.printStackTrace();
        }
        return null;
    }

}
