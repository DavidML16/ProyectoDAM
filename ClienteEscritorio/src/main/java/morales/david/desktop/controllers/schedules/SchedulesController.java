package morales.david.desktop.controllers.schedules;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import morales.david.desktop.controllers.schedules.scheduler.SchedulerGUI;
import morales.david.desktop.controllers.schedules.scheduler.SchedulerManager;
import morales.david.desktop.interfaces.Controller;
import morales.david.desktop.models.Schedule;
import morales.david.desktop.models.SchedulerItem;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SchedulesController implements Initializable, Controller {

    @FXML
    private AnchorPane anchorPane;

    private SchedulerGUI schedulerGui;

    private List<SchedulerItem> schedules;

    @Override
    public void initialize(URL location, ResourceBundle resources) { }

    public void resized() {
        schedulerGui.resize();
    }

    public List<SchedulerItem> getSchedules() {
        return schedules;
    }

    public void init(List<SchedulerItem> schedules, String searchType, String searchQuery) {

        this.schedules = schedules;

        SchedulerManager schedulerManager = new SchedulerManager(schedules, searchType, searchQuery);

        schedulerGui = new SchedulerGUI(anchorPane, schedulerManager);

        schedulerGui.init();
        schedulerGui.displayCurrentTimetable();

        anchorPane.widthProperty().addListener(event -> {
            resized();
        });
        anchorPane.heightProperty().addListener(event -> {
            resized();
        });

        Platform.runLater(() -> resized());

    }

}
