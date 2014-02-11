package net.es.lookup.service;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.es.lookup.bootstrap.ScanLSJob;
import net.es.lookup.common.ReservedKeys;
import net.es.lookup.common.ReservedValues;
import net.es.lookup.common.exception.internal.DatabaseException;
import net.es.lookup.database.DBMapping;
import net.es.lookup.database.MongoDBMaintenanceJob;
import net.es.lookup.database.ServiceDAOMongoDb;
import net.es.lookup.pubsub.client.ArchiveService;
import net.es.lookup.pubsub.amq.AMQueueManager;
import net.es.lookup.pubsub.amq.AMQueuePump;
import net.es.lookup.pubsub.client.ReplicationService;
import net.es.lookup.utils.config.data.Cache;
import net.es.lookup.utils.config.reader.BootStrapConfigReader;
import net.es.lookup.utils.config.reader.LookupServiceConfigReader;
import net.es.lookup.utils.config.reader.QueueServiceConfigReader;
import net.es.lookup.utils.config.reader.SubscriberConfigReader;
import net.es.lookup.utils.config.data.LookupServiceOptions;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.LinkedList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;


public class Invoker {

    private static int port = 8080;
    private static LookupService lookupService = null;
    //private static ServiceDAOMongoDb dao = null;
    private static String host = "localhost";
    private static LookupServiceConfigReader lcfg;
    private static SubscriberConfigReader sfg;
    private static QueueServiceConfigReader qcfg;
    private static BootStrapConfigReader bcfg;
    private static String configPath = "etc/";
    private static String lookupservicecfg = "lookupservice.yaml";
    private static String queuecfg = "queueservice.yaml";
    private static String subscribecfg = "subscriber.yaml";
    private static String bootstrapcfg = "bootstrap.yaml";
    private static String bootstrapoutput = "active-hosts.json";
    private static String logConfig = "./etc/log4j.properties";
    private static String queueDataDir = "../data";

    private static boolean cacheservice = false;
    private static boolean bootstrapservice = false;

    private static List<ReplicationService> replicationServiceList;
    private static List<ArchiveService> archiveServiceList;

    public static String getConfigPath() {

        return configPath;
    }

    public static String getBootstrapoutput() {

        return bootstrapoutput;
    }

    public static String getLogConfig() {

        return logConfig;
    }

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


        LookupServiceConfigReader.init(configPath + lookupservicecfg);
        QueueServiceConfigReader.init(configPath + queuecfg);


        BootStrapConfigReader.init(configPath + bootstrapcfg);


        lcfg = LookupServiceConfigReader.getInstance();
        qcfg = QueueServiceConfigReader.getInstance();
        bcfg = BootStrapConfigReader.getInstance();

        port = lcfg.getPort();
        host = lcfg.getHost();
        bootstrapservice = lcfg.isBootstrapserviceOn();

        cacheservice = lcfg.isCacheserviceOn();



        int dbpruneInterval = lcfg.getPruneInterval();
        long prunethreshold = lcfg.getPruneThreshold();
        System.out.println("starting ServiceDAOMongoDb");

        String dburl = lcfg.getDbUrl();
        int dbport = lcfg.getDbPort();
        String collname = lcfg.getCollName();

        List<String> services = new LinkedList<String>();

        // Start DB and Queue
        try {

            if(lcfg.isCoreserviceOn()){
                new ServiceDAOMongoDb(dburl, dbport, "lookup", collname);
                new AMQueueManager("lookup");
                new AMQueuePump("lookup");
                services.add(LookupService.LOOKUP_SERVICE);
            }

            if(lcfg.isBootstrapserviceOn()){
                services.add(LookupService.BOOTSTRAP_SERVICE);

                Scheduler bootstrapScheduler = StdSchedulerFactory.getDefaultScheduler();
                bootstrapScheduler.start();
                JobDetail bootstrapJob = newJob(ScanLSJob.class)
                        .withIdentity("scanLS", "bootstrap")
                        .build();

                Trigger bootstrapTrigger = newTrigger().withIdentity("scanLSTrigger", "bootstrap")
                        .startNow()
                        .withSchedule(simpleSchedule()
                                .repeatForever()
                                .withIntervalInSeconds(1800))
                        .build();

                bootstrapScheduler.scheduleJob(bootstrapJob, bootstrapTrigger);
            }


            if(cacheservice){
                SubscriberConfigReader.init(configPath + subscribecfg);
                sfg = SubscriberConfigReader.getInstance();
                replicationServiceList = new LinkedList<ReplicationService>();
                archiveServiceList = new LinkedList<ArchiveService>();
                List<Cache> caches = sfg.getCacheList();
                for(Cache cache: caches){
                    String name = cache.getName();
                    new ServiceDAOMongoDb(dburl, dbport, name, collname);
                    new AMQueueManager(name);
                    new AMQueuePump(name);
                    services.add(name);
                    if (cache.getType().equals(ReservedValues.CACHE_TYPE_REPLICATION)){
                        ReplicationService replicationService = new ReplicationService(cache);
                        replicationService.start();
                        replicationServiceList.add(replicationService);
                    }else if(cache.getType().equals(ReservedValues.CACHE_TYPE_ARCHIVE)){
                        ArchiveService archiveService = new ArchiveService(cache);
                        archiveService.start();
                        archiveServiceList.add(archiveService);
                    }
                }
            }


        } catch (DatabaseException e) {

            System.out.println("Error connecting to database; Please check if MongoDB is running");
            System.exit(1);

        }

        System.out.println("starting Lookup Service");
        // Create the REST service
        Invoker.lookupService = new LookupService(Invoker.host, Invoker.port);
        Invoker.lookupService.setDatadirectory(queueDataDir);
        Invoker.lookupService.setQueueurl(qcfg.getUrl());
        // Start the service

        Invoker.lookupService.startService(services);

        System.out.println("Started service");

        //DB Pruning
        try {

            SchedulerFactory sf = new StdSchedulerFactory();
            Scheduler scheduler = sf.getScheduler();
            scheduler.start();

            List<String> dbnames = DBMapping.getKeys();
            for (String dbname : dbnames) {
                // define the job and tie it to  mongoJob class
                JobDetail job = newJob(MongoDBMaintenanceJob.class)
                        .withIdentity(dbname + "clean", "DBMaintenance")
                        .build();
                job.getJobDataMap().put(MongoDBMaintenanceJob.PRUNE_THRESHOLD, prunethreshold);
                job.getJobDataMap().put(MongoDBMaintenanceJob.DBNAME, dbname);

                // Trigger the job to run now, and then every dbpruneInterval seconds
                Trigger trigger = newTrigger().withIdentity(dbname + "DBTrigger", "DBMaintenance")
                        .startNow()
                        .withSchedule(simpleSchedule()
                                .withIntervalInSeconds(dbpruneInterval)
                                .repeatForever()
                                .withMisfireHandlingInstructionIgnoreMisfires())
                        .build();


                scheduler.scheduleJob(job, trigger);
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