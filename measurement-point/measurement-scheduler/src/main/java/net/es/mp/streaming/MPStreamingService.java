package net.es.mp.streaming;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mongodb.DB;

import net.es.mp.authz.Authorizer;
import net.es.mp.container.MPContainer;
import net.es.mp.container.MPService;
import net.es.mp.streaming.rest.StreamResource;
import net.es.mp.streaming.rest.StreamsResource;
import net.es.mp.streaming.types.Stream;

public class MPStreamingService implements MPService {
    static private Logger log = Logger.getLogger(MPStreamingService.class);
    static private Logger netLogger = Logger.getLogger("netLogger");
    static private MPStreamingService instance = null;
    
    private MPContainer container;
    private StreamManager manager;
    private Authorizer<Stream> authorizer;
    
    final static private String PROP_AUTHORIZER = "authorizer";
    final static private String STREAMING_DB_NAME = "mpStreams";
    final static private String STREAMING_CONFIG = "MPStreamingService";
    
    synchronized public void init(MPContainer mpc, Map globalConfig){
        //check if already initialized
        if(instance != null){
            throw new RuntimeException("MPSchedulingService is already initialized");
        }
        
        //load config
        Map config = null;
        if(globalConfig.containsKey(STREAMING_CONFIG) && 
                globalConfig.get(STREAMING_CONFIG) != null){
            config = (Map)globalConfig.get(STREAMING_CONFIG);
        }else{
            config = new HashMap<String,String>();
        }
        
        //load authorizer
        if(!config.containsKey(PROP_AUTHORIZER) || 
                config.get(PROP_AUTHORIZER) == null){
            throw new RuntimeException("Missing property " + PROP_AUTHORIZER + 
                    " in block " + STREAMING_CONFIG);
        }
        try {
            this.authorizer = (Authorizer<Stream>) this.getClass().getClassLoader().loadClass((String)config.get(PROP_AUTHORIZER)).newInstance();
            this.authorizer.init(config);
        } catch (Exception e) {
            throw new RuntimeException("Unable to load authorizer: " + e.getMessage());
        }
        
        //set container
        this.container = mpc;
        
        //create manager
        this.manager = new StreamManager();
        
        //set instance 
        instance = this;
    }
    /**
     * Returns shared instance of this class
     * 
     * @return shared instance of this class
     * @throws IOException 
     * @throws NullPointerException 
     * @throws IllegalArgumentException 
     */
    synchronized static public MPStreamingService getInstance() {
        return instance;
    }
    
    /**
     * @return the database
     */
    public DB getDatabase() {
        return this.container.getDatabase(STREAMING_DB_NAME);
    }
    
    public StreamManager getManager(){
        return this.manager;
    }
    
    public void addServiceResources(List<String> resourceList) {
        resourceList.add(StreamResource.class.getName());
        resourceList.add(StreamsResource.class.getName());
    }
    
    /**
     * @return the container
     */
    public MPContainer getContainer() {
        return this.container;
    }
    /**
     * @return the authorizer
     */
    public Authorizer<Stream> getAuthorizer() {
        return this.authorizer;
    }

}
