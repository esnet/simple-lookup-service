package net.es.lookup.utils.config.reader;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Author: sowmya
 * Date: 4/12/16
 * Time: 3:58 PM
 *
 * This class reads the subscriber config file and returns the
 * various parameters
 */
public class SubscriberConfigReader {

    private static SubscriberConfigReader instance;
    private static final String DEFAULT_FILE = "subscriber.yaml";
    private static final String DEFAULT_PATH = "etc";
    private static String configFile = DEFAULT_PATH + "/" + DEFAULT_FILE;

    //subscriber.yml keys
    public static final String USERNAME ="username";
    public static final String PASSWORD  = "password";
    public static final String VHOST  = "vhost";
    public static final String QUEUE  = "queue";
    public static final String QUEUE_NAME  = "name";
    public static final String QUEUE_HOST = "host";
    public static final String QUEUE_PORT = "port";
    public static final String QUERIES = "queries";
    public static final String EXCHANGE_NAME = "exchange_name";
    public static final String EXCHANGE_TYPE = "exchange_type";
    public static final String QUEUE_DURABILITY = "queue_durability";
    public static final String QUEUE_EXCLUSIVE = "queue_exclusive";
    public static final String QUEUE_AUTODELETE = "queue_autodelete";
    public static final String THREAD_POOL = "thread_pool";

    public static final String DESTINATION = "destination";
    public static final String DESTINATION_TYPE = "type";
    public static final String DESTINATION_URL = "url";
    public static final String DESTINATION_ELASTIC_WRITEINDEX = "write_index";
    public static final String DESTINATION_ELASTIC_SEARCHINDEX = "search_index";
    public static final String DESTINATION_ELASTIC_DOCUMENTTYPE = "document_type";


    //subscriber fields
    private List<Map> queues;
    private List<Map> destination;

    private static Logger LOG = Logger.getLogger(BaseConfigReader.class);
    private int threadPool;

    /**
     * Constructor - private because this is a Singleton
     */
    private SubscriberConfigReader() {
        queues = new ArrayList<Map>();
    }

    /*
   * set the config file
   * */

    public static void init(String cFile) {

        configFile = cFile;
    }

    /**
     * @return the initialized SubscriberConfigReader singleton instance
     */
    public static SubscriberConfigReader getInstance() {

        if (SubscriberConfigReader.instance == null) {
            SubscriberConfigReader.instance = new SubscriberConfigReader();
            SubscriberConfigReader.instance.setInfo(configFile);
        }
        return SubscriberConfigReader.instance;
    }

    /**
     * Returns the list of queues specified in the config file
     * @return List<Map> List of queues config specified as a map
     * */
    public List<Map> getQueues(){
        return queues;
    }

    /**
     * Returns the list of destinations specified in the config file
     * @return List<Map> A list of destination config expressed as a Map
     * */
    public List<Map> getDestination() {

        return destination;
    }

    /**
     * This method reads the config file and populates the config file parameters
     * */
    private void setInfo(String configPath) {

        BaseConfigReader cfg = BaseConfigReader.getInstance();
        Map yamlMap = cfg.getConfiguration(configPath);
        assert yamlMap != null : "Could not load configuration file from " +
                "file: ${basedir}/" + configPath;


        try {
            queues = (List<Map>) yamlMap.get(QUEUE);
            destination = (List) yamlMap.get(DESTINATION);
            threadPool = (Integer) yamlMap.get(THREAD_POOL);



        } catch (Exception e) {
            LOG.error("Error parsing config file. Please check config parameters " + e.toString());
            System.exit(1);
        }


    }

    /**
     * This method returns the specified thread pool size
     * */
    public int getThreadPool() {
        return threadPool;
    }
}
