package morales.david.desktop;

import javafx.application.Application;
import javafx.stage.Stage;
import morales.david.desktop.managers.ScreenManager;
import morales.david.desktop.managers.SocketManager;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {

        SocketManager socketManager = SocketManager.getInstance();
        socketManager.setDaemon(true);
        socketManager.start();

        ScreenManager screenManager = ScreenManager.getInstance();

        screenManager.setStage(primaryStage);
        screenManager.getStage().setResizable(false);

        screenManager.openScene("login.fxml", "Login");

    }

    public static void main(String[] args) {
        launch(args);
    }

}
