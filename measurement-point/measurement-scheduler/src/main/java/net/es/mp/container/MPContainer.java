package net.es.mp.container;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriBuilder;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.sun.jersey.api.core.ClassNamesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;

import net.es.mp.authn.AuthnSubjectFactory;
import net.es.mp.server.WebServer;

import org.apache.log4j.Logger;
import org.ho.yaml.Yaml;

public class MPContainer {
    static private Logger log = Logger.getLogger(MPContainer.class);
    static private Logger netLogger = Logger.getLogger("netLogger");
    
    private String configFile = null;
    private WebServer webServer;
    private AuthnSubjectFactory authnSubjectFactory;
    
    private Mongo mongo;
    private String dbUser;
    private String dbPassword;
    private String resourceURL;
    
    final private static String PROP_SERVER_HOST = "serverHost";
    final private static String PROP_HTTP = "http";
    final private static String PROP_HTTP_PORT = "port";
    final private static String PROP_HTTP_PROXY_MODE = "proxyMode";
    final private static String PROP_HTTPS = "https";
    final private static String PROP_HTTPS_PORT = "port";
    final private static String PROP_HTTPS_KEYSTORE = "keystore";
    final private static String PROP_HTTPS_KEYSTORE_PASSWORD = "keystorePassword";
    final private static String PROP_HTTPS_CLIENT_AUTH = "clientAuth";
    final private static String PROP_HTTPS_PROXY_MODE = "proxyMode";
    final private static String PROP_SERVICES = "services";
    final private static String PROP_AUTHENTICATORS = "authenticators";
    final private static String PROP_DB = "database";
    final private static String PROP_DB_HOST = "host";
    final private static String PROP_DB_PORT = "port";
    final private static String PROP_DB_USER = "user";
    final private static String PROP_DB_PASSWORD = "password";
    final private static String PROP_RESOURCE_URL = "resourceURL";
    final private static String PROP_RESOURCE_URL_PROTO = "protocol";
    final private static String PROP_RESOURCE_URL_HOST = "host";
    final private static String PROP_RESOURCE_URL_PORT = "port";
    
    final static private String DEFAULT_HOST = "localhost";
    final static private String DEFAULT_DB_HOST = "localhost";
    final static private int DEFAULT_DB_PORT = 27017;
    final static private boolean DEFAULT_PROXY_MODE = false;
    
    /**
     * Initialize container
     */
    public MPContainer(String newConfigFile) {
        configFile = newConfigFile;
        
        Map config;
        try {
            config = (Map) Yaml.load(new File(configFile));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }
        
        //initialize database
        if(config.containsKey(PROP_DB) && config.get(PROP_DB) != null){
            this.initializeDatabase((Map)config.get(PROP_DB));
        }
        
        //set server host
        String serverHost = DEFAULT_HOST;
        if(config.containsKey(PROP_SERVER_HOST) && config.get(PROP_SERVER_HOST) != null){
            serverHost = (String) config.get(PROP_SERVER_HOST);
        }
        log.debug("Server host is " + serverHost);
        
        //load authenticators
        this.authnSubjectFactory = new AuthnSubjectFactory();
        if(config.containsKey(PROP_AUTHENTICATORS)){
            List<String> authenticators = (List<String>)config.get(PROP_AUTHENTICATORS);
            for(String authenticator : authenticators){
                this.authnSubjectFactory.addAuthenticator(authenticator);
            }
        }
        
        //load services
        List<String> mpServiceList = new ArrayList<String>();
        if(config.containsKey(PROP_SERVICES)){
            List<String> services = (List<String>)config.get(PROP_SERVICES);
            for(String serviceClass : services){
                MPService service = null;
                try {
                    service = (MPService)this.getClass().getClassLoader().loadClass(serviceClass).newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                } 
                service.init(this, config);
                service.addServiceResources(mpServiceList);
            }
        }
        /*
         * NOTE: PackagesResourceConfig does not appear to work
         * with one-jar class loader so loading individually for now
         */
        String resourceProto = null;
        int resourcePort = 0;
        String resourceHost = serverHost; 
        String[] services = new String[mpServiceList.size()];
        mpServiceList.toArray(services);
        for(String serv : services){
            System.out.println("SERVICE! " + serv);
        }
        ResourceConfig rc = new ClassNamesResourceConfig(services);
        //create server
        this.webServer = new WebServer(serverHost,rc);
        
        //configure HTTP
        if(config.containsKey(PROP_HTTP) && config.get(PROP_HTTP) != null){
            resourcePort = this.configureHttp((Map)config.get(PROP_HTTP));
            resourceProto = "http";
        }
        
        //configure HTTPS
        if(config.containsKey(PROP_HTTPS) && config.get(PROP_HTTPS) != null){
            resourcePort = this.configureHttps((Map)config.get(PROP_HTTPS));
            resourceProto = "https";
        }
        
        //determine url
        Map urlConfig = null;
        if(config.containsKey(PROP_RESOURCE_URL)){
            urlConfig = (Map) config.get(PROP_RESOURCE_URL);
        }
        this.configureResourceURL(urlConfig, resourceProto, 
                resourceHost, resourcePort);
       
    }

