package net.es.lookup.utils;

import net.es.lookup.common.exception.internal.ConfigurationException;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Author: sowmya
 * Date: 8/5/13
 * Time: 4:26 PM
 */
public class BootStrapConfigReader {

    private static BootStrapConfigReader instance;
    private static String configFile = "";

    private List<String> sourceLocator = new ArrayList<String>();
    private List<Integer> sourcePriority = new ArrayList<Integer>();

    private int sourceCount;

    private static Logger LOG = Logger.getLogger(ConfigHelper.class);

    /**
     * Constructor - private because this is a Singleton
     */
    private BootStrapConfigReader() {

    }

    /*
   * set the config file
   * */

    public static void init(String cFile) {

        configFile = cFile;
    }

    /**
     * @return the initialized LookupServiceConfigReader singleton instance
     */
    public static BootStrapConfigReader getInstance() throws ConfigurationException {

        if (BootStrapConfigReader.instance == null) {
            BootStrapConfigReader.instance = new BootStrapConfigReader();
            BootStrapConfigReader.instance.setInfo(configFile);
        }
        return BootStrapConfigReader.instance;
    }


    public int getSourceCount() {

        return sourceCount;
    }

    public String getSourceLocator(int index) throws ConfigurationException {

        if (sourceLocator != null && !sourceLocator.isEmpty() && (index >= 0 && index < sourceCount)) {
            return sourceLocator.get(index);
        } else {
            throw new ConfigurationException("Error retrieving source locator information");
        }
    }

    public int getSourcePriority(int index) throws ConfigurationException {
        if (sourcePriority != null && !sourcePriority.isEmpty() && (index >= 0 && index < sourceCount)) {
            return sourcePriority.get(index);
        } else {
            throw new ConfigurationException("Error retrieving source priority information");
        }
    }

    private void setInfo(String configFile) throws ConfigurationException {

        if(configFile == null || configFile.isEmpty()){
            throw new ConfigurationException("Configuration file is not specified");
        }
        ConfigHelper cfg = ConfigHelper.getInstance();
        Map yamlMap = cfg.getConfiguration(configFile);
        assert yamlMap != null : "Could not load configuration file from " +
                "file: ${basedir}/" + configFile;


        try {
            List sourcesList = (List) yamlMap.get("hosts");
            sourceCount = sourcesList.size();
            for (int i = 0; i < sourcesList.size(); i++) {
                String srcHost = (String) ((Map) (sourcesList.get(i))).get("locator");
                sourceLocator.add(i, srcHost);
                int priority = (Integer) ((Map) (sourcesList.get(i))).get("priority");
                if(priority<0 || priority>100){
                    throw new ConfigurationException("net.es.lookup.utils.BootStrapConfigReader: Priority values should be between 0 -100");
                }
                sourcePriority.add(i, priority);

            }

        } catch (Exception e) {
            LOG.error("net.es.lookup.utils: Error parsing config file. Please check config parameters " + e.toString());
            System.exit(1);
        }


    }


}
