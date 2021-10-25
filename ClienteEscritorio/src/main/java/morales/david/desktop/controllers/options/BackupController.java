package morales.david.desktop.controllers.options;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import morales.david.desktop.interfaces.Controller;
import morales.david.desktop.netty.ClientManager;
import morales.david.desktop.models.packets.PacketBuilder;
import morales.david.desktop.models.packets.PacketType;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class BackupController implements Initializable, Controller {

    @FXML
    private AnchorPane root;

    @FXML
    private TextField emailField;

    @FXML
    private Button createBackupButton;

    @FXML
    private Label directoryChooser;

    private ObservableList<File> directories;

    private static File initialDirectory;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Platform.runLater(() -> {

            root.requestFocus();

            directories.addListener((ListChangeListener) change -> {

                if(change.next() && change.getAddedSize() > 0) {
                    directoryChooser.setText(directories.get(0).getAbsolutePath());
                }

            });

            directoryChooser.setOnMouseClicked(event -> {

                DirectoryChooser chooser = new DirectoryChooser();

                if(initialDirectory != null)
                    chooser.setInitialDirectory(initialDirectory);

                chooser.setTitle("Selecciona el directorio de salida");

                File directory = chooser.showDialog(directoryChooser.getParent().getScene().getWindow());

                if(directory != null && directory.isDirectory()) {

                    directories.add(0, directory);
                    directoryChooser.setText(directory.getAbsolutePath());

                    if(directories.size() > 0 && directories.get(0).isDirectory())
                        initialDirectory = directories.get(0);

                }

            });

        });

        directories = FXCollections.observableList(new ArrayList<>());

    }

    @FXML
    void handleButtonAction(MouseEvent event) {

        if(event.getSource() == createBackupButton) {

            if(directories.size() == 0)
                return;

            setInProgress(true);

            PacketBuilder packetBuilder = new PacketBuilder()
                    .ofType(PacketType.DATABASEBACKUP.getRequest())
                    .addArgument("email", emailField.getText())
                    .addArgument("sendEmail", emailField.getText().trim().isEmpty() ? "false" : "true");

            ClientManager.getInstance().sendPacketIO(packetBuilder.build());

            emailField.setText("");
            directories.clear();
            directoryChooser.setText("Pulsa para elegir el directorio de salida");

        }

    }

    public void setInProgress(boolean executing) {

        if(executing) {

            createBackupButton.setText("Creando copia de seguridad...");

            createBackupButton.setDisable(true);

        } else {

            createBackupButton.setText("Crear copia de seguridad");

            createBackupButton.setDisable(false);

        }

    }

    public void receivedSQL(String date, String sql) {

        BufferedWriter output = null;
        try {

            File file = new File(initialDirectory, "BACKUP_" + date.replaceAll(" ", "_").replaceAll(":", "_") + ".sql");

            if(file.exists())
                file.delete();
            else
                file.createNewFile();

            output = new BufferedWriter(new FileWriter(file));
            output.write(sql);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
