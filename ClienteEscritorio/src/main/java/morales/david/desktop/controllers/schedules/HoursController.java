package morales.david.desktop.controllers.schedules;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import morales.david.desktop.interfaces.Controller;
import morales.david.desktop.managers.DataManager;
import morales.david.desktop.managers.SocketManager;
import morales.david.desktop.models.Day;
import morales.david.desktop.models.Hour;
import morales.david.desktop.models.packets.Packet;
import morales.david.desktop.models.packets.PacketBuilder;
import morales.david.desktop.models.packets.PacketType;

import java.net.URL;
import java.util.ResourceBundle;

public class HoursController implements Initializable, Controller {

    @FXML
    private TableView<Hour> hoursTable;

    @FXML
    private TableColumn<Hour, String> nameColumn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        showTable();

        Platform.runLater(() -> {

            Packet hoursRequestPacket = new PacketBuilder()
                    .ofType(PacketType.HOURS.getRequest())
                    .build();

            SocketManager.getInstance().sendPacketIO(hoursRequestPacket);

        });

    }

    public void showTable() {

        ObservableList<Hour> list = DataManager.getInstance().getHours();

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        nameColumn.setCellFactory(
                TextFieldTableCell.forTableColumn());

        nameColumn.setOnEditCommit(event -> {

            if(SocketManager.getInstance().getClientSession().isTeacherRole())
                return;

            Hour hour = event.getTableView().getItems().get(event.getTablePosition().getRow());
            hour.setName(event.getNewValue());

            Packet hourUpdatePacket = new PacketBuilder()
                    .ofType(PacketType.UPDATEHOUR.getRequest())
                    .addArgument("hour", hour)
                    .build();

            SocketManager.getInstance().sendPacketIO(hourUpdatePacket);

        });

        hoursTable.setItems(list);

        if(SocketManager.getInstance().getClientSession().isTeacherRole()) {
            hoursTable.setEditable(false);
        } else {
            hoursTable.setEditable(true);
        }

        hoursTable.setPlaceholder(new Label("No existe ninguna hora registrada"));

    }

}
