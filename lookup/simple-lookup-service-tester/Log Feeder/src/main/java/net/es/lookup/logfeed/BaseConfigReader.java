package net.es.lookup.logfeed;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;
import org.ho.yaml.Yaml;

/**
 * Created by Kamala Narayan on 8/2/16.
 */
public class BaseConfigReader {

    private static BaseConfigReader instance;


    public static BaseConfigReader getInstance() {

        if (instance == null) {

            instance = new BaseConfigReader();

        }

        return instance;

    }

    private BaseConfigReader() {

    }


    @SuppressWarnings({"static-access", "unchecked"})
    public Map getConfiguration(String configFile) {

        Map configuration = null;
        InputStream yamlFile = this.getClass().getClassLoader().getSystemResourceAsStream(configFile);

        try {

            configuration = (Map) Yaml.load(yamlFile);

        } catch (NullPointerException ex) {

            try {

                yamlFile = new FileInputStream(new File(configFile));

            }
            catch (FileNotFoundException e)
            {
                System.err.println(configFile + " not found\n. Config file required to start Latency Checker");
                System.exit(1);
            }

            configuration = (Map) Yaml.load(yamlFile);

        }

        return configuration;


    }


}
