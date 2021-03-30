package morales.david.desktop.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import morales.david.desktop.interfaces.Controller;
import morales.david.desktop.managers.ScreenManager;
import morales.david.desktop.managers.SocketManager;
import morales.david.desktop.utils.Constants;
import morales.david.desktop.utils.HashUtil;

import java.io.IOException;
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
    private Label messageLabel;


    @Override
    public void initialize(URL url, ResourceBundle rb) {}

    @FXML
    public void handleButtonAction(MouseEvent event) throws IOException {

        if(event.getSource() == loginButton) {

            login(usernameField.getText(), passwordField.getText());

        }

    }

    public void login(String user, String pass) {

        final String username = user;
        final String password = HashUtil.sha1(pass);

        StringBuilder sb = new StringBuilder()
            .append(Constants.REQUEST_LOGIN)
            .append(Constants.ARGUMENT_DIVIDER)
            .append(username)
            .append(Constants.ARGUMENT_DIVIDER)
            .append(password);

        SocketManager.getInstance().sendMessageIO(sb.toString());

    }

    public Label getMessageLabel() {
        return messageLabel;
    }

}
