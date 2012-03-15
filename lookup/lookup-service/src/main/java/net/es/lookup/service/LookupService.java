package net.es.lookup.service;

import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import org.glassfish.grizzly.http.server.HttpServer;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


/**
 *
 */
public class LookupService {

    private int port = 8080;
    private HttpServer httpServer = null;

    public LookupService (int port) {
        this.port = port;
    }

    public void startService() {
        System.out.println("Starting HTTP server");
        try {
            this.httpServer = this.startServer();
        } catch (IOException e) {
            System.out.println("Failed to start HTTP server: " + e);
        }
    }

    private URI getBaseURI() {
        return UriBuilder.fromUri("http://localhost/").port(this.port).build();
    }

    public final URI BASE_URI = getBaseURI();

    protected HttpServer startServer() throws IOException {
        System.out.println("Creating Resource...");
        ResourceConfig rc = new PackagesResourceConfig("net.es.lookup.service","net.es.lookup.resources");
        System.out.println(("Starting grizzly..."));
        return GrizzlyServerFactory.createHttpServer(BASE_URI,rc);
    }

}
