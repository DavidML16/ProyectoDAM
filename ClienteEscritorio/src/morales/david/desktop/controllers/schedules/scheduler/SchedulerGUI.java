package morales.david.desktop.controllers.schedules.scheduler;

import com.jfoenix.controls.JFXButton;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import morales.david.desktop.interfaces.Hideable;
import morales.david.desktop.managers.ScreenManager;
import morales.david.desktop.models.Day;
import morales.david.desktop.models.Hour;
import morales.david.desktop.models.SchedulerItem;
import morales.david.desktop.models.TimeZone;
import morales.david.desktop.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class SchedulerGUI {

    public static final int ANIMATION_DURATION = 100;
    public static final int ANIMATION_DISTANCE = 50;
    public static final double FOCUS_ANIMATION_OFFSET_FACTOR = 0.6;

    private static final int DAY_LENGTH = 5;
    private static final int HOURS_LENGTH = 7;

    private AnchorPane background;
    private GridPane scheduleGrid;

    private SchedulerManager schedulerManager;

    private EventHandler<MouseEvent> schedulePressedEvent;
    private EventHandler<MouseEvent> scheduleDraggedEvent;
    private EventHandler<MouseEvent> scheduleReleasedEvent;

    private SchedulerItemPane schedulePreview;

    private boolean firstDrag = true;
    private boolean primaryButton;
    private double scheduleInnerX;
    private double scheduleInnerY;

    private JFXButton infoButton;

    private OptionsPane scheduleContextMenu;
    private JFXButton edit;
    private JFXButton copy;
    private JFXButton cut;
    private JFXButton paste;
    private JFXButton delete;

    private SchedulerItemPane selectedScheduleButton;
    private SchedulerItem selectedScheduleItem;

    private HBox tabBox;
    private Label tabMorning;
    private Label tabAfternoon;

    private JFXButton[] days;
    private JFXButton[] hours;
    private SchedulerItemPane[][] schedules;

    private List<Hideable> menus;

    public SchedulerGUI(AnchorPane anchorPane, SchedulerManager schedulerManager) {
        this.background = anchorPane;
        this.schedulerManager = schedulerManager;
        this.schedulerManager.setGui(this);
    }

    public void init() {

        scheduleGrid = new GridPane();
        scheduleGrid.setVgap(Constants.GAP_SIZE);
        scheduleGrid.setHgap(Constants.GAP_SIZE);
        if(background.getChildren().size() > 1)
            background.getChildren().remove(0);
        background.setPrefSize(900, 600);
        background.getChildren().add(scheduleGrid);
        background.getStyleClass().clear();
        background.getStyleClass().add("mainPane");
        background.setTopAnchor(scheduleGrid, Constants.GAP_SIZE * 4);
        background.setRightAnchor(scheduleGrid, Constants.GAP_SIZE * 4);
        background.setBottomAnchor(scheduleGrid, Constants.GAP_SIZE * 4);
        background.setLeftAnchor(scheduleGrid, Constants.GAP_SIZE * 4);

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
        edit = new JFXButton("Editar");
        edit.setOnAction(event -> {
            edit(event);
        });
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
        scheduleContextMenu.addButton(edit);
        scheduleContextMenu.addButton(copy);
        scheduleContextMenu.addButton(cut);
        scheduleContextMenu.addButton(paste);
        scheduleContextMenu.addButton(delete);

        schedulePreview = new SchedulerItemPane(null, true, schedulerManager);
        schedulePreview.prefWidthProperty().bind(schedules[0][0].widthProperty());
        schedulePreview.prefHeightProperty().bind(schedules[0][0].heightProperty());
        schedulePreview.setVisible(false);
        background.getChildren().add(schedulePreview);

        selectMorningTurn();

        new Timeline(new KeyFrame(Duration.millis(100), event -> resizeFonts())).play();
        new Timeline(new KeyFrame(Duration.millis(200), event -> resizeFonts())).play();

        resize();

    }

    public void initControlArrays() {

        tabBox = new HBox();
        tabBox.getStyleClass().add("mainPane");
        tabBox.setSpacing(Constants.GAP_SIZE);

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

        schedulePressedEvent = (MouseEvent event) -> {
            schedulePressed(event);
        };
        scheduleDraggedEvent = (MouseEvent event) -> {
            scheduleDragged(event);
        };
        scheduleReleasedEvent = (MouseEvent event) -> {
            scheduleReleased(event);
        };

        schedules = new SchedulerItemPane[DAY_LENGTH][HOURS_LENGTH];
        for (int i = 0; i < schedules.length; i++) {
            for (int j = 0; j < schedules[0].length; j++) {
                SchedulerItemPane schedule = new SchedulerItemPane(null, false, schedulerManager);
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
                schedulerManager.getCurrentTable().switchSchedule(is1, js1, is2, js2);
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

                            SchedulerItem schedulerItem = schedulerManager.getCurrentTable().getScheduleItem(i, j);

                            if(schedulerItem != null && schedulerItem.getScheduleList() != null && schedulerItem.getScheduleList().size() > 0) {
                                schedulePreview.setVisible(true);
                                schedulePreview.clear();
                                schedulePreview.setSchedulerItem(schedulerItem);
                                schedulePreview.setReferenceButton(hours[0]);
                            }

                            break;
                        }
                    }
                }
                firstDrag = false;
            }

            schedulePreview.setLayoutX(event.getSceneX() - scheduleInnerX);
            schedulePreview.setLayoutY(event.getSceneY() - scheduleInnerY);

        }

    }

    private void schedulePressed(MouseEvent event) {
        if(event.getClickCount() <= 1) {
            if (event.isSecondaryButtonDown()) {
                primaryButton = false;
                getSelectedScheduleButton(event);
                selectedScheduleItem = getSelectedSchedule(event);
                scheduleContextMenu.showOnCoordinates(event.getSceneX(), event.getSceneY(), selectedScheduleButton);
            } else {
                hideAllMenus();
                primaryButton = true;
                scheduleInnerX = event.getX();
                scheduleInnerY = event.getY();
            }
        }
    }

    private void edit(Event event) {
        schedulerManager.getCurrentTable().openSchedulerItemModal(selectedScheduleItem, schedulerManager.getSelectedTimeZone());
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
                    selectedScheduleButton = schedules[day][hour];
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
                    SchedulerItem schedule = schedulerManager.getCurrentTable().getScheduleItem(day, hour);
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

        Font font1 = Font.font("System", FontWeight.BOLD, (h + w) * 0.09);
        Font font2 = Font.font("System", FontWeight.BOLD, h * 0.2);

        for (JFXButton b : days)
            b.setFont(font1);

        for (JFXButton b : hours)
            b.setFont(font2);

        for (SchedulerItemPane[] ba : schedules)
            for (SchedulerItemPane b : ba)
                b.resizeFont();

        schedulePreview.resizeFont();
        infoButton.setFont(font2);

    }

    public void displayCurrentTimetable() {

        for(Node node : scheduleGrid.getChildren()) {

            if(node.getClass() != SchedulerItemPane.class) continue;

            SchedulerItemPane schedulerItemPane = (SchedulerItemPane) node;

            schedulerItemPane.clear();

        }

        scheduleGrid.getChildren().removeIf(node -> (node.getClass() == SchedulerItemPane.class || node.getClass() == JFXButton.class));
        scheduleGrid.getChildren().remove(tabBox);
        scheduleGrid.getRowConstraints().clear();
        RowConstraints rc = new RowConstraints();
        rc.setPercentHeight(7.5);
        scheduleGrid.getRowConstraints().add(rc);
        scheduleGrid.add(tabBox, 1, 0, 5, 1);

        infoButton = new JFXButton();
        infoButton.getStyleClass().add("backButton");
        infoButton.setTextAlignment(TextAlignment.CENTER);
        infoButton.setText(schedulerManager.getSearchQuery());
        infoButton.setWrapText(true);
        infoButton.setMinSize(100, 40);
        infoButton.setPrefSize(500, 500);
        infoButton.setMouseTransparent(true);
        infoButton.setDisableVisualFocus(true);
        infoButton.setFocusTraversable(false);
        scheduleGrid.add(infoButton, 0, 0, 1, 2);

        int pos = 0;
        Day[] dayArray = schedulerManager.getCurrentTable().getDays();
        RowConstraints rc2 = new RowConstraints();
        rc2.setPercentHeight(8.5);
        scheduleGrid.getRowConstraints().add(rc2);
        for (int i = 0; i < days.length; i++) {
            scheduleGrid.add(days[i], pos + 1, 1, 1, 1);
            if(dayArray[i] != null)
                days[i].setText(dayArray[i].getName());
            pos++;
        }

        Hour[] hourArray = schedulerManager.getCurrentTable().getHours();
        for (int i = 0; i < hours.length; i++) {
            scheduleGrid.add(hours[i], 0, i + 2, 1, 1);
            if(hourArray[i] != null)
                hours[i].setText(hourArray[i].getName());
        }

        pos = 0;
        SchedulerItem[][] scheduleArray = schedulerManager.getCurrentTable().getScheduleItems();
        for (int i = 0; i < schedules.length; i++) {
            for (int j = 0; j < schedules[0].length; j++) {

                if(!schedulerManager.getSearchType().equalsIgnoreCase("TEACHER") && j == 3) {
                    if(i == 0) {
                        JFXButton breakTime = new JFXButton();
                        breakTime.setText("R  E  C  R  E  O");
                        breakTime.getStyleClass().add("breakTimeButton");
                        breakTime.setTextAlignment(TextAlignment.CENTER);
                        breakTime.setAlignment(Pos.CENTER);
                        breakTime.setMinSize(0, 0);
                        breakTime.setPrefSize(2000, 110);
                        scheduleGrid.add(breakTime, pos + 1, 5, 5, 1);
                    }
                    continue;
                }

                SchedulerItemPane button = schedules[i][j];

                Rectangle clip = new Rectangle();
                clip.widthProperty().bind(button.widthProperty());
                clip.heightProperty().bind(button.heightProperty());
                button.setClip(clip);

                scheduleGrid.add(button, pos + 1, j + 2, 1, 1);

                SchedulerItem schedule = scheduleArray[i][j];

                button.setSchedulerItem(schedule);
                button.setReferenceButton(hours[0]);

            }
            pos++;
        }

        resize();

    }

}
