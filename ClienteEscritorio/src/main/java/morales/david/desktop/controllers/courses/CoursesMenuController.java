package morales.david.desktop.controllers.courses;

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
import morales.david.desktop.netty.ClientManager;
import morales.david.desktop.models.packets.Packet;
import morales.david.desktop.models.packets.PacketBuilder;
import morales.david.desktop.models.packets.PacketType;
import morales.david.desktop.utils.Constants;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CoursesMenuController implements Initializable, Controller {

    @FXML
    private Button coursesNavigationButton;

    @FXML
    private Button groupsNavigationButton;

    @FXML
    private Button subjectsNavigationButton;

    @FXML
    private BorderPane viewPane;

    private String actualView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        actualView = "";

        Platform.runLater(() -> loadView("courses/courses.fxml", "Cursos", null));
        coursesNavigationButton.getStyleClass().add("buttonPressed");

        Platform.runLater(() -> {

            Packet coursesRequestPacket = new PacketBuilder().ofType(PacketType.COURSES.getRequest()).build();

            ClientManager.getInstance().sendPacketIO(coursesRequestPacket);

        });

    }

    @FXML
    public void handleButtonAction(MouseEvent event) {

        if(event.getSource() == coursesNavigationButton)
            loadView("courses/courses.fxml", "Cursos", event);
        else if (event.getSource() == groupsNavigationButton)
            loadView("courses/groups.fxml", "Grupos", event);
        else if (event.getSource() == subjectsNavigationButton)
            loadView("courses/subjects.fxml", "Asignaturas", event);

    }

    private void removePressed() {

        coursesNavigationButton.getStyleClass().remove("buttonPressed");
        groupsNavigationButton.getStyleClass().remove("buttonPressed");
        subjectsNavigationButton.getStyleClass().remove("buttonPressed");

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
