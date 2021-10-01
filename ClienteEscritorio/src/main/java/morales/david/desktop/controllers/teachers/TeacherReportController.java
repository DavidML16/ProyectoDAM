package morales.david.desktop.controllers.teachers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;
import javafx.util.StringConverter;
import morales.david.desktop.interfaces.Controller;
import morales.david.desktop.managers.DataManager;
import morales.david.desktop.managers.ScreenManager;
import morales.david.desktop.managers.SocketManager;
import morales.david.desktop.managers.eventcallbacks.EmptyClassroomsConfirmationListener;
import morales.david.desktop.managers.eventcallbacks.EventManager;
import morales.david.desktop.managers.eventcallbacks.ScheduleErrorListener;
import morales.david.desktop.models.Classroom;
import morales.david.desktop.models.Day;
import morales.david.desktop.models.Hour;
import morales.david.desktop.models.TimeZone;
import morales.david.desktop.models.packets.Packet;
import morales.david.desktop.models.packets.PacketBuilder;
import morales.david.desktop.models.packets.PacketType;
import morales.david.desktop.utils.FxUtilTest;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

public class TeacherReportController implements Initializable, Controller {

    @FXML
    private ComboBox<Day> dayComboBox;

    @FXML
    private ComboBox<Hour> hourComboBox;

    @FXML
    private Button exportButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        removePressed();

        dayComboBox.setItems(DataManager.getInstance().getDays());
        dayComboBox.setConverter(new StringConverter<Day>() {
            @Override
            public String toString(Day object) {
                return object != null ? object.getName() : "";
            }

            @Override
            public Day fromString(String string) {
                return dayComboBox.getItems().stream().filter(object ->
                        object.getName().equals(string)).findFirst().orElse(null);
            }
        });

        hourComboBox.setItems(DataManager.getInstance().getHours());
        hourComboBox.setConverter(new StringConverter<Hour>() {
            @Override
            public String toString(Hour object) {
                return object != null ? object.getName() : "";
            }

            @Override
            public Hour fromString(String string) {
                return hourComboBox.getItems().stream().filter(object ->
                        object.getName().equals(string)).findFirst().orElse(null);
            }
        });

        FxUtilTest.autoCompleteComboBoxPlus(dayComboBox, (typedText, itemToCompare) -> itemToCompare.getName().toLowerCase().contains(typedText.toLowerCase()));
        FxUtilTest.autoCompleteComboBoxPlus(hourComboBox, (typedText, itemToCompare) -> itemToCompare.getName().toLowerCase().contains(typedText.toLowerCase()));

        dayComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            hourComboBox.setDisable(false);
        });

        hourComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            exportButton.setDisable(false);
        });

    }

    private void removePressed() {
        hourComboBox.setDisable(true);
        exportButton.setDisable(true);
    }

    @FXML
    public void handleButtonAction(MouseEvent event) {

        if(event.getSource() == exportButton) {

            if(!dayComboBox.getSelectionModel().isEmpty() && !hourComboBox.getSelectionModel().isEmpty()) {

                Day day = dayComboBox.getSelectionModel().getSelectedItem();
                Hour hour = hourComboBox.getSelectionModel().getSelectedItem();

                TimeZone timeZone = getTimeZoneBy(day, hour);

                PacketBuilder packetBuilder = new PacketBuilder()
                        .ofType(PacketType.EXPORTINSPECTION.getRequest())
                        .addArgument("timeZone", timeZone);

                SocketManager.getInstance().sendPacketIO(packetBuilder.build());

            }


        }

    }

    public TimeZone getTimeZoneBy(Day day, Hour hour) {
        for(TimeZone timeZone : new ArrayList<>(DataManager.getInstance().getTimeZones())) {
            if(timeZone.getDay().getId() == day.getId() && timeZone.getHour().getId() == hour.getId()) {
                return timeZone;
            }
        }
        return null;
    }

}
