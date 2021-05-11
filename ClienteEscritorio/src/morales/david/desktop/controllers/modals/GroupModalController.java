package morales.david.desktop.controllers.modals;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import morales.david.desktop.managers.DataManager;
import morales.david.desktop.models.Course;
import morales.david.desktop.models.Group;
import morales.david.desktop.utils.ComboBoxAutoComplete;
import morales.david.desktop.utils.Utils;

import java.net.URL;
import java.util.ResourceBundle;

public class GroupModalController implements Initializable {

    @FXML
    private ComboBox<Course> courseField;

    @FXML
    private TextField letterField;

    private Group group;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        courseField.setItems(DataManager.getInstance().getCourses());

    }

    public void setData(Group group, boolean edit) {

        this.group = group;

        if(!edit) return;

        letterField.setText(group.getLetter());

        Course course = group.getCourse();

        for(Course cachedCourse : DataManager.getInstance().getCourses()) {
            if(cachedCourse.getId() == course.getId()) {
                courseField.getSelectionModel().select(cachedCourse);
                break;
            }
        }

    }

    public Group getData() {
        group.setLetter(letterField.getText().toUpperCase());
        group.setCourse(courseField.getSelectionModel().getSelectedItem());
        return group;
    }

    public boolean validateInputs() {

        if(letterField.getText().isEmpty())
            return false;
        if(courseField.getSelectionModel().isEmpty())
            return false;

        return true;

    }

    @FXML
    void numberInputKeyTyped(KeyEvent event) {

        if(!Utils.isInteger(event.getCharacter()))
            event.consume();

    }

}
