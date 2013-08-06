package net.es.lookup.utils;

import net.es.lookup.common.exception.internal.ConfigurationException;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Singleton to store database configuration.
 *
 * @author Sowmya Balasubramanian
 */
public class SubscriberConfigReader {

    private static SubscriberConfigReader instance;
    private static String configFile = "";

    private List<String> sourceHost = new ArrayList<String>();
    private List<Integer> sourcePort = new ArrayList<Integer>();
    private List<List<Map<String, Object>>> queriesList = new ArrayList();

    private int sourceCount;

    private static Logger LOG = Logger.getLogger(ConfigHelper.class);

    /**
     * Constructor - private because this is a Singleton
     */
    private SubscriberConfigReader() {

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
    public static SubscriberConfigReader getInstance() throws ConfigurationException {

        if (SubscriberConfigReader.instance == null) {
            SubscriberConfigReader.instance = new SubscriberConfigReader();
            SubscriberConfigReader.instance.setInfo(configFile);
        }
        return SubscriberConfigReader.instance;
    }


    public int getSourceCount() {

        return sourceCount;
    }

    public String getSourceHost(int index) throws ConfigurationException {

        if (sourceHost != null && !sourceHost.isEmpty() && (index >= 0 && index < sourceCount)) {
            return sourceHost.get(0);
        } else {
            throw new ConfigurationException("Error retrieving source port information");
        }
    }

    public int getSourcePort(int index) throws ConfigurationException {

        if (sourcePort != null && !sourcePort.isEmpty() && (index >= 0 && index < sourceCount)) {
            return sourcePort.get(0);
        } else {
            throw new ConfigurationException("Error retrieving source port information");
        }
    }

    public List<Map<String, Object>> getQueries(int index) throws ConfigurationException {

        if (queriesList != null && !queriesList.isEmpty() && (index >= 0 && index < sourceCount)) {
            return queriesList.get(index);
        } else {
            throw new ConfigurationException("Error retrieving source port information");
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
            List sourcesList = (List) yamlMap.get("sources");
            sourceCount = sourcesList.size();
            for (int i = 0; i < sourcesList.size(); i++) {
                String srcHost = (String) ((Map) (sourcesList.get(i))).get("host");
                sourceHost.add(i, srcHost);
                int port = (Integer) ((Map) (sourcesList.get(i))).get("port");
                sourcePort.add(i, port);
                List<Map<String,Object>> queryList = (List) ((Map) (sourcesList.get(i))).get("queries");

                queriesList.add(i, queryList);
            }

        } catch (Exception e) {
            LOG.error("Error parsing config file: subscriber.yaml. Please check config parameters " + e.toString());
            System.exit(1);
        }


    }


}