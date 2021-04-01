package morales.david.desktop.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import morales.david.desktop.interfaces.Controller;
import morales.david.desktop.managers.ScreenManager;
import morales.david.desktop.managers.SocketManager;
import morales.david.desktop.utils.Constants;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable, Controller {

    @FXML
    private BorderPane viewPane;

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

        Platform.runLater(() -> loadView("home.fxml"));

    }

    @FXML
    public void handleButtonAction(MouseEvent event) {

        if(event.getSource() == homeNavigationButton)
            loadView("home.fxml");
        else if (event.getSource() == importNavigationButton)
            loadView("import.fxml");
        else if(event.getSource() == disconnectNavigationButton)
            disconnect();

    }

    private void disconnect() {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "¿Estás seguro que quieres salir?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {

            SocketManager.getInstance().sendMessageIO(Constants.REQUEST_DISCONNECT);

        }

    }

    private void loadView(String view) {

        Parent parent = null;

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/" + view));
            parent = loader.load();

            Controller controller = loader.getController();

            viewPane.setCenter(parent);

            ScreenManager.getInstance().setController(controller);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
