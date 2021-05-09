package morales.david.desktop.controllers.schedules.scheduler;

import com.jfoenix.controls.JFXButton;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import javafx.util.Duration;
import morales.david.desktop.managers.DataManager;
import morales.david.desktop.models.Day;
import morales.david.desktop.models.Hour;

import java.util.List;

public class SchedulerGUI {

    private static final int ANIMATION_DURATION = 200;
    private final int ANIMATION_DISTANCE = 50;
    private static final double FOCUS_ANIMATION_OFFSET_FACTOR = 0.6;
    private static final double GAP_SIZE = 10;
    private static final double FONT_FACTOR = 0.22;

    private static final int DAY_LENGTH = 5;
    private static final int HOURS_LENGTH = 6;

    private AnchorPane background;
    private GridPane subjectGrid;

    private JFXButton[] days;
    private JFXButton[] hours;
    private JFXButton[][] schedules;

    public SchedulerGUI(AnchorPane anchorPane) {

        this.background = anchorPane;

    }

    public void init() {

        subjectGrid = new GridPane();
        subjectGrid.setVgap(GAP_SIZE);
        subjectGrid.setHgap(GAP_SIZE);
        if(background.getChildren().size() > 1)
            background.getChildren().remove(0);
        background.getChildren().add(subjectGrid);
        background.setTopAnchor(subjectGrid, GAP_SIZE);
        background.setRightAnchor(subjectGrid, GAP_SIZE);
        background.setBottomAnchor(subjectGrid, GAP_SIZE);
        background.setLeftAnchor(subjectGrid, GAP_SIZE);

        days = new JFXButton[DAY_LENGTH];
        List<Day> dayList = DataManager.getInstance().getDays();
        for (int i = 0; i < days.length; i++) {
            if(dayList.get(i) != null) {
                JFXButton day = new JFXButton(dayList.get(i).getName());
                day.getStyleClass().add("dayButton");
                day.setMinSize(100, 40);
                day.setPrefSize(500, 500);
                days[i] = day;
            }
        }

        hours = new JFXButton[HOURS_LENGTH];
        for (int i = 0; i < hours.length; i++) {
            JFXButton hour = new JFXButton();
            hour.getStyleClass().add("hourButton");
            hour.setMinSize(100, 40);
            hour.setPrefSize(500, 500);
            hours[i] = hour;
        }

        schedules = new JFXButton[DAY_LENGTH][HOURS_LENGTH];
        for (int i = 0; i < schedules.length; i++) {
            for (int j = 0; j < schedules[0].length; j++) {
                JFXButton schedule = new JFXButton();
                schedule.getStyleClass().add("scheduleButton");
                schedule.setMinSize(100, 40);
                schedule.setPrefSize(500, 500);
                schedules[i][j] = schedule;
            }
        }

        displayCurrentTimetable();

    }

    public void resize() {

        new Timeline(
                new KeyFrame(Duration.millis(5), event -> resizeFonts())
        ).play();

    }

    public void resizeFonts() {

        double w = hours[0].getWidth();
        double h = hours[0].getHeight();

        Font font1 = new Font((h + w) * 0.09);
        Font font2 = new Font(h * 0.3);
        Font font3 = new Font(h * 0.25);

        for (JFXButton b : days)
            b.setFont(font1);

        for (JFXButton b : hours)
            b.setFont(font2);

        for (JFXButton[] ba : schedules)
            for (JFXButton b : ba)
                b.setFont(font3);

    }

    public void displayCurrentTimetable() {

        subjectGrid.getChildren().removeIf(node -> (node.getClass() == JFXButton.class));
        subjectGrid.getRowConstraints().clear();
        RowConstraints rc = new RowConstraints();
        rc.setPercentHeight(5);
        subjectGrid.getRowConstraints().add(rc);

        int pos = 0;
        for (int i = 0; i < days.length; i++) {
            subjectGrid.add(days[i], pos + 1, 1, 1, 1);
            pos++;
        }

        List<Hour> hourList = DataManager.getInstance().getHours();
        for (int i = 0; i < hours.length; i++) {
            subjectGrid.add(hours[i], 0, i + 2, 1, 1);
            if(hourList.get(i) != null)
                hours[i].setText(hourList.get(i).getName());
        }

        pos = 0;
        for (int i = 0; i < schedules.length; i++) {
            for (int j = 0; j < schedules[0].length; j++) {
                int height = 1;
                subjectGrid.add(schedules[i][j], pos + 1, j + 2, 1, 1);
                schedules[i][j].setText(i + " " + j);
                j += height - 1;
            }
            pos++;
        }

        resize();

    }

}
