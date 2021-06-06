package morales.david.desktop.controllers.modals;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import morales.david.desktop.controllers.schedules.SchedulesController;
import morales.david.desktop.controllers.schedules.scheduler.SchedulerManager;
import morales.david.desktop.managers.SocketManager;
import morales.david.desktop.models.*;
import morales.david.desktop.models.packets.Packet;
import morales.david.desktop.models.packets.PacketBuilder;
import morales.david.desktop.models.packets.PacketType;
import morales.david.desktop.utils.FxUtilTest;
import morales.david.desktop.utils.Utils;
import org.controlsfx.control.ListSelectionView;

import java.io.IOException;
import java.net.URL;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void handleButtonAction(MouseEvent event) {

        if(event.getSource() == newScheduleButton) {

            newSchedule();

        }

    }

    public void setData(SchedulerManager schedulerManager, SchedulerItem schedulerItem, TimeZone timeZone) {

        this.schedulerManager = schedulerManager;
        this.schedulerItem = schedulerItem;
        this.timeZone = timeZone;

        this.dialogTitle.setText("Turnos del " + timeZone.getDay().getName() + " de " + timeZone.getHour().getName());

        initItems();

    }

    public void initItems() {

        vBox.getChildren().clear();

        for(Schedule schedule : schedulerItem.getScheduleList()) {

            try {

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/modals/schedulerItem.fxml"));
                AnchorPane parent = loader.load();

                SchedulerItemController controller = loader.getController();
                controller.setData(this, schedulerManager, schedulerItem, timeZone, schedule);

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

            controller.setData(new Schedule(), false);

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

            ((Stage)dialog.getDialogPane().getScene().getWindow()).getIcons().add(new Image("/resources/images/schedule-icon-inverted.png"));

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

    public void editSchedule(Schedule schedule) {

        if(schedule == null)
            return;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/modals/schedulerModal.fxml"));
        try {

            DialogPane parent = loader.load();
            SchedulerModalController controller = loader.getController();

            controller.setData(schedule.duplicateUUID(), true);

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

            ((Stage)dialog.getDialogPane().getScene().getWindow()).getIcons().add(new Image("/resources/images/schedule-icon-inverted.png"));

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