package net.es.lookup.service;

import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import org.glassfish.grizzly.http.server.HttpServer;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;



/**
 *
 */
public class LookupService {

    private int port = 8080;
    private HttpServer httpServer = null;
    private static LookupService instance = null;

    static {
        LookupService.instance = new LookupService();
    }

    public static LookupService getInstance(int port) {
        return LookupService.instance;
    }

    private synchronized void init() {
        if (LookupService.instance != null) {
            // An instance has been already created.
            throw new RuntimeException("Attempt to create a second instance of LookupService");
        }
        LookupService.instance = this;
    }

    public LookupService () {
        // Default port is 8080
        this.port = 8080;
        init();
    }

    public LookupService (int port) {
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
        ResourceConfig rc = new PackagesResourceConfig("net.es.lookup.service","net.es.lookup.resources");
        System.out.println(("Starting grizzly..."));
        return GrizzlyServerFactory.createHttpServer(UriBuilder.fromUri("http://localhost/").port(this.port).build(),
                                                     rc);
    }

}
