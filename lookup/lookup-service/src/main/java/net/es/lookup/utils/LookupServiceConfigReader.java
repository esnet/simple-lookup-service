package net.es.lookup.utils;

import java.util.Map;
import java.util.HashMap;

import org.apache.log4j.Logger;
/**
 * Singleton to store database configuration.
 * @author Sowmya Balasubramanian
 */
public class LookupServiceConfigReader {

    private static LookupServiceConfigReader instance;
    private static final String DEFAULT_FILE = "lookupservice.yaml";
    private static final String DEFAULT_PATH = "etc";
    private static String configFile = DEFAULT_PATH+"/"+DEFAULT_FILE;

    Map<String,Object> lookupServiceMap = new HashMap<String,Object>();
    private String host = "127.0.0.1";
    private int port = 8085;
    private int maxleasetime = 7200;
    
    private static final int MINIMUM_INTERVAL = 1800;
    private static final int MINIMUM_THRESHOLD = 0;
    

    Map<String,Object> databaseMap = new HashMap<String,Object>();
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
    
    public static void init(String cFile){
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

    public long getMaxleasetime() {
        return this.maxleasetime;
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
    
    private void setInfo(String configPath) {
        ConfigHelper cfg = ConfigHelper.getInstance();
        Map yamlMap = cfg.getConfiguration(configPath);
        assert yamlMap != null:  "Could not load configuration file from " +
            "file: ${basedir}/"+configPath;
        
        try{
        	this.lookupServiceMap = (HashMap)yamlMap.get("lookupservice");
            this.host = (String) this.lookupServiceMap.get("host");
            this.port = (Integer)this.lookupServiceMap.get("port");
            this.maxleasetime = (Integer)this.lookupServiceMap.get("maxleasetime");
            
            this.databaseMap = (HashMap)yamlMap.get("database");
            this.dburl = (String) this.databaseMap.get("DBUrl");
            this.dbport = (Integer)this.databaseMap.get("DBPort");
            this.dbname = (String) this.databaseMap.get("DBName");
            this.collname = (String) this.databaseMap.get("DBCollName");
            this.pruneThreshold = (Integer)this.databaseMap.get("pruneThreshold");
            this.pruneInterval = (Integer)this.databaseMap.get("pruneInterval");
        }catch(Exception e){
        	LOG.error("Error parsing config file; Please check config parameters");
        	System.exit(1);
        }
        
     
    }
}