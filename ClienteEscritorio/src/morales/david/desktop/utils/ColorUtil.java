package morales.david.desktop.utils;

import java.awt.*;
import java.util.Random;

public class ColorUtil {

    private static Random randomGenerator = new Random();

    private static final String[] mColors = {
            "#ffadad",
            "#ffd6a5",
            "#fdffb6",
            "#9bf6ff",
            "#a0c4ff",
            "#bdb2ff",
            "#ffc6ff",
            "#60d394",
            "#aaf683",
            "#ffd97d",
            "#79addc",
            "#fcf5c7",
            "#dec0f1",
            "#edd6c8",
            "#d6fcff"
    };

    public static String getColor() {

        int randomNumber = randomGenerator.nextInt(mColors.length);
        return mColors[randomNumber];

    }

    public static String getFontColor(String colorString) {
        Color color = Color.decode(colorString);
        double lum = (((0.299 * color.getRed()) + ((0.587 * color.getGreen()) + (0.114 * color.getBlue()))));
        return lum > 186 ? "#000000" : "#FFFFFF";
    }

}
