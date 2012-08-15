package net.es.lookup.utils;

import java.util.Map;
import java.util.HashMap;
/**
 * Singleton to store database configuration.
 * @author Sowmya Balasubramanian
 */
public class DatabaseConfigReader {

    private static DatabaseConfigReader instance;
    private static final String DEFAULT_FILE = "lookupservice.yaml";
    private static final String DEFAULT_PATH = "etc";
    private static String configFile = DEFAULT_PATH+"/"+DEFAULT_FILE;

    Map<String,String> databaseMap = new HashMap<String,String>();
    private String dburl = "127.0.0.1";
    private int dbport = 27017;
    private String dbname = "LookupService";
    private String collname = "services";
    
    private boolean archive=false;
    Map<String,String> archiveDatabaseMap = new HashMap<String,String>();
    private String archiveDBUrl = "";
    private int archiveDBPort = 0;
    private String archiveDBName = "";
    private String archiveDBCollName = "";

    /**
     * Constructor - private because this is a Singleton
     */
    private DatabaseConfigReader() {
        
    }
    
    /**
     * set config file
     * */
    public static void init(String cFile){
    	configFile = cFile;
    }

    /**
     * @return the initialized DatabaseConfigReader singleton instance
     */
    public static DatabaseConfigReader getInstance() {
        if (DatabaseConfigReader.instance == null) {
            DatabaseConfigReader.instance = new DatabaseConfigReader();
            DatabaseConfigReader.instance.setInfo(configFile);
        }
        return DatabaseConfigReader.instance;
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
    
    public boolean getArchive() {
        return this.archive;
    }
    
    public String getArchiveDbUrl() {
        return this.archiveDBUrl;
    }

    public int getArchiveDbPort() {
        return this.archiveDBPort;
    }

    public String getArchiveDbName() {
        return this.archiveDBName;
    }
    
    public String getArchiveDbCollName() {
        return this.archiveDBCollName;
    }
    
    private void setInfo(String configPath) {
        ConfigHelper cfg = ConfigHelper.getInstance();
        Map yamlMap = cfg.getConfiguration(configPath);
        assert yamlMap != null:  "Could not load configuration file from " +
            "file: {$basedir}/"+configPath;
        this.databaseMap = (HashMap)yamlMap.get("database");
        this.dburl = (String) this.databaseMap.get("DBUrl");
        System.out.println((String)this.databaseMap.get("DBPort"));
        this.dbport = Integer.parseInt((String)this.databaseMap.get("DBPort"));
        this.dbname = (String) this.databaseMap.get("DBName");
        this.collname = (String) this.databaseMap.get("DBCollName");
        
        this.archive = (Boolean)yamlMap.get("archive");
        
        if(this.archive){
        	this.databaseMap = (HashMap)yamlMap.get("archiveDatabase");
        	this.archiveDBUrl = (String) this.databaseMap.get("archiveDBUrl");
            System.out.println((String)this.databaseMap.get("archiveDBPort"));
            this.archiveDBPort = Integer.parseInt((String)this.databaseMap.get("archiveDBPort"));
            this.archiveDBName = (String) this.databaseMap.get("archiveDBName");
            this.archiveDBCollName = (String) this.databaseMap.get("archiveDBCollName");
        }
     
    }
}
