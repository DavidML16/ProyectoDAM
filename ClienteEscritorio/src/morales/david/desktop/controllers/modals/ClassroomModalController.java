package morales.david.desktop.controllers.modals;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import morales.david.desktop.models.Classroom;
import morales.david.desktop.models.Teacher;
import morales.david.desktop.utils.Utils;

import java.net.URL;
import java.util.ResourceBundle;

public class ClassroomModalController implements Initializable {

    @FXML
    private TextField nameField;

    private Classroom classroom;

    @Override
    public void initialize(URL location, ResourceBundle resources) { }

    public void setData(Classroom classroom, boolean edit) {

        this.classroom = classroom;

        if(!edit) return;

        nameField.setText(classroom.getName());

    }

    public Classroom getData() {
        classroom.setName(nameField.getText());
        return classroom;
    }

    public boolean validateInputs() {

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
