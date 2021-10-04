package morales.david.desktop.controllers.options;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import morales.david.desktop.interfaces.Controller;
import morales.david.desktop.managers.SocketManager;
import morales.david.desktop.models.packets.PacketBuilder;
import morales.david.desktop.models.packets.PacketType;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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

            SocketManager.getInstance().sendPacketIO(packetBuilder.addArgument("type", "all").build());

        } else if(event.getSource() == cleanSchedules) {

            SocketManager.getInstance().sendPacketIO(packetBuilder.addArgument("type", "schedules").build());

        } else if(event.getSource() == cleanSubjects) {

            SocketManager.getInstance().sendPacketIO(packetBuilder.addArgument("type", "subjects").build());

        } else if(event.getSource() == cleanTeachers) {

            SocketManager.getInstance().sendPacketIO(packetBuilder.addArgument("type", "teachers").build());

        } else if(event.getSource() == cleanCourses) {

            SocketManager.getInstance().sendPacketIO(packetBuilder.addArgument("type", "courses").build());

        } else if(event.getSource() == cleanGroups) {

            SocketManager.getInstance().sendPacketIO(packetBuilder.addArgument("type", "groups").build());

        } else if(event.getSource() == cleanClassrooms) {

            SocketManager.getInstance().sendPacketIO(packetBuilder.addArgument("type", "classrooms").build());

        }

    }

}
