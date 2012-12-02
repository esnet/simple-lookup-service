package net.es.lookup.service;

import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.ClassNamesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import net.es.lookup.resources.AccessRecordResource;
import net.es.lookup.resources.KeyResource;
import net.es.lookup.resources.RecordResource;
import org.glassfish.grizzly.http.server.HttpServer;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

//import com.sun.grizzly.websockets.WebSocket;
//import com.sun.grizzly.websockets.WebSocketAddOn;
//import com.sun.grizzly.websockets.WebSocketApplication;


/**
 * This class contains the method to start the lookup service
 * NOTE: All the resource classes (ie., classes that contain @Path annotation) need to be explicitly loaded in
 * startServer method
 */
public class LookupService {

    public static String SERVICE_URI_PREFIX = "lookup";
    private int port = 8080;
    private String host = "localhost";
    private HttpServer httpServer = null;
    private static LookupService instance = null;

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

        } catch (IOException e) {

            System.out.println("Failed to start HTTP server: " + e);

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


    private String[] getResourceNames() {

        //define resources here
        String[] services = {
                AccessRecordResource.class.getName(),
                KeyResource.class.getName(),
                RecordResource.class.getName(),
                //SubscribeResource.class.getName(),
        };

        return services;

    }

}
