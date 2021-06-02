package morales.david.desktop.controllers.schedules.scheduler;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import morales.david.desktop.models.SchedulerItem;
import morales.david.desktop.utils.ColorUtil;

public class SchedulerItemPane extends JFXButton {

    private SchedulerItem schedulerItem;

    public SchedulerItemPane(SchedulerItem schedulerItem) {
        this.schedulerItem = schedulerItem;
        regenerate();
    }

    public SchedulerItem getSchedulerItem() {
        return schedulerItem;
    }

    public void setSchedulerItem(SchedulerItem schedulerItem) {
        this.schedulerItem = schedulerItem;
        regenerate();
    }

    public void regenerate() {

        getStyleClass().clear();
        getStyleClass().add("scheduleButton");

        setTextAlignment(TextAlignment.CENTER);
        setAlignment(Pos.CENTER);
        setMinSize(100, 40);
        setPrefSize(500, 500);

        setStyle("-fx-background-color: #FFFFFF;");

        if(schedulerItem != null && schedulerItem.getScheduleList().size() > 0) {
            String color = schedulerItem.getScheduleList().get(0).getSubject().getColor();
            setStyle("-fx-background-color: " + color + ";");
        }

    }

    public void setContentText(SchedulerManager schedulerManager, int day, int hour) {
        if(schedulerItem != null && schedulerItem.getScheduleList().size() > 0) {
            setText(schedulerManager.getCurrentTable().getScheduleItemText(day, hour));
        } else {
            setText("");
        }
    }

}
