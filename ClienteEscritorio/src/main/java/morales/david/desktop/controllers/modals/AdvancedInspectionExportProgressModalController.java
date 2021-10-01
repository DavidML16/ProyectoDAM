package morales.david.desktop.controllers.modals;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class AdvancedInspectionExportProgressModalController implements Initializable {

    @FXML
    private Label titleLabel;

    @FXML
    private Label exportProgressLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        exportProgressLabel.setText("Por favor espere...");

    }

    public void setText(String text) {

        exportProgressLabel.setText(text);

    }

}
