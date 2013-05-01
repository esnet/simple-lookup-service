package net.es.lookup.utils;

import net.es.lookup.common.exception.internal.ConfigurationException;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Singleton to store database configuration.
 *
 * @author Sowmya Balasubramanian
 */
public class LookupServiceConfigReader {

    private static LookupServiceConfigReader instance;
    private static final String DEFAULT_FILE = "lookupservice.yaml";
    private static final String DEFAULT_PATH = "etc";
    private static final String DEFAULT_MODE = "master";
    private static String configFile = DEFAULT_PATH + "/" + DEFAULT_FILE;

    private Map<String, Object> lookupServiceMap = new HashMap<String, Object>();
    private String host = "127.0.0.1";
    private int port = 8085;
    private Map<String, Object> leaseTimeMap = new HashMap<String, Object>();
    private int maxlease;
    private int minlease;
    private int defaultlease;


    private String mode = DEFAULT_MODE;

    private List<HashMap<String,Object>> sources;
    private int sourceCount;

    private static final int MINIMUM_INTERVAL = 1800;
    private static final int MINIMUM_THRESHOLD = 0;


    private Map<String, Object> databaseMap = new HashMap<String, Object>();
    private String dburl = "127.0.0.1";
    private int dbport = 27017;
    private String dbname = "LookupService";
    private String collname = "services";
    private int pruneInterval = MINIMUM_INTERVAL;
    private int pruneThreshold = 0;

    private static Logger LOG = Logger.getLogger(ConfigHelper.class);

    /**
     * Constructor - private because this is a Singleton
     */
    private LookupServiceConfigReader() {

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
    public static LookupServiceConfigReader getInstance() {

        if (LookupServiceConfigReader.instance == null) {
            LookupServiceConfigReader.instance = new LookupServiceConfigReader();
            LookupServiceConfigReader.instance.setInfo(configFile);
        }
        return LookupServiceConfigReader.instance;
    }


    public String getHost() {

        return this.host;
    }

    public int getPort() {

        return this.port;
    }

    public long getMaxLease() {

        return this.maxlease;
    }

    public long getDefaultLease() {

        return this.defaultlease;
    }

    public long getMinLease() {

        return this.minlease;
    }

    public String getDbUrl() {

        return this.dburl;
    }

    public int getDbPort() {

        return this.dbport;
    }

    public String getDbName() {

        return this.dbname;
    }

    public String getCollName() {

        return this.collname;
    }

    public int getPruneInterval() {

        return this.pruneInterval;
    }

    public int getPruneThreshold() {

        return this.pruneThreshold;
    }

    public String getMode() {

        return mode;
    }

    public Map<String,Object> getSource(int index) {

        return sources.get(index);
    }

    public int getSourceCount(){
        return sourceCount;
    }

    public String getSourceHost(int index) throws ConfigurationException {

        if(sources != null && !sources.isEmpty() && (index>=0 && index<sourceCount)){
            return (String) sources.get(index).get("host");
        }else{
            throw new ConfigurationException("Error retrieving source host information");
        }

    }

    public int getSourcePort(int index) throws ConfigurationException {
        if(sources != null && !sources.isEmpty() && (index>=0 && index<sourceCount)){
            return (Integer) sources.get(index).get("port");
        }else{
            throw new ConfigurationException("Error retrieving source port information");
        }
    }


    private void setInfo(String configPath) {

        ConfigHelper cfg = ConfigHelper.getInstance();
        Map yamlMap = cfg.getConfiguration(configPath);
        assert yamlMap != null : "Could not load configuration file from " +
                "file: ${basedir}/" + configPath;


        try {
            lookupServiceMap = (HashMap) yamlMap.get("lookupservice");
            host = (String) lookupServiceMap.get("host");
            port = (Integer) lookupServiceMap.get("port");
            mode = (String) lookupServiceMap.get("mode");

            if (lookupServiceMap.containsKey("sources")) {

                sources = (List<HashMap<String,Object>>) lookupServiceMap.get("sources");
                sourceCount = sources.size();

            }


            leaseTimeMap = (HashMap) lookupServiceMap.get("lease");
            maxlease = (Integer) leaseTimeMap.get("max");
            minlease = (Integer) leaseTimeMap.get("min");
            defaultlease = (Integer) leaseTimeMap.get("default");

            databaseMap = (HashMap) yamlMap.get("database");
            dburl = (String) databaseMap.get("DBUrl");
            dbport = (Integer) databaseMap.get("DBPort");
            dbname = (String) databaseMap.get("DBName");
            collname = (String) databaseMap.get("DBCollName");
            pruneThreshold = (Integer) databaseMap.get("pruneThreshold");
            pruneInterval = (Integer) databaseMap.get("pruneInterval");
        } catch (Exception e) {
            LOG.error("Error parsing config file. Please check config parameters " + e.toString());
            System.exit(1);
        }


    }
}