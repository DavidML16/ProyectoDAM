package morales.david.desktop.controllers.schedules.scheduler;

import com.jfoenix.controls.JFXButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import morales.david.desktop.models.Schedule;
import morales.david.desktop.models.SchedulerItem;

import java.util.ArrayList;
import java.util.List;

public class SchedulerItemPane extends AnchorPane {

    private SchedulerItem schedulerItem;

    private GridPane gridPane;

    private List<JFXButton> showedButtons;

    private boolean isPreview;

    private JFXButton referenceButton;

    public SchedulerItemPane(SchedulerItem schedulerItem, boolean isPreview) {
        this.schedulerItem = schedulerItem;
        this.showedButtons = new ArrayList<>();
        this.isPreview = isPreview;
        regenerate();
    }

    public SchedulerItem getSchedulerItem() {
        return schedulerItem;
    }

    public void setSchedulerItem(SchedulerItem schedulerItem) {
        this.schedulerItem = schedulerItem;
        regenerate();
    }

    public JFXButton getReferenceButton() {
        return referenceButton;
    }

    public void setReferenceButton(JFXButton referenceButton) {
        this.referenceButton = referenceButton;
        resizeFont();
    }

    public void clear() {
        if(gridPane != null) {
            gridPane.getChildren().clear();
            gridPane = null;
            showedButtons.clear();
        }
    }

    public void resizeFont() {

        if(referenceButton == null) return;

        double w = referenceButton.getWidth();
        double h = referenceButton.getHeight();

        if(showedButtons.size() == 0) return;

        if(showedButtons.size() == 1) {

            Font font1 = Font.font("System", FontWeight.LIGHT, h * 0.17);

            showedButtons.get(0).setFont(font1);

        } else if(showedButtons.size() == 2) {

            Font font2 = Font.font("System", FontWeight.LIGHT, h * 0.16);

            for(JFXButton button : showedButtons) {

                button.setFont(font2);

            }

        }

    }

    public void regenerate() {

        getStyleClass().clear();
        getStyleClass().add("scheduleButton");

        if(!isPreview) {
            setMinSize(100, 40);
            setPrefSize(500, 500);
        }

        setStyle("-fx-background-color: #FFFFFF;");

        if(schedulerItem != null && schedulerItem.getScheduleList().size() > 0) {

            gridPane = new GridPane();
            getChildren().add(gridPane);

            gridPane.setPickOnBounds(false);
            gridPane.setMouseTransparent(true);
            gridPane.setHgap(5);
            gridPane.setVgap(5);

            if(schedulerItem.getScheduleList().size() == 1) {

                AnchorPane.setTopAnchor(gridPane, 0.0);
                AnchorPane.setBottomAnchor(gridPane, 0.0);
                AnchorPane.setLeftAnchor(gridPane, 0.0);
                AnchorPane.setRightAnchor(gridPane, 0.0);

                Schedule schedule = schedulerItem.getScheduleList().get(0);

                JFXButton button = getButton(schedule);

                GridPane.setHgrow(button, Priority.ALWAYS);
                GridPane.setFillWidth(button, true);
                GridPane.setVgrow(button, Priority.ALWAYS);
                GridPane.setFillHeight(button, true);

                gridPane.add(button, 0, 0, 2, 1);

                return;

            }

            AnchorPane.setTopAnchor(gridPane, 5.0);
            AnchorPane.setBottomAnchor(gridPane, 5.0);
            AnchorPane.setLeftAnchor(gridPane, 5.0);
            AnchorPane.setRightAnchor(gridPane, 5.0);

            int column = 0;
            int row = 0;
            int index = 1;

            for(Schedule schedule : schedulerItem.getScheduleList()) {

                JFXButton button = getButton(schedule);

                if(index < schedulerItem.getScheduleList().size() || column != 0)
                    gridPane.add(button, column, row, 1, 1);
                else
                    gridPane.add(button, column, row, 2, 1);

                column++;
                if(column >= 2) {
                    column = 0;
                    row++;
                }

                index++;

            }

        }

    }

    private JFXButton getButton(Schedule schedule) {

        JFXButton button = new JFXButton();

        button.setPickOnBounds(false);
        button.setMouseTransparent(true);

        button.setPrefSize(500, 500);

        button.setStyle("-fx-background-color: " + schedule.getSubject().getColor() + ";");
        button.setText(schedule.getText());

        button.setTextAlignment(TextAlignment.CENTER);

        showedButtons.add(button);

        return button;

    }

}
