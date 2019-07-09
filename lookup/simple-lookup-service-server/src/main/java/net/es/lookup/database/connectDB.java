package net.es.lookup.database;

import net.es.lookup.utils.config.reader.LookupServiceConfigReader;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;

public class connectDB {

    public static ServiceElasticSearch connect() throws FileNotFoundException, URISyntaxException {
        LookupServiceConfigReader.init("etc/lookupservice.yaml");
        LookupServiceConfigReader config = LookupServiceConfigReader.getInstance();

        String server = config.getElasticServer();
        int port1 = config.getElasticPort1();
        int port2 = config.getElasticPort2();
        String dbName = config.getElasticDbName();

        return new ServiceElasticSearch(server, port1, port2, dbName);
    }
}
