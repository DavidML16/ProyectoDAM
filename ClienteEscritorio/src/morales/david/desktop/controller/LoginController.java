package morales.david.desktop.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

/**
 *
 * @author oXCToo
 */
public class LoginController implements Initializable {

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
    public void handleButtonAction(MouseEvent event) {

        //TODO LOGIN

    }

}
