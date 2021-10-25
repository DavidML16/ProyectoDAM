package morales.david.desktop.controllers.modals;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import morales.david.desktop.controllers.schedules.scheduler.SchedulerManager;
import morales.david.desktop.netty.ClientManager;
import morales.david.desktop.models.*;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class SchedulerItemModalController implements Initializable {

    @FXML
    private Label dialogTitle;

    @FXML
    private VBox vBox;

    @FXML
    private Button newScheduleButton;

    private SchedulerManager schedulerManager;
    private SchedulerItem schedulerItem;
    private TimeZone timeZone;

    private List<Classroom> emptyClassrooms;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(ClientManager.getInstance().getClientSession().isTeacherRole()) {
            newScheduleButton.setVisible(false);
        }
    }

    @FXML
    public void handleButtonAction(MouseEvent event) {

        if(event.getSource() == newScheduleButton) {

            newSchedule();

        }

    }

    public void setData(SchedulerManager schedulerManager, SchedulerItem schedulerItem, TimeZone timeZone, List<Classroom> emptyClassrooms) {

        this.schedulerManager = schedulerManager;
        this.schedulerItem = schedulerItem;
        this.timeZone = timeZone;
        this.emptyClassrooms = emptyClassrooms;

        this.dialogTitle.setText("Turnos del " + timeZone.getDay().getName() + " de " + timeZone.getHour().getName());

        initItems();

    }

    public void initItems() {

        vBox.getChildren().clear();

        for(Schedule schedule : schedulerItem.getScheduleList()) {

            try {

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/modals/schedulerItem.fxml"));
                AnchorPane parent = loader.load();

                SchedulerItemController controller = loader.getController();
                controller.setData(this, schedulerManager, schedulerItem, timeZone, schedule, emptyClassrooms);

                vBox.getChildren().add(parent);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public void newSchedule() {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/modals/schedulerModal.fxml"));
        try {

            DialogPane parent = loader.load();
            SchedulerModalController controller = loader.getController();

            controller.setData(new Schedule(), false, emptyClassrooms);

            Dialog<ButtonType> dialog = new Dialog<>();

            dialog.setDialogPane(parent);
            dialog.setTitle("");

            ButtonType addBtn = new ButtonType("Añadir", ButtonBar.ButtonData.YES);
            ButtonType cancelBtn = new ButtonType("Cancelar", ButtonBar.ButtonData.NO);

            dialog.getDialogPane().getButtonTypes().addAll(addBtn, cancelBtn);

            Button addButton = (Button) dialog.getDialogPane().lookupButton(addBtn);
            addButton.getStyleClass().addAll("dialogButton", "addButton");

            addButton.addEventFilter(
                    ActionEvent.ACTION,
                    event -> {
                        if (!controller.validateInputs()) {
                            event.consume();
                        }
                    }
            );

            Button cancelButton = (Button) dialog.getDialogPane().lookupButton(cancelBtn);
            cancelButton.getStyleClass().addAll("dialogButton", "cancelButton");

            ((Stage)dialog.getDialogPane().getScene().getWindow()).getIcons().add(new Image("/images/schedule-icon-inverted.png"));

            Optional<ButtonType> clickedButton = dialog.showAndWait();

            if(clickedButton.get() == addBtn) {

                Schedule schedule = controller.getData();
                schedule.setTimeZone(timeZone);

                schedulerManager.getCurrentTable().addSchedule(schedulerItem, schedule);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void editSchedule(Schedule schedule, List<Classroom> emptyClassrooms) {

        if(schedule == null)
            return;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/modals/schedulerModal.fxml"));
        try {

            DialogPane parent = loader.load();
            SchedulerModalController controller = loader.getController();

            controller.setData(schedule.duplicateUUID(), true, emptyClassrooms);

            Dialog<ButtonType> dialog = new Dialog<>();

            dialog.setDialogPane(parent);
            dialog.setTitle("");

            ButtonType updateBtn = new ButtonType("Actualizar", ButtonBar.ButtonData.YES);
            ButtonType cancelBtn = new ButtonType("Cancelar", ButtonBar.ButtonData.NO);

            dialog.getDialogPane().getButtonTypes().addAll(updateBtn, cancelBtn);

            Button updateButton = (Button) dialog.getDialogPane().lookupButton(updateBtn);
            updateButton.getStyleClass().addAll("dialogButton", "updateButton");

            updateButton.addEventFilter(
                    ActionEvent.ACTION,
                    event -> {
                        if (!controller.validateInputs()) {
                            event.consume();
                        }
                    }
            );

            Button cancelButton = (Button) dialog.getDialogPane().lookupButton(cancelBtn);
            cancelButton.getStyleClass().addAll("dialogButton", "cancelButton");

            ((Stage)dialog.getDialogPane().getScene().getWindow()).getIcons().add(new Image("/images/schedule-icon-inverted.png"));

            Optional<ButtonType> clickedButton = dialog.showAndWait();

            if(clickedButton.get() == updateBtn) {

                Schedule updatedSchedule = controller.getData();

                schedulerManager.getCurrentTable().updateSchedule(schedulerItem, updatedSchedule);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
