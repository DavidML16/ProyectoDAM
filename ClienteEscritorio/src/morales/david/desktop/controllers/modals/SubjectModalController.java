package morales.david.desktop.controllers.modals;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import morales.david.desktop.managers.DataManager;
import morales.david.desktop.models.Course;
import morales.david.desktop.models.Subject;
import morales.david.desktop.utils.Utils;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.ListSelectionView;

import java.net.URL;
import java.util.ResourceBundle;

public class SubjectModalController implements Initializable {

    @FXML
    private Spinner<Integer> numberField;

    @FXML
    private TextField abreviationField;

    @FXML
    private TextField nameField;

    @FXML
    private ListSelectionView<Course> courseField;

    private Subject subject;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        numberField.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 999999));

        courseField.getSourceItems().addAll(DataManager.getInstance().getCourses());
        courseField.setSourceHeader(new Label("Disponible:"));
        courseField.setTargetHeader(new Label("Seleccionado:"));

    }

    public void setData(Subject subject, boolean edit) {

        this.subject = subject;

        if(!edit) return;

        numberField.getValueFactory().setValue(subject.getNumber());
        abreviationField.setText(subject.getAbreviation());
        nameField.setText(subject.getName());

        for(Course course : subject.getCourses())
            for(Course cachedCourse : DataManager.getInstance().getCourses())
                if(cachedCourse.getId() == course.getId())
                    courseField.getTargetItems().add(cachedCourse);

    }

    public Subject getData() {
        subject.setNumber(numberField.getValue());
        subject.setAbreviation(abreviationField.getText());
        subject.setName(nameField.getText());
        return subject;
    }

    public boolean validateInputs() {

        if(numberField.getValue() == null)
            return false;
        if(abreviationField.getText().isEmpty())
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
