package net.es.lookup.service;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.es.lookup.common.MemoryManager;
import net.es.lookup.common.exception.internal.DatabaseException;
import net.es.lookup.database.ElasticSearchMaintenanceJob;
import net.es.lookup.database.ServiceElasticSearch;
import net.es.lookup.timer.Scheduler;
import net.es.lookup.utils.config.reader.LookupServiceConfigReader;
import net.es.lookup.utils.config.reader.QueueServiceConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
  private static int elasticPort = 9300;
  private static int restClientPort = 9301;
  private static String elasticHost = "localhost";
  private static LookupService lookupService = null;

  private static String host = "localhost";
  private static LookupServiceConfigReader lookupServiceConfigReader;
  private static QueueServiceConfigReader queueServiceConfigReader;

  private static String configPath = "etc/";
  private static final String lookupservicecfg = "lookupservice.yaml";
  // private static final String queuecfg = "queueservice.yaml";

  private static String logConfig = "./etc/log4j2.properties";

  private static Logger LOG;

  /**
   * Main program to start the Lookup ServiceRecord.
   *
   * @param args [-h, ?] for help [-p server-port
   * @throws Exception throws generic exception
   */
  public static void main(String[] args) throws Exception {

    parseArgs(args);
    // set log config
    System.setProperty("log4j2.warn", "true");
    System.setProperty("log4j.configurationFile", logConfig);

    LOG = LogManager.getLogger(Invoker.class.getName());
    // StdOutErrToLog.redirectStdOutErrToLog();

    LookupServiceConfigReader.init(configPath + lookupservicecfg);
    // QueueServiceConfigReader.init(configPath + queuecfg);

    lookupServiceConfigReader = LookupServiceConfigReader.getInstance();
    // queueServiceConfigReader = QueueServiceConfigReader.getInstance();

    port = lookupServiceConfigReader.getPort();

    host = lookupServiceConfigReader.getHost();
    restClientPort = lookupServiceConfigReader.getElasticRestClientPort();
    elasticPort = lookupServiceConfigReader.getElasticServerPort();
    elasticHost = lookupServiceConfigReader.getElasticServer();

    LOG.info("starting ServiceElasticSearch");

    String dbname = lookupServiceConfigReader.getElasticDbName();

    List<String> services = new LinkedList<>();

    // Initialize services
    try {
      new ServiceElasticSearch(elasticHost, elasticPort, restClientPort, dbname);
    } catch (DatabaseException e) {
      LOG.fatal("Unable to initialize database" + e.getMessage());
      System.exit(-1);
    }
    services.add(LookupService.LOOKUP_SERVICE);

    LOG.info("starting Lookup Service");
    // Create the REST service
    Invoker.lookupService = new LookupService(Invoker.host, Invoker.port);

    // Start the service
    Invoker.lookupService.startService();

    // DB Pruning
    Scheduler scheduler = Scheduler.getInstance();
    int dbpruneInterval = lookupServiceConfigReader.getPruneInterval();
    long prunethreshold = lookupServiceConfigReader.getPruneThreshold();

     JobDetail job =
        newJob(ElasticSearchMaintenanceJob.class)
            .withIdentity(LookupService.LOOKUP_SERVICE + "clean", "DBMaintenance")
            .build();
    job.getJobDataMap().put(ElasticSearchMaintenanceJob.PRUNE_THRESHOLD, prunethreshold);
    job.getJobDataMap().put(ElasticSearchMaintenanceJob.DBNAME, dbname);

    // Trigger the job to run now, and then every dbpruneInterval seconds
    Trigger trigger =
        newTrigger()
            .withIdentity(LookupService.LOOKUP_SERVICE + "DBTrigger", "DBMaintenance")
            .startNow()
            .withSchedule(
                simpleSchedule()
                    .withIntervalInSeconds(dbpruneInterval)
                    .repeatForever()
                    .withMisfireHandlingInstructionIgnoreMisfires())
            .withPriority(Thread.MAX_PRIORITY)
            .build();

    scheduler.schedule(job, trigger);

    /*   if (queueServiceConfigReader != null && queueServiceConfigReader.isServiceOn()) {

      PublishService publishService = PublishService.getInstance();
      publishService.setMaxPushEvents(queueServiceConfigReader.getBatchSize());
      publishService.setMaxInterval(queueServiceConfigReader.getPushInterval());
      publishService.setHost(queueServiceConfigReader.getHost());
      publishService.setPort(queueServiceConfigReader.getPort());
      publishService.setUserName(queueServiceConfigReader.getUserName());
      publishService.setPassword(queueServiceConfigReader.getPassword());
      publishService.setVhost(queueServiceConfigReader.getVhost());
      publishService.setPollingInterval(queueServiceConfigReader.getPollingInterval());
      publishService.setExchangeName(queueServiceConfigReader.getExchangeName());
      publishService.setExchangeType(queueServiceConfigReader.getExchangeType());
      publishService.setExchangeDurability(queueServiceConfigReader.getExchangeDurability());
      publishService.startService();
    }*/

    JobDetail gcInvoker =
        newJob(MemoryManager.class).withIdentity("gc", "MemoryManagement").build();

    Trigger gcTrigger =
        newTrigger()
            .withIdentity("gc trigger", "MemoryManagement")
            .startNow()
            .withSchedule(
                simpleSchedule()
                    .withIntervalInMinutes(10)
                    .repeatForever()
                    .withMisfireHandlingInstructionIgnoreMisfires())
            .build();

    scheduler.schedule(gcInvoker, gcTrigger);

    // Block forever
    Object blockMe = new Object();
    synchronized (blockMe) {
      blockMe.wait();
    }
  }

  private static void parseArgs(String[] args) throws java.io.IOException {

    OptionParser parser = new OptionParser();
    parser.acceptsAll(asList("h", "?"), "show help then exit");
    OptionSpec<String> argPort =
        parser.accepts("p", "server port").withRequiredArg().ofType(String.class);
    OptionSpec<String> argHost = parser.accepts("h", "host").withRequiredArg().ofType(String.class);
    OptionSpec<String> argConfigPath =
        parser.accepts("c", "configPath").withRequiredArg().ofType(String.class);
    OptionSpec<String> argLogPath =
        parser.accepts("l", "logConfig").withRequiredArg().ofType(String.class);

    OptionSet options = parser.parse(args);

    // check for help
    if (options.has("?")) {
      parser.printHelpOn(System.out);
      System.exit(0);
    }

    if (options.has(argPort)) {

      port = Integer.parseInt(options.valueOf(argPort));
    }

    if (options.has(argHost)) {
      host = options.valueOf(argHost);
    }

    if (options.has(argConfigPath)) {
      configPath = options.valueOf(argConfigPath);
      System.out.println("Config files Path:" + configPath);
    }

    if (options.has(argLogPath)) {

      logConfig = options.valueOf(argLogPath);
    }
  }
}
