package morales.david.desktop.controllers.schedules;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import morales.david.desktop.controllers.schedules.scheduler.SchedulerGUI;
import morales.david.desktop.interfaces.Controller;
import morales.david.desktop.managers.ScreenManager;

import java.net.URL;
import java.util.ResourceBundle;

public class SchedulesController implements Initializable, Controller {

    @FXML
    private AnchorPane anchorPane;

    private SchedulerGUI schedulerGui;

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

}
