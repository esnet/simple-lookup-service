package net.es.lookup.service;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.es.lookup.common.exception.LSClientException;
import net.es.lookup.common.exception.internal.DatabaseException;
import net.es.lookup.database.DBPool;
import net.es.lookup.database.MongoDBMaintenanceJob;
import net.es.lookup.database.ServiceDAOMongoDb;
import net.es.lookup.pubsub.Publisher;
import net.es.lookup.pubsub.amq.AMQueueManager;
import net.es.lookup.pubsub.amq.AMQueuePump;
import net.es.lookup.pubsub.client.Cache;
import net.es.lookup.utils.config.elements.CacheConfig;
import net.es.lookup.utils.config.elements.PublisherConfig;
import net.es.lookup.utils.config.reader.LookupServiceConfigReader;
import net.es.lookup.utils.config.reader.QueueServiceConfigReader;
import net.es.lookup.utils.config.reader.SubscriberConfigReader;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

import java.net.URI;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;


public class Invoker {

    private static int port = 8080;
    private static LookupService lookupService = null;
    private static CacheService cacheService = null;
    //private static ServiceDAOMongoDb dao = null;
    private static String host = "localhost";
    private static LookupServiceConfigReader lcfg;
    private static SubscriberConfigReader sfg;
    private static QueueServiceConfigReader qcfg;
    private static String configPath = "etc/";
    private static String lookupservicecfg = "lookupservice.yaml";
    private static String queuecfg = "queueservice.yaml";
    private static String subscribecfg = "subscriber.yaml";
    private static String logConfig = "./etc/log4j.properties";
    private static String queueDataDir = "../elements";

    private static boolean cacheServiceRequest = false;

