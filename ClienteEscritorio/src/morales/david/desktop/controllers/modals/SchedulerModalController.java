package morales.david.desktop.controllers.modals;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
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
import java.util.List;
import java.util.ResourceBundle;

public class SchedulerModalController implements Initializable {

    @FXML
    private Label titleLabel;

    @FXML
    private ComboBox<Teacher> teacherField;

    @FXML
    private ComboBox<Subject> subjectField;

    @FXML
    private ComboBox<Classroom> classroomField;

    @FXML
    private ComboBox<Group> groupField;

    @FXML
    private CheckBox showAllCheckBox;

    private Schedule schedule;

    private List<Classroom> emptyClassrooms;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        teacherField.setItems(DataManager.getInstance().getTeachers());
        teacherField.setConverter(new StringConverter<Teacher>() {
            @Override
            public String toString(Teacher object) {
                return object != null ? object.getName() : "";
            }

            @Override
            public Teacher fromString(String string) {
                return teacherField.getItems().stream().filter(object -> object.getName().equals(string)).findFirst().orElse(null);
            }
        });
        FxUtilTest.autoCompleteComboBoxPlus(teacherField, (typedText, itemToCompare) ->
                itemToCompare.getName().toLowerCase().contains(typedText.toLowerCase())
                || itemToCompare.getAbreviation().toLowerCase().contains(typedText.toLowerCase())
                || itemToCompare.getDepartment().toLowerCase().contains(typedText.toLowerCase())
                || Integer.toString(itemToCompare.getNumber()).contains(typedText.toLowerCase())
        );

        subjectField.setItems(DataManager.getInstance().getSubjects());
        subjectField.setConverter(new StringConverter<Subject>() {
            @Override
            public String toString(Subject object) {
                return object != null ? object.getName() : "";
            }

            @Override
            public Subject fromString(String string) {
                return subjectField.getItems().stream().filter(object -> object.getName().equals(string)).findFirst().orElse(null);
            }
        });
        FxUtilTest.autoCompleteComboBoxPlus(subjectField, (typedText, itemToCompare) ->
                itemToCompare.getName().toLowerCase().contains(typedText.toLowerCase())
                || itemToCompare.getAbreviation().toLowerCase().contains(typedText.toLowerCase())
                || Integer.toString(itemToCompare.getNumber()).toLowerCase().contains(typedText.toLowerCase())
        );

        classroomField.setItems(FXCollections.emptyObservableList());
        classroomField.setConverter(new StringConverter<Classroom>() {
            @Override
            public String toString(Classroom object) {
                return object != null ? object.getName() : "";
            }

            @Override
            public Classroom fromString(String string) {
                return classroomField.getItems().stream().filter(object -> object.getName().equals(string)).findFirst().orElse(null);
            }
        });
        FxUtilTest.autoCompleteComboBoxPlus(classroomField, (typedText, itemToCompare) -> itemToCompare.getName().toLowerCase().contains(typedText.toLowerCase()));

        showAllCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue)
                if(emptyClassrooms != null)
                    classroomField.setItems(FXCollections.observableArrayList(emptyClassrooms));
                else
                    classroomField.setItems(FXCollections.emptyObservableList());
            else
                classroomField.setItems(DataManager.getInstance().getClassrooms());
        });

        groupField.setItems(DataManager.getInstance().getGroups());
        groupField.setConverter(new StringConverter<Group>() {
            @Override
            public String toString(Group object) {
                return object != null ? object.toString() : "";
            }

            @Override
            public Group fromString(String string) {
                return groupField.getItems().stream().filter(object -> object.toString().equals(string)).findFirst().orElse(null);
            }
        });
        FxUtilTest.autoCompleteComboBoxPlus(groupField, (typedText, itemToCompare) -> itemToCompare.toString().toLowerCase().contains(typedText.toLowerCase()));

    }

    public void setData(Schedule schedule, boolean edit, List<Classroom> emptyClassrooms) {

        this.schedule = schedule;
        this.emptyClassrooms = emptyClassrooms;

        if(emptyClassrooms != null)
            classroomField.setItems(FXCollections.observableArrayList(emptyClassrooms));

        titleLabel.setText("Añadir nuevo turno");

        if(!edit) return;

        titleLabel.setText("Editar turno");

        Teacher teacher = schedule.getTeacher();
        for(Teacher cachedTeacher : DataManager.getInstance().getTeachers()) {
            if(cachedTeacher.getId() == teacher.getId()) {
                teacherField.getSelectionModel().select(cachedTeacher);
                break;
            }
        }

        Subject subject = schedule.getSubject();
        for(Subject cachedSubject : DataManager.getInstance().getSubjects()) {
            if(cachedSubject.getId() == subject.getId()) {
                subjectField.getSelectionModel().select(cachedSubject);
                break;
            }
        }

        Classroom classroom = schedule.getClassroom();
        for(Classroom cachedClassroom : DataManager.getInstance().getClassrooms()) {
            if(cachedClassroom.getId() == classroom.getId()) {
                classroomField.getSelectionModel().select(cachedClassroom);
                break;
            }
        }

        Group group = schedule.getGroup();
        for(Group cachedGroup : DataManager.getInstance().getGroups()) {
            if(cachedGroup.getId() == group.getId()) {
                groupField.getSelectionModel().select(cachedGroup);
                break;
            }
        }

    }

    public Schedule getData() {
        schedule.setTeacher(teacherField.getSelectionModel().getSelectedItem());
        schedule.setSubject(subjectField.getSelectionModel().getSelectedItem());
        schedule.setClassroom(classroomField.getSelectionModel().getSelectedItem());
        schedule.setGroup(groupField.getSelectionModel().getSelectedItem());
        return schedule;
    }

    public boolean validateInputs() {

        if(teacherField.getSelectionModel().isEmpty())
            return false;

        if(subjectField.getSelectionModel().isEmpty())
            return false;

        if(classroomField.getSelectionModel().isEmpty())
            return false;

        if(groupField.getSelectionModel().isEmpty())
            return false;

        return true;

    }

}
