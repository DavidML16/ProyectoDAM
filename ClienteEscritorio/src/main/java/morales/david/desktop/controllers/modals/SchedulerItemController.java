package morales.david.desktop.controllers.modals;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import morales.david.desktop.controllers.schedules.scheduler.SchedulerManager;
import morales.david.desktop.netty.ClientManager;
import morales.david.desktop.models.*;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SchedulerItemController implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Button deleteButton;

    @FXML
    private Button editButton;

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

    private SchedulerItemModalController controller;

    private SchedulerManager schedulerManager;
    private SchedulerItem schedulerItem;
    private TimeZone timeZone;
    private Schedule schedule;

    private List<Classroom> emptyClassrooms;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(ClientManager.getInstance().getClientSession().isTeacherRole()) {
            deleteButton.setVisible(false);
            editButton.setVisible(false);
        }
    }

    public void setData(SchedulerItemModalController controller, SchedulerManager schedulerManager,
                        SchedulerItem schedulerItem, TimeZone timeZone, Schedule schedule, List<Classroom> emptyClassrooms) {

        this.controller = controller;
        this.schedulerManager = schedulerManager;
        this.schedulerItem = schedulerItem;
        this.timeZone = timeZone;
        this.schedule = schedule;
        this.emptyClassrooms = emptyClassrooms;

        if(schedule.getTeacher() != null)
            teacherLabel.setText(schedule.getTeacher().getName() + ", " + schedule.getTeacher().getAbreviation());

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

    @FXML
    public void handleButtonAction(MouseEvent event) {

        if(event.getSource() == deleteButton) {

            schedulerManager.getCurrentTable().deleteSchedule(schedulerItem, schedule);

        } else if(event.getSource() == editButton) {

            controller.editSchedule(schedule, emptyClassrooms);

        }

    }

}
