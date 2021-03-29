package morales.david.desktop;

import javafx.application.Application;
import javafx.stage.Stage;
import morales.david.desktop.managers.ScreenManager;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {

        ScreenManager screenManager = ScreenManager.getInstance();

        screenManager.setStage(primaryStage);

        screenManager.openScene("login.fxml", "Login");

    }

    public static void main(String[] args) {
        launch(args);
    }

}
