package morales.david.desktop.utils;

import morales.david.desktop.managers.DataManager;
import org.json.JSONObject;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.function.Consumer;

public final class ConfigUtil {

    private static ConfigUtil INSTANCE = null;

    public static ConfigUtil getInstance() {
        if(INSTANCE == null) {
            try {
                INSTANCE = new ConfigUtil();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return INSTANCE;
    }

    private Properties properties;

    public ConfigUtil() throws IOException {

        properties = new Properties();

        File directory = new File("");
        File propertiesFile = new File(directory.getAbsolutePath() + File.separator + "configuracion.properties");

        if(!propertiesFile.exists()) {

            properties.setProperty("server_ip", "localhost");
            properties.setProperty("server_port", "6565");

            FileOutputStream fr = new FileOutputStream(propertiesFile);
            properties.store(fr, null);
            fr.close();

        }

        FileInputStream fi = new FileInputStream(propertiesFile);
        properties.load(fi);
        fi.close();

    }

    public Map<String, String> getConfigParams() {

        Map<String, String> parameters = new HashMap<>();

        String server_ip = properties.getProperty("server_ip");
        parameters.put("server_ip", server_ip);

        String server_port = properties.getProperty("server_port");
        parameters.put("server_port", server_port);

        return parameters;

    }

    public void saveProperties() {

        File directory = new File("");
        File propertiesFile = new File(directory.getAbsolutePath() + File.separator + "configuracion.properties");

        FileOutputStream fr = null;
        try {

            fr = new FileOutputStream(propertiesFile);
            properties.store(fr, null);
            fr.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Properties getProperties() {
        return properties;
    }


}
