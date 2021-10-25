package morales.david.desktop.controllers.options;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import morales.david.desktop.interfaces.Controller;
import morales.david.desktop.netty.ClientManager;
import morales.david.desktop.models.packets.PacketBuilder;
import morales.david.desktop.models.packets.PacketType;

import java.net.URL;
import java.util.ResourceBundle;

public class CleanController implements Initializable, Controller {

    @FXML
    private AnchorPane root;

    @FXML
    private Button cleanSchedules;

    @FXML
    private Button cleanSubjects;

    @FXML
    private Button cleanTeachers;

    @FXML
    private Button cleanCourses;

    @FXML
    private Button cleanGroups;

    @FXML
    private Button cleanClassrooms;

    @FXML
    private Button totalCleanButton;


    @Override
    public void initialize(URL location, ResourceBundle resources) { }

    @FXML
    void handleButtonAction(MouseEvent event) {

        PacketBuilder packetBuilder = new PacketBuilder()
                .ofType(PacketType.DATABASECLEAN.getRequest());

        if(event.getSource() == totalCleanButton) {

            ClientManager.getInstance().sendPacketIO(packetBuilder.addArgument("type", "all").build());

        } else if(event.getSource() == cleanSchedules) {

            ClientManager.getInstance().sendPacketIO(packetBuilder.addArgument("type", "schedules").build());

        } else if(event.getSource() == cleanSubjects) {

            ClientManager.getInstance().sendPacketIO(packetBuilder.addArgument("type", "subjects").build());

        } else if(event.getSource() == cleanTeachers) {

            ClientManager.getInstance().sendPacketIO(packetBuilder.addArgument("type", "teachers").build());

        } else if(event.getSource() == cleanCourses) {

            ClientManager.getInstance().sendPacketIO(packetBuilder.addArgument("type", "courses").build());

        } else if(event.getSource() == cleanGroups) {

            ClientManager.getInstance().sendPacketIO(packetBuilder.addArgument("type", "groups").build());

        } else if(event.getSource() == cleanClassrooms) {

            ClientManager.getInstance().sendPacketIO(packetBuilder.addArgument("type", "classrooms").build());

        }

    }

}
