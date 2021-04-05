package morales.david.desktop.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import morales.david.desktop.interfaces.Controller;
import morales.david.desktop.managers.ScreenManager;
import morales.david.desktop.managers.SocketManager;
import morales.david.desktop.models.Packet;
import morales.david.desktop.models.PacketBuilder;
import morales.david.desktop.utils.Constants;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable, Controller {

    @FXML
    private StackPane viewPane;

    @FXML
    private Button teachersNavigationButton;

    @FXML
    private Button groupsNavigationButton;

    @FXML
    private Button roomsNavigationButton;

    @FXML
    private Button inspectorNavigationButton;

    @FXML
    private Button scheduleNavigationButton;

    @FXML
    private Button importNavigationButton;

    @FXML
    private Button homeNavigationButton;

    @FXML
    private Button disconnectNavigationButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Platform.runLater(() -> loadView("home.fxml", "Inicio"));

    }

    @FXML
    public void handleButtonAction(MouseEvent event) {

        if(event.getSource() == homeNavigationButton)
            loadView("home.fxml", "Inicio");
        else if (event.getSource() == teachersNavigationButton)
            loadView("teachers.fxml", "Profesores");
        else if (event.getSource() == groupsNavigationButton)
            loadView("groups.fxml", "Grupos");
        else if (event.getSource() == roomsNavigationButton)
            loadView("rooms.fxml", "Aulas");
        else if (event.getSource() == inspectorNavigationButton)
            loadView("inspections.fxml", "Guardias");
        else if (event.getSource() == scheduleNavigationButton)
            loadView("schedules.fxml", "Horarios");
        else if (event.getSource() == importNavigationButton)
            loadView("import.fxml", "Importar");
        else if(event.getSource() == disconnectNavigationButton)
            disconnect();

    }

    private void disconnect() {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "¿Estás seguro que quieres salir?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {

            Packet disconnectRequestPacket = new PacketBuilder()
                    .ofType(Constants.REQUEST_DISCONNECT)
                    .build();

            SocketManager.getInstance().sendPacketIO(disconnectRequestPacket);

        }

    }

    private void loadView(String view, String title) {

        Node newView = null;
        Node oldView = null;

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/views/" + view));
            newView = loader.load();

            if(viewPane.getChildren().size() > 0) {
                oldView = viewPane.getChildren().get(0);
                viewPane.getChildren().remove(oldView);
            }

            viewPane.getChildren().add(newView);

            ScreenManager.getInstance().getStage().setTitle(title + Constants.WINDOW_TITLE);

            ScreenManager.getInstance().setController(loader.getController());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
