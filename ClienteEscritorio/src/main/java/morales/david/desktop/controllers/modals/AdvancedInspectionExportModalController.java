package morales.david.desktop.controllers.modals;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import morales.david.desktop.managers.AdvInspectionManager;
import morales.david.desktop.managers.AdvSchedulerManager;
import morales.david.desktop.managers.DataManager;
import morales.david.desktop.models.*;
import org.controlsfx.control.CheckListView;
import org.controlsfx.control.ListSelectionView;

import java.io.File;
import java.net.URL;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AdvancedInspectionExportModalController implements Initializable {

    @FXML
    private CheckListView<Hour> mondayHours;
    @FXML
    private Button mondaySelectAll;

    @FXML
    private CheckListView<Hour> tuesdayHours;
    @FXML
    private Button tuesdaySelectAll;

    @FXML
    private CheckListView<Hour> wednesdayHours;
    @FXML
    private Button wednesdaySelectAll;

    @FXML
    private CheckListView<Hour> thursdayHours;
    @FXML
    private Button thursdaySelectAll;

    @FXML
    private CheckListView<Hour> fridayHours;
    @FXML
    private Button fridaySelectAll;

    @FXML
    private Label directoryChooser;

    private ObservableList<File> directories;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        directories = FXCollections.observableList(new ArrayList<>());

        mondayHours.setItems(FXCollections.observableList(DataManager.getInstance().getHours()));
        tuesdayHours.setItems(FXCollections.observableList(DataManager.getInstance().getHours()));
        wednesdayHours.setItems(FXCollections.observableList(DataManager.getInstance().getHours()));
        thursdayHours.setItems(FXCollections.observableList(DataManager.getInstance().getHours()));
        fridayHours.setItems(FXCollections.observableList(DataManager.getInstance().getHours()));

        mondaySelectAll.setOnMouseClicked((event) -> mondayHours.getCheckModel().checkAll());
        tuesdaySelectAll.setOnMouseClicked((event) -> tuesdayHours.getCheckModel().checkAll());
        wednesdaySelectAll.setOnMouseClicked((event) -> wednesdayHours.getCheckModel().checkAll());
        thursdaySelectAll.setOnMouseClicked((event) -> thursdayHours.getCheckModel().checkAll());
        fridaySelectAll.setOnMouseClicked((event) -> fridayHours.getCheckModel().checkAll());

        Platform.runLater(() -> {

            directories.addListener((ListChangeListener) change -> {

                if(change.next() && change.getAddedSize() > 0) {
                    directoryChooser.setText(directories.get(0).getAbsolutePath());
                }

            });

            DirectoryChooser chooser = new DirectoryChooser();

            if(AdvInspectionManager.getInstance().getExportDirectory() != null)
                chooser.setInitialDirectory(AdvInspectionManager.getInstance().getExportDirectory());

            chooser.setTitle("Selecciona el directorio de salida");

            directoryChooser.setOnMouseClicked(event -> {

                File directory = chooser.showDialog(directoryChooser.getParent().getScene().getWindow());

                if(directory != null && directory.isDirectory()) {

                    directories.add(0, directory);
                    directoryChooser.setText(directory.getAbsolutePath());

                    if(directories.size() > 0 && directories.get(0).isDirectory())
                        AdvInspectionManager.getInstance().setExportDirectory(directories.get(0));

                }

            });

        });

    }

    public void setExportQuerys() {

        AdvInspectionManager manager = AdvInspectionManager.getInstance();

        if(directories.size() > 0 && directories.get(0).isDirectory())
            manager.setExportDirectory(directories.get(0));

        List<TimeZone> timeZones = new ArrayList<>();

        for(Day day : DataManager.getInstance().getDays()) {

            if(day.getId() == 1)
                getDayTimeZones(timeZones, day, mondayHours.getCheckModel().getCheckedItems());
            else if(day.getId() == 2)
                getDayTimeZones(timeZones, day, tuesdayHours.getCheckModel().getCheckedItems());
            else if(day.getId() == 3)
                getDayTimeZones(timeZones, day, wednesdayHours.getCheckModel().getCheckedItems());
            else if(day.getId() == 4)
                getDayTimeZones(timeZones, day, thursdayHours.getCheckModel().getCheckedItems());
            else if(day.getId() == 5)
                getDayTimeZones(timeZones, day, fridayHours.getCheckModel().getCheckedItems());

        }

        manager.setTimeZones(timeZones);

    }

    private void getDayTimeZones(List<TimeZone> timeZones, Day day, List<Hour> hours) {

        for(Hour hour : hours)
            timeZones.add(getTimeZoneBy(day, hour));

    }

    public TimeZone getTimeZoneBy(Day day, Hour hour) {

        for(TimeZone timeZone : new ArrayList<>(DataManager.getInstance().getTimeZones()))
            if(timeZone.getDay().getId() == day.getId() && timeZone.getHour().getId() == hour.getId())
                return timeZone;

        return null;

    }


    public boolean validateInputs() {

        if(mondayHours.getCheckModel().getCheckedItems().size() == 0
                && tuesdayHours.getCheckModel().getCheckedItems().size() == 0
                && wednesdayHours.getCheckModel().getCheckedItems().size() == 0
                && thursdayHours.getCheckModel().getCheckedItems().size() == 0
                && fridayHours.getCheckModel().getCheckedItems().size() == 0)
            return false;

        if(directories.size() == 0)
            return false;

        return true;

    }

}
