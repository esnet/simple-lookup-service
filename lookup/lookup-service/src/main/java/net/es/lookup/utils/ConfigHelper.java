package net.es.lookup.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

import org.ho.yaml.Yaml;


public class ConfigHelper {

    private static ConfigHelper instance;

    public static ConfigHelper getInstance() {
        if (instance == null) {
            instance = new ConfigHelper();
        }
        return instance;
    }

    private ConfigHelper() {

    }

    @SuppressWarnings({ "static-access", "unchecked" })
    public Map getConfiguration(String configPath) {
        Map configuration = null;
        //String absfilename = path+"/"+fname;
        //System.out.println(absfilename);
        InputStream yamlFile = this.getClass().getClassLoader().getSystemResourceAsStream(configPath);
        try {
            configuration = (Map) Yaml.load(yamlFile);
        } catch (NullPointerException ex) {
            try {
                yamlFile = new FileInputStream(new File(configPath));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                System.exit(1);
            }
            configuration = (Map) Yaml.load(yamlFile);
        }
        return configuration;
    }
}

