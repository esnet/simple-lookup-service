package net.es.lookup.utils.config.reader;

import net.es.lookup.common.exception.internal.ConfigurationException;
import org.apache.log4j.Logger;

import java.net.URI;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Singleton to store database configuration.
 *
 * @author Sowmya Balasubramanian
 */
public class SubscriberConfigReader {

    private static final String PRIMARY_KEY = "caches";
    private static final String CACHE_NAME = "name";
    private static final String CACHE_TYPE = "type";
    private static final String CACHE_SOURCE = "publishers";
    private static final String SOURCE_ACCESSPOINT = "locator";
    private static final String SOURCE_QUERIES = "queries";
    private static final String WILDCARD = "*";
    private static final String BOOTSTRAPSERVER = "bootstrapserver";
    private static final String RECONNECTINTERVAL = "reconnectInterval";


    private static SubscriberConfigReader instance;
    private static String configFile = "";


    private int reconnectInterval;


    private static Logger LOG = Logger.getLogger(BaseConfigReader.class);

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

    /**
     * @return Returns the list of cache names
     */

    private void setInfo(String configFile) throws ConfigurationException {

        if (configFile == null || configFile.isEmpty()) {
            throw new ConfigurationException("Configuration file is not specified");
        }
        BaseConfigReader cfg = BaseConfigReader.getInstance();
        Map yamlMap = cfg.getConfiguration(configFile);
        if(yamlMap == null || yamlMap.isEmpty()){
            LOG.info( "Could not load configuration file from " +
                    "file: ${basedir}/" + configFile);
            return;
        }


        try {
            List cList = (List) yamlMap.get(PRIMARY_KEY);
            if (cList == null || cList.isEmpty()){
                return;
            }
            String bclientlocator = (String)yamlMap.get(BOOTSTRAPSERVER);
            URI bclientURI = new URI(bclientlocator);

            reconnectInterval = (Integer)yamlMap.get(RECONNECTINTERVAL);

            for (int i = 0; i < cList.size(); i++) {

                String cacheName = (String) ((Map) (cList.get(i))).get(CACHE_NAME);

                String cType = (String) ((Map) (cList.get(i))).get(CACHE_TYPE);


                List sList = (List) ((Map) (cList.get(i))).get(CACHE_SOURCE);


                if (sList.isEmpty()) {
                    throw new ConfigurationException("Missing source information");
                }


                for (int j = 0; j < sList.size(); j++) {

                    List<Map<String,Object>> queryList = new LinkedList<Map<String,Object>>();

                    String sAccessPoint = (String) ((Map) (sList.get(j))).get(SOURCE_ACCESSPOINT);


                    List<Object> cQueryList = (List) ((Map) (sList.get(j))).get(SOURCE_QUERIES);

                    for (int k = 0; k < cQueryList.size(); k++) {
                        Object tmp = cQueryList.get(k);

                        if (tmp instanceof Map) {
                            queryList.add((Map)tmp);
                        } else if (tmp instanceof String && tmp.equals(WILDCARD)) {
                            Map<String,Object> query = new HashMap<String,Object>();
                            queryList.add(query);

                        }

                    }

                    if (sAccessPoint.equals(WILDCARD)) {
                        //get bootstrap list
                        ///add to subscribe list

                    } else {

                    }


                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("Error parsing config file: subscriber.yaml. Please check config parameters " + e.toString());
            System.exit(1);
        }


    }


    public int getReconnectInterval() {

        return reconnectInterval;
    }
}