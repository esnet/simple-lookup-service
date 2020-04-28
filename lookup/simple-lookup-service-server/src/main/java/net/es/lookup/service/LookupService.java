package net.es.lookup.service;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.core.UriBuilder;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.http.server.ServerConfiguration;
import org.glassfish.grizzly.nio.transport.TCPNIOTransport;
import org.glassfish.grizzly.nio.transport.TCPNIOTransportBuilder;
import org.glassfish.grizzly.strategies.SameThreadIOStrategy;
import org.glassfish.grizzly.threadpool.ThreadPoolConfig;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * This class contains the method to start the lookup service NOTE: All the resource classes (ie.,
 * classes that contain @Path annotation) need to be explicitly loaded in startServer method
 */
public class LookupService {

  public static String SERVICE_URI_PREFIX = "lookup";
  private int port = 8080;
  private String host = "localhost";

  private String datadirectory = "../elements";
  private HttpServer httpServer = null;
  private static LookupService instance = null;
  public static final String LOOKUP_SERVICE = "lookup";

  private static Logger LOG = LogManager.getLogger(LookupService.class);

  public int getPort() {

    return port;
  }

  public void setPort(int port) {

    this.port = port;
  }

  public String getHost() {

    return host;
  }

  public void setHost(String host) {

    this.host = host;
  }

  public String getDatadirectory() {

    return datadirectory;
  }

  public void setDatadirectory(String datadirectory) {

    this.datadirectory = datadirectory;
  }

  public static LookupService getInstance() {

    return LookupService.instance;
  }

  private synchronized void init() {

    if (LookupService.instance != null) {

      // An instance has been already created.
      throw new RuntimeException("Attempt to create a second instance of LookupService");
    }

    LookupService.instance = this;
  }

  /** Default constructor for LookupService. * */
  public LookupService() {

    // Default port is 8080 and default host is localhost
    this.host = "localhost";
    this.port = 8080;
    init();
  }

  /** Constructor to configure lookup service on localhost and specified port. */
  public LookupService(int port) {

    this.port = port;
    init();
  }

  /** This constructor will configure both host and port. */
  public LookupService(String host, int port) {

    this.host = host;
    this.port = port;
    init();
  }

  /** This method starts the lookup service with the specified list of services. */
  public void startService() {

    LOG.info("Starting HTTP server");
    try {

      this.httpServer = this.startServer();
    } catch (IOException e) {

      LOG.info("IOEXception Failed to start HTTP server: " + e);
    }
  }

  protected HttpServer startServer() throws IOException {

    LOG.info("Creating Resource...");

    final ResourceConfig rc = new ResourceConfig();
    rc.register(net.es.lookup.resources.KeyResource.class);
    rc.register(net.es.lookup.resources.MainResource.class);
    rc.register(net.es.lookup.resources.RecordResource.class);

    LOG.info("Creating Resource..."+rc.getResources().toString());
    LOG.info("Starting grizzly...");
    String hosturl = "http://" + this.host + "/";

    HttpServer server =
        GrizzlyHttpServerFactory.createHttpServer(
            UriBuilder.fromUri(hosturl).port(this.port).build(), rc);

    return server;
  }
}
