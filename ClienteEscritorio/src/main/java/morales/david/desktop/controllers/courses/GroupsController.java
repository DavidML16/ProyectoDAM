package morales.david.desktop.controllers.courses;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import morales.david.desktop.controllers.modals.GroupModalController;
import morales.david.desktop.interfaces.Controller;
import morales.david.desktop.managers.DataManager;
import morales.david.desktop.managers.SocketManager;
import morales.david.desktop.models.Group;
import morales.david.desktop.models.packets.Packet;
import morales.david.desktop.models.packets.PacketBuilder;
import morales.david.desktop.models.packets.PacketType;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class GroupsController implements Initializable, Controller {

    @FXML
    private TableView<Group> groupsTable;

    @FXML
    private TableColumn<Group, Integer> idColumn;

    @FXML
    private TableColumn<Group, String> courseColumn;

    @FXML
    private TableColumn<Group, String> letterColumn;

    @FXML
    private TextField filterField;

    @FXML
    private Button newButton;

    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        showTable();

        Platform.runLater(() -> {

            Packet groupsRequestPacket = new PacketBuilder()
                    .ofType(PacketType.GROUPS.getRequest())
                    .build();

            SocketManager.getInstance().sendPacketIO(groupsRequestPacket);

        });

    }

    @FXML
    public void handleButtonAction(MouseEvent event) {

        if(event.getSource() == newButton) {

            newGroup();

        } else if(event.getSource() == editButton) {

            Group selected = groupsTable.getSelectionModel().getSelectedItem();

            editGroup(selected);

        } else if(event.getSource() == deleteButton) {

            deleteGroup();

        }

    }

    public void showTable() {

        ObservableList<Group> list = DataManager.getInstance().getGroups();

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        courseColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCourse().toString()));
        letterColumn.setCellValueFactory(new PropertyValueFactory<>("letter"));

        groupsTable.setItems(list);

        groupsTable.setRowFactory(tv -> {

            TableRow<Group> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2 && !row.isEmpty()) {

                    Group selected = row.getItem();

                    editGroup(selected);

                }
            });

            return row ;
            
        });

        filterField.textProperty().addListener(observable -> {

            if(filterField.textProperty().get().isEmpty()) {
                groupsTable.setItems(list);
                return;
            }

            ObservableList<Group> tableItems = FXCollections.observableArrayList();
            ObservableList<TableColumn<Group, ?>> cols = groupsTable.getColumns();

            for(int i=0; i< list.size(); i++) {

                for(int j=0; j<cols.size(); j++) {
                    TableColumn col = cols.get(j);
                    String cellValue = col.getCellData(list.get(i)).toString();
                    cellValue = cellValue.toLowerCase();
                    if(cellValue.contains(filterField.textProperty().get().toLowerCase())) {
                        tableItems.add(list.get(i));
                        break;
                    }
                }
            }

            groupsTable.setItems(tableItems);


        });

        groupsTable.setPlaceholder(new Label("No existe ningun grupo registrado"));

    }

    private void deleteGroup() {

        Group group = groupsTable.getSelectionModel().getSelectedItem();

        Packet removeGroupRequestPacket = new PacketBuilder()
                .ofType(PacketType.REMOVEGROUP.getRequest())
                .addArgument("group", group)
                .build();

        SocketManager.getInstance().sendPacketIO(removeGroupRequestPacket);

    }

    private void editGroup(Group group) {

        if(group == null)
            return;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/modals/editGroupModal.fxml"));
        try {

            DialogPane parent = loader.load();
            GroupModalController controller = loader.getController();

            controller.setData(group, true);

            Dialog<ButtonType> dialog = new Dialog<>();

            dialog.setDialogPane(parent);
            dialog.setTitle("");

            ButtonType updateBtn = new ButtonType("Actualizar", ButtonBar.ButtonData.YES);
            ButtonType cancelBtn = new ButtonType("Cancelar", ButtonBar.ButtonData.NO);

            dialog.getDialogPane().getButtonTypes().addAll(updateBtn, cancelBtn);

            Button updateButton = (Button) dialog.getDialogPane().lookupButton(updateBtn);
            updateButton.getStyleClass().addAll("dialogButton", "updateButton");

            updateButton.addEventFilter(
                    ActionEvent.ACTION,
                    event -> {
                        if (!controller.validateInputs()) {
                            event.consume();
                        }
                    }
            );

            Button cancelButton = (Button) dialog.getDialogPane().lookupButton(cancelBtn);
            cancelButton.getStyleClass().addAll("dialogButton", "cancelButton");

            ((Stage)dialog.getDialogPane().getScene().getWindow()).getIcons().add(new Image("/images/schedule-icon-inverted.png"));

            Optional<ButtonType> clickedButton = dialog.showAndWait();

            if(clickedButton.get() == updateBtn) {

                Group updatedGroup = controller.getData();

                Packet updateGroupRequestPacket = new PacketBuilder()
                        .ofType(PacketType.UPDATEGROUP.getRequest())
                        .addArgument("group", updatedGroup)
                        .build();

                SocketManager.getInstance().sendPacketIO(updateGroupRequestPacket);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void newGroup() {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/modals/newGroupModal.fxml"));
        try {

            DialogPane parent = loader.load();
            GroupModalController controller = loader.getController();

            controller.setData(new Group(), false);

            Dialog<ButtonType> dialog = new Dialog<>();

            dialog.setDialogPane(parent);
            dialog.setTitle("");

            ButtonType addBtn = new ButtonType("Añadir", ButtonBar.ButtonData.YES);
            ButtonType cancelBtn = new ButtonType("Cancelar", ButtonBar.ButtonData.NO);

            dialog.getDialogPane().getButtonTypes().addAll(addBtn, cancelBtn);

            Button addButton = (Button) dialog.getDialogPane().lookupButton(addBtn);
            addButton.getStyleClass().addAll("dialogButton", "addButton");

            addButton.addEventFilter(
                    ActionEvent.ACTION,
                    event -> {
                        if (!controller.validateInputs()) {
                            event.consume();
                        }
                    }
            );

            Button cancelButton = (Button) dialog.getDialogPane().lookupButton(cancelBtn);
            cancelButton.getStyleClass().addAll("dialogButton", "cancelButton");

            ((Stage)dialog.getDialogPane().getScene().getWindow()).getIcons().add(new Image("/images/schedule-icon-inverted.png"));

            Optional<ButtonType> clickedButton = dialog.showAndWait();

            if(clickedButton.get() == addBtn) {

                Group group = controller.getData();

                Packet addGroupRequestPacket = new PacketBuilder()
                        .ofType(PacketType.ADDGROUP.getRequest())
                        .addArgument("group", group)
                        .build();

                SocketManager.getInstance().sendPacketIO(addGroupRequestPacket);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
