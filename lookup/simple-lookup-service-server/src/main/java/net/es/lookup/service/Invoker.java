package net.es.lookup.service;


import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.es.lookup.common.MemoryManager;
import net.es.lookup.common.exception.internal.DatabaseException;
import net.es.lookup.database.MongoDBMaintenanceJob;
import net.es.lookup.database.ServiceDAOMongoDb;
import net.es.lookup.timer.Scheduler;
import net.es.lookup.utils.config.reader.LookupServiceConfigReader;
import net.es.lookup.utils.log.StdOutErrLog;
import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.Trigger;

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

    private static String configPath = "etc/";
    private static String lookupservicecfg = "lookupservice.yaml";

    private static String logConfig = "./etc/log4j.properties";


    private static Logger LOG;




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
        StdOutErrLog.redirectStdOutErrToLog();

        LOG = Logger.getLogger(Invoker.class);



        Scheduler scheduler = Scheduler.getInstance();

        LookupServiceConfigReader.init(configPath + lookupservicecfg);


        lcfg = LookupServiceConfigReader.getInstance();

        port = lcfg.getPort();
        host = lcfg.getHost();
        //cacheServiceRequest = lcfg.isCacheserviceOn();

        int dbpruneInterval = lcfg.getPruneInterval();
        long prunethreshold = lcfg.getPruneThreshold();

        LOG.info("starting ServiceDAOMongoDb");

        String dburl = lcfg.getDbUrl();
        int dbport = lcfg.getDbPort();
        String collname = lcfg.getCollName();

        List<String> services = new LinkedList<String>();
        // Initialize services
        try {

            if (lcfg.isCoreserviceOn()) {
                new ServiceDAOMongoDb(dburl, dbport, LookupService.LOOKUP_SERVICE, collname);
                services.add(LookupService.LOOKUP_SERVICE);
            }


        } catch (DatabaseException e) {

            LOG.info("Error connecting to database; Please check if MongoDB is running");
            System.exit(1);

        }
        LOG.info("starting Lookup Service");
        // Create the REST service
        Invoker.lookupService = new LookupService(Invoker.host, Invoker.port);

        // Start the service
        Invoker.lookupService.startService(services);

        try {
            //DB Pruning for core LS
            if (lcfg.isCoreserviceOn()) {
                JobDetail job = newJob(MongoDBMaintenanceJob.class)
                        .withIdentity(LookupService.LOOKUP_SERVICE + "clean", "DBMaintenance")
                        .build();
                job.getJobDataMap().put(MongoDBMaintenanceJob.PRUNE_THRESHOLD, prunethreshold);
                job.getJobDataMap().put(MongoDBMaintenanceJob.DBNAME, LookupService.LOOKUP_SERVICE);

                // Trigger the job to run now, and then every dbpruneInterval seconds
                Trigger trigger = newTrigger().withIdentity(LookupService.LOOKUP_SERVICE + "DBTrigger", "DBMaintenance")
                        .startNow()
                        .withSchedule(simpleSchedule()
                                .withIntervalInSeconds(dbpruneInterval)
                                .repeatForever()
                                .withMisfireHandlingInstructionIgnoreMisfires())
                        .build();

                scheduler.schedule(job, trigger);
            }



            JobDetail gcInvoker = newJob(MemoryManager.class)
                    .withIdentity("gc", "MemoryManagement")
                    .build();

            Trigger gcTrigger = newTrigger().withIdentity("gc trigger", "MemoryManagement")
                    .startNow()
                    .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(60)
                            .repeatForever()
                            .withMisfireHandlingInstructionIgnoreMisfires())
                    .build();

            scheduler.schedule(gcInvoker, gcTrigger);


        } catch (Exception se) {
            LOG.error(se.getMessage());

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

    }


}
