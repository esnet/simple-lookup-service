package net.es.lookup.database;

import net.es.lookup.utils.config.reader.LookupServiceConfigReader;

import java.net.URISyntaxException;

public class connectDB {

  private static boolean initialized = false;
  private static String server;
  private static int port1;
  private static int port2;
  private static String dbName;

  public static ServiceElasticSearch connect() throws URISyntaxException {
    if (!initialized) {
      LookupServiceConfigReader.init("etc/lookupservice.yaml");
      LookupServiceConfigReader config = LookupServiceConfigReader.getInstance();

      server = config.getElasticServer();
      port1 = config.getElasticPort1();
      port2 = config.getElasticPort2();
      dbName = config.getElasticDbName();
      initialized = true;
    }
    return new ServiceElasticSearch(server, port1, port2, dbName);
  }
}
