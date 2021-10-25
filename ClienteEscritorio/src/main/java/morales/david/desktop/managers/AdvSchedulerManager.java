package morales.david.desktop.managers;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import morales.david.desktop.netty.ClientManager;
import morales.david.desktop.controllers.modals.AdvancedScheduleExportProgressModalController;
import morales.david.desktop.models.*;
import morales.david.desktop.models.packets.PacketBuilder;
import morales.david.desktop.models.packets.PacketType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class AdvSchedulerManager {

    private static AdvSchedulerManager INSTANCE = null;

    public static AdvSchedulerManager getInstance() {
        if(INSTANCE == null)
            INSTANCE = new AdvSchedulerManager();
        return INSTANCE;
    }


    private List<Teacher> teachers = new ArrayList<>();

    public synchronized List<Teacher> getTeachers() {
        return teachers;
    }

    public synchronized void setTeachers(List<Teacher> teachers) {
        this.teachers = teachers;
    }


    private List<Group> groups = new ArrayList<>();

    public synchronized List<Group> getGroups() {
        return groups;
    }

    public synchronized void setGroups(List<Group> groups) {
        this.groups = groups;
    }


    private List<Classroom> classrooms = new ArrayList<>();

    public synchronized List<Classroom> getClassrooms() {
        return classrooms;
    }

    public synchronized void setClassrooms(List<Classroom> classrooms) {
        this.classrooms = classrooms;
    }


    private List<ExportableItem> exportableItems = new ArrayList<>();

    public void initExport() {

        exportableItems.clear();
        exportableSchedules.clear();

        for(Teacher teacher : teachers)
            exportableItems.add(new ExportableItem("TEACHER", teacher.getName(), teacher));

        for(Group group : groups)
            exportableItems.add(new ExportableItem("GROUP", group.toString(), group));

        for(Classroom classroom : classrooms)
            exportableItems.add(new ExportableItem("CLASSROOM", classroom.getName(), classroom));

        PacketBuilder packetBuilder = new PacketBuilder()
                .ofType(PacketType.ADVSCHEDULE.getRequest())
                .addArgument("exportableItems", exportableItems);

        ClientManager.getInstance().sendPacketIO(packetBuilder.build());

    }


    private List<ExportableSchedule> exportableSchedules = new ArrayList<>();

    public void addExportableSchedule(ExportableSchedule scheduler) {

        if(exportableSchedules.size() == 0) {

            Platform.runLater(() -> openModal());

            setExporting(true);

        }

        exportableSchedules.add(scheduler);

        try {

            ExportSchedulerManager.getInstance().exportSchedule(
                    scheduler.getSchedulerItems(),
                    scheduler.getExportType(),
                    scheduler.getExportQuery(),
                    false,
                    exportDirectory);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Platform.runLater(() -> {
            if(progressDialog != null && progressDialogController != null)
                progressDialogController.setText(exportableSchedules.size() + " / " + exportableItems.size());
        });

        if(exportableItems.size() == exportableSchedules.size()) {

            setExporting(false);

            Platform.runLater(() -> {
                if(progressDialog != null && progressDialogController != null)
                    progressDialogController.setText("Exportación finalizada, \nya puedes cerrar esta ventana");
            });

        }

    }

    private Dialog progressDialog;
    private AdvancedScheduleExportProgressModalController progressDialogController;

    private void openModal() {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/modals/advancedScheduleExportProgressModal.fxml"));

        try {

            DialogPane parent = loader.load();
            AdvancedScheduleExportProgressModalController controller = loader.getController();

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

    public List<ExportableSchedule> getExportableSchedules() {
        return exportableSchedules;
    }

    public void setExportableSchedules(List<ExportableSchedule> exportableSchedules) {
        this.exportableSchedules = exportableSchedules;
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
