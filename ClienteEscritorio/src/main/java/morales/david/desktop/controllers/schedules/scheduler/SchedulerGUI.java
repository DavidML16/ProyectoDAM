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
import morales.david.desktop.netty.ClientManager;
import morales.david.desktop.models.Day;
import morales.david.desktop.models.Hour;
import morales.david.desktop.models.SchedulerItem;
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

    private SchedulerItem lastOverSchedule;

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
        if(!ClientManager.getInstance().getClientSession().isTeacherRole())
            edit = new JFXButton("Editar");
        else
            edit = new JFXButton("Ver más");

        edit.setOnAction(event -> {
            edit(event);
        });

        scheduleContextMenu.addButton(edit);
        if(!ClientManager.getInstance().getClientSession().isTeacherRole()) {
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
        }

        schedulePreview = new SchedulerItemPane(null, true, schedulerManager);
        schedulePreview.prefWidthProperty().bind(schedules[0][0].widthProperty());
        schedulePreview.prefHeightProperty().bind(schedules[0][0].heightProperty());
        schedulePreview.setOpacity(0.55);
        schedulePreview.setVisible(false);
        background.getChildren().add(schedulePreview);

        lastOverSchedule = null;

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

    private int[] getSelectedSchedules(MouseEvent event) {

        int[] result = new int[5];

        int onSubject = 0;
        int is1 = -1;
        int js1 = -1;
        int is2 = -1;
        int js2 = -1;

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
                    onSubject = 1;
                    break;
                }
            }
        }

        result[0] = is1;
        result[1] = js1;
        result[2] = is2;
        result[3] = js2;
        result[4] = onSubject;

        return result;

    }

    private void scheduleReleased(MouseEvent event) {

        if(ClientManager.getInstance().getClientSession().isTeacherRole())
            return;

        if(primaryButton) {

            int[] result = getSelectedSchedules(event);

            if (result[4] == 1) {
                schedulerManager.getCurrentTable().switchSchedule(result[0], result[1], result[2], result[3]);
                displayCurrentTimetable();
            }

            schedulePreview.setVisible(false);
            firstDrag = true;

            schedulerManager.removeHighlight(schedules);

            lastOverSchedule = null;

        }

    }

    private void scheduleDragged(MouseEvent event) {

        if(ClientManager.getInstance().getClientSession().isTeacherRole())
            return;

        if (primaryButton) {

            if (firstDrag) {
                hideAllMenus();
                for (int i = 0; i < schedules.length; i++) {
                    for (int j = 0; j < schedules[0].length; j++) {
                        if (schedules[i][j] == event.getSource()) {

                            SchedulerItem schedulerItem = schedulerManager.getCurrentTable().getScheduleItem(i, j);

                            if(schedulerItem != null && schedulerItem.getScheduleList() != null && schedulerItem.getScheduleList().size() > 0) {
                                schedulePreview.setVisible(true);
                                schedulePreview.setOpacity(0.65);
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
            schedulePreview.setOpacity(0.65);

            int[] result = getSelectedSchedules(event);

            SchedulerItem origin = schedulerManager.getCurrentTable().getScheduleItem(result[0], result[1]);

            if(origin == null || origin.getScheduleList().size() == 0)
                return;

            SchedulerItem destiny = schedulerManager.getCurrentTable().getScheduleItem(result[2], result[3]);

            if(destiny != null) {

                if(lastOverSchedule == null || !lastOverSchedule.equals(destiny)) {

                    lastOverSchedule = destiny;

                    schedulerManager.highlightSchedule(destiny, schedules, false);

                }

            } else {

                lastOverSchedule = null;

                schedulerManager.removeHighlight(schedules);

            }

            if(origin != null) {

                schedulerManager.highlightSchedule(origin, schedules, true);

            }

        }

    }

    private void schedulePressed(MouseEvent event) {
        if (event.isSecondaryButtonDown()) {
            primaryButton = false;
            getSelectedScheduleButton(event);
            selectedScheduleItem = getSelectedSchedule(event);
            scheduleContextMenu.showOnCoordinates(event.getSceneX(), event.getSceneY(), selectedScheduleButton);
        } else {
            if(ClientManager.getInstance().getClientSession().isTeacherRole())
                return;
            hideAllMenus();
            primaryButton = true;
            scheduleInnerX = event.getX();
            scheduleInnerY = event.getY();
        }
    }

    private void edit(Event event) {
        schedulerManager.getCurrentTable().openSchedulerItemModal(selectedScheduleItem, schedulerManager.getSelectedTimeZone());
    }

    private void copy(Event event) {
        if(ClientManager.getInstance().getClientSession().isTeacherRole())
            return;
        getSelectedScheduleButton(event);
        schedulerManager.copyCurrentClipboard();
        displayCurrentTimetable();
    }

    private void cut(Event event) {
        if(ClientManager.getInstance().getClientSession().isTeacherRole())
            return;
        getSelectedScheduleButton(event);
        schedulerManager.copyCurrentClipboard();
        schedulerManager.deleteSchedule();
        displayCurrentTimetable();
    }

    private void paste(Event event) {
        if(ClientManager.getInstance().getClientSession().isTeacherRole())
            return;
        getSelectedScheduleButton(event);
        schedulerManager.pasteCurrentClipboard();
        displayCurrentTimetable();
    }

    public void delete() {
        if(ClientManager.getInstance().getClientSession().isTeacherRole())
            return;
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
