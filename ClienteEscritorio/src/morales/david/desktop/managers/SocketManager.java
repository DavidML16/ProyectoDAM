package morales.david.desktop.managers;

import javafx.application.Platform;
import javafx.scene.paint.Color;
import morales.david.desktop.controllers.LoginController;
import morales.david.desktop.models.ClientSession;
import morales.david.desktop.utils.Constants;

import java.io.*;
import java.net.Socket;

public class SocketManager extends Thread {

    private static SocketManager INSTANCE = null;

    public static SocketManager getInstance() {
        if(INSTANCE == null)
            INSTANCE = new SocketManager();
        return INSTANCE;
    }

    private Socket socket;

    private BufferedReader input;
    private BufferedWriter output;

    private String receivedMessage;
    private String[] receivedArguments;

    private ClientSession clientSession;

    public SocketManager() {

        try {

            socket = new Socket(Constants.SERVER_IP, Constants.SERVER_PORT);

            output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        } catch (IOException e) {

            e.printStackTrace();

        }

        this.clientSession = new ClientSession();

    }

    @Override
    public void run() {

        while(true) {

            try {

                if (input != null && input.ready()) {

                    receivedMessage = input.readLine();
                    receivedArguments = receivedMessage.split(Constants.ARGUMENT_DIVIDER);

                    ScreenManager screenManager = ScreenManager.getInstance();

                    switch (receivedArguments[0]) {

                        case Constants.CONFIRMATION_LOGIN:

                            if(screenManager.getController() instanceof LoginController) {

                                Platform.runLater(() -> {

                                    screenManager.getStage().setResizable(true);
                                    screenManager.openScene("home.fxml", "Home");

                                });

                            }

                            break;

                        case Constants.ERROR_LOGIN:

                            if(screenManager.getController() instanceof LoginController) {

                                LoginController loginController = (LoginController) screenManager.getController();

                                Platform.runLater(() -> {

                                    loginController.getMessageLabel().setText(Constants.MESSAGES_ERROR_LOGIN);
                                    loginController.getMessageLabel().setTextFill(Color.TOMATO);

                                });

                            }

                            break;

                        case Constants.CONFIRMATION_DETAILS:

                            clientSession.setId(Integer.parseInt(receivedArguments[1]));
                            clientSession.setName(receivedArguments[2]);
                            clientSession.setRole(receivedArguments[3]);

                            break;


                    }

                }

            } catch (IOException e) {
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
