package morales.david.android.utils;

import android.app.Activity;
import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;

import morales.david.android.managers.eventcallbacks.ConfirmationEventListener;
import morales.david.android.managers.eventcallbacks.ErrorEventListener;
import morales.david.android.managers.eventcallbacks.EventManager;

public class ConfigUtil {

    public void getConfigString(Activity context) {
        new Thread(() -> {
            try (InputStream inputStream = new URL("https://raw.githubusercontent.com/DavidML16/ProyectoDAM/master/config.json").openStream();
                 Scanner scanner = new Scanner(inputStream)) {
                String configString = "";
                while(scanner.hasNext()) {
                    configString += scanner.next();
                }
                EventManager.getInstance().notify(context, "config", new ConfirmationEventListener("config", configString));
            } catch (MalformedURLException e) {
                EventManager.getInstance().notify(context, "config", new ErrorEventListener("config", "ERROR"));
            } catch (IOException e) {
                EventManager.getInstance().notify(context, "config", new ErrorEventListener("config", "ERROR"));
            }
        }).start();
    }

    public Map<String, String> getConfigParams(String configString) {

        Map<String, String> parameters = new HashMap<>();

        try {

            JSONObject jsonObject = new JSONObject(configString);

            String server_ip = jsonObject.getString("server_ip");
            parameters.put("server_ip", server_ip);

            String server_port = jsonObject.getString("server_port");
            parameters.put("server_port", server_port);

            String server_file_transfer_port = jsonObject.getString("server_file_transfer_port");
            parameters.put("server_file_transfer_port", server_file_transfer_port);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return parameters;

    }

}
