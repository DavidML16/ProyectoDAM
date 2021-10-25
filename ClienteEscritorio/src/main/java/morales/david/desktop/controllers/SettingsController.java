package morales.david.desktop.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import morales.david.desktop.interfaces.Controller;
import morales.david.desktop.managers.ScreenManager;
import morales.david.desktop.netty.ClientManager;
import morales.david.desktop.utils.ConfigUtil;
import morales.david.desktop.utils.Constants;
import morales.david.desktop.utils.Utils;

import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

public class SettingsController implements Initializable, Controller {

    @FXML
    private AnchorPane root;

    @FXML
    private TextField ipDirectionField;

    @FXML
    private TextField portField;

    @FXML
    private Button saveButton;

    @FXML
    private Button backButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        Platform.runLater(() -> {

            ipDirectionField.setText(Constants.SERVER_IP);
            portField.setText(""+Constants.SERVER_PORT);

            root.requestFocus();

        });

    }

    @FXML
    public void handleButtonAction(MouseEvent event) {

        if(event.getSource() == backButton) {

            ScreenManager screenManager = ScreenManager.getInstance();

            screenManager.openScene("login.fxml", "Iniciar sesión" + Constants.WINDOW_TITLE);

        } else if(event.getSource() == saveButton) {

            Constants.SERVER_IP = ipDirectionField.getText();
            Constants.SERVER_PORT = Integer.parseInt(portField.getText());

            ConfigUtil configUtil = ConfigUtil.getInstance();
            Properties properties = configUtil.getProperties();

            properties.setProperty("server_ip", Constants.SERVER_IP);
            properties.setProperty("server_port", ""+Constants.SERVER_PORT);
            configUtil.saveProperties();

            ClientManager.getInstance().close();

            ScreenManager screenManager = ScreenManager.getInstance();

            screenManager.openScene("login.fxml", "Iniciar sesión" + Constants.WINDOW_TITLE);

        }

    }

    @FXML
    void numberInputKeyTyped(KeyEvent event) {

        if(!Utils.isInteger(event.getCharacter()))
            event.consume();

    }

}
