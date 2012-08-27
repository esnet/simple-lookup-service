package net.es.lookup.service;

import static java.util.Arrays.asList;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.es.lookup.database.ServiceDAOMongoDb;
import net.es.lookup.database.MongoDBMaintenanceJob;
import net.es.lookup.common.Message;
import net.es.lookup.common.Service;
import net.es.lookup.utils.LookupServiceConfigReader;
import net.es.lookup.utils.DatabaseConfigReader;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;
import org.quartz.JobDetail;
import org.quartz.Trigger;

import java.util.ArrayList;
import java.util.List;



public class Invoker {

    private static int port = 8080;
    private static LookupService lookupService = null;
    private static ServiceDAOMongoDb dao = null;
    private static String host = "localhost";
    private static LookupServiceConfigReader lcfg;
    private static DatabaseConfigReader dcfg;
    private static String cfg="";
    private static String logConfig ="./etc/log4j.properties";
    private static int dbpruneInterval;
    /**
     * Main program to start the Lookup Service
     * @param args [-h, ?] for help
     *              [-p server-port
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        parseArgs( args );
        
        if(cfg != null && !cfg.isEmpty()){
        	 System.out.println("Starting Lookup Service using config File: "+ cfg);
        	 LookupServiceConfigReader.init(cfg);
        	 DatabaseConfigReader.init(cfg);
        }else{
        	System.out.println("Starting Lookup Service using default options");
        }
        lcfg = LookupServiceConfigReader.getInstance();
        dcfg = DatabaseConfigReader.getInstance();
        port = lcfg.getPort();
        host = lcfg.getHost();
        dbpruneInterval = dcfg.getPruneInterval();
        
        System.setProperty("log4j.configuration", "file:" + logConfig);

        System.out.println("starting ServiceDAOMongoDb");

        Invoker.dao = new ServiceDAOMongoDb();
        
        System.out.println("starting Lookup Service");
        
        // Create the REST service
        Invoker.lookupService = new LookupService(Invoker.host,Invoker.port);
        // Start the service
        Invoker.lookupService.startService();
        

        //DB Pruning
        try {
            // Grab the Scheduler instance from the Factory 
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

            // and start it off
            scheduler.start();

            // define the job and tie it to our HelloJob class
            JobDetail job = newJob(MongoDBMaintenanceJob.class)
                .withIdentity("myJob", "group1") // name "myJob", group "group1"
                .build();
                  
            // Trigger the job to run now, and then every 40 seconds
            Trigger trigger = newTrigger()
                .withIdentity("myTrigger", "group1")
                .startNow()
                .withSchedule(simpleSchedule()
                    .withIntervalInSeconds(dbpruneInterval)
                    .repeatForever())            
                .build();
                  
            // Tell quartz to schedule the job using our trigger
            scheduler.scheduleJob(job, trigger);
            scheduler.shutdown();

        } catch (SchedulerException se) {
            se.printStackTrace();
        }
        
        // Block forever
        Object blockMe = new Object();
        synchronized (blockMe) {
            blockMe.wait();
        }
    }

    public static void parseArgs(String args[])  throws java.io.IOException {

        OptionParser parser = new OptionParser();
        parser.acceptsAll( asList( "h", "?" ), "show help then exit" );
        OptionSpec<String> PORT = parser.accepts("p", "server port").withRequiredArg().ofType(String.class);
        OptionSpec<String> HOST = parser.accepts("h", "host").withRequiredArg().ofType(String.class);
        OptionSpec<String> CONFIG = parser.accepts("c", "config").withRequiredArg().ofType(String.class);
        OptionSpec<String> LOGCONFIG = parser.accepts("l", "logConfig").withRequiredArg().ofType(String.class);
        OptionSet options = parser.parse( args );

        // check for help
        if ( options.has( "?" ) ) {
            parser.printHelpOn( System.out );
            System.exit(0);
        }
        if (options.has(PORT) ){
            port = Integer.parseInt(options.valueOf(PORT));
        }
        if (options.has(HOST) ){
            host = options.valueOf(HOST);
        }
        
        if (options.has(CONFIG) ){
            cfg = options.valueOf(CONFIG);
        }
        
        if(options.has(LOGCONFIG)){
            logConfig = (String) options.valueOf(LOGCONFIG);
        }     
        
   }

}