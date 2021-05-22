package morales.david.desktop.controllers.schedules;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import morales.david.desktop.controllers.schedules.scheduler.SchedulerGUI;
import morales.david.desktop.interfaces.Controller;
import morales.david.desktop.managers.ScreenManager;
import morales.david.desktop.models.Schedule;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SchedulesController implements Initializable, Controller {

    @FXML
    private AnchorPane anchorPane;

    private SchedulerGUI schedulerGui;

    private List<Schedule> schedules;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        schedulerGui = new SchedulerGUI(anchorPane);

        Platform.runLater(() -> {
            schedulerGui.init();
            schedulerGui.displayCurrentTimetable();
        });

    }

    public void resized() {
        schedulerGui.resize();
    }

    public List<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<Schedule> schedules) {
        this.schedules = schedules;
    }

}