    private void configureResourceURL(Map urlConfig, String proto, 
            String host, int port) {
        
        //read config
        if(urlConfig != null){
            if(urlConfig.containsKey(PROP_RESOURCE_URL_PROTO) && 
                urlConfig.get(PROP_RESOURCE_URL_PROTO) != null){
                proto = (String)urlConfig.get(PROP_RESOURCE_URL_PROTO);
            }
        
            if(urlConfig.containsKey(PROP_RESOURCE_URL_HOST) && 
                urlConfig.get(PROP_RESOURCE_URL_HOST) != null){
                host = (String)urlConfig.get(PROP_RESOURCE_URL_HOST);
            }
        
            if(urlConfig.containsKey(PROP_RESOURCE_URL_PORT) && 
                urlConfig.get(PROP_RESOURCE_URL_PORT) != null){
                port = (Integer)urlConfig.get(PROP_RESOURCE_URL_PORT);
            }
        }
        
        //set resource URL
        this.resourceURL = UriBuilder.fromPath("/").scheme(proto).host(host).port(port).build().toASCIIString();
        log.debug("resourceURL=" + this.resourceURL);
    }

    private void initializeDatabase(Map dbProps) {
        //get host and port
        String dbHost = DEFAULT_DB_HOST;
        int dbPort = DEFAULT_DB_PORT;
        this.dbUser = null;
        this.dbPassword = null;
        
        if(dbProps.containsKey(PROP_DB_HOST) && dbProps.get(PROP_DB_HOST) != null){
            dbHost = (String)dbProps.get(PROP_DB_HOST);
        }
        if(dbProps.containsKey(PROP_DB_PORT) && dbProps.get(PROP_DB_PORT) != null){
            dbPort = (Integer)dbProps.get(PROP_DB_PORT);
        }
        if(dbProps.containsKey(PROP_DB_USER) && dbProps.get(PROP_DB_USER) != null){
            this.dbUser = (String)dbProps.get(PROP_DB_USER);
        }
        if(dbProps.containsKey(PROP_DB_PASSWORD) && dbProps.get(PROP_DB_PASSWORD) != null){
            this.dbPassword = (String)dbProps.get(PROP_DB_PASSWORD);
        }
        
        //initialize database
        try {
            this.mongo = new Mongo(dbHost, dbPort);
            
        } catch (Exception e) {
            this.log.error("Error creating database: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private int configureHttp(Map httpConfig) {
        this.log.debug("Configuring HTTP...");
        boolean proxyMode = DEFAULT_PROXY_MODE;
        if(httpConfig.containsKey(PROP_HTTP_PROXY_MODE) && 
                httpConfig.get(PROP_HTTP_PROXY_MODE) != null){
            proxyMode = (Boolean) httpConfig.get(PROP_HTTP_PROXY_MODE);
        }
        if(!httpConfig.containsKey(PROP_HTTP_PORT) ||
                httpConfig.get(PROP_HTTP_PORT) == null){
            throw new RuntimeException("No port specified in http configuration");
        }
        int port = (Integer)httpConfig.get(PROP_HTTP_PORT);
        try {
            this.webServer.addHttpListener(port, proxyMode);
        } catch (IOException e) {
            this.log.error("Error configuring HTTP: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error configuring HTTP: " + e.getMessage());
        }
        this.log.debug("    port=" + httpConfig.get(PROP_HTTP_PORT));
        return port;
    }

    private int configureHttps(Map httpsConfig) {
        this.log.debug("Configuring HTTPS...");
        if(!httpsConfig.containsKey(PROP_HTTPS_PORT) ||
                httpsConfig.get(PROP_HTTPS_PORT) == null){
            throw new RuntimeException("No port specified in https configuration");
        }
        int port =  (Integer)httpsConfig.get(PROP_HTTPS_PORT);
        this.log.debug("    port=" + port);
        
        if(!httpsConfig.containsKey(PROP_HTTPS_KEYSTORE) ||
                httpsConfig.get(PROP_HTTPS_KEYSTORE) == null){
            throw new RuntimeException("No keystore specified in https configuration");
        }
        String keystore = (String)httpsConfig.get(PROP_HTTPS_KEYSTORE);
        this.log.debug("    keystore=" + keystore);
        
        if(!httpsConfig.containsKey(PROP_HTTPS_KEYSTORE_PASSWORD) ||
                httpsConfig.get(PROP_HTTPS_KEYSTORE_PASSWORD) == null){
            throw new RuntimeException("No keystorePassword specified in https configuration");
        }
        String keystorePass = (String)httpsConfig.get(PROP_HTTPS_KEYSTORE_PASSWORD);
        this.log.debug("    keystorePass=" + keystorePass);
        
        String clientAuth = WebServer.HTTPS_CLIENT_AUTH_OFF;
        if(httpsConfig.containsKey(PROP_HTTPS_CLIENT_AUTH) &&
                httpsConfig.get(PROP_HTTPS_CLIENT_AUTH) != null){
            clientAuth = (String) httpsConfig.get(PROP_HTTPS_CLIENT_AUTH);
        }
        
        //get proxy mode
        boolean proxyMode = DEFAULT_PROXY_MODE;
        if(httpsConfig.containsKey(PROP_HTTPS_PROXY_MODE) && 
                httpsConfig.get(PROP_HTTPS_PROXY_MODE) != null){
            proxyMode = (Boolean) httpsConfig.get(PROP_HTTPS_PROXY_MODE);
        }
        this.log.debug("    clientAuth=" + clientAuth);
        
        try {
            this.webServer.addHttpsListener(port, keystore, keystorePass, clientAuth, proxyMode);
        } catch (IOException e) {
            this.log.error("Error configuring HTTPS: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error configuring HTTPS: " + e.getMessage());
        }
        
        return port;
    }

    /**
     * @return the server
     */
    public WebServer getWebServer() {
        return this.webServer;
    }

    /**
     * @return the authnSubjectFactory
     */
    public AuthnSubjectFactory getAuthnSubjectFactory() {
        return this.authnSubjectFactory;
    }

    /**
     * @return the database
     */
    public DB getDatabase(String dbName) {
        DB db = this.mongo.getDB(dbName);
        if(this.dbUser != null && this.dbPassword != null){
            db.authenticate(this.dbUser, this.dbPassword.toCharArray());
        }
        return db;
    }

    /**
     * @return the rootURL
     */
    public String getResourceURL() {
        return this.resourceURL;
    }
}
