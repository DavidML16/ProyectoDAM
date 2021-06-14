package morales.david.desktop.controllers.schedules;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import morales.david.desktop.interfaces.Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DaysHoursMenuController implements Initializable, Controller {

    @FXML
    private SplitPane splitPane;

    @FXML
    private BorderPane leftView;

    @FXML
    private BorderPane rightView;

    private String actualView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        actualView = "";

        Platform.runLater(() -> loadView("schedules/days.fxml", "schedules/hours.fxml"));

    }

    private void loadView(String left, String right) {

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/" + left));
            Node newView = loader.load();

            leftView.setCenter(newView);

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/" + right));
            Node newView = loader.load();

            rightView.setCenter(newView);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
