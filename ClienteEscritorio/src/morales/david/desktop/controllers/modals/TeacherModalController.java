package morales.david.desktop.controllers.modals;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import morales.david.desktop.models.Teacher;
import morales.david.desktop.utils.Utils;

import java.net.URL;
import java.util.ResourceBundle;

public class TeacherModalController implements Initializable {

    @FXML
    private TextField numberField;

    @FXML
    private TextField nameField;

    @FXML
    private TextField abreviationField;

    @FXML
    private TextField departmentField;

    @FXML
    private Spinner<Integer> minDayHoursField;

    @FXML
    private Spinner<Integer> maxDayHoursField;

    private Teacher teacher;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        minDayHoursField.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12));
        maxDayHoursField.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12));

    }

    public void setData(Teacher teacher, boolean edit) {

        this.teacher = teacher;

        if(!edit) return;

        numberField.setText(Integer.toString(teacher.getNumber()));
        nameField.setText(teacher.getName());
        abreviationField.setText(teacher.getAbreviation());
        departmentField.setText(teacher.getDepartment());
        minDayHoursField.getValueFactory().setValue(teacher.getMinDayHours());
        maxDayHoursField.getValueFactory().setValue(teacher.getMaxDayHours());

    }

    public Teacher getData() {
        teacher.setNumber(Integer.parseInt(numberField.getText()));
        teacher.setName(nameField.getText());
        teacher.setAbreviation(abreviationField.getText());
        teacher.setMinDayHours(minDayHoursField.getValue());
        teacher.setMaxDayHours(maxDayHoursField.getValue());
        teacher.setDepartment(departmentField.getText());
        return teacher;
    }

    public boolean validateInputs() {

        if(numberField.getText().isEmpty())
            return false;
        if(nameField.getText().isEmpty())
            return false;
        if(abreviationField.getText().isEmpty())
            return false;
        if(minDayHoursField.getValue() == null)
            return false;
        if(maxDayHoursField.getValue() == null)
            return false;
        if(departmentField.getText().isEmpty())
            return false;

        return true;

    }

    @FXML
    void numberInputKeyTyped(KeyEvent event) {

        if(!Utils.isInteger(event.getCharacter()))
            event.consume();

    }

}
