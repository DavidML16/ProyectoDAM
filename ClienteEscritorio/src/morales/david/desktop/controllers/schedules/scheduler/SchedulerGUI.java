package morales.david.desktop.controllers.schedules.scheduler;

import com.jfoenix.controls.JFXButton;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import morales.david.desktop.interfaces.Hideable;
import morales.david.desktop.models.Day;
import morales.david.desktop.models.Hour;
import morales.david.desktop.models.Schedule;
import morales.david.desktop.models.SchedulerItem;

import java.util.ArrayList;
import java.util.List;

public class SchedulerGUI {

    public static final int ANIMATION_DURATION = 200;
    public static final int ANIMATION_DISTANCE = 50;
    public static final double FOCUS_ANIMATION_OFFSET_FACTOR = 0.6;
    public static final double GAP_SIZE = 5;
    public static final double FONT_FACTOR = 0.22;

    private static final int DAY_LENGTH = 5;
    private static final int HOURS_LENGTH = 7;

    private AnchorPane background;
    private GridPane subjectGrid;

    private SchedulerManager schedulerManager;

    private EventHandler<ActionEvent> scheduleActionEvent;
    private EventHandler<MouseEvent> schedulePressedEvent;
    private EventHandler<MouseEvent> scheduleDraggedEvent;
    private EventHandler<MouseEvent>scheduleReleasedEvent;

    private JFXButton schedulePreview;

    public boolean controlDown = false;
    private boolean firstDrag = true;
    private boolean primaryButton;
    private double subjectStartX;
    private double subjectStartY;
    private double subjectInnerX;
    private double subjectInnerY;

    private OptionsPane scheduleContextMenu;
    private JFXButton copy;
    private JFXButton cut;
    private JFXButton paste;
    private JFXButton delete;

    private JFXButton selectedSchedule;

    private HBox tabBox;
    private Label tabMorning;
    private Label tabAfternoon;

    private JFXButton[] days;
    private JFXButton[] hours;
    private JFXButton[][] schedules;

    private List<Hideable> menus;

