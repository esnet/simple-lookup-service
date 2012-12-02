package net.es.lookup.utils;

import org.apache.log4j.Logger;
import org.ho.yaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;


public class ConfigHelper {

    private static ConfigHelper instance;
    private static Logger LOG = Logger.getLogger(ConfigHelper.class);

    public static ConfigHelper getInstance() {

        if (instance == null) {

            instance = new ConfigHelper();

        }

        return instance;

    }

    private ConfigHelper() {

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

            } catch (FileNotFoundException e) {

                //e.printStackTrace();
                LOG.error(configFile + " not found\n. Config file required to start Lookup Service");
                System.exit(1);

            }

            configuration = (Map) Yaml.load(yamlFile);

        }

        return configuration;

    }


}

