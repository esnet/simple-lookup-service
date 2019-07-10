package net.es.lookup.database;

import net.es.lookup.utils.config.reader.LookupServiceConfigReader;

import java.net.URISyntaxException;

/**
 * Class to create a connection to the database
 */
public class connectDB {

  private static boolean initialized = false;
  private static String server;
  private static int port1;
  private static int port2;
  private static String dbName;

  /**
   * Reads the connection for database configurations
   * Does nothing if file has already been read
   */
  public connectDB() {
    if (!initialized) {
      LookupServiceConfigReader.init("etc/lookupservice.yaml");
      LookupServiceConfigReader config = LookupServiceConfigReader.getInstance();

      server = config.getElasticServer();
      port1 = config.getElasticServerPort();
      port2 = config.getElasticRestClientPort();
      dbName = config.getElasticDbName();
      initialized = true;
    }
  }

  /**
   * Creates a new connection to the database
   * @return Client for the database
   * @throws URISyntaxException In case URI of database doesn't work
   */
  public ServiceElasticSearch connect() throws URISyntaxException {
    return new ServiceElasticSearch(server, port1, port2, dbName);
  }
}
