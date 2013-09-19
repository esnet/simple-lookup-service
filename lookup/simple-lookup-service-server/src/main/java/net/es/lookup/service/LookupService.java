package net.es.lookup.service;

import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.ClassNamesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import net.es.lookup.resources.*;
import org.glassfish.grizzly.http.server.HttpServer;
import org.apache.activemq.broker.BrokerService;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

/**
 * This class contains the method to start the lookup service
 * NOTE: All the resource classes (ie., classes that contain @Path annotation) need to be explicitly loaded in
 * startServer method
 */
public class LookupService {

    public static String SERVICE_URI_PREFIX = "lookup";
    private int port = 8080;
    private String host = "localhost";
    private String datadirectory = "data";
    private HttpServer httpServer = null;
    private static LookupService instance = null;
    BrokerService broker = null;
    private String queueurl;
    private boolean queueServiceRequired;

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

    public boolean isQueueServiceRequired() {

        return queueServiceRequired;
    }

    public void setQueueServiceRequired(boolean queueServiceRequired) {

        this.queueServiceRequired = queueServiceRequired;
    }

    public String getQueueurl() {

        return queueurl;
    }

    public void setQueueurl(String queueurl) {

        this.queueurl = queueurl;
    }

    //static {
    //  LookupService.instance = new LookupService();
    //}


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


    public LookupService() {

        // Default port is 8080 and default host is localhost
        this.host = "localhost";
        this.port = 8080;
        init();

    }


    public LookupService(int port) {

        this.port = port;
        init();

    }


    public LookupService(String host, int port) {

        this.host = host;
        this.port = port;
        init();

    }


    public void startService() {

        System.out.println("Starting HTTP server");
        try {

            this.httpServer = this.startServer();
            if(queueServiceRequired){
                this.broker = this.startBroker();
            }



        } catch (IOException e) {

            System.out.println("Failed to start HTTP server: " + e);

        } catch (Exception e){
            System.out.println("Failed to start broker: " + e);
        }

    }


    protected HttpServer startServer() throws IOException {

        System.out.println("Creating Resource...");

        String[] serviceResources = getResourceNames();
        ResourceConfig rc = new ClassNamesResourceConfig(serviceResources);
        System.out.println();
        Set set = rc.getRootResourceClasses();
        Iterator iter = set.iterator();

        while (iter.hasNext()) {

            System.out.println(iter.next());

        }

        System.out.println(("Starting grizzly..."));
        String hosturl = "http://" + this.host + "/";
        //return GrizzlyServerFactory.createHttpServer(UriBuilder.fromUri(hosturl).port(this.port).build(),rc);

        HttpServer server = GrizzlyServerFactory.createHttpServer(UriBuilder.fromUri(hosturl).port(this.port).build(),
                rc);


        return server;

    }


    protected BrokerService startBroker() throws Exception{
        System.out.println("Creating ActiveMQ Broker");
        BrokerService br = new BrokerService();

        String url = queueurl;
        br.addConnector(url);
        br.setDataDirectory(datadirectory);
        br.start();
        return br;
    }

    private String[] getResourceNames() {

        //define resources here
        String[] services = {
                AccessRecordResource.class.getName(),
                KeyResource.class.getName(),
                RecordResource.class.getName(),
                SubscribeResource.class.getName(),
                BootStrapResource.class.getName()
        };

        return services;

    }

}
