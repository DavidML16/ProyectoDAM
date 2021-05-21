package morales.david.desktop.controllers.schedules;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import morales.david.desktop.interfaces.Controller;
import morales.david.desktop.models.Classroom;
import morales.david.desktop.models.Group;
import morales.david.desktop.models.Teacher;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        removePressed();

    }

    private void removePressed() {
        teacherSearchButton.getStyleClass().remove("buttonPressed");
        groupSearchButton.getStyleClass().remove("buttonPressed");
        classroomSearchButton.getStyleClass().remove("buttonPressed");
        teacherPanel.setVisible(false);
        groupPanel.setVisible(false);
        classroomPanel.setVisible(false);
        searchButton.setDisable(true);
    }

    @FXML
    public void handleButtonAction(MouseEvent event) {

        removePressed();

        if(event.getSource() == teacherSearchButton) {
            searchType = "TEACHER";
            teacherSearchButton.getStyleClass().add("buttonPressed");
            teacherPanel.setVisible(true);
            searchButton.setDisable(false);
        } else if(event.getSource() == groupSearchButton) {
            searchType = "GROUP";
            groupSearchButton.getStyleClass().add("buttonPressed");
            groupPanel.setVisible(true);
            searchButton.setDisable(false);
        } else if(event.getSource() == classroomSearchButton) {
            searchType = "CLASSROOM";
            classroomSearchButton.getStyleClass().add("buttonPressed");
            classroomPanel.setVisible(true);
            searchButton.setDisable(false);
        }

    }

}
