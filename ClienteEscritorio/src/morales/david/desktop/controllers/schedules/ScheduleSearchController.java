package morales.david.desktop.controllers.schedules;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import morales.david.desktop.interfaces.Controller;
import morales.david.desktop.managers.DataManager;
import morales.david.desktop.managers.SocketManager;
import morales.david.desktop.models.Classroom;
import morales.david.desktop.models.Group;
import morales.david.desktop.models.Teacher;
import morales.david.desktop.models.packets.PacketBuilder;
import morales.david.desktop.models.packets.PacketType;
import morales.david.desktop.utils.FxUtilTest;

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

    @FXML
    private Button exportButton;

    private String searchType;

    private SchedulesMenuController parentController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        searchType = "";

        removePressed();

        teacherComboBox.setItems(DataManager.getInstance().getTeachers());
        teacherComboBox.setConverter(new StringConverter<Teacher>() {
            @Override
            public String toString(Teacher object) {
                return object != null ? object.getName() : "";
            }

            @Override
            public Teacher fromString(String string) {
                return teacherComboBox.getItems().stream().filter(object ->
                        object.getName().equals(string)).findFirst().orElse(null);
            }
        });

        groupComboBox.setItems(DataManager.getInstance().getGroups());
        groupComboBox.setConverter(new StringConverter<Group>() {
            @Override
            public String toString(Group object) {
                return object != null ? object.toString() : "";
            }

            @Override
            public Group fromString(String string) {
                return groupComboBox.getItems().stream().filter(object ->
                        object.toString().equals(string)).findFirst().orElse(null);
            }
        });

        classroomComboBox.setItems(DataManager.getInstance().getClassrooms());
        classroomComboBox.setConverter(new StringConverter<Classroom>() {
            @Override
            public String toString(Classroom object) {
                return object != null ? object.getName() : "";
            }

            @Override
            public Classroom fromString(String string) {
                return classroomComboBox.getItems().stream().filter(object ->
                        object.getName().equals(string)).findFirst().orElse(null);
            }
        });

        FxUtilTest.autoCompleteComboBoxPlus(teacherComboBox, (typedText, itemToCompare) -> itemToCompare.getName().toLowerCase().contains(typedText.toLowerCase()));
        FxUtilTest.autoCompleteComboBoxPlus(groupComboBox, (typedText, itemToCompare) -> itemToCompare.toString().toLowerCase().contains(typedText.toLowerCase()));
        FxUtilTest.autoCompleteComboBoxPlus(classroomComboBox, (typedText, itemToCompare) -> itemToCompare.getName().toLowerCase().contains(typedText.toLowerCase()));

    }

    private void removePressed() {

        teacherSearchButton.getStyleClass().remove("buttonPressed");
        groupSearchButton.getStyleClass().remove("buttonPressed");
        classroomSearchButton.getStyleClass().remove("buttonPressed");

        teacherPanel.setVisible(false);
        teacherComboBox.getSelectionModel().clearSelection();
        teacherComboBox.setItems(DataManager.getInstance().getTeachers());

        groupPanel.setVisible(false);
        groupComboBox.getSelectionModel().clearSelection();
        groupComboBox.setItems(DataManager.getInstance().getGroups());

        classroomPanel.setVisible(false);
        classroomComboBox.getSelectionModel().clearSelection();
        classroomComboBox.setItems(DataManager.getInstance().getClassrooms());

        searchButton.setDisable(true);
        exportButton.setDisable(true);
    }

    @FXML
    public void handleButtonAction(MouseEvent event) {

        if(event.getSource() == searchButton || event.getSource() == exportButton) {

            if(searchType.equalsIgnoreCase(""))
                return;

            PacketBuilder packetBuilder = new PacketBuilder()
                    .ofType(PacketType.SEARCHSCHEDULE.getRequest())
                    .addArgument("type", searchType);

            if(searchType.equalsIgnoreCase("TEACHER") && teacherComboBox.getSelectionModel().getSelectedItem() != null) {

                Teacher teacher = FxUtilTest.getComboBoxValue(teacherComboBox);
                packetBuilder.addArgument("item", teacher);

            } else if(searchType.equalsIgnoreCase("GROUP") && groupComboBox.getSelectionModel().getSelectedItem() != null) {

                Group group = FxUtilTest.getComboBoxValue(groupComboBox);
                packetBuilder.addArgument("item", group);

            } else if(searchType.equalsIgnoreCase("CLASSROOM") && classroomComboBox.getSelectionModel().getSelectedItem() != null) {

                Classroom classroom = FxUtilTest.getComboBoxValue(classroomComboBox);
                packetBuilder.addArgument("item", classroom);

            }

            if(event.getSource() == searchButton)
                packetBuilder.addArgument("callback", "SEARCH");
            else
                packetBuilder.addArgument("callback", "EXPORT");

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
                exportButton.setDisable(false);
            } else if (event.getSource() == groupSearchButton) {
                searchType = "GROUP";
                groupSearchButton.getStyleClass().add("buttonPressed");
                groupPanel.setVisible(true);
                searchButton.setDisable(false);
                exportButton.setDisable(false);
            } else if (event.getSource() == classroomSearchButton) {
                searchType = "CLASSROOM";
                classroomSearchButton.getStyleClass().add("buttonPressed");
                classroomPanel.setVisible(true);
                searchButton.setDisable(false);
                exportButton.setDisable(false);
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
