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
import javafx.scene.layout.BorderPane;
import morales.david.desktop.interfaces.Controller;
import morales.david.desktop.managers.ScreenManager;
import morales.david.desktop.ClientManager;
import morales.david.desktop.models.packets.Packet;
import morales.david.desktop.models.packets.PacketBuilder;
import morales.david.desktop.models.packets.PacketType;
import morales.david.desktop.utils.Constants;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable, Controller {

    @FXML
    private Button homeNavigationButton;

    @FXML
    private Button teachersNavigationButton;

    @FXML
    private Button coursesGroupsNavigationButton;

    @FXML
    private Button classroomsNavigationButton;

    @FXML
    private Button scheduleNavigationButton;

    @FXML
    private Button optionsNavigationButton;

    @FXML
    private Button disconnectNavigationButton;

    @FXML
    private BorderPane viewPane;

    private String actualView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        actualView = "";

        Platform.runLater(() -> loadView("home.fxml", "Inicio", null));

        if(ClientManager.getInstance().getClientSession().isTeacherRole()) {
            optionsNavigationButton.setVisible(false);
        }

    }

    @FXML
    public void handleButtonAction(MouseEvent event) {

        if(event.getSource() == homeNavigationButton)
            loadView("home.fxml", "Inicio", event);
        else if (event.getSource() == teachersNavigationButton)
            loadView("teachers/teachersmenu.fxml", "Profesores", event);
        else if (event.getSource() == coursesGroupsNavigationButton)
            loadView("courses/coursesmenu.fxml", "Cursos", event);
        else if (event.getSource() == classroomsNavigationButton)
            loadView("classrooms/classroomsmenu.fxml", "Aulas", event);
        else if (event.getSource() == scheduleNavigationButton)
            loadView("schedules/schedulesmenu.fxml", "Horarios", event);
        else if (event.getSource() == optionsNavigationButton)
            loadView("options/optionsmenu.fxml", "Opciones", event);
        else if(event.getSource() == disconnectNavigationButton)
            disconnect();

    }

    private void disconnect() {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "¿Estás seguro que quieres desconectarte?", ButtonType.YES, ButtonType.NO);
        alert.setHeaderText("Vas a salir de la aplicación");
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {

            Packet disconnectRequestPacket = new PacketBuilder()
                    .ofType(PacketType.DISCONNECT.getRequest())
                    .build();

            ClientManager.getInstance().sendPacketIO(disconnectRequestPacket);

        }

    }

    private void removePressed() {

        homeNavigationButton.getStyleClass().remove("buttonPressed");
        teachersNavigationButton.getStyleClass().remove("buttonPressed");
        coursesGroupsNavigationButton.getStyleClass().remove("buttonPressed");
        classroomsNavigationButton.getStyleClass().remove("buttonPressed");
        scheduleNavigationButton.getStyleClass().remove("buttonPressed");
        optionsNavigationButton.getStyleClass().remove("buttonPressed");

    }

    private void loadView(String view, String title, MouseEvent event) {

        Node newView = null;
        Node oldView = null;

        if(actualView.equalsIgnoreCase(view))
            return;

        if(event != null) {
            Button button = (Button) event.getSource();
            removePressed();
            if(button != homeNavigationButton) {
                button.getStyleClass().add("buttonPressed");
            }
        }

        actualView = view;

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/" + view));
            newView = loader.load();

            viewPane.setCenter(newView);

            ScreenManager.getInstance().getStage().setTitle(title + Constants.WINDOW_TITLE);

            ScreenManager.getInstance().setController(loader.getController());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
