package morales.david.desktop.utils;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Utils {

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static String capitalizeFirstLetter(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }

    public static void centerWindow(Stage stage) {
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);
    }

    public static String stripString(String name) {

        String result = "";

        result = name.toLowerCase().replaceAll(" ", "_");

        result = result.replaceAll("á", "a");
        result = result.replaceAll("é", "e");
        result = result.replaceAll("í", "i");
        result = result.replaceAll("ó", "o");
        result = result.replaceAll("ú", "u");

        return result;

    }

}
