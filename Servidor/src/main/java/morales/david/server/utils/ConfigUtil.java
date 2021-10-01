package morales.david.server.utils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConfigUtil {

    private Properties properties;

    public ConfigUtil() throws IOException {

        properties = new Properties();

        File directory = new File("");
        File propertiesFile = new File(directory.getAbsolutePath() + File.separator + "configuracion.properties");

        if(!propertiesFile.exists()) {

            properties.setProperty("setup_first_time", "true");

            properties.setProperty("server_port", "6565");
            properties.setProperty("server_file_transfer_port", "6566");
            properties.setProperty("client_connection_checking_interval", "5000");

            properties.setProperty("db_ip", "localhost");
            properties.setProperty("db_port", "3306");
            properties.setProperty("db_database", "db_proyecto");
            properties.setProperty("db_username", "david");
            properties.setProperty("db_password", "161100");

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

        String setup_first_time = properties.getProperty("setup_first_time");
        parameters.put("setup_first_time", setup_first_time);

        String server_port = properties.getProperty("server_port");
        parameters.put("server_port", server_port);

        String server_file_transfer_port = properties.getProperty("server_file_transfer_port");
        parameters.put("server_file_transfer_port", server_file_transfer_port);

        String client_check_interval = properties.getProperty("client_connection_checking_interval");
        parameters.put("client_connection_checking_interval", client_check_interval);

        String db_ip = properties.getProperty("db_ip");
        parameters.put("db_ip", db_ip);

        String db_port = properties.getProperty("db_port");
        parameters.put("db_port", db_port);

        String db_database = properties.getProperty("db_database");
        parameters.put("db_database", db_database);

        String db_username = properties.getProperty("db_username");
        parameters.put("db_username", db_username);

        String db_password = properties.getProperty("db_password");
        parameters.put("db_password", db_password);

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
