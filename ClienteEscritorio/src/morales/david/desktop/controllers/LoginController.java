package morales.david.desktop.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import morales.david.desktop.interfaces.Controller;
import morales.david.desktop.managers.ScreenManager;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable, Controller {

    @FXML
    private Label lblErrors;

    @FXML
    private TextField txtUsername;

    @FXML
    private TextField txtPassword;

    @FXML
    private Button btnSignin;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        //TODO CONEXION CON EL SERVIDOR

    }

    @FXML
    public void handleButtonAction(MouseEvent event) throws IOException {

        //TODO LOGIN

        if(event.getSource() == btnSignin) {

            ScreenManager.getInstance().openScene("dashboard.fxml", "Dashboard");
            ScreenManager.getInstance().getStage().setMaximized(true);

        }

    }

}
