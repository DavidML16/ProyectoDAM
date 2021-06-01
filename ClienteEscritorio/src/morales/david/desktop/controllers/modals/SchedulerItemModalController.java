package morales.david.desktop.controllers.modals;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import morales.david.desktop.controllers.schedules.SchedulesController;
import morales.david.desktop.controllers.schedules.scheduler.SchedulerManager;
import morales.david.desktop.models.*;
import morales.david.desktop.utils.FxUtilTest;
import morales.david.desktop.utils.Utils;
import org.controlsfx.control.ListSelectionView;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SchedulerItemModalController implements Initializable {

    @FXML
    private Label dialogTitle;

    @FXML
    private VBox vBox;

    private SchedulerManager schedulerManager;
    private SchedulerItem schedulerItem;
    private TimeZone timeZone;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

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
                controller.setData(schedulerManager, schedulerItem, timeZone, schedule);

                vBox.getChildren().add(parent);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

}
