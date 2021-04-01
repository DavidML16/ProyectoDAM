package morales.david.server.clients;

import morales.david.server.utils.Constants;
import morales.david.server.utils.DBConnection;

import java.io.IOException;
import java.net.SocketException;

public class ClientProtocol {

    private ClientThread clientThread;

    private boolean logged;

    private String[] lastArguments;

    public ClientProtocol(ClientThread clientThread) {
        this.clientThread = clientThread;
        this.logged = false;
    }

    public void parseInput(String[] arguments) {

        lastArguments = arguments;

        switch (arguments[0]) {

            case Constants.REQUEST_LOGIN:
                login();
                break;

            case Constants.REQUEST_DISCONNECT:
                disconnect();
                break;

        }

    }

    private void login() {

        final String username = lastArguments[1];
        final String password = lastArguments[2];

        ClientSession clientSession = clientThread.getClientSession();
        DBConnection dbConnection = clientThread.getDbConnection();

        if(dbConnection.existsCredential(username, password)) {

            dbConnection.getUserDetails(username, clientSession);

            StringBuilder sb = new StringBuilder()
                    .append(Constants.CONFIRMATION_LOGIN)
                    .append(Constants.ARGUMENT_DIVIDER)
                    .append(clientSession.getId())
                    .append(Constants.ARGUMENT_DIVIDER)
                    .append(clientSession.getName())
                    .append(Constants.ARGUMENT_DIVIDER)
                    .append(clientSession.getRole());

            sendMessageIO(sb.toString());

            logged = true;

        } else {

            sendMessageIO(Constants.ERROR_LOGIN);

        }

    }

    private void disconnect() {

        if(logged) {

            sendMessageIO(Constants.CONFIRMATION_DISCONNECT);
            clientThread.setConnected(false);

        } else {

            sendMessageIO(Constants.ERROR_DISCONNECT);

        }

    }

    public void sendMessageIO(String message) {
        try {
            clientThread.getOutput().write(message);
            clientThread.getOutput().newLine();
            clientThread.getOutput().flush();
        } catch (IOException e) {
            System.out.println(Constants.LOG_SERVER_ERROR_IO_SEND);
            e.printStackTrace();
        }
    }

    public String readMessageIO() {
        try {
            return clientThread.getInput().readLine();
        } catch (SocketException e) {
            clientThread.setConnected(false);
        } catch (IOException e) {
            System.out.println(Constants.LOG_SERVER_ERROR_IO_READ);
            e.printStackTrace();
        }
        return null;
    }

}
