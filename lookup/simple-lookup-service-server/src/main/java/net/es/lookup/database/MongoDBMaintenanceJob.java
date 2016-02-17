package net.es.lookup.database;

import net.es.lookup.common.Message;
import net.es.lookup.common.exception.internal.DatabaseException;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobDataMap;

import java.util.Date;


import java.util.List;


public class MongoDBMaintenanceJob implements Job {

    private static Logger LOG = Logger.getLogger(MongoDBMaintenanceJob.class);
    private ServiceDAOMongoDb db;
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

        String dbname = data.getString(DBNAME);
        this.db = DBPool.getDb(dbname);

        long prune_threshold = data.getLong(PRUNE_THRESHOLD);
        Instant now = new Instant();
        Instant pTime = now.minus(prune_threshold);
        DateTime pruneTime = pTime.toDateTime();

        Date daterange = pruneTime.toDate();
        try {

            count = db.deleteExpiredRecords(daterange);

        } catch (DatabaseException e) {

            LOG.error("DBException! Could not query database");

        }





/*        try {

            result = db.queryAll();

        } catch (DatabaseException e) {

            LOG.error("DBException! Could not query database");

        }
        if (result != null && result.size() > 0) {

            for (int i = 0; i < result.size(); i++) {

                Message message = result.get(i);
                DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
                DateTime dt = fmt.parseDateTime(message.getExpires());
                DateTimeComparator dtc = DateTimeComparator.getInstance();

                if (dtc.compare(dt, pruneTime) < 0) {

                    String uri = message.getURI();

                    try {
                         db.deleteRecord(uri);

                    } catch (Exception e) {

                        LOG.error("Error pruning DB!!" + e.getMessage());

                    }

                }

            }

        }*/

    }


}