package net.es.lookup.database;

import net.es.lookup.common.Message;
import net.es.lookup.common.exception.internal.DatabaseException;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Date;
import java.util.List;


public class MongoDBMaintenanceJob implements Job {

    private static Logger LOG = Logger.getLogger(MongoDBMaintenanceJob.class);
    private ServiceDaoMongoDb db;
    public static String PRUNE_THRESHOLD = "prune_threshold"; //parameter will be set during run time
    public static final String DBNAME = "db_name"; //parameter will be set during run time

    public MongoDBMaintenanceJob() {

        LOG.info("Initializing MongoDBPrune...");

    }


    public void execute(JobExecutionContext context) throws JobExecutionException {

        List<Message> result = null;
        long count;
        LOG.info("Running MongoDBPrune...");
        JobDataMap data = context.getJobDetail().getJobDataMap();

        this.db = ServiceDaoMongoDb.getInstance();

        long prune_threshold = data.getLong(PRUNE_THRESHOLD);
        Instant now = new Instant();
        Instant pTime = now.minus(prune_threshold);
        DateTime pruneTime = pTime.toDateTime();

        Date daterange = pruneTime.toDate();
        try {

            count = db.deleteExpiredRecords(daterange);
            LOG.info("Record deleted: "+count);

        } catch (DatabaseException e) {

            LOG.error("DBException! Could not query database");

        }

    }


}