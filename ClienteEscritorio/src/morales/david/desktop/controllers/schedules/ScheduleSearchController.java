package morales.david.desktop.controllers.schedules;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import morales.david.desktop.interfaces.Controller;
import morales.david.desktop.managers.DataManager;
import morales.david.desktop.managers.SocketManager;
import morales.david.desktop.models.Classroom;
import morales.david.desktop.models.Group;
import morales.david.desktop.models.Teacher;
import morales.david.desktop.models.packets.Packet;
import morales.david.desktop.models.packets.PacketBuilder;
import morales.david.desktop.models.packets.PacketType;

import java.net.URL;
import java.util.ResourceBundle;

public class ScheduleSearchController implements Initializable, Controller {

    @FXML
    private Button teacherSearchButton;

    @FXML
    private Button groupSearchButton;

    @FXML
    private Button classroomSearchButton;

    @FXML private VBox teacherPanel;
    @FXML private ComboBox<Teacher> teacherComboBox;

    @FXML private VBox groupPanel;
    @FXML private ComboBox<Group> groupComboBox;

    @FXML private VBox classroomPanel;
    @FXML private ComboBox<Classroom> classroomComboBox;

    @FXML
    private Button searchButton;

    private String searchType;

    private SchedulesMenuController parentController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        searchType = "";

        removePressed();

        teacherComboBox.setItems(DataManager.getInstance().getTeachers());
        groupComboBox.setItems(DataManager.getInstance().getGroups());
        classroomComboBox.setItems(DataManager.getInstance().getClassrooms());

    }

    private void removePressed() {
        teacherSearchButton.getStyleClass().remove("buttonPressed");
        groupSearchButton.getStyleClass().remove("buttonPressed");
        classroomSearchButton.getStyleClass().remove("buttonPressed");
        teacherPanel.setVisible(false);
        teacherComboBox.getSelectionModel().clearSelection();
        groupPanel.setVisible(false);
        groupComboBox.getSelectionModel().clearSelection();
        classroomPanel.setVisible(false);
        classroomComboBox.getSelectionModel().clearSelection();
        searchButton.setDisable(true);
    }

    @FXML
    public void handleButtonAction(MouseEvent event) {

        if(event.getSource() == searchButton) {

            if(searchType.equalsIgnoreCase(""))
                return;

            PacketBuilder packetBuilder = new PacketBuilder()
                    .ofType(PacketType.SCHEDULES.getRequest())
                    .addArgument("type", searchType);

            if(searchType.equalsIgnoreCase("TEACHER") && teacherComboBox.getSelectionModel().getSelectedItem() != null) {

                Teacher teacher = teacherComboBox.getSelectionModel().getSelectedItem();
                packetBuilder.addArgument("item", teacher);

            } else if(searchType.equalsIgnoreCase("GROUP") && groupComboBox.getSelectionModel().getSelectedItem() != null) {

                Group group = groupComboBox.getSelectionModel().getSelectedItem();
                packetBuilder.addArgument("item", group);

            } else if(searchType.equalsIgnoreCase("CLASSROOM") && classroomComboBox.getSelectionModel().getSelectedItem() != null) {

                Classroom classroom = classroomComboBox.getSelectionModel().getSelectedItem();
                packetBuilder.addArgument("item", classroom);

            }

            if(packetBuilder.hasArgument("item")) {

                SocketManager.getInstance().sendPacketIO(packetBuilder.build());

                removePressed();

            }

        } else {

            removePressed();

            if (event.getSource() == teacherSearchButton) {
                searchType = "TEACHER";
                teacherSearchButton.getStyleClass().add("buttonPressed");
                teacherPanel.setVisible(true);
                searchButton.setDisable(false);
            } else if (event.getSource() == groupSearchButton) {
                searchType = "GROUP";
                groupSearchButton.getStyleClass().add("buttonPressed");
                groupPanel.setVisible(true);
                searchButton.setDisable(false);
            } else if (event.getSource() == classroomSearchButton) {
                searchType = "CLASSROOM";
                classroomSearchButton.getStyleClass().add("buttonPressed");
                classroomPanel.setVisible(true);
                searchButton.setDisable(false);
            }

        }

    }

    public SchedulesMenuController getParentController() {
        return parentController;
    }

    public void setParentController(SchedulesMenuController parentController) {
        this.parentController = parentController;
    }

}
