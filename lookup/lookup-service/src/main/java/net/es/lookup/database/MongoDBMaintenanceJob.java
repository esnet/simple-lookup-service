package net.es.lookup.database;

import net.es.lookup.common.Service;

import net.es.lookup.common.Message;
import net.es.lookup.common.ReservedKeywords;
import net.es.lookup.common.exception.internal.DatabaseException;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.DateTimeComparator;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;

import org.apache.log4j.Logger;

@DisallowConcurrentExecution
public class MongoDBMaintenanceJob implements Job{

	private static Logger LOG = Logger.getLogger(MongoDBMaintenanceJob.class);
	private ServiceDAOMongoDb db;
	public static String PRUNE_THRESHOLD = "prune_threshold"; //parameter will be set during run time


	public MongoDBMaintenanceJob(){

		this.db = ServiceDAOMongoDb.getInstance();

	}

	
	public void execute(JobExecutionContext context) throws JobExecutionException {

        List<Service> result = null;
        LOG.info("Running MongoDBPrune...");
        JobDataMap data = context.getJobDetail().getJobDataMap();
        long prune_threshold = data.getLong(PRUNE_THRESHOLD);
        Instant now = new Instant();
        Instant pTime = now.minus(prune_threshold);
        DateTime pruneTime = pTime.toDateTime();

        try{

            result = db.queryAll();

        }catch(DatabaseException e){

            LOG.error("DBException! Could not query database");

        }

        List<Message> messages = new ArrayList<Message>();

        if(result != null && result.size()>0){

            for (int i=0; i<result.size(); i++){

                Map m = result.get(i).getMap();
                DateTimeFormatter fmt =  ISODateTimeFormat.dateTime();
                DateTime dt = fmt.parseDateTime((String)m.get(ReservedKeywords.RECORD_EXPIRES));
                DateTimeComparator dtc =  DateTimeComparator.getInstance();

                if(dtc.compare(dt,pruneTime)<0){

                    String uri = (String)m.get(ReservedKeywords.RECORD_URI);

                    try{

                        messages.add(db.deleteService(uri));

                    }catch(Exception e){

                        LOG.error("Error pruning DB!!");

                    }

                }

            }

        }

	}

    
}