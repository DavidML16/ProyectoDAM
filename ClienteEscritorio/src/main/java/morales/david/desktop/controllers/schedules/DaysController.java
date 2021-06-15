package morales.david.desktop.controllers.schedules;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import morales.david.desktop.controllers.modals.CourseModalController;
import morales.david.desktop.interfaces.Controller;
import morales.david.desktop.managers.DataManager;
import morales.david.desktop.managers.SocketManager;
import morales.david.desktop.models.Course;
import morales.david.desktop.models.Day;
import morales.david.desktop.models.packets.Packet;
import morales.david.desktop.models.packets.PacketBuilder;
import morales.david.desktop.models.packets.PacketType;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class DaysController implements Initializable, Controller {

    @FXML
    private TableView<Day> daysTable;

    @FXML
    private TableColumn<Day, Integer> idColumn;

    @FXML
    private TableColumn<Day, String> nameColumn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        showTable();

        Platform.runLater(() -> {

            Packet daysRequestPacket = new PacketBuilder()
                    .ofType(PacketType.DAYS.getRequest())
                    .build();

            SocketManager.getInstance().sendPacketIO(daysRequestPacket);

        });

    }

    public void showTable() {

        ObservableList<Day> list = DataManager.getInstance().getDays();

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        nameColumn.setCellFactory(
                TextFieldTableCell.forTableColumn());

        nameColumn.setOnEditCommit(event -> {

            if(SocketManager.getInstance().getClientSession().isTeacherRole())
                return;

            Day day = event.getTableView().getItems().get(event.getTablePosition().getRow());
            day.setName(event.getNewValue());

            Packet dayUpdatePacket = new PacketBuilder()
                    .ofType(PacketType.UPDATEDAY.getRequest())
                    .addArgument("day", day)
                    .build();

            SocketManager.getInstance().sendPacketIO(dayUpdatePacket);

        });

        daysTable.setItems(list);

        if(SocketManager.getInstance().getClientSession().isTeacherRole()) {
            daysTable.setEditable(false);
        } else {
            daysTable.setEditable(true);
        }

        daysTable.setPlaceholder(new Label("No existe ningun día registrado"));

    }

}
