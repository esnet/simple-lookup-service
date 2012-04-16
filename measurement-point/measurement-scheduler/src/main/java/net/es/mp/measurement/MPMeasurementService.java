package net.es.mp.measurement;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mongodb.DB;

import net.es.mp.authz.Authorizer;
import net.es.mp.container.MPContainer;
import net.es.mp.container.MPService;
import net.es.mp.measurement.rest.MeasurementResource;
import net.es.mp.measurement.rest.MeasurementsResource;
import net.es.mp.measurement.types.Measurement;
import net.es.mp.measurement.types.validators.MeasurementValidator;
import net.es.mp.types.MPType;

public class MPMeasurementService implements MPService{
    static private Logger log = Logger.getLogger(MPMeasurementService.class);
    static private Logger netLogger = Logger.getLogger("netLogger");
    static private MPMeasurementService instance = null;
    
    private MPContainer container;
    private MeasurementManager manager;
    private Authorizer<Measurement> authorizer;
    private HashMap<String,MeasurementValidator> validatorMap;
    private boolean requiresValidator;
    
    final static private String PROP_AUTHORIZER = "authorizer";
    final static private String PROP_VALIDATORS = "validators";
    final static private String PROP_VALIDATOR_TYPE = "type";
    final static private String PROP_VALIDATOR_CLASS = "class";
    final static private String PROP_REQ_VALIDATOR = "requireValidator";
    final static private String MEASUREMENT_DB_NAME = "mpMeasurements";
    final static private String MEASUREMENT_CONFIG = "MPMeasurementService";
    
    public void init(MPContainer mpc, Map globalConfig) {
        //check if already initialized
        if(instance != null){
            throw new RuntimeException("MPMeasurementService is already initialized");
        }
        
        //load config
        Map config = null;
        if(globalConfig.containsKey(MEASUREMENT_CONFIG) && 
                globalConfig.get(MEASUREMENT_CONFIG) != null){
            config = (Map)globalConfig.get(MEASUREMENT_CONFIG);
        }else{
            config = new HashMap<String,String>();
        }
        
        //load authorizer
        if(!config.containsKey(PROP_AUTHORIZER) || 
                config.get(PROP_AUTHORIZER) == null){
            throw new RuntimeException("Missing property " + PROP_AUTHORIZER + 
                    " in block " + MEASUREMENT_CONFIG);
        }
        try {
            this.authorizer = (Authorizer<Measurement>) this.getClass().getClassLoader().loadClass((String)config.get(PROP_AUTHORIZER)).newInstance();
            this.authorizer.init(config);
        } catch (Exception e) {
            throw new RuntimeException("Unable to load authorizer: " + e.getMessage());
        }
        
        //load validators
        this.validatorMap = new HashMap<String,MeasurementValidator>();
        if(config.containsKey(PROP_VALIDATORS) && 
                config.get(PROP_VALIDATORS) != null){
            List<Map> validatorList = (List<Map>) config.get(PROP_VALIDATORS);
            for(Map validatorListMap : validatorList){
                if(!validatorListMap.containsKey(PROP_VALIDATOR_TYPE) || 
                        validatorListMap.get(PROP_VALIDATOR_TYPE) == null){
                    throw new RuntimeException(PROP_VALIDATORS + 
                            " item missing " + PROP_VALIDATOR_TYPE);
                }
                if(!validatorListMap.containsKey(PROP_VALIDATOR_CLASS) || 
                        validatorListMap.get(PROP_VALIDATOR_CLASS) == null){
                    throw new RuntimeException(PROP_VALIDATORS + 
                            " item missing " + PROP_VALIDATOR_CLASS);
                }
                try {
                    this.validatorMap.put(validatorListMap.get(PROP_VALIDATOR_TYPE)+"",
                            (MeasurementValidator)this.getClass().getClassLoader().loadClass((String) validatorListMap.get(PROP_VALIDATOR_CLASS)).newInstance());
                } catch (Exception e) {
                    throw new RuntimeException("Unable to load validator: " + e.getMessage());
                }
            }
        }
        
        //determine if validator required
        this.requiresValidator = true;
        if(config.containsKey(PROP_REQ_VALIDATOR) && 
                config.get(PROP_REQ_VALIDATOR) != null){
            this.requiresValidator = (Boolean) config.get(PROP_REQ_VALIDATOR);
        }
        
        //set container
        this.container = mpc;
        
        //set manager
        this.manager = new MeasurementManager();
        
        //set instance 
        instance = this;
    }
    
    public void addServiceResources(List<String> resourceList) {
        resourceList.add(MeasurementResource.class.getName());
        resourceList.add(MeasurementsResource.class.getName());
    }
    
    /**
     * Returns shared instance of this class
     * 
     * @return shared instance of this class
     * @throws IOException 
     * @throws NullPointerException 
     * @throws IllegalArgumentException 
     */
    synchronized static public MPMeasurementService getInstance() {
        return instance;
    }
    
    /**
     * @return the database
     */
    public DB getDatabase() {
        return this.container.getDatabase(MEASUREMENT_DB_NAME);
    }
    
    public MeasurementManager getManager(){
        return this.manager;
    }
    
    /**
     * @return the container
     */
    public MPContainer getContainer() {
        return this.container;
    }

    /**
     * @return the validatorMap
     */
    public HashMap<String, MeasurementValidator> getValidatorMap() {
        return this.validatorMap;
    }

    /**
     * @return the requiresValidator
     */
    public boolean getRequiresValidator() {
        return this.requiresValidator;
    }

    /**
     * @return the authorizer
     */
    public Authorizer<Measurement> getAuthorizer() {
        return this.authorizer;
    }

}
