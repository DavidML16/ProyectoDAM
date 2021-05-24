package morales.david.desktop.controllers.schedules.scheduler;

import com.jfoenix.controls.JFXButton;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import morales.david.desktop.models.Day;
import morales.david.desktop.models.Hour;
import morales.david.desktop.models.Schedule;

public class SchedulerGUI {

    private static final int ANIMATION_DURATION = 200;
    private final int ANIMATION_DISTANCE = 50;
    private static final double FOCUS_ANIMATION_OFFSET_FACTOR = 0.6;
    private static final double GAP_SIZE = 5;
    private static final double FONT_FACTOR = 0.22;

    private static final int DAY_LENGTH = 5;
    private static final int HOURS_LENGTH = 7;

    private AnchorPane background;
    private GridPane subjectGrid;

    private TimetableManager timetableManager;

    private EventHandler<ActionEvent> scheduleActionEvent;
    private EventHandler<MouseEvent> schedulePressedEvent;
    private EventHandler<MouseEvent> scheduleDraggedEvent;
    private EventHandler<MouseEvent>scheduleReleasedEvent;
    private EventHandler<KeyEvent> scheduleKeyReleasedEvent;

    private HBox tabBox;
    private Label tabMorning;
    private Label tabAfternoon;

    private JFXButton[] days;
    private JFXButton[] hours;
    private JFXButton[][] schedules;

    public SchedulerGUI(AnchorPane anchorPane, TimetableManager timetableManager) {
        this.background = anchorPane;
        this.timetableManager = timetableManager;
    }

    public void init() {

        subjectGrid = new GridPane();
        subjectGrid.setVgap(GAP_SIZE);
        subjectGrid.setHgap(GAP_SIZE);
        if(background.getChildren().size() > 1)
            background.getChildren().remove(0);
        background.setPrefSize(900, 600);
        background.getChildren().add(subjectGrid);
        background.getStyleClass().clear();
        background.getStyleClass().add("mainPane");
        background.setTopAnchor(subjectGrid, GAP_SIZE * 4);
        background.setRightAnchor(subjectGrid, GAP_SIZE * 4);
        background.setBottomAnchor(subjectGrid, GAP_SIZE * 4);
        background.setLeftAnchor(subjectGrid, GAP_SIZE * 4);

        initControlArrays();

        days = new JFXButton[DAY_LENGTH];
        for (int i = 0; i < days.length; i++) {
            JFXButton day = new JFXButton();
            day.getStyleClass().add("dayButton");
            day.setMinSize(100, 40);
            day.setPrefSize(500, 500);
            days[i] = day;
        }

        hours = new JFXButton[HOURS_LENGTH];
        for (int i = 0; i < hours.length; i++) {
            JFXButton hour = new JFXButton();
            hour.getStyleClass().add("hourButton");
            hour.setMinSize(100, 40);
            hour.setPrefSize(500, 500);
            hours[i] = hour;
        }

        selectMorningTurn();

        resize();

    }

    public void initControlArrays() {

        tabBox = new HBox();
        tabBox.getStyleClass().add("mainPane");
        tabBox.setSpacing(GAP_SIZE);

        tabMorning = new Label("TURNO DE MAÑANA");
        tabMorning.setAlignment(Pos.CENTER);
        tabMorning.setMinSize(0, 0);
        tabMorning.setPrefSize(1000, 110);
        tabMorning.getStyleClass().add("schedulerTabButton");
        tabMorning.setOnMousePressed(event -> {
            selectMorningTurn();
        });

        tabAfternoon = new Label("TURNO DE TARDE");
        tabAfternoon.setAlignment(Pos.CENTER);
        tabAfternoon.setMinSize(0, 0);
        tabAfternoon.setPrefSize(1000, 110);
        tabAfternoon.getStyleClass().add("schedulerTabButton");
        tabAfternoon.setOnMousePressed(event -> {
            selectAfternoonTurn();
        });

        tabBox.getChildren().addAll(tabMorning, tabAfternoon);

        scheduleActionEvent = (ActionEvent event) -> {
            scheduleMenu(event);
        };
        schedulePressedEvent = (MouseEvent event) -> {
            schedulePressed(event);
        };
        scheduleDraggedEvent = (MouseEvent event) -> {
            scheduleDragged(event);
        };
        scheduleReleasedEvent = (MouseEvent event) -> {
            scheduleReleased(event);
        };
        scheduleKeyReleasedEvent = (KeyEvent event) -> {
            scheduleKeyReleased(event);
        };

        schedules = new JFXButton[DAY_LENGTH][HOURS_LENGTH - 1];
        for (int i = 0; i < schedules.length; i++) {
            for (int j = 0; j < schedules[0].length; j++) {
                JFXButton schedule = new JFXButton();
                schedule.getStyleClass().add("scheduleButton");
                schedule.setTextAlignment(TextAlignment.CENTER);
                schedule.setMinSize(100, 40);
                schedule.setPrefSize(500, 500);
                schedule.setOnAction(scheduleActionEvent);
                schedule.setOnMousePressed(schedulePressedEvent);
                schedule.setOnMouseDragged(scheduleDraggedEvent);
                schedule.setOnMouseReleased(scheduleReleasedEvent);
                schedule.setOnKeyReleased(scheduleKeyReleasedEvent);
                schedules[i][j] = schedule;
            }
        }

    }

