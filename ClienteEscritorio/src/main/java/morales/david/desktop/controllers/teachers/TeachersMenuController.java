package morales.david.desktop.controllers.teachers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
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

public class TeachersMenuController implements Initializable, Controller {

    @FXML
    private Button teachersNavigationButton;

    @FXML
    private Button credentialsNavigationButton;

    @FXML
    private Button inspectorReportNavigationButton;

    @FXML
    private BorderPane viewPane;

    private String actualView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        actualView = "";

        Platform.runLater(() -> loadView("teachers/teachers.fxml", "Profesores", null));
        teachersNavigationButton.getStyleClass().add("buttonPressed");

        Platform.runLater(() -> {

            Packet teachersRequestPacket = new PacketBuilder().ofType(PacketType.TEACHERS.getRequest()).build();

            ClientManager.getInstance().sendPacketIO(teachersRequestPacket);

        });

    }

    @FXML
    public void handleButtonAction(MouseEvent event) {

        if(event.getSource() == teachersNavigationButton)
            loadView("teachers/teachers.fxml", "Profesores", event);
        else if (event.getSource() == credentialsNavigationButton)
            loadView("teachers/credentials.fxml", "Credenciales", event);
        else if (event.getSource() == inspectorReportNavigationButton)
            loadView("teachers/teacherreport.fxml", "Parte de guardia", event);

    }

    private void removePressed() {

        teachersNavigationButton.getStyleClass().remove("buttonPressed");
        credentialsNavigationButton.getStyleClass().remove("buttonPressed");
        inspectorReportNavigationButton.getStyleClass().remove("buttonPressed");

    }

    private void loadView(String view, String title, MouseEvent event) {

        Node newView = null;
        Node oldView = null;

        if(actualView.equalsIgnoreCase(view))
            return;

        if(event != null) {
            Button button = (Button) event.getSource();
            removePressed();
            button.getStyleClass().add("buttonPressed");
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
