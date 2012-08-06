package net.es.lookup.utils;

import java.util.Map;
import java.util.HashMap;
/**
 * Singleton to store database configuration.
 * @author Sowmya Balasubramanian
 */
public class LookupServiceConfigReader {

    private static LookupServiceConfigReader instance;
    private static final String DEFAULT_FILE = "lookupservice.yaml";
    private static final String DEFAULT_PATH = "etc";

    Map<String,Object> lookupServiceMap = new HashMap<String,Object>();
    private String host = "127.0.0.1";
    private int port = 8085;
    private long maxleasetime = 7200;

    /**
     * Constructor - private because this is a Singleton
     */
    private LookupServiceConfigReader() {
        
    }

    /**
     * @return the initialized LookupServiceConfigReader singleton instance
     */
    public static LookupServiceConfigReader getInstance() {
        if (LookupServiceConfigReader.instance == null) {
            LookupServiceConfigReader.instance = new LookupServiceConfigReader();
            String configPath = DEFAULT_PATH+"/"+DEFAULT_FILE;
            LookupServiceConfigReader.instance.setInfo(configPath);
        }
        return LookupServiceConfigReader.instance;
    }
    
    public static LookupServiceConfigReader getInstance(String configPath) {
        if (LookupServiceConfigReader.instance == null) {
            LookupServiceConfigReader.instance = new LookupServiceConfigReader();
            LookupServiceConfigReader.instance.setInfo(configPath);
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
    
    private void setInfo(String configPath) {
        ConfigHelper cfg = ConfigHelper.getInstance();
        Map yamlMap = cfg.getConfiguration(configPath);
        assert yamlMap != null:  "Could not load configuration file from " +
            "file: ${basedir}/"+configPath;
        this.lookupServiceMap = (HashMap)yamlMap.get("lookupservice");
        this.host = (String) this.lookupServiceMap.get("host");
        System.out.println((String)this.lookupServiceMap.get("host"));
        this.port = Integer.parseInt((String)this.lookupServiceMap.get("port"));
        this.maxleasetime = Long.parseLong((String)this.lookupServiceMap.get("maxleasetime"));
     
    }
}