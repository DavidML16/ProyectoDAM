package morales.david.desktop;

import com.google.gson.Gson;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import morales.david.desktop.managers.ScreenManager;
import morales.david.desktop.managers.SocketManager;
import morales.david.desktop.utils.Constants;

public class Client extends Application {

    public static final Gson GSON = new Gson();

    @Override
    public void start(Stage primaryStage) {

        SocketManager socketManager = SocketManager.getInstance();
        socketManager.setDaemon(true);
        socketManager.start();

        primaryStage.getIcons().add(new Image("/resources/images/schedule-icon.png"));

        ScreenManager screenManager = ScreenManager.getInstance();

        screenManager.setStage(primaryStage);

        screenManager.openScene("login.fxml", "Iniciar sesión" + Constants.WINDOW_TITLE);

    }

    public static void main(String[] args) {
        launch(args);
    }

}
