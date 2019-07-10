package net.es.lookup.database;

import net.es.lookup.common.Message;
import net.es.lookup.common.exception.internal.DatabaseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;

public class ElasticSearchMaintenanceJob implements Job {

  private static Logger LOG = LogManager.getLogger(ElasticSearchMaintenanceJob.class);
  private ServiceElasticSearch db;
  public static String PRUNE_THRESHOLD = "prune_threshold"; // parameter will be set during run time
  public static final String DBNAME = "db_name"; // parameter will be set during run time

  public ElasticSearchMaintenanceJob() {

    LOG.info("Initializing ElasticSearchPrune...");
  }

  public void execute(JobExecutionContext context) throws JobExecutionException {

    List<Message> result = null;
    long count;
    LOG.info("Running ElasticSearchPrune...");
    JobDataMap data = context.getJobDetail().getJobDataMap();

    try {
      connectDB connect = new connectDB();
      ServiceElasticSearch db = connect.connect();
    } catch ( URISyntaxException e) {
      LOG.error("Error in URI");
    }

    long prune_threshold = data.getLong(PRUNE_THRESHOLD);
    Instant now = new Instant();
    Instant pTime = now.minus(prune_threshold);
    DateTime pruneTime = pTime.toDateTime();

    DateTime daterange = pruneTime.toDateTime();
    try {

      count = db.deleteExpiredRecords(daterange);
      LOG.info("Record deleted: " + count);

    } catch (IOException e) {

      LOG.error("IOException! Could not query database");
    }
  }
}
