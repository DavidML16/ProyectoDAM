package morales.david.server.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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

            properties.setProperty("server_port", "6565");
            properties.setProperty("server_file_transfer_port", "6566");
            properties.setProperty("client_connection_checking_interval", "5000");

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

        String server_port = properties.getProperty("server_port");
        parameters.put("server_port", server_port);

        String server_file_transfer_port = properties.getProperty("server_file_transfer_port");
        parameters.put("server_file_transfer_port", server_file_transfer_port);

        String client_check_interval = properties.getProperty("client_connection_checking_interval");
        parameters.put("client_connection_checking_interval", client_check_interval);

        return parameters;

    }

}
