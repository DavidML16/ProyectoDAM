package morales.david.desktop.controllers.courses;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
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
import morales.david.desktop.controllers.modals.CourseModalController;
import morales.david.desktop.controllers.modals.SubjectModalController;
import morales.david.desktop.interfaces.Controller;
import morales.david.desktop.managers.DataManager;
import morales.david.desktop.managers.SocketManager;
import morales.david.desktop.models.Course;
import morales.david.desktop.models.Subject;
import morales.david.desktop.models.packets.Packet;
import morales.david.desktop.models.packets.PacketBuilder;
import morales.david.desktop.models.packets.PacketType;
import morales.david.desktop.utils.Constants;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class SubjectsController implements Initializable, Controller {

    @FXML
    private TableView<Subject> subjectsTable;

    @FXML
    private TableColumn<Subject, Integer> idColumn;

    @FXML
    private TableColumn<Subject, Integer> numberColumn;

    @FXML
    private TableColumn<Subject, String> abreviationColumn;

    @FXML
    private TableColumn<Subject, String> nameColumn;

    @FXML
    private TableColumn<Subject, String> courseColumn;

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

            Packet coursesRequestPacket = new PacketBuilder()
                    .ofType(PacketType.SUBJECTS.getRequest())
                    .build();

            SocketManager.getInstance().sendPacketIO(coursesRequestPacket);

        });

    }

    @FXML
    public void handleButtonAction(MouseEvent event) {

        if(event.getSource() == newButton) {

            newSubject();

        } else if(event.getSource() == editButton) {

            Subject selected = subjectsTable.getSelectionModel().getSelectedItem();

            editSubject(selected);

        } else if(event.getSource() == deleteButton) {

            deleteSubject();

        }

    }

    public void showTable() {

        ObservableList<Subject> list = DataManager.getInstance().getSubjects();

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        numberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        abreviationColumn.setCellValueFactory(new PropertyValueFactory<>("abreviation"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        courseColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCourses().size() + " Curso"
                + (cellData.getValue().getCourses().size() != 1 ? "s" : "")));

        subjectsTable.setItems(list);

        subjectsTable.setRowFactory( tv -> {

            TableRow<Subject> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2 && !row.isEmpty()) {

                    Subject selected = row.getItem();

                    editSubject(selected);

                }
            });

            return row ;
            
        });

        filterField.textProperty().addListener(observable -> {

            if(filterField.textProperty().get().isEmpty()) {
                subjectsTable.setItems(list);
                return;
            }

            ObservableList<Subject> tableItems = FXCollections.observableArrayList();
            ObservableList<TableColumn<Subject, ?>> cols = subjectsTable.getColumns();

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

            subjectsTable.setItems(tableItems);


        });

        subjectsTable.setPlaceholder(new Label("No existe ninguna asignatura registrada"));

    }

    private void deleteSubject() {

        Subject subject = subjectsTable.getSelectionModel().getSelectedItem();

        Packet removeSubjectRequestPacket = new PacketBuilder()
                .ofType(PacketType.REMOVESUBJECT.getRequest())
                .addArgument("subject", subject)
                .build();

        SocketManager.getInstance().sendPacketIO(removeSubjectRequestPacket);

    }

    private void editSubject(Subject subject) {

        if(subject == null)
            return;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/modals/editSubjectModal.fxml"));
        try {

            DialogPane parent = loader.load();
            SubjectModalController controller = loader.getController();

            controller.setData(subject, true);

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

                Subject updatedSubject = controller.getData();

                Packet updateSubjectRequestPacket = new PacketBuilder()
                        .ofType(PacketType.UPDATESUBJECT.getRequest())
                        .addArgument("subject", updatedSubject)
                        .build();

                SocketManager.getInstance().sendPacketIO(updateSubjectRequestPacket);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void newSubject() {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/modals/newSubjectModal.fxml"));
        try {

            DialogPane parent = loader.load();
            SubjectModalController controller = loader.getController();

            controller.setData(new Subject(), false);

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

                Subject subject = controller.getData();

                Packet addSubjectRequestPacket = new PacketBuilder()
                        .ofType(PacketType.ADDSUBJECT.getRequest())
                        .addArgument("subject", subject)
                        .build();

                SocketManager.getInstance().sendPacketIO(addSubjectRequestPacket);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
