package morales.david.desktop.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class ConfigUtil {

    public static void getConfigString(final Consumer<String> consumer) {
        try (InputStream inputStream = new URL("https://raw.githubusercontent.com/DavidML16/AParkour/master/version.txt").openStream();
            Scanner scanner = new Scanner(inputStream)) {
            if (scanner.hasNext()) {
                consumer.accept(scanner.next());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
