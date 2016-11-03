package net.es.lookup.utils.config.reader;

import org.apache.log4j.Logger;

import java.util.Map;

/**
 * Author: sowmya
 * Date: 3/26/13
 * Time: 11:14 AM
 */
public class QueueServiceConfigReader {

    private static QueueServiceConfigReader instance;
    private static final String DEFAULT_FILE = "queueservice.yaml";
    private static final String DEFAULT_PATH = "etc";
    private static String configFile = DEFAULT_PATH + "/" + DEFAULT_FILE;
    public static final String USERNAME ="username";
    public static final String PASSWORD  = "password";
    public static final String VHOST  = "vhost";
    public static final String EXCHANGE_NAME  = "exchange_name";
    public static final String EXCHANGE_TYPE = "exchange_type";
    public static final String EXCHANGE_DURABILITY = "durable";


    private int port = 5672;
    private String host;
    private String protocol = "tcp";

    private boolean serviceOn = false;

    private boolean persistent = false;

    private long ttl = 120000;

    private int batchSize=10;
    private int pushInterval = 120;
    private int pollingInterval = 10;

    private String userName;
    private String password;
    private String vhost;

    private String exchangeName;
    private String exchangeType;
    private boolean exchangeDurability;

    private static Logger LOG = Logger.getLogger(BaseConfigReader.class);
    

    /**
     * Constructor - private because this is a Singleton
     */
    private QueueServiceConfigReader() {

        host = "";

    }

    /*
   * set the config file
   * */

    public static void init(String cFile) {

        configFile = cFile;
    }

    /**
     * @return the initialized QueueServiceConfigReader singleton instance
     */
    public static QueueServiceConfigReader getInstance() {

        if (QueueServiceConfigReader.instance == null) {
            QueueServiceConfigReader.instance = new QueueServiceConfigReader();
            QueueServiceConfigReader.instance.setInfo(configFile);
        }
        return QueueServiceConfigReader.instance;
    }

    public String getHost() {

        return this.host;
    }

    public int getPort() {

        return this.port;
    }

    public String getProtocol() {

        return protocol;
    }

    public long getTtl() {

        return ttl;
    }


    public boolean isQueuePersistent() {

        return persistent;
    }

    public int getBatchSize() {

        return batchSize;
    }

    public int getPushInterval() {

        return pushInterval;
    }

    public String getUserName() {

        return userName;
    }

    public String getPassword() {

        return password;
    }

    public String getVhost() {

        return vhost;
    }

    public int getPollingInterval() {

        return pollingInterval;
    }

    public String getExchangeName() {

        return exchangeName;
    }

    public String getExchangeType() {

        return exchangeType;
    }

    public boolean getExchangeDurability() {

        return exchangeDurability;
    }

    private void setInfo(String configFile) {

        BaseConfigReader cfg = BaseConfigReader.getInstance();
        Map yamlMap = cfg.getConfiguration(configFile);
        assert yamlMap != null : "Could not load configuration file from " +
                "file: ${basedir}/" + configFile;


        try {
            userName = (String) yamlMap.get(USERNAME);
            password = (String) yamlMap.get(PASSWORD);
            vhost = (String) yamlMap.get(VHOST);
            host = (String) yamlMap.get("host");
            String service = (String) yamlMap.get("queueservice");
            if(service.equals("on")){
                serviceOn = true;
            }else{
                serviceOn=false;
            }
            batchSize = (Integer) yamlMap.get("batch_size");
            pushInterval = (Integer) yamlMap.get("push_interval");
            pollingInterval = (Integer) yamlMap.get("polling_interval");

            exchangeName = (String) yamlMap.get(EXCHANGE_NAME);
            exchangeType = (String) yamlMap.get(EXCHANGE_TYPE);
            exchangeDurability = (Boolean) yamlMap.get(EXCHANGE_DURABILITY);
     } catch (Exception e) {
            LOG.error("Error parsing config file; Please check config parameters" + e.toString());
            System.exit(1);
        }


    }

    public boolean isServiceOn() {

        return serviceOn;
    }


}
