package net.es.lookup.service;

import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.ClassNamesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.ws.rs.core.UriBuilder;
import net.es.lookup.resources.KeyResource;
import net.es.lookup.resources.RecordResource;
import net.es.lookup.resources.RegisterQueryResource;
import org.apache.log4j.Logger;
import org.glassfish.grizzly.http.server.HttpServer;



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
  private static final int MAX_SERVICES = 10;
  public static final String LOOKUP_SERVICE = "lookup";

  private static Logger LOG = Logger.getLogger(LookupService.class);

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

  /**
   * Default constructor for LookupService.
   * **/
  public LookupService() {

    // Default port is 8080 and default host is localhost
    this.host = "localhost";
    this.port = 8080;
    init();
  }


  /**
   * Constructor to configure lookup service on localhost and specified port.
   * */
  public LookupService(int port) {

    this.port = port;
    init();
  }

  /**
   * This constructor will configure both host and port.
   * */
  public LookupService(String host, int port) {

    this.host = host;
    this.port = port;
    init();
  }


  /**
   * This method starts the lookup service with the specified list of services.
   * */
  public void startService(List<String> services) {

    List<String> resources = new LinkedList<String>();
    if (services.size() == 0 || services.size() > MAX_SERVICES) {
      LOG.info("Too many or too little services");
      System.exit(0);

    } else {

      resources.add(RecordResource.class.getName());
      resources.add(KeyResource.class.getName());
      resources.add(RegisterQueryResource.class.getName());
    }

    Object[] resourcesObject = resources.toArray();
    String[] resourcesAsString = new String[resourcesObject.length];
    for (int i = 0; i < resourcesAsString.length; i++) {
      resourcesAsString[i] = (String) resourcesObject[i];
    }

    LOG.info("Starting HTTP server");
    try {

      this.httpServer = this.startServer(resourcesAsString);

    } catch (IOException e) {

      LOG.info("IOEXception Failed to start HTTP server: " + e);
    }
  }

  protected HttpServer startServer(String[] serviceResources) throws IOException {

    LOG.info("Creating Resource...");

    ResourceConfig rc = new ClassNamesResourceConfig(serviceResources);
    Set set = rc.getRootResourceClasses();
    Iterator iter = set.iterator();

    while (iter.hasNext()) {
      LOG.debug(iter.next());
    }

    LOG.info("Starting grizzly...");
    String hosturl = "http://" + this.host + "/";

    HttpServer server =
        GrizzlyServerFactory.createHttpServer(
            UriBuilder.fromUri(hosturl).port(this.port).build(), rc);

    return server;
  }
}
