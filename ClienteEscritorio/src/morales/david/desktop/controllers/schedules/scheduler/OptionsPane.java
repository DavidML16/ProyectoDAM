package morales.david.desktop.controllers.schedules.scheduler;

import com.jfoenix.controls.JFXButton;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class OptionsPane extends AbstractPane {

    TranslateTransition SlideIn;
    FadeTransition FadeIn;
    TranslateTransition SlideOut;
    FadeTransition FadeOut;
    ParallelTransition show;
    ParallelTransition hide;

    public OptionsPane(Pane parent) {
        super(parent);

        SlideIn = new TranslateTransition(Duration.millis(SchedulerGUI.ANIMATION_DURATION));
        SlideIn.setFromY(SchedulerGUI.ANIMATION_DISTANCE);
        SlideIn.setToY(0);

        FadeIn = new FadeTransition(Duration.millis(SchedulerGUI.ANIMATION_DURATION));
        FadeIn.setFromValue(0);
        FadeIn.setToValue(1);

        SlideOut = new TranslateTransition(Duration.millis(SchedulerGUI.ANIMATION_DURATION));
        SlideOut.setToY(SchedulerGUI.ANIMATION_DISTANCE);

        FadeOut = new FadeTransition(Duration.millis(SchedulerGUI.ANIMATION_DURATION));
        FadeOut.setToValue(0);

        show = new ParallelTransition(getPane());
        show.getChildren().add(SlideIn);
        show.getChildren().add(FadeIn);

        hide = new ParallelTransition(getPane());
        hide.getChildren().add(SlideOut);
        hide.getChildren().add(FadeOut);
        hide.setOnFinished(event -> getPane().setVisible(false));
    }

    public void showOnCoordinates(double x, double y, JFXButton source) {
        setSource(source);

        int size = getPane().getChildren().size();

        double w = source.getHeight();
        double h = source.getHeight();

        show(x, y, w, h);
    }

    private void show(double x, double y, double w, double h) {
        int size = getPane().getChildren().size();

        if (x + w * getWidthFactor() > getParent().getWidth()) {
            x = getParent().getWidth() - w * getWidthFactor();
        }
        if (y + h * size * getHeightFactor() > getParent().getHeight()) {
            y = getParent().getHeight() - h * size * getHeightFactor();
        }

        setHidden(false);

        getPane().setPrefWidth(w * getWidthFactor());
        getPane().setPrefHeight(h * size * getHeightFactor());
        getPane().setLayoutX(x);
        getPane().setLayoutY(y);

        for (Node n : getPane().getChildren()) {
            JFXButton b = (JFXButton) n;
            b.setFont(new Font(h * 0.2));
        }

        Timeline focus = new Timeline(new KeyFrame(
                Duration.millis(SchedulerGUI.ANIMATION_DURATION * SchedulerGUI.FOCUS_ANIMATION_OFFSET_FACTOR),
                n -> getPane().getChildren().get(0).requestFocus()));
        focus.play();

        getPane().setVisible(true);
        show.play();
    }

    @Override
    public void hide() {
        if (!isHidden()) {
            setHidden(true);

            getSource().requestFocus();

            hide.play();
        }
    }

    @Override
    public void cancel() {
        setHidden(true);
        getPane().setVisible(false);
    }

    public void addButton(JFXButton button) {

        button.setPrefWidth(500);
        button.setPrefHeight(150);

        button.addEventHandler(ActionEvent.ACTION, event -> {
            hide();
        });

        int size = getPane().getChildren().size();

        getPane().getChildren().remove(getDone());
        getPane().add(button, 0, size - 1, 1, 1);
        getPane().add(getDone(), 0, size, 1, 1);

    }

}
