package morales.david.desktop.managers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import morales.david.desktop.controllers.DashboardController;
import morales.david.desktop.interfaces.Controller;

import java.io.IOException;

public final class ScreenManager {

    private static ScreenManager INSTANCE = null;

    public static ScreenManager getInstance() {
        if(INSTANCE == null)
            INSTANCE = new ScreenManager();
        return INSTANCE;
    }

    private Stage stage;
    private Scene scene;
    private Controller controller, dashboardController;

    public synchronized Stage getStage() {
        return stage;
    }

    public synchronized void setStage(Stage stage) {
        this.stage = stage;
    }

    public synchronized Scene getScene() {
        return scene;
    }

    public synchronized void setScene(Scene scene) {
        this.scene = scene;
    }

    public synchronized Controller getController() {
        return controller;
    }

    public synchronized void setController(Controller controller) {
        this.controller = controller;
    }

    public synchronized Controller getDashboardController() { return dashboardController; }

    public synchronized void setDashboardController(Controller dashboardController) { this.dashboardController = dashboardController; }

    public synchronized void openScene(String url, String title) {

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/views/" + url));
            Parent parent = loader.load();

            controller = loader.getController();

            if(controller instanceof DashboardController)
                dashboardController = controller;

            scene = new Scene(parent);
            stage.setTitle(title);
            stage.setScene(scene);

            if(!stage.isShowing())
                stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
