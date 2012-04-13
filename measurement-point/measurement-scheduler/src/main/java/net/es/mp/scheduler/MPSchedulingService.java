package net.es.mp.scheduler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.mongodb.DB;

import net.es.mp.authz.Authorizer;
import net.es.mp.container.MPContainer;
import net.es.mp.container.MPService;
import net.es.mp.scheduler.jobs.MPJobScheduler;
import net.es.mp.scheduler.types.Schedule;
import net.es.mp.types.MPType;

import org.apache.log4j.Logger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;


public class MPSchedulingService implements MPService{
    static private Logger log = Logger.getLogger(MPSchedulingService.class);
    static private Logger netLogger = Logger.getLogger("netLogger");
    static private MPSchedulingService instance = null;
    
    private MPContainer container;
    private ScheduleManager manager;
    private Authorizer<Schedule> authorizer;
    private Scheduler threadScheduler;
    private Map<String, Map> toolConfigMap;
    
    final static private String PROP_AUTHORIZER = "authorizer";
    final private static String PROP_JOB_SCHEDULERS = "jobSchedulers";
    final private static String PROP_JOB_SCHEDULER_TYPE = "type";
    final private static String PROP_JOB_SCHEDULER_CLASS = "class";
    final private static String PROP_JOB_THREAD_POOL_SIZE = "jobThreadPoolSize";
    
  
    final static private int DEFAULT_THREAD_POOL_SIZE = 50;
    final static private String SCHEDULER_DB_NAME = "mpScheduler";
    final static private String SCHEDULER_CONFIG = "MPSchedulingService";
    
    /**
     * Initialize global values
     * @throws IOException 
     * @throws NullPointerException 
     * @throws IllegalArgumentException 
     */
    synchronized public void init(MPContainer mpc, Map globalConfig){
        //check if already initialized
        if(instance != null){
            throw new RuntimeException("MPSchedulingService is already initialized");
        }
        
        //load config
        Map config = null;
        if(globalConfig.containsKey(SCHEDULER_CONFIG) && 
                globalConfig.get(SCHEDULER_CONFIG) != null){
            config = (Map)globalConfig.get(SCHEDULER_CONFIG);
        }else{
            config = new HashMap<String,String>();
        }
        
        //load authorizer
        if(!config.containsKey(PROP_AUTHORIZER) || 
                config.get(PROP_AUTHORIZER) == null){
            throw new RuntimeException("Missing property " + PROP_AUTHORIZER + 
                    " in block " + SCHEDULER_CONFIG);
        }
        try {
            this.authorizer = (Authorizer<Schedule>) this.getClass().getClassLoader().loadClass((String)config.get(PROP_AUTHORIZER)).newInstance();
            this.authorizer.init(config);
        } catch (Exception e) {
            throw new RuntimeException("Unable to load authorizer: " + e.getMessage());
        }
        
        //set container
        this.container = mpc;
        
        //load job schedulers
        HashMap<String, MPJobScheduler> jobRoutingTable = new HashMap<String, MPJobScheduler>();
        this.toolConfigMap = new HashMap<String, Map>();
        if(config.containsKey(PROP_JOB_SCHEDULERS) && 
                config.get(PROP_JOB_SCHEDULERS) != null){
            List<Map> schedList = (List<Map>) config.get(PROP_JOB_SCHEDULERS);
            for(Map schedMap : schedList){
                if(!schedMap.containsKey(PROP_JOB_SCHEDULER_TYPE) || 
                        schedMap.get(PROP_JOB_SCHEDULER_TYPE) == null){
                    throw new RuntimeException(PROP_JOB_SCHEDULERS + 
                            " item missing " + PROP_JOB_SCHEDULER_TYPE);
                }
                if(!schedMap.containsKey(PROP_JOB_SCHEDULER_CLASS) || 
                        schedMap.get(PROP_JOB_SCHEDULER_CLASS) == null){
                    throw new RuntimeException(PROP_JOB_SCHEDULERS + 
                            " item missing " + PROP_JOB_SCHEDULER_CLASS);
                }
                try {
                    System.out.println("props=" + schedMap.get(PROP_JOB_SCHEDULER_TYPE));
                    jobRoutingTable.put(schedMap.get(PROP_JOB_SCHEDULER_TYPE)+"",
                            (MPJobScheduler)this.getClass().getClassLoader().loadClass((String) schedMap.get(PROP_JOB_SCHEDULER_CLASS)).newInstance());
                    this.toolConfigMap.put(schedMap.get(PROP_JOB_SCHEDULER_TYPE)+"", schedMap);
                } catch (Exception e) {
                    throw new RuntimeException("Unable to load job scheduler: " + e.getMessage());
                }
            }
        }
        
        
        //initialize manager
        this.manager = new ScheduleManager(jobRoutingTable);
        
        //init quartz scheduler
        int threadPoolSize = DEFAULT_THREAD_POOL_SIZE;
        if(config.containsKey(PROP_JOB_THREAD_POOL_SIZE) && config.get(PROP_JOB_THREAD_POOL_SIZE) != null){
            threadPoolSize = (Integer) config.get(PROP_JOB_THREAD_POOL_SIZE);
        }
        log.debug("threadPoolSize is " + threadPoolSize);
        try {
            this.initThreadScheduler(threadPoolSize);
        } catch (SchedulerException e) {
            throw new RuntimeException(e.getMessage());
        }
       
        //set instance
        instance = this;
    }

    private void initThreadScheduler(int threadPoolSize) throws SchedulerException {
        Properties props = new Properties();
        props.setProperty("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
        props.setProperty("org.quartz.threadPool.threadCount", threadPoolSize + "");
        SchedulerFactory schedFactory = new StdSchedulerFactory(props);
        this.threadScheduler = schedFactory.getScheduler();
        this.threadScheduler.start();
    }

    /**
     * Returns shared instance of this class
     * 
     * @return shared instance of this class
     * @throws IOException 
     * @throws NullPointerException 
     * @throws IllegalArgumentException 
     */
    synchronized static public MPSchedulingService getInstance() {
        return instance;
    }
    
    /**
     * @return the database
     */
    public DB getDatabase() {
        return this.container.getDatabase(SCHEDULER_DB_NAME);
    }

    /**
     * @return the manager
     */
    public ScheduleManager getManager() {
        return manager;
    }
    
    /**
     * @return the threadScheduler
     */
    public Scheduler getThreadScheduler() {
        return this.threadScheduler;
    }

    /**
     * @return the toolConfigMap
     */
    public Map getToolConfig(String tool) {
        return this.toolConfigMap.get(tool);
    }

    public void addServiceResources(List<String> resourceList) {
        resourceList.add("net.es.mp.scheduler.rest.ScheduleResource");
        resourceList.add("net.es.mp.scheduler.rest.SchedulesResource");
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
    public Authorizer<Schedule> getAuthorizer() {
        return this.authorizer;
    }
    
}
