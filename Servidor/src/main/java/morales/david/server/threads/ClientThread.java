package morales.david.server.threads;

import morales.david.server.Server;
import morales.david.server.utils.Constants;

import java.io.*;
import java.net.Socket;

public class ClientThread extends Thread {

    private Server server;

    private Socket socket;

    private BufferedReader input;
    private BufferedWriter output;

    private boolean connected;

    private String receivedMessage;
    private String[] receivedArguments;

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

    }

    @Override
    public void run() {

        while(connected) {

            try {

                if(input.ready()) {

                    receivedMessage = readMessageIO();
                    receivedArguments = receivedMessage.split(Constants.ARGUMENT_DIVIDER);

                }

            } catch (IOException e) {
                System.out.println(Constants.LOG_SERVER_ERROR_IO);
                e.printStackTrace();
            }

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
        } catch (IOException e) {
            System.out.println(Constants.LOG_SERVER_ERROR_IO_READ);
            e.printStackTrace();
        }
        return null;
    }

}