    private void selectMorningTurn() {
        timetableManager.setMorning(true);
        tabMorning.getStyleClass().removeIf(s -> (s == "selectedSchedulerTabButton"));
        tabAfternoon.getStyleClass().add("selectedSchedulerTabButton");
        displayCurrentTimetable();
    }

    private void selectAfternoonTurn() {
        timetableManager.setMorning(false);
        tabAfternoon.getStyleClass().removeIf(s -> (s == "selectedSchedulerTabButton"));
        tabMorning.getStyleClass().add("selectedSchedulerTabButton");
        displayCurrentTimetable();
    }

    private void scheduleKeyReleased(KeyEvent event) {
    }

    private void scheduleReleased(MouseEvent event) {
    }

    private void scheduleDragged(MouseEvent event) {
    }

    private void schedulePressed(MouseEvent event) {
    }

    private void scheduleMenu(ActionEvent event) {
    }

    public void resize() {

        new Timeline(new KeyFrame(Duration.millis(5), event -> resizeFonts())).play();

    }

    public void resizeFonts() {

        double w = hours[0].getWidth();
        double h = hours[0].getHeight();

        Font font1 = Font.font("Arial", FontWeight.BOLD, (h + w) * 0.09);
        Font font2 = Font.font("Arial", FontWeight.BOLD, h * 0.3);
        Font font3 = new Font(h * 0.20);

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
        subjectGrid.getChildren().remove(tabBox);
        subjectGrid.getRowConstraints().clear();
        RowConstraints rc = new RowConstraints();
        rc.setPercentHeight(10);
        subjectGrid.getRowConstraints().add(rc);
        subjectGrid.add(tabBox, 1, 0, 5, 1);

        int pos = 0;
        Day[] dayArray = timetableManager.getCurrentTable().getDays();
        for (int i = 0; i < days.length; i++) {
            subjectGrid.add(days[i], pos + 1, 1, 1, 1);
            if(dayArray[i] != null)
                days[i].setText(dayArray[i].getName());
            pos++;
        }

        Hour[] hourArray = timetableManager.getCurrentTable().getHours();
        for (int i = 0; i < hours.length; i++) {
            subjectGrid.add(hours[i], 0, i + 2, 1, 1);
            if(hourArray[i] != null)
                hours[i].setText(hourArray[i].getName());
        }

        pos = 0;
        Schedule[][] scheduleArray = timetableManager.getCurrentTable().getSchedules();
        for (int i = 0; i < schedules.length; i++) {
            for (int j = 0; j < schedules[0].length; j++) {

                if(i == 0 && j == 3) {
                    JFXButton breakTime = new JFXButton();
                    breakTime.setText("R  E  C  R  E  O");
                    breakTime.getStyleClass().add("breakTimeButton");
                    breakTime.setTextAlignment(TextAlignment.CENTER);
                    breakTime.setAlignment(Pos.CENTER);
                    breakTime.setMinSize(0, 0);
                    breakTime.setPrefSize(2000, 110);
                    subjectGrid.add(breakTime, pos + 1, 5, 5, 1);
                }

                subjectGrid.add(schedules[i][j], pos + 1, j + (j >= 3 ? 3 : 2), 1, 1);

                Schedule schedule = scheduleArray[i][j];

                if(schedule != null) {

                    StringBuilder sb = new StringBuilder();
                    sb.append(schedule.getTeacher().getName());
                    sb.append("\n");
                    sb.append(schedule.getSubject().getAbreviation() + "     " + schedule.getClassroom().toString());
                    sb.append("\n");
                    sb.append(schedule.getGroup().toString());

                    schedules[i][j].setText(sb.toString());

                } else {

                    schedules[i][j].setText("");

                }

            }
            pos++;
        }

        resize();

    }

}
