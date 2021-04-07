package morales.david.desktop.managers;

import com.google.gson.JsonElement;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.paint.Color;
import morales.david.desktop.Client;
import morales.david.desktop.controllers.ImportController;
import morales.david.desktop.controllers.LoginController;
import morales.david.desktop.controllers.TeachersController;
import morales.david.desktop.models.ClientSession;
import morales.david.desktop.models.Packet;
import morales.david.desktop.models.Teacher;
import morales.david.desktop.utils.Constants;

import java.io.*;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public final class SocketManager extends Thread {

    private static SocketManager INSTANCE = null;

    public static SocketManager getInstance() {
        if(INSTANCE == null)
            INSTANCE = new SocketManager();
        return INSTANCE;
    }

    private Socket socket;

    private BufferedReader input;
    private BufferedWriter output;

    private Packet receivedPacket;

    private ClientSession clientSession;

    public SocketManager() {

        openSocket();

        this.clientSession = new ClientSession();

    }

    public void openSocket() {

        try {

            socket = new Socket(Constants.SERVER_IP, Constants.SERVER_PORT);

            output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

    private void close() {

        try {
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {

        ScreenManager screenManager = ScreenManager.getInstance();

        while(true) {

            try {

                if (input != null && input.ready()) {

                    receivedPacket = readPacketIO();

                    switch (receivedPacket.getType()) {

                        case Constants.CONFIRMATION_LOGIN: {

                            clientSession.setId(((Double) receivedPacket.getArgument("id")).intValue());
                            clientSession.setName((String) receivedPacket.getArgument("name"));
                            clientSession.setRole((String) receivedPacket.getArgument("role"));

                            if (screenManager.getController() instanceof LoginController) {

                                Platform.runLater(() -> {

                                    screenManager.openScene("dashboard.fxml", "Inicio" + Constants.WINDOW_TITLE);

                                });

                            }

                            break;
                        }

                        case Constants.ERROR_LOGIN: {

                            if (screenManager.getController() instanceof LoginController) {

                                LoginController loginController = (LoginController) screenManager.getController();

                                Platform.runLater(() -> {

                                    loginController.getMessageLabel().setText(Constants.MESSAGES_ERROR_LOGIN);
                                    loginController.getMessageLabel().setTextFill(Color.TOMATO);

                                });

                            }

                            break;

                        }

                        case Constants.CONFIRMATION_DISCONNECT: {

                            clientSession = new ClientSession();

                            if (!(screenManager.getController() instanceof LoginController)) {

                                Platform.runLater(() -> {

                                    openSocket();

                                    screenManager.openScene("login.fxml", "Iniciar sesión" + Constants.WINDOW_TITLE);

                                });

                            }

                            break;
                        }

                        case Constants.CONFIRMATION_SENDACCESSFILE: {

                            if (screenManager.getController() instanceof ImportController) {

                                ImportController importController = (ImportController) screenManager.getController();

                                Platform.runLater(() -> {

                                    importController.receivedFile();

                                });

                            }

                            break;
                        }

                        case Constants.ERROR_SENDACCESSFILE: {

                            if (screenManager.getController() instanceof ImportController) {

                                ImportController importController = (ImportController) screenManager.getController();

                                Platform.runLater(() -> {

                                    importController.getMessageLabel().setText(Constants.MESSAGES_ERROR_RECEIVEDFILE);
                                    importController.getMessageLabel().setTextFill(Color.TOMATO);

                                });

                            }

                            break;
                        }

                        case Constants.CONFIRMATION_TEACHERS: {

                            if(!(ScreenManager.getInstance().getController() instanceof TeachersController))
                                break;

                            List<LinkedTreeMap> tchs = (List<LinkedTreeMap>) receivedPacket.getArgument("teachers");

                            List<Teacher> teachers = new ArrayList<>();

                            for(LinkedTreeMap teacherMap : tchs)
                                teachers.add(Teacher.parse(teacherMap));

                            DataManager.getInstance().getTeachers().clear();
                            DataManager.getInstance().getTeachers().addAll(teachers);

                            break;

                        }

                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public Socket getSocket() { return socket; }

    // Methods to send data in socket I/O's
    public void sendPacketIO(Packet packet) {
        try {
            output.write(packet.toString());
            output.newLine();
            output.flush();
        } catch (IOException e) {
            System.out.println(Constants.LOG_SERVER_ERROR_IO_SEND);
            e.printStackTrace();
        }
    }

    public Packet readPacketIO() {
        try {
            String json = input.readLine();
            return Client.GSON.fromJson(json, Packet.class);
        } catch (IOException e) {
            System.out.println(Constants.LOG_SERVER_ERROR_IO_READ);
            e.printStackTrace();
        }
        return null;
    }

}
