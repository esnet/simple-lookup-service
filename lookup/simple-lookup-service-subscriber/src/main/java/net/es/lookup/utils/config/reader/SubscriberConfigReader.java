package net.es.lookup.utils.config.reader;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Author: sowmya
 * Date: 4/12/16
 * Time: 3:58 PM
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
    public static final String QUEUE_HOST = "host";
    public static final String QUEUE_PORT = "port";
    public static final String QUERIES = "queries";
    public static final String EXCHANGE_NAME = "exchange";

    public static final String DESTINATION = "destination";
    public static final String DESTINATION_TYPE = "type";
    public static final String DESTINATION_URL = "url";



    //Subscriber fields

    private String userName;
    private String password;
    private String vhost;
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
     * @return the initialized LookupServiceConfigReader singleton instance
     */
    public static SubscriberConfigReader getInstance() {

        if (SubscriberConfigReader.instance == null) {
            SubscriberConfigReader.instance = new SubscriberConfigReader();
            SubscriberConfigReader.instance.setInfo(configFile);
        }
        return SubscriberConfigReader.instance;
    }

    public List<Map> getQueues(){
        return queues;
    }

    public List<Map> getDestination() {

        return destination;
    }

    private void setInfo(String configPath) {

        BaseConfigReader cfg = BaseConfigReader.getInstance();
        Map yamlMap = cfg.getConfiguration(configPath);
        assert yamlMap != null : "Could not load configuration file from " +
                "file: ${basedir}/" + configPath;


        try {
            queues = (List<Map>) yamlMap.get(QUEUE);
            destination = (List) yamlMap.get(DESTINATION);



        } catch (Exception e) {
            LOG.error("Error parsing config file. Please check config parameters " + e.toString());
            System.exit(1);
        }


    }

    public int getThreadPool() {

        return threadPool;
    }
}
