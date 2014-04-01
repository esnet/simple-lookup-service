package net.es.lookup.utils.config.reader;

import org.apache.log4j.Logger;

import java.util.HashMap;
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
    private static String configFile = DEFAULT_PATH + "/" + DEFAULT_FILE;
    private static final int MINIMUM_INTERVAL = 1800;
    private static final int MINIMUM_THRESHOLD = 0;

    //Lookup service fields
    private String host = "127.0.0.1";
    private int port = 8085;

    //Lookup service Lease fields
    private int maxlease;
    private int minlease;
    private int defaultlease;


    //core service
    private boolean coreservice = false;

    //cache service

    private boolean cacheservice=false;


    //database
    private String dburl = "127.0.0.1";
    private int dbport = 27017;
    private String dbname = "LookupService";
    private String collname = "services";
    private int pruneInterval = MINIMUM_INTERVAL;
    private int pruneThreshold = MINIMUM_THRESHOLD;

    private static Logger LOG = Logger.getLogger(BaseConfigReader.class);

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


    public boolean isCoreserviceOn() {

        return coreservice;
    }

    public boolean isCacheserviceOn() {

        return cacheservice;
    }

    private void setInfo(String configPath) {

        BaseConfigReader cfg = BaseConfigReader.getInstance();
        Map yamlMap = cfg.getConfiguration(configPath);
        assert yamlMap != null : "Could not load configuration file from " +
                "file: ${basedir}/" + configPath;


        try {
            HashMap<String, Object> lookupServiceMap = (HashMap) yamlMap.get("lookupservice");
            host = (String) lookupServiceMap.get("host");
            port = (Integer) lookupServiceMap.get("port");

            String core = (String) lookupServiceMap.get("coreservice");

            if(core != null && core.equalsIgnoreCase("on")){
                coreservice = true;
            }else{
                coreservice = false;
            }

            String cs = (String) lookupServiceMap.get("cacheservice");

            if(cs != null && cs.equalsIgnoreCase("on")){
                cacheservice = true;
            }else{
                cacheservice = false;
            }

            HashMap<String, Object> leaseTimeMap = (HashMap) lookupServiceMap.get("lease");
            maxlease = (Integer) leaseTimeMap.get("max");
            minlease = (Integer) leaseTimeMap.get("min");
            defaultlease = (Integer) leaseTimeMap.get("default");

            HashMap<String, Object> databaseMap = (HashMap) yamlMap.get("database");
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