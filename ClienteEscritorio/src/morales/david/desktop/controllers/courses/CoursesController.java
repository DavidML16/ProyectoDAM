package morales.david.desktop.controllers.courses;

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
import morales.david.desktop.controllers.modals.CourseModalController;
import morales.david.desktop.controllers.modals.TeacherModalController;
import morales.david.desktop.interfaces.Controller;
import morales.david.desktop.managers.DataManager;
import morales.david.desktop.managers.SocketManager;
import morales.david.desktop.models.Course;
import morales.david.desktop.models.Subject;
import morales.david.desktop.models.Teacher;
import morales.david.desktop.models.packets.Packet;
import morales.david.desktop.models.packets.PacketBuilder;
import morales.david.desktop.models.packets.PacketType;
import morales.david.desktop.utils.Constants;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class CoursesController implements Initializable, Controller {

    @FXML
    private TableView<Course> coursesTable;

    @FXML
    private TableColumn<Course, Integer> idColumn;

    @FXML
    private TableColumn<Course, Integer> levelColumn;

    @FXML
    private TableColumn<Course, String> nameColumn;

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
                    .ofType(PacketType.COURSES.getRequest())
                    .build();

            SocketManager.getInstance().sendPacketIO(coursesRequestPacket);

        });

    }

    @FXML
    public void handleButtonAction(MouseEvent event) {

        if(event.getSource() == newButton) {

            newCourse();

        } else if(event.getSource() == editButton) {

            Course selected = coursesTable.getSelectionModel().getSelectedItem();

            editCourse(selected);

        } else if(event.getSource() == deleteButton) {

            deleteCourse();

        }

    }

    public void showTable() {

        ObservableList<Course> list = DataManager.getInstance().getCourses();

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        levelColumn.setCellValueFactory(new PropertyValueFactory<>("level"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        coursesTable.setItems(list);

        coursesTable.setRowFactory( tv -> {

            TableRow<Course> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2 && !row.isEmpty()) {

                    Course selected = row.getItem();

                    editCourse(selected);

                }
            });

            return row ;
            
        });

        filterField.textProperty().addListener(observable -> {

            if(filterField.textProperty().get().isEmpty()) {
                coursesTable.setItems(list);
                return;
            }

            ObservableList<Course> tableItems = FXCollections.observableArrayList();
            ObservableList<TableColumn<Course, ?>> cols = coursesTable.getColumns();

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

            coursesTable.setItems(tableItems);

        });

        coursesTable.setPlaceholder(new Label("No existe ningun curso registrado"));

    }

    private void deleteCourse() {

        Course course = coursesTable.getSelectionModel().getSelectedItem();

        Packet removeCourseRequestPacket = new PacketBuilder()
                .ofType(PacketType.REMOVECOURSE.getRequest())
                .addArgument("course", course)
                .build();

        SocketManager.getInstance().sendPacketIO(removeCourseRequestPacket);

    }

    private void editCourse(Course course) {

        if(course == null)
            return;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/modals/editCourseModal.fxml"));
        try {

            DialogPane parent = loader.load();
            CourseModalController controller = loader.getController();

            controller.setData(course, true);

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

                Course updatedCourse = controller.getData();

                Packet updateCourseRequestPacket = new PacketBuilder()
                        .ofType(PacketType.UPDATECOURSE.getRequest())
                        .addArgument("course", updatedCourse)
                        .build();

                SocketManager.getInstance().sendPacketIO(updateCourseRequestPacket);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void newCourse() {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/modals/newCourseModal.fxml"));
        try {

            DialogPane parent = loader.load();
            CourseModalController controller = loader.getController();

            controller.setData(new Course(), false);

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

                Course course = controller.getData();

                Packet addCourseRequestPacket = new PacketBuilder()
                        .ofType(PacketType.ADDCOURSE.getRequest())
                        .addArgument("course", course)
                        .build();

                SocketManager.getInstance().sendPacketIO(addCourseRequestPacket);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
