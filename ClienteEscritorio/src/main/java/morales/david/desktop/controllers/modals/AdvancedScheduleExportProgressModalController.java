package morales.david.desktop.controllers.modals;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import morales.david.desktop.managers.AdvSchedulerManager;
import morales.david.desktop.managers.DataManager;
import morales.david.desktop.models.Classroom;
import morales.david.desktop.models.Group;
import morales.david.desktop.models.Teacher;
import org.controlsfx.control.ListSelectionView;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AdvancedScheduleExportProgressModalController implements Initializable {

    @FXML
    private Label titleLabel;

    @FXML
    private Label exportProgressLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        exportProgressLabel.setText("Por favor espere...");

    }

    public void setText(String text) {

        exportProgressLabel.setText(text);

    }

}
