package net.es.lookup.utils.config.reader;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

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

  // Lookup service fields
  private String host = "127.0.0.1";
  private int port = 8085;

  // Lookup service Lease fields
  private int maxlease;
  private int minlease;
  private int defaultlease;

  // database
  private String dburl = "127.0.0.1";
  private int dbport = 27017;
  private String dbname = "LookupService";
  private String collname = "services";
  private int pruneInterval = MINIMUM_INTERVAL;
  private int pruneThreshold = MINIMUM_THRESHOLD;

  private static Logger LOG = LogManager.getLogger(BaseConfigReader.class);
  private String elasticServer;
  private int elasticPort1;
  private int elasticPort2;
  private String elasticDbName;

  /** Constructor - private because this is a Singleton. */
  private LookupServiceConfigReader() {}

  /*
   * set the config file
   * */

  public static void init(String cfile) {

    configFile = cfile;
  }

  /**
   * This method returns the Singleton instance of this class.
   *
   * @return LookupServiceConfigReader
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

  public String getElasticServer() {
    return elasticServer;
  }

  public void setElasticServer(String elasticServer) {
    this.elasticServer = elasticServer;
  }

  public int getElasticPort1() {
    return elasticPort1;
  }

  public void setElasticPort1(int elasticPort1) {
    this.elasticPort1 = elasticPort1;
  }

  public int getElasticPort2() {
    return elasticPort2;
  }

  public void setElasticPort2(int elasticPort2) {
    this.elasticPort2 = elasticPort2;
  }

  public String getElasticDbName() {
    return elasticDbName;
  }

  public void setElasticDbName(String elasticDbName) {
    this.elasticDbName = elasticDbName;
  }

  private void setInfo(String configPath) {

    BaseConfigReader cfg = BaseConfigReader.getInstance();
    Map yamlMap = cfg.getConfiguration(configPath);
    assert yamlMap != null
        : "Could not load configuration file from " + "file: ${basedir}/" + configPath;

    try {
      HashMap<String, Object> lookupServiceMap = (HashMap) yamlMap.get("lookupservice");
      host = (String) lookupServiceMap.get("host");
      port = (Integer) lookupServiceMap.get("port");

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

      Map<String, Object> elasticMap = (HashMap) yamlMap.get("elastic");
      elasticServer = (String) elasticMap.get("DBUrl");
      elasticPort1 = (Integer) elasticMap.get("DBPort1");
      elasticPort2 = (Integer) elasticMap.get("DBPort2");
      elasticDbName = (String) elasticMap.get("ElasticDBName");

    } catch (Exception e) {
      LOG.error("Error parsing config file. Please check config parameters " + e.toString());
      System.exit(1);
    }
  }
}
