package morales.david.desktop.controllers;

import javafx.application.Platform;
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
import morales.david.desktop.controllers.modals.ClassroomModalController;
import morales.david.desktop.interfaces.Controller;
import morales.david.desktop.managers.DataManager;
import morales.david.desktop.managers.SocketManager;
import morales.david.desktop.models.Classroom;
import morales.david.desktop.models.Subject;
import morales.david.desktop.models.packets.Packet;
import morales.david.desktop.models.packets.PacketBuilder;
import morales.david.desktop.models.packets.PacketType;
import morales.david.desktop.utils.Constants;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class ClassroomsController implements Initializable, Controller {

    @FXML
    private TableView<Classroom> classroomsTable;

    @FXML
    private TableColumn<Classroom, Integer> idColumn;

    @FXML
    private TableColumn<Classroom, String> nameColumn;

    @FXML
    private TableColumn<Classroom, Integer> floorColumn;

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

            Packet classroomsRequestPacket = new PacketBuilder()
                    .ofType(PacketType.CLASSROOMS.getRequest())
                    .build();

            SocketManager.getInstance().sendPacketIO(classroomsRequestPacket);

        });

    }

    @FXML
    public void handleButtonAction(MouseEvent event) {

        if(event.getSource() == newButton) {

            newTeacher();

        } else if(event.getSource() == editButton) {

            Classroom selected = classroomsTable.getSelectionModel().getSelectedItem();

            editClassroom(selected);

        } else if(event.getSource() == deleteButton) {

            deleteClassroom();

        }

    }

    public void showTable() {

        ObservableList<Classroom> list = DataManager.getInstance().getClassrooms();

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        floorColumn.setCellValueFactory(new PropertyValueFactory<>("floor"));

        classroomsTable.setItems(list);

        classroomsTable.setRowFactory( tv -> {

            TableRow<Classroom> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2 && !row.isEmpty()) {

                    Classroom selected = row.getItem();

                    editClassroom(selected);

                }
            });

            return row ;
            
        });

        filterField.textProperty().addListener(observable -> {

            if(filterField.textProperty().get().isEmpty()) {
                classroomsTable.setItems(list);
                return;
            }

            ObservableList<Classroom> tableItems = FXCollections.observableArrayList();
            ObservableList<TableColumn<Classroom, ?>> cols = classroomsTable.getColumns();

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

            classroomsTable.setItems(tableItems);

        });

    }

    private void deleteClassroom() {

        Classroom classroom = classroomsTable.getSelectionModel().getSelectedItem();

        Packet removeClassroomRequestPacket = new PacketBuilder()
                .ofType(PacketType.REMOVECLASSROOM.getRequest())
                .addArgument("classroom", classroom)
                .build();

        SocketManager.getInstance().sendPacketIO(removeClassroomRequestPacket);

    }

    private void editClassroom(Classroom classroom) {

        if(classroom == null)
            return;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/modals/editClassroomModal.fxml"));
        try {

            DialogPane parent = loader.load();
            ClassroomModalController controller = loader.getController();

            controller.setData(classroom, true);

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

            ((Stage)dialog.getDialogPane().getScene().getWindow()).getIcons().add(new Image("/resources/images/schedule-icon-inverted.png"));

            Optional<ButtonType> clickedButton = dialog.showAndWait();

            if(clickedButton.get() == updateBtn) {

                Classroom updatedClassroom = controller.getData();

                Packet updateClassroomRequestPacket = new PacketBuilder()
                        .ofType(PacketType.UPDATECLASSROOM.getRequest())
                        .addArgument("classroom", updatedClassroom)
                        .build();

                SocketManager.getInstance().sendPacketIO(updateClassroomRequestPacket);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void newTeacher() {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/modals/newClassroomModal.fxml"));
        try {

            DialogPane parent = loader.load();
            ClassroomModalController controller = loader.getController();

            controller.setData(new Classroom(), false);

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

            ((Stage)dialog.getDialogPane().getScene().getWindow()).getIcons().add(new Image("/resources/images/schedule-icon-inverted.png"));

            Optional<ButtonType> clickedButton = dialog.showAndWait();

            if(clickedButton.get() == addBtn) {

                Classroom classroom = controller.getData();

                Packet addClassroomRequestPacket = new PacketBuilder()
                        .ofType(PacketType.ADDCLASSROOM.getRequest())
                        .addArgument("classroom", classroom)
                        .build();

                SocketManager.getInstance().sendPacketIO(addClassroomRequestPacket);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
