package morales.david.desktop.controllers.modals;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import morales.david.desktop.models.Schedule;
import morales.david.desktop.models.SchedulerItem;
import morales.david.desktop.models.TimeZone;

import java.net.URL;
import java.util.ResourceBundle;

public class SchedulerItemController implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Button deleteButton;

    @FXML
    private Label teacherLabel;

    @FXML
    private Label subjectLabel;

    @FXML
    private Label classroomlabel;

    @FXML
    private Label timeZoneLabel;

    @FXML
    private Label groupLabel;

    private SchedulerItem schedulerItem;
    private TimeZone timeZone;
    private Schedule schedule;

    @Override
    public void initialize(URL location, ResourceBundle resources) { }

    public void setData(SchedulerItem schedulerItem, TimeZone timeZone, Schedule schedule) {

        this.schedulerItem = schedulerItem;
        this.timeZone = timeZone;
        this.schedule = schedule;

        if(schedule.getTeacher() != null)
            teacherLabel.setText(schedule.getTeacher().getName());

        if(schedule.getSubject() != null) {
            subjectLabel.setText(schedule.getSubject().getName());
            anchorPane.setStyle("-fx-background-color: " + schedule.getSubject().getColor() + ";");
        }

        if(schedule.getClassroom() != null)
            classroomlabel.setText(schedule.getClassroom().getName());

        if(schedule.getTimeZone() != null)
            timeZoneLabel.setText(schedule.getTimeZone().toString());

        if(schedule.getGroup() != null)
            groupLabel.setText(schedule.getGroup().toString());

    }

}