    /**
     * Main program to start the Lookup ServiceRecord
     *
     * @param args [-h, ?] for help
     *             [-p server-port
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        parseArgs(args);
        //set log config
        System.setProperty("log4j.configuration", "file:" + logConfig);

        SchedulerFactory sf = new StdSchedulerFactory();
        Scheduler scheduler = sf.getScheduler();
        scheduler.start();

        LookupServiceConfigReader.init(configPath + lookupservicecfg);
        QueueServiceConfigReader.init(configPath + queuecfg);


        lcfg = LookupServiceConfigReader.getInstance();
        qcfg = QueueServiceConfigReader.getInstance();

        port = lcfg.getPort();
        host = lcfg.getHost();
        cacheServiceRequest = lcfg.isCacheserviceOn();

        int dbpruneInterval = lcfg.getPruneInterval();
        long prunethreshold = lcfg.getPruneThreshold();
        System.out.println("starting ServiceDAOMongoDb");

        String dburl = lcfg.getDbUrl();
        int dbport = lcfg.getDbPort();
        String collname = lcfg.getCollName();

        List<String> services = new LinkedList<String>();

        // Initialize services
        try {

            if (lcfg.isCoreserviceOn()) {
                new ServiceDAOMongoDb(dburl, dbport, LookupService.LOOKUP_SERVICE, collname);
                new AMQueueManager(LookupService.LOOKUP_SERVICE);
                new AMQueuePump(LookupService.LOOKUP_SERVICE);
                services.add(LookupService.LOOKUP_SERVICE);
            }


            if (cacheServiceRequest) {
                SubscriberConfigReader.init(configPath + subscribecfg);
                sfg = SubscriberConfigReader.getInstance();

                List<CacheConfig> cacheConfigList = sfg.getCacheList();

                List<Cache> cacheList = new LinkedList<Cache>();
                for(CacheConfig config: cacheConfigList){

                    String name = config.getName();
                    String type = config.getType();

                    List<PublisherConfig> publisherConfigList = config.getPublishers();
                    List<Publisher> publishers = new LinkedList<Publisher>();
                    for(PublisherConfig publisherConfig: publisherConfigList){
                        URI accesspoint = publisherConfig.getLocator();
                        List<Map<String,Object>> queries = publisherConfig.getQueries();

                        Publisher publisher = new Publisher(accesspoint,queries);
                        publishers.add(publisher);
                    }

                    try{
                        Cache cache = new Cache(name,type,publishers);
                        cacheList.add(cache);
                        new ServiceDAOMongoDb(dburl, dbport, name, collname);
                        services.add(name);

                    }catch(LSClientException e){
                        System.out.println("Error initializing cache: "+name+"; Type: "+type);
                        continue;
                    }
                }


                Invoker.cacheService = CacheService.initialize(cacheList,scheduler);

                System.out.println("Cache service initialized: " + Invoker.cacheService.isInitialized());
            }

        } catch (DatabaseException e) {

            System.out.println("Error connecting to database; Please check if MongoDB is running");
            System.exit(1);

        }
        System.out.println("starting Lookup Service");
        // Create the REST service
        Invoker.lookupService = new LookupService(Invoker.host, Invoker.port);
        Invoker.lookupService.setDatadirectory(queueDataDir);
        System.out.println("Starting queue at Queue url:" + qcfg.getUrl());
        Invoker.lookupService.setQueueurl(qcfg.getUrl());

        if (Invoker.cacheService.isInitialized()) {
            System.out.println("Starting cache service");
            Invoker.cacheService.startService();
        } else{
            if(cacheServiceRequest){
                System.out.println("Error starting cache service");
            }


        }

        // Start the service
        Invoker.lookupService.startService(services);


        //DB Pruning
        try {


            List<String> dbnames = DBPool.getKeys();
            for (String dbname : dbnames) {
                // define the job and tie it to  mongoJob class
                JobDetail job = newJob(MongoDBMaintenanceJob.class)
                        .withIdentity(dbname + "clean", "DBMaintenance")
                        .build();
                job.getJobDataMap().put(MongoDBMaintenanceJob.PRUNE_THRESHOLD, prunethreshold);
                job.getJobDataMap().put(MongoDBMaintenanceJob.DBNAME, dbname);

                // Trigger the job to run now, and then every dbpruneInterval seconds
                Trigger trigger = newTrigger().withIdentity(dbname+"DBTrigger", "DBMaintenance")
                        .startNow()
                        .withSchedule(simpleSchedule()
                                .withIntervalInSeconds(dbpruneInterval)
                                .repeatForever()
                                .withMisfireHandlingInstructionIgnoreMisfires())
                        .build();

                scheduler.scheduleJob(job, trigger);
            }


            for(String group: scheduler.getJobGroupNames()) {
                // enumerate each job in group
                for(JobKey jobKey : scheduler.getJobKeys((GroupMatcher<JobKey>) GroupMatcher.groupEquals(group))) {
                    System.out.println("Found job identified by: " + jobKey);
                }
            }



        } catch (SchedulerException se) {
            se.printStackTrace();

        }


        // Block forever
        Object blockMe = new Object();
        synchronized (blockMe) {
            blockMe.wait();

        }

    }


    public static void parseArgs(String args[]) throws java.io.IOException {

        OptionParser parser = new OptionParser();
        parser.acceptsAll(asList("h", "?"), "show help then exit");
        OptionSpec<String> PORT = parser.accepts("p", "server port").withRequiredArg().ofType(String.class);
        OptionSpec<String> HOST = parser.accepts("h", "host").withRequiredArg().ofType(String.class);
        OptionSpec<String> CONFIG = parser.accepts("c", "configPath").withRequiredArg().ofType(String.class);
        OptionSpec<String> LOGCONFIG = parser.accepts("l", "logConfig").withRequiredArg().ofType(String.class);
        OptionSpec<String> QUEUEDATADIR = parser.accepts("d", "queueDataDir").withRequiredArg().ofType(String.class);
        OptionSet options = parser.parse(args);

        // check for help
        if (options.has("?")) {

            parser.printHelpOn(System.out);
            System.exit(0);

        }

        if (options.has(PORT)) {

            port = Integer.parseInt(options.valueOf(PORT));

        }

        if (options.has(HOST)) {

            host = options.valueOf(HOST);

        }

        if (options.has(CONFIG)) {

            configPath = options.valueOf(CONFIG);
            System.out.println("Config files Path:" + configPath);

        }

        if (options.has(LOGCONFIG)) {

            logConfig = options.valueOf(LOGCONFIG);

        }


        if (options.has(QUEUEDATADIR)) {

            queueDataDir = options.valueOf(QUEUEDATADIR);

        }

    }


}
