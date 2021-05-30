package morales.david.server.utils;

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

}