    public SchedulerGUI(AnchorPane anchorPane, SchedulerManager schedulerManager) {
        this.background = anchorPane;
        this.schedulerManager = schedulerManager;
        this.schedulerManager.setGui(this);
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

        menus = new ArrayList<Hideable>();

        scheduleContextMenu = new OptionsPane(background);
        menus.add(scheduleContextMenu);
        copy = new JFXButton("Copiar");
        copy.setOnAction(event -> {
            copy(event);
        });
        cut = new JFXButton("Cortar");
        cut.setOnAction(event -> {
            cut(event);
        });
        paste = new JFXButton("Pegar");
        paste.setOnAction(event -> {
            paste(event);
        });
        delete = new JFXButton("Eliminar");
        delete.setOnAction(event -> {
            delete();
        });
        scheduleContextMenu.addButton(copy);
        scheduleContextMenu.addButton(cut);
        scheduleContextMenu.addButton(paste);
        scheduleContextMenu.addButton(delete);

        schedulePreview = new JFXButton();
        schedulePreview.setTextAlignment(TextAlignment.CENTER);
        schedulePreview.setAlignment(Pos.CENTER);
        schedulePreview.prefWidthProperty().bind(schedules[0][0].widthProperty());
        schedulePreview.prefHeightProperty().bind(schedules[0][0].heightProperty());
        schedulePreview.setVisible(false);
        background.getChildren().add(schedulePreview);

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
                schedules[i][j] = schedule;
            }
        }

    }

    private void selectMorningTurn() {
        hideAllMenus();
        schedulerManager.setMorning(true);
        tabMorning.getStyleClass().removeIf(s -> (s == "selectedSchedulerTabButton"));
        tabAfternoon.getStyleClass().add("selectedSchedulerTabButton");
        displayCurrentTimetable();
    }

    private void selectAfternoonTurn() {
        hideAllMenus();
        schedulerManager.setMorning(false);
        tabAfternoon.getStyleClass().removeIf(s -> (s == "selectedSchedulerTabButton"));
        tabMorning.getStyleClass().add("selectedSchedulerTabButton");
        displayCurrentTimetable();
    }

    private void scheduleReleased(MouseEvent event) {

        if(primaryButton) {

            boolean onSubject = false;
            int is1 = 0;
            int js1 = 0;
            int is2 = 0;
            int js2 = 0;

            for (int i = 0; i < schedules.length; i++) {
                for (int j = 0; j < schedules[0].length; j++) {
                    if (schedules[i][j] == event.getSource()) {
                        is1 = i;
                        js1 = j;
                        break;
                    }
                }
            }
            for (int i = 0; i < schedules.length; i++) {
                for (int j = 0; j < schedules[0].length; j++) {
                    if (schedules[i][j].getLayoutX() < event.getSceneX() && schedules[i][j].getLayoutX() + schedules[i][j].getWidth() > event.getSceneX() &&
                            schedules[i][j].getLayoutY() < event.getSceneY() && schedules[i][j].getLayoutY() + schedules[i][j].getHeight() > event.getSceneY()) {
                        is2 = i;
                        js2 = j;
                        onSubject = true;
                        break;
                    }
                }
            }

            if (onSubject) {
                if (controlDown) {
                    schedulerManager.copy(is1, js1);
                    schedulerManager.paste(is2, js2);
                } else {
                    schedulerManager.getCurrentTable().switchSchedule(is1, js1, is2, js2);
                }
                displayCurrentTimetable();
            }

            schedulePreview.setVisible(false);
            firstDrag = true;

        }

    }

    private void scheduleDragged(MouseEvent event) {

        if (primaryButton) {

            if (firstDrag) {
                hideAllMenus();
                for (int i = 0; i < schedules.length; i++) {
                    for (int j = 0; j < schedules[0].length; j++) {
                        if (schedules[i][j] == event.getSource()) {
                            String text = schedulerManager.getCurrentTable().getScheduleText(i, j);
                            SchedulerItem schedulerItem = schedulerManager.getCurrentTable().getSchedule(i, j);
                            if(!text.equalsIgnoreCase("")) {
                                schedulePreview.setVisible(true);
                                schedulePreview.setText(text);
                                schedulePreview.setStyle("-fx-background-color: " + schedulerItem.getScheduleList().get(0).getSubject().getColor() + ";");
                            }
                            break;
                        }
                    }
                }
                firstDrag = false;
            }

            schedulePreview.setLayoutX(event.getSceneX() - subjectInnerX);
            schedulePreview.setLayoutY(event.getSceneY() - subjectInnerY);

        }

    }

    private void schedulePressed(MouseEvent event) {
        if (event.isSecondaryButtonDown()) {
            primaryButton = false;
            getSelectedScheduleButton(event);
            scheduleContextMenu.showOnCoordinates(event.getSceneX(), event.getSceneY(), selectedSchedule);
        } else {
            hideAllMenus();
            primaryButton = true;
            subjectStartX = event.getSceneX();
            subjectStartY = event.getSceneY();
            subjectInnerX = event.getX();
            subjectInnerY = event.getY();
        }
    }

    private void scheduleMenu(ActionEvent event) {
    }

    private void copy(Event event) {
        getSelectedScheduleButton(event);
        schedulerManager.copyCurrentClipboard();
        displayCurrentTimetable();
    }

    private void cut(Event event) {
        getSelectedScheduleButton(event);
        schedulerManager.copyCurrentClipboard();
        schedulerManager.deleteSchedule();
        displayCurrentTimetable();
    }

    private void paste(Event event) {
        getSelectedScheduleButton(event);
        schedulerManager.pasteCurrentClipboard();
        displayCurrentTimetable();
    }

    public void delete() {
        schedulerManager.deleteSchedule();
        displayCurrentTimetable();
    }

    private void getSelectedScheduleButton(Event event) {
        for (int day = 0; day < schedules.length; day++) {
            for (int hour = 0; hour < schedules[0].length; hour++) {
                if (event.getSource() == schedules[day][hour]) {
                    selectedSchedule = schedules[day][hour];
                    schedulerManager.setSelectedIndexDay(day);
                    schedulerManager.setSelectedIndexHour(hour);
                    break;
                }
            }
        }
    }

    private SchedulerItem getSelectedSchedule(Event event) {
        for (int day = 0; day < schedules.length; day++) {
            for (int hour = 0; hour < schedules[0].length; hour++) {
                if (event.getSource() == schedules[day][hour]) {
                    SchedulerItem schedule = schedulerManager.getCurrentTable().getSchedule(day, hour);
                    return schedule;
                }
            }
        }
        return null;
    }

    public void hideAllMenus() {
        for (Hideable menu : menus) {
            menu.hide();
        }
    }

    public void resize() {

        new Timeline(new KeyFrame(Duration.millis(5), event -> resizeFonts())).play();

    }

    public void resizeFonts() {

        double w = hours[0].getWidth();
        double h = hours[0].getHeight();

        Font font1 = Font.font("Arial", FontWeight.BOLD, (h + w) * 0.09);
        Font font2 = Font.font("Arial", FontWeight.BOLD, h * 0.3);
        Font font3 = Font.font("Arial", FontWeight.LIGHT, h * 0.2);

        for (JFXButton b : days)
            b.setFont(font1);

        for (JFXButton b : hours)
            b.setFont(font2);

        for (JFXButton[] ba : schedules)
            for (JFXButton b : ba)
                b.setFont(font3);

        schedulePreview.setFont(Font.font("Arial", FontWeight.BOLD, h * 0.2));

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
        Day[] dayArray = schedulerManager.getCurrentTable().getDays();
        for (int i = 0; i < days.length; i++) {
            subjectGrid.add(days[i], pos + 1, 1, 1, 1);
            if(dayArray[i] != null)
                days[i].setText(dayArray[i].getName());
            pos++;
        }

        Hour[] hourArray = schedulerManager.getCurrentTable().getHours();
        for (int i = 0; i < hours.length; i++) {
            subjectGrid.add(hours[i], 0, i + 2, 1, 1);
            if(hourArray[i] != null)
                hours[i].setText(hourArray[i].getName());
        }

        pos = 0;
        SchedulerItem[][] scheduleArray = schedulerManager.getCurrentTable().getSchedules();
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

                SchedulerItem schedule = scheduleArray[i][j];

                schedules[i][j].setStyle("-fx-background-color: #FFFFFF;");

                if(schedule != null && schedule.getScheduleList().size() > 0) {

                    schedules[i][j].setText(schedulerManager.getCurrentTable().getScheduleText(i, j));
                    schedules[i][j].setStyle("-fx-background-color: " + schedule.getScheduleList().get(0).getSubject().getColor() + ";");

                } else {

                    schedules[i][j].setText("");

                }

            }
            pos++;
        }

        resize();

    }

}
