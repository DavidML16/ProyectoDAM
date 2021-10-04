package morales.david.desktop.controllers.options;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import morales.david.desktop.interfaces.Controller;
import morales.david.desktop.managers.ScreenManager;
import morales.david.desktop.utils.Constants;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class OptionsMenuController implements Initializable, Controller {

    @FXML
    private Button importNavigationButton;

    @FXML
    private Button backupNavigationButton;

    @FXML
    private Button deleteDataNavigationButton;

    @FXML
    private BorderPane viewPane;

    private String actualView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        actualView = "";

        Platform.runLater(() -> loadView("options/import.fxml", "Importar", null));
        importNavigationButton.getStyleClass().add("buttonPressed");

    }

    @FXML
    public void handleButtonAction(MouseEvent event) {

        if(event.getSource() == importNavigationButton)
            loadView("options/import.fxml", "Importar", event);
        else if(event.getSource() == backupNavigationButton)
            loadView("options/backup.fxml", "Copias de seguridad", event);
        else if(event.getSource() == deleteDataNavigationButton)
            loadView("options/clean.fxml", "Borrado de datos", event);

    }

    private void removePressed() {

        importNavigationButton.getStyleClass().remove("buttonPressed");
        backupNavigationButton.getStyleClass().remove("buttonPressed");
        deleteDataNavigationButton.getStyleClass().remove("buttonPressed");

    }

    private void loadView(String view, String title, MouseEvent event) {

        Node newView = null;
        Node oldView = null;

        if(actualView.equalsIgnoreCase(view))
            return;

        if(event != null) {
            Button button = (Button) event.getSource();
            removePressed();
            button.getStyleClass().add("buttonPressed");
        }

        actualView = view;

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/" + view));
            newView = loader.load();

            viewPane.setCenter(newView);

            ScreenManager.getInstance().getStage().setTitle(title + Constants.WINDOW_TITLE);

            ScreenManager.getInstance().setController(loader.getController());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
