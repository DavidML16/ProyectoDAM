package morales.david.desktop.utils;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;

public class ConfigUtil {

    public void getConfigString(final Consumer<String> consumer) {
        try (InputStream inputStream = new URL("https://raw.githubusercontent.com/DavidML16/ProyectoDAM/master/config.json").openStream();
            Scanner scanner = new Scanner(inputStream)) {
            String configString = "";
            while(scanner.hasNext()) {
                configString += scanner.next();
            }
            consumer.accept(configString);
        } catch (MalformedURLException e) {
            consumer.accept(null);
        } catch (IOException e) {
            consumer.accept(null);
        }
    }

    public Map<String, String> getConfigParams(String configString) {

        Map<String, String> parameters = new HashMap<>();

        JSONObject jsonObject = new JSONObject(configString);

        String server_ip = jsonObject.getString("server_ip");
        parameters.put("server_ip", server_ip);

        String server_port = jsonObject.getString("server_port");
        parameters.put("server_port", server_port);

        String server_file_transfer_port = jsonObject.getString("server_file_transfer_port");
        parameters.put("server_file_transfer_port", server_file_transfer_port);

        return parameters;

    }

}
