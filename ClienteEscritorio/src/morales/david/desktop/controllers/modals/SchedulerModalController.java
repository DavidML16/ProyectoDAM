package morales.david.desktop.controllers.modals;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;
import morales.david.desktop.managers.DataManager;
import morales.david.desktop.models.*;
import morales.david.desktop.utils.FxUtilTest;
import morales.david.desktop.utils.Utils;
import org.controlsfx.control.ListSelectionView;

import java.net.URL;
import java.util.ResourceBundle;

public class SchedulerModalController implements Initializable {

    @FXML
    private ComboBox<Teacher> teacherField;

    @FXML
    private ComboBox<Subject> subjectField;

    @FXML
    private ComboBox<Classroom> classroomField;

    @FXML
    private ComboBox<TimeZone> timezoneField;

    @FXML
    private ListSelectionView<Group> courseField;

    private SchedulerItem schedulerItem;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        teacherField.setConverter(new StringConverter<Teacher>() {
            @Override
            public String toString(Teacher object) {
                return object != null ? object.getName() : "";
            }

            @Override
            public Teacher fromString(String string) {
                return teacherField.getItems().stream().filter(object ->
                        object.getName().equals(string)).findFirst().orElse(null);
            }
        });
        FxUtilTest.autoCompleteComboBoxPlus(teacherField, (typedText, itemToCompare) -> itemToCompare.getName().toLowerCase().contains(typedText.toLowerCase()));

        subjectField.setConverter(new StringConverter<Subject>() {
            @Override
            public String toString(Subject object) {
                return object != null ? object.getAbreviation() + ", " + object.getName() : "";
            }

            @Override
            public Subject fromString(String string) {
                return subjectField.getItems().stream().filter(object ->
                        object.getName().equals(string) || object.getAbreviation().equals(string)).findFirst().orElse(null);
            }
        });
        FxUtilTest.autoCompleteComboBoxPlus(subjectField, (typedText, itemToCompare) -> itemToCompare.getName().toLowerCase().contains(typedText.toLowerCase()) ||
                itemToCompare.getAbreviation().toLowerCase().contains(typedText.toLowerCase()));

    }

    public void setData(SchedulerItem schedulerItem, boolean edit) {

        this.schedulerItem = schedulerItem;

        if(!edit) return;

    }

    public SchedulerItem getData() {
        return schedulerItem;
    }

    public boolean validateInputs() {

        return true;

    }

    @FXML
    void numberInputKeyTyped(KeyEvent event) {

        if(!Utils.isInteger(event.getCharacter()))
            event.consume();

    }

}
