package morales.david.desktop.controllers.modals;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import morales.david.desktop.models.Classroom;
import morales.david.desktop.models.Course;
import morales.david.desktop.utils.Utils;

import java.net.URL;
import java.util.ResourceBundle;

public class CourseModalController implements Initializable {

    @FXML
    private Spinner<Integer> levelField;

    @FXML
    private TextField nameField;

    private Course course;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        levelField.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 99));

    }

    public void setData(Course course, boolean edit) {

        this.course = course;

        if(!edit) return;

        levelField.getValueFactory().setValue(course.getLevel());
        nameField.setText(course.getName());

    }

    public Course getData() {
        course.setLevel(levelField.getValue());
        course.setName(nameField.getText());
        return course;
    }

    public boolean validateInputs() {

        if(levelField.getValue() == null)
            return false;
        if(nameField.getText().isEmpty())
            return false;

        return true;

    }

    @FXML
    void numberInputKeyTyped(KeyEvent event) {

        if(!Utils.isInteger(event.getCharacter()))
            event.consume();

    }

}
