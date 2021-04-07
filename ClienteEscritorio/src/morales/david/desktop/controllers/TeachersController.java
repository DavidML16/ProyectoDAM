package morales.david.desktop.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import morales.david.desktop.interfaces.Controller;
import morales.david.desktop.managers.DataManager;
import morales.david.desktop.managers.SocketManager;
import morales.david.desktop.models.Packet;
import morales.david.desktop.models.PacketBuilder;
import morales.david.desktop.models.Teacher;
import morales.david.desktop.utils.Constants;

import java.net.URL;
import java.util.ResourceBundle;

public class TeachersController implements Initializable, Controller {

    @FXML
    private TableView<Teacher> teachersTable;

    @FXML
    private TableColumn<Teacher, Integer> idColumn;

    @FXML
    private TableColumn<Teacher, Integer> numberColumn;

    @FXML
    private TableColumn<Teacher, String> nameColumn;

    @FXML
    private TableColumn<Teacher, String> abreviationColumn;

    @FXML
    private TableColumn<Teacher, Integer> minDayHoursColumn;

    @FXML
    private TableColumn<Teacher, Integer> maxDayHoursColumn;

    @FXML
    private TableColumn<Teacher, String> departmentColumn;

    @FXML
    private Button newButton;

    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Platform.runLater(() -> {

            Packet teachersRequestPacket = new PacketBuilder()
                    .ofType(Constants.REQUEST_TEACHERS)
                    .build();

            SocketManager.getInstance().sendPacketIO(teachersRequestPacket);

        });

        showTable();

    }

    public void showTable() {

        ObservableList<Teacher> list = DataManager.getInstance().getTeachers();

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        numberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        abreviationColumn.setCellValueFactory(new PropertyValueFactory<>("abreviation"));
        minDayHoursColumn.setCellValueFactory(new PropertyValueFactory<>("minDayHours"));
        maxDayHoursColumn.setCellValueFactory(new PropertyValueFactory<>("maxDayHours"));
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));

        teachersTable.setItems(list);

    }



}
