package morales.david.desktop.controllers.teachers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import morales.david.desktop.netty.ClientManager;
import morales.david.desktop.controllers.modals.AdvancedInspectionExportModalController;
import morales.david.desktop.interfaces.Controller;
import morales.david.desktop.managers.*;
import morales.david.desktop.models.Day;
import morales.david.desktop.models.Hour;
import morales.david.desktop.models.TimeZone;
import morales.david.desktop.models.packets.PacketBuilder;
import morales.david.desktop.models.packets.PacketType;
import morales.david.desktop.utils.FxUtilTest;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class TeacherReportController implements Initializable, Controller {

    @FXML
    private ComboBox<Day> dayComboBox;

    @FXML
    private ComboBox<Hour> hourComboBox;

    @FXML
    private Button exportButton;

    @FXML
    private Button exportAdvancedButton;

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

                ClientManager.getInstance().sendPacketIO(packetBuilder.build());

            }

        } else if(event.getSource() == exportAdvancedButton) {

            advancedExport();

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

    private void advancedExport() {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/modals/advancedInspectionExportModal.fxml"));

        try {

            DialogPane parent = loader.load();
            AdvancedInspectionExportModalController controller = loader.getController();

            Dialog<ButtonType> dialog = new Dialog<>();

            dialog.setDialogPane(parent);
            dialog.setTitle("");

            ButtonType updateBtn = new ButtonType("Exportar", ButtonBar.ButtonData.YES);
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

                controller.setExportQuerys();

                AdvInspectionManager.getInstance().initExport();

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
