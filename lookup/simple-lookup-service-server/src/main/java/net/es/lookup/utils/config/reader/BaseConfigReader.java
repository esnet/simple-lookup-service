package net.es.lookup.utils.config.reader;

import org.apache.log4j.Logger;
import org.ho.yaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;


public class BaseConfigReader {

    private static BaseConfigReader instance;
    private static Logger LOG = Logger.getLogger(BaseConfigReader.class);

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

            } catch (FileNotFoundException e) {

                //e.printStackTrace();
                LOG.error(configFile + " not found\n. Config file required to start Lookup Service Record");
                System.exit(1);

            }

            configuration = (Map) Yaml.load(yamlFile);

        }

        return configuration;


    }


}

