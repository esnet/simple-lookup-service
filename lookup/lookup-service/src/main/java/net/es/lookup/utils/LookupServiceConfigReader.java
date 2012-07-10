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
    private static final String DEFAULT_PATH = "config";

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
            LookupServiceConfigReader.instance.setInfo(DEFAULT_PATH,DEFAULT_FILE);
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
    
    private void setInfo(String path, String fname) {
        ConfigHelper cfg = ConfigHelper.getInstance();
        Map yamlMap = cfg.getConfiguration(path,fname);
        assert yamlMap != null:  "Could not load configuration file from " +
            "file: ${basedir}/"+path + fname;
        this.lookupServiceMap = (HashMap)yamlMap.get("lookupservice");
        this.host = (String) this.lookupServiceMap.get("host");
        System.out.println((String)this.lookupServiceMap.get("host"));
        this.port = Integer.parseInt((String)this.lookupServiceMap.get("port"));
        this.maxleasetime = Long.parseLong((String)this.lookupServiceMap.get("maxleasetime"));
     
    }
}