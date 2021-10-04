package morales.david.desktop.controllers.options;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import morales.david.desktop.interfaces.Controller;
import morales.david.desktop.managers.SocketManager;
import morales.david.desktop.models.packets.Packet;
import morales.david.desktop.models.packets.PacketBuilder;
import morales.david.desktop.models.packets.PacketType;

import java.net.URL;
import java.util.ResourceBundle;

public class BackupController implements Initializable, Controller {

    @FXML
    private AnchorPane root;

    @FXML
    private TextField emailField;

    @FXML
    private Button createBackupButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Platform.runLater(() -> root.requestFocus());

    }

    @FXML
    void handleButtonAction(MouseEvent event) {

        if(event.getSource() == createBackupButton) {

            setInProgress(true);

            PacketBuilder packetBuilder = new PacketBuilder()
                    .ofType(PacketType.DATABASEBACKUP.getRequest())
                    .addArgument("email", emailField.getText());

            SocketManager.getInstance().sendPacketIO(packetBuilder.build());

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

}
