package morales.david.desktop.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import morales.david.desktop.interfaces.Controller;
import morales.david.desktop.managers.ScreenManager;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable, Controller {

    @FXML
    private BorderPane viewPane;

    @FXML
    private Button homeNavigationButton;

    @FXML
    private Button teachersNavigationButton;

    @FXML
    private Button groupsNavigationButton;

    @FXML
    private Button roomsNavigationButton;

    @FXML
    private Button scheduleNavigationButton;

    @FXML
    private Button importNavigationButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void handleButtonAction(MouseEvent event) {

        if(event.getSource() == importNavigationButton) {

            loadView("import.fxml");

        }

    }

    private void loadView(String view) {

        Parent parent = null;

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/" + view));
            parent = loader.load();

            Controller controller = loader.getController();

            viewPane.setCenter(parent);

            ScreenManager.getInstance().setController(controller);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
