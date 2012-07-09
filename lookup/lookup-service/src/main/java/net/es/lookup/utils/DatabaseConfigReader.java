package net.es.lookup.utils;

import java.util.Map;
import java.util.HashMap;
/**
 * Singleton to store database configuration.
 * @author Sowmya Balasubramanian
 */
public class DatabaseConfigReader {

    private static DatabaseConfigReader instance;
    private static final String DEFAULT_FILE = "database.yaml";
    private static final String DEFAULT_PATH = "config";

    Map<String,String> databaseMap = new HashMap<String,String>();
    private String dburl = "127.0.0.1";
    private int dbport = 27017;
    private String dbname = "LookupService";
    private String collname = "services";

    /**
     * Constructor - private because this is a Singleton
     */
    private DatabaseConfigReader() {
        
    }

    /**
     * @return the initialized DatabaseConfigReader singleton instance
     */
    public static DatabaseConfigReader getInstance() {
        if (DatabaseConfigReader.instance == null) {
            DatabaseConfigReader.instance = new DatabaseConfigReader();
            DatabaseConfigReader.instance.setInfo(DEFAULT_PATH,DEFAULT_FILE);
        }
        return DatabaseConfigReader.instance;
    }

    public String getDburl() {
        return this.dburl;
    }

    public int getDbport() {
        return this.dbport;
    }

    public String getDbname() {
        return this.dbname;
    }
    
    public String getCollname() {
        return this.collname;
    }
    
    private void setInfo(String path, String fname) {
        ConfigHelper cfg = ConfigHelper.getInstance();
        Map yamlMap = cfg.getConfiguration(path,fname);
        assert yamlMap != null:  "Could not load configuration file from " +
            "file: ${basedir}/"+path + fname;
        this.databaseMap = (HashMap)yamlMap.get("database");
        this.dburl = (String) this.databaseMap.get("dburl");
        System.out.println((String)this.databaseMap.get("dbport"));
        this.dbport = Integer.parseInt((String)this.databaseMap.get("dbport"));
        this.dbname = (String) this.databaseMap.get("dbname");
        this.collname = (String) this.databaseMap.get("collname");
     
    }
}
