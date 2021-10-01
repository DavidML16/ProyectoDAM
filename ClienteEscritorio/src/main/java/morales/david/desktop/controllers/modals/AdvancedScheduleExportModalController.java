package morales.david.desktop.controllers.modals;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import morales.david.desktop.managers.*;
import morales.david.desktop.models.*;
import morales.david.desktop.models.packets.Packet;
import morales.david.desktop.models.packets.PacketBuilder;
import morales.david.desktop.models.packets.PacketType;
import org.controlsfx.control.ListSelectionView;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AdvancedScheduleExportModalController implements Initializable {

    @FXML
    private ListSelectionView<Teacher> teachersField;

    @FXML
    private ListSelectionView<Group> groupsField;

    @FXML
    private ListSelectionView<Classroom> classroomsField;

    @FXML
    private Label directoryChooser;

    private ObservableList<File> directories;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        directories = FXCollections.observableList(new ArrayList<>());

        if(teachersField != null) {
            teachersField.getSourceItems().addAll(DataManager.getInstance().getTeachers());
            teachersField.setSourceHeader(new Label("Disponible:"));
            teachersField.setTargetHeader(new Label("Seleccionado:"));
        }

        if(groupsField != null) {
            groupsField.getSourceItems().addAll(DataManager.getInstance().getGroups());
            groupsField.setSourceHeader(new Label("Disponible:"));
            groupsField.setTargetHeader(new Label("Seleccionado:"));
        }

        if(classroomsField != null) {
            classroomsField.getSourceItems().addAll(DataManager.getInstance().getClassrooms());
            classroomsField.setSourceHeader(new Label("Disponible:"));
            classroomsField.setTargetHeader(new Label("Seleccionado:"));
        }

        Platform.runLater(() -> {

            directories.addListener((ListChangeListener) change -> {

                if(change.next() && change.getAddedSize() > 0) {
                    directoryChooser.setText(directories.get(0).getAbsolutePath());
                }

            });

            DirectoryChooser chooser = new DirectoryChooser();

            if(AdvSchedulerManager.getInstance().getExportDirectory() != null)
                chooser.setInitialDirectory(AdvSchedulerManager.getInstance().getExportDirectory());

            chooser.setTitle("Selecciona el directorio de salida");

            directoryChooser.setOnMouseClicked(event -> {

                File directory = chooser.showDialog(teachersField.getParent().getScene().getWindow());

                if(directory != null && directory.isDirectory()) {

                    directories.add(0, directory);
                    directoryChooser.setText(directory.getAbsolutePath());

                    if(directories.size() > 0 && directories.get(0).isDirectory())
                        AdvSchedulerManager.getInstance().setExportDirectory(directories.get(0));

                }

            });

        });

    }

    public void setExportQuerys() {

        AdvSchedulerManager manager = AdvSchedulerManager.getInstance();

        if(directories.size() > 0 && directories.get(0).isDirectory())
            manager.setExportDirectory(directories.get(0));

        manager.setTeachers(teachersField.getTargetItems());
        manager.setGroups(groupsField.getTargetItems());
        manager.setClassrooms(classroomsField.getTargetItems());

    }

    public boolean validateInputs() {

        if(teachersField.getTargetItems().size() == 0
                && groupsField.getTargetItems().size() == 0
                && classroomsField.getTargetItems().size() == 0)
            return false;

        if(directories.size() == 0)
            return false;

        return true;

    }

}
