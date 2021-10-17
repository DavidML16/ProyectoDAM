package morales.david.desktop.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import morales.david.desktop.interfaces.Controller;
import morales.david.desktop.managers.ScreenManager;
import morales.david.desktop.ClientManager;
import morales.david.desktop.models.packets.Packet;
import morales.david.desktop.models.packets.PacketBuilder;
import morales.david.desktop.models.packets.PacketType;
import morales.david.desktop.utils.Constants;
import morales.david.desktop.utils.HashUtil;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable, Controller {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button settingsButton;

    @FXML
    private Label messageLabel;


    @Override
    public void initialize(URL url, ResourceBundle rb) {

        Platform.runLater(() -> {

            loginButton.getScene().setOnKeyPressed(e -> {

                if (e.getCode() == KeyCode.ENTER) {

                    login();

                }

            });

        });

    }

    @FXML
    public void handleButtonAction(MouseEvent event) {

        if(event.getSource() == loginButton) {

            login();

        } else if(event.getSource() == settingsButton) {

            ScreenManager screenManager = ScreenManager.getInstance();

            screenManager.openScene("settings.fxml", "Configuración" + Constants.WINDOW_TITLE);

        }

    }

    public void login() {

        final String username = usernameField.getText();
        final String password = HashUtil.sha1(passwordField.getText());

        if(username.isEmpty() || passwordField.getText().isEmpty()) {

            messageLabel.setTextFill(Color.TOMATO);
            messageLabel.setText(Constants.MESSAGES_ERROR_LOGIN_EMPTY);

            return;

        }

        Packet loginRequestPacket = new PacketBuilder()
                .ofType(PacketType.LOGIN.getRequest())
                .addArgument("username", username)
                .addArgument("password", password)
                .build();

        ClientManager clientManager = ClientManager.getInstance();

        if(clientManager.isClosed()) {

            clientManager.addPendingPacket(loginRequestPacket);

            clientManager.open();

        } else {

            ClientManager.getInstance().sendPacketIO(loginRequestPacket);

        }

    }

    public Label getMessageLabel() {
        return messageLabel;
    }

}
