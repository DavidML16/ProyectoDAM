package morales.david.desktop.controllers;

import javafx.application.Platform;
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
import morales.david.desktop.controllers.modals.TeacherModalController;
import morales.david.desktop.interfaces.Controller;
import morales.david.desktop.managers.DataManager;
import morales.david.desktop.managers.SocketManager;
import morales.david.desktop.models.Packet;
import morales.david.desktop.models.PacketBuilder;
import morales.david.desktop.models.Teacher;
import morales.david.desktop.utils.Constants;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
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

        showTable();

        Platform.runLater(() -> {

            Packet teachersRequestPacket = new PacketBuilder()
                    .ofType(Constants.REQUEST_TEACHERS)
                    .build();

            SocketManager.getInstance().sendPacketIO(teachersRequestPacket);

        });

    }

    @FXML
    public void handleButtonAction(MouseEvent event) {

        if(event.getSource() == newButton) {

            newTeacher();

        } else if(event.getSource() == editButton) {

            Teacher selected = teachersTable.getSelectionModel().getSelectedItem();

            editTeacher(selected);

        } else if(event.getSource() == deleteButton) {

            deleteTeacher();

        }

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

        teachersTable.setRowFactory( tv -> {

            TableRow<Teacher> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2 && !row.isEmpty()) {

                    Teacher selected = row.getItem();

                    editTeacher(selected);

                }
            });

            return row ;
            
        });

    }

    private void deleteTeacher() {

        Teacher teacher = teachersTable.getSelectionModel().getSelectedItem();

        Packet removeTeacherRequestPacket = new PacketBuilder()
                .ofType(Constants.REQUEST_REMOVETEACHER)
                .addArgument("teacher", teacher)
                .build();

        SocketManager.getInstance().sendPacketIO(removeTeacherRequestPacket);

    }

    private void editTeacher(Teacher teacher) {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/modals/editTeacherModal.fxml"));
        try {

            DialogPane parent = loader.load();
            TeacherModalController controller = loader.getController();

            controller.setData(teacher, true);

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

                Teacher updatedTeacher = controller.getData();

                Packet updateTeacherRequestPacket = new PacketBuilder()
                        .ofType(Constants.REQUEST_UPDATETEACHER)
                        .addArgument("teacher", updatedTeacher)
                        .build();

                SocketManager.getInstance().sendPacketIO(updateTeacherRequestPacket);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void newTeacher() {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/modals/newTeacherModal.fxml"));
        try {

            DialogPane parent = loader.load();
            TeacherModalController controller = loader.getController();

            controller.setData(new Teacher(), false);

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

                Teacher teacher = controller.getData();

                Packet addTeacherRequestPacket = new PacketBuilder()
                        .ofType(Constants.REQUEST_ADDTEACHER)
                        .addArgument("teacher", teacher)
                        .build();

                SocketManager.getInstance().sendPacketIO(addTeacherRequestPacket);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
