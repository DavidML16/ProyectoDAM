package morales.david.desktop.managers;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import morales.david.desktop.netty.ClientManager;
import morales.david.desktop.controllers.modals.AdvancedInspectionExportProgressModalController;
import morales.david.desktop.models.*;
import morales.david.desktop.models.packets.PacketBuilder;
import morales.david.desktop.models.packets.PacketType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AdvInspectionManager {

    private static AdvInspectionManager INSTANCE = null;

    public static AdvInspectionManager getInstance() {
        if(INSTANCE == null)
            INSTANCE = new AdvInspectionManager();
        return INSTANCE;
    }


    private List<TimeZone> timeZones = new ArrayList<>();

    public synchronized List<TimeZone> getTimeZones() {
        return timeZones;
    }

    public synchronized void setTimeZones(List<TimeZone> timeZones) {
        this.timeZones = timeZones;
    }


    public void initExport() {

        exportableInspections.clear();

        PacketBuilder packetBuilder = new PacketBuilder()
                .ofType(PacketType.ADVINSPECTION.getRequest())
                .addArgument("timeZones", timeZones);

        ClientManager.getInstance().sendPacketIO(packetBuilder.build());

    }


    private List<ExportableInspection> exportableInspections = new ArrayList<>();

    public void addExportableInspection(ExportableInspection inspection) {

        if(exportableInspections.size() == 0) {

            Platform.runLater(() -> openModal());

            setExporting(true);

        }

        exportableInspections.add(inspection);

        try {

            ExportInspectorManager.getInstance().exportSchedule(
                    inspection.getScheduleTurns(),
                    inspection.getTimeZone(),
                    false,
                    exportDirectory);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Platform.runLater(() -> {
            if(progressDialog != null && progressDialogController != null)
                progressDialogController.setText(exportableInspections.size() + " / " + timeZones.size());
        });

        if(timeZones.size() == exportableInspections.size()) {

            setExporting(false);

            Platform.runLater(() -> {
                if(progressDialog != null && progressDialogController != null)
                    progressDialogController.setText("Exportación finalizada, \nya puedes cerrar esta ventana");
            });

        }

    }

    private Dialog progressDialog;
    private AdvancedInspectionExportProgressModalController progressDialogController;

    private void openModal() {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/modals/advancedInspectionExportProgressModal.fxml"));

        try {

            DialogPane parent = loader.load();
            AdvancedInspectionExportProgressModalController controller = loader.getController();

            Dialog<ButtonType> dialog = new Dialog<>();

            dialog.setDialogPane(parent);
            dialog.setTitle("");

            ((Stage)dialog.getDialogPane().getScene().getWindow()).getIcons().add(new Image("/images/schedule-icon-inverted.png"));


            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            Node closeButton = dialog.getDialogPane().lookupButton(ButtonType.CLOSE);
            closeButton.managedProperty().bind(closeButton.visibleProperty());
            closeButton.setVisible(false);

            dialog.setOnCloseRequest((event) -> {
                if(isExporting())
                    event.consume();
            });

            progressDialog = dialog;
            progressDialogController = controller;

            dialog.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public List<ExportableInspection> getExportableInspections() {
        return exportableInspections;
    }

    public void setExportableInspections(List<ExportableInspection> exportableInspections) {
        this.exportableInspections = exportableInspections;
    }

    private boolean isExporting = false;

    public boolean isExporting() {
        return isExporting;
    }

    public void setExporting(boolean exporting) {
        isExporting = exporting;
    }


    private File exportDirectory;

    public File getExportDirectory() {
        return exportDirectory;
    }

    public void setExportDirectory(File exportDirectory) {
        this.exportDirectory = exportDirectory;
    }

}
