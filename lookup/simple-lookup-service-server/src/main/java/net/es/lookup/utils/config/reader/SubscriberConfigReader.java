package net.es.lookup.utils.config.reader;

import net.es.lookup.bootstrap.BootStrapClient;
import net.es.lookup.common.exception.internal.ConfigurationException;
import net.es.lookup.queries.Query;
import net.es.lookup.utils.config.data.Cache;
import net.es.lookup.utils.config.data.SubscriberSource;
import org.apache.log4j.Logger;

import java.net.URI;
import java.util.*;

/**
 * Singleton to store database configuration.
 *
 * @author Sowmya Balasubramanian
 */
public class SubscriberConfigReader {

    private static final String PRIMARY_KEY = "caches";
    private static final String CACHE_NAME = "name";
    private static final String CACHE_TYPE = "type";
    private static final String CACHE_SOURCE = "sources";
    private static final String SOURCE_ACCESSPOINT = "locator";
    private static final String SOURCE_QUERIES = "queries";
    private static final String WILDCARD = "*";
    private static final String BOOTSTRAPSERVER = "bootstrapserver";


    private static SubscriberConfigReader instance;
    private static String configFile = "";

    private List<Cache> cacheList;


    private static Logger LOG = Logger.getLogger(BaseConfigReader.class);

    /**
     * Constructor - private because this is a Singleton
     */
    private SubscriberConfigReader() {

        cacheList = new LinkedList<Cache>();


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
    public List<Cache> getCacheList() {

        return cacheList;
    }

    public int getCacheCount(){

        return cacheList.size();
    }

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

            for (int i = 0; i < cList.size(); i++) {

                String cacheName = (String) ((Map) (cList.get(i))).get(CACHE_NAME);

                String cType = (String) ((Map) (cList.get(i))).get(CACHE_TYPE);


                List<SubscriberSource> sourceList = new LinkedList<SubscriberSource>();

                List sList = (List) ((Map) (cList.get(i))).get(CACHE_SOURCE);

                if (sList.isEmpty()) {
                    throw new ConfigurationException("Missing source information");
                }

                for (int j = 0; j < sList.size(); j++) {
                    List<Query> queryList = new LinkedList<Query>();

                    String accesspoint = (String) ((Map) (sList.get(i))).get(SOURCE_ACCESSPOINT);
                    System.out.println(accesspoint);
                    List<Object> cQueryList = (List) ((Map) (sList.get(i))).get(SOURCE_QUERIES);

                    for (int k = 0; k < cQueryList.size(); k++) {
                        Object tmp = cQueryList.get(k);

                        if (tmp instanceof Map) {
                            Query query = new Query((Map) tmp);
                            System.out.println(tmp.toString());
                            queryList.add(query);
                        } else if (tmp instanceof String && tmp.equals(WILDCARD)) {
                            Query query = new Query();
                            queryList.add(query);

                        }


                    }

                    if (accesspoint.equals(WILDCARD)) {
                        //get bootstrap list
                        BootStrapClient client = new BootStrapClient(bclientURI);
                        List<String> lsList = client.getAllUrls();
                        for(String s: lsList){
                            SubscriberSource source = new SubscriberSource(s, queryList);
                            sourceList.add(source);
                        }
                    } else {

                        SubscriberSource source = new SubscriberSource(accesspoint, queryList);
                        sourceList.add(source);

                    }


                }

                Cache cache = new Cache(cacheName, cType, sourceList);
                cacheList.add(cache);
            }

        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("Error parsing config file: subscriber.yaml. Please check config parameters " + e.toString());
            System.exit(1);
        }


    }


}