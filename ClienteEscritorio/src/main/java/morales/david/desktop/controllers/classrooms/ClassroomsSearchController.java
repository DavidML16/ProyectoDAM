package morales.david.desktop.controllers.classrooms;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.util.StringConverter;
import morales.david.desktop.interfaces.Controller;
import morales.david.desktop.managers.DataManager;
import morales.david.desktop.managers.ScreenManager;
import morales.david.desktop.ClientManager;
import morales.david.desktop.managers.eventcallbacks.EmptyClassroomsConfirmationListener;
import morales.david.desktop.managers.eventcallbacks.EventManager;
import morales.david.desktop.managers.eventcallbacks.ScheduleErrorListener;
import morales.david.desktop.models.*;
import morales.david.desktop.models.packets.Packet;
import morales.david.desktop.models.packets.PacketBuilder;
import morales.david.desktop.models.packets.PacketType;
import morales.david.desktop.utils.FxUtilTest;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

public class ClassroomsSearchController implements Initializable, Controller {

    @FXML
    private ComboBox<Day> dayComboBox;

    @FXML
    private ComboBox<Hour> hourComboBox;

    @FXML
    private Button searchButton;

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
            searchButton.setDisable(false);
        });

    }

    private void removePressed() {
        hourComboBox.setDisable(true);
        searchButton.setDisable(true);
    }

    @FXML
    public void handleButtonAction(MouseEvent event) {

        if(event.getSource() == searchButton) {

            String uuid = UUID.randomUUID().toString();

            if(!dayComboBox.getSelectionModel().isEmpty() && !hourComboBox.getSelectionModel().isEmpty()) {

                Day day = dayComboBox.getSelectionModel().getSelectedItem();
                Hour hour = hourComboBox.getSelectionModel().getSelectedItem();

                TimeZone timeZone = getTimeZoneBy(day, hour);

                EventManager.getInstance().subscribe(uuid, (eventType, scheduleListenerType) -> {

                    if(scheduleListenerType instanceof EmptyClassroomsConfirmationListener) {

                        List<Classroom> emptyClassrooms = ((EmptyClassroomsConfirmationListener) scheduleListenerType).getClassroomList();

                        ScreenManager.getInstance().openEmptyClassroomsView(timeZone, emptyClassrooms);

                    } else if (scheduleListenerType instanceof ScheduleErrorListener) {

                        ScheduleErrorListener errorListener = (ScheduleErrorListener) scheduleListenerType;

                        System.out.println(errorListener.getMessage());

                    }

                });

                Packet emptyClassroomsRequestPacket = new PacketBuilder()
                        .ofType(PacketType.EMPTYCLASSROOMSTIMEZONE.getRequest())
                        .addArgument("uuid", uuid)
                        .addArgument("timeZone", timeZone)
                        .build();

                ClientManager.getInstance().sendPacketIO(emptyClassroomsRequestPacket);

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
