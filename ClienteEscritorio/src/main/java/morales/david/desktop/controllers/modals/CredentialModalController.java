package morales.david.desktop.controllers.modals;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;
import morales.david.desktop.managers.DataManager;
import morales.david.desktop.models.Credential;
import morales.david.desktop.models.Teacher;
import morales.david.desktop.utils.FxUtilTest;
import morales.david.desktop.utils.HashUtil;
import morales.david.desktop.utils.Utils;

import java.net.URL;
import java.util.ResourceBundle;

public class CredentialModalController implements Initializable {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    private ComboBox<String> roleField;

    @FXML
    private ComboBox<Teacher> teacherField;

    @FXML
    private CheckBox passwordCheckbox;

    @FXML
    private Button removeTeacherButton;

    private Credential credential;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        roleField.setItems(FXCollections.observableArrayList("Profesor", "Directivo"));

        teacherField.setItems(DataManager.getInstance().getTeachers());

        if(passwordCheckbox != null) {
            passwordField.setDisable(true);
            passwordCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> passwordField.setDisable(!newValue));
        }

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

        removeTeacherButton.setOnMouseClicked(event -> {
           teacherField.getSelectionModel().clearSelection();
        });

    }

    public void setData(Credential credential, boolean edit) {

        this.credential = credential;

        if(!edit) return;

        usernameField.setText(credential.getUsername());
        roleField.getSelectionModel().select(Utils.capitalizeFirstLetter(credential.getRole()));

        if(credential.getTeacher() != null) {

            Teacher teacher = credential.getTeacher();

            for (Teacher cachedTeacher : DataManager.getInstance().getTeachers()) {
                if (cachedTeacher.getId() == teacher.getId()) {
                    teacherField.getSelectionModel().select(cachedTeacher);
                    break;
                }
            }

        }

    }

    public Credential getData() {
        credential.setUsername(usernameField.getText());
        if(passwordCheckbox == null)
            credential.setPassword(HashUtil.sha1(passwordField.getText()));
        else
            if(passwordCheckbox.isSelected())
                credential.setPassword(HashUtil.sha1(passwordField.getText()));
        credential.setRole(roleField.getSelectionModel().getSelectedItem().toLowerCase());
        if(teacherField.getSelectionModel().getSelectedItem() != null) {
            credential.setTeacher(teacherField.getSelectionModel().getSelectedItem());
        } else
            credential.setTeacher(null);
        return credential;
    }

    public boolean validateInputs() {

        if(usernameField.getText().isEmpty())
            return false;
        if(passwordCheckbox == null) {
            if (passwordField.getText().isEmpty()) {
                return false;
            }
        } else {
            if (passwordCheckbox.isSelected()) {
                if (passwordField.getText().isEmpty()) {
                    return false;
                }
            }
        }
        if(roleField.getSelectionModel().isEmpty())
            return false;

        return true;

    }

    @FXML
    void numberInputKeyTyped(KeyEvent event) {

        if(!Utils.isInteger(event.getCharacter()))
            event.consume();

    }

}
