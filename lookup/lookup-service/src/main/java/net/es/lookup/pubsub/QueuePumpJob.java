package net.es.lookup.pubsub;

import net.es.lookup.common.Message;
import net.es.lookup.common.Service;
import net.es.lookup.common.exception.internal.DatabaseException;
import net.es.lookup.common.exception.internal.QueryException;
import net.es.lookup.common.exception.internal.QueueException;
import net.es.lookup.database.ServiceDAOMongoDb;
import net.es.lookup.protocol.json.JSONSubRequest;
import net.es.lookup.pubsub.amq.AMQueueManager;
import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;


/**
 * User: sowmya
 * Date: 2/25/13
 * Time: 2:50 PM
 */
@DisallowConcurrentExecution
public class QueuePumpJob implements Job {

    private static Logger LOG = Logger.getLogger(QueuePumpJob.class);
    private static QueueManager queueManager;
    private static String qid = "";
    private static int count = 0;
    private static ServiceDAOMongoDb databaseInstance;

    static{
        try {
            queueManager = AMQueueManager.getInstance();
            String squery = "";
            JSONSubRequest query = new JSONSubRequest(squery);
            qid = queueManager.getQueues(query).get(0);
            LOG.info("Created queue with id: " + qid);
            System.out.println("created queue:" + qid);
        } catch (QueueException e) {
            System.out.println("Queue Exception caught");
            System.out.println(e.getMessage());
        } catch (QueryException e){
            System.out.println("Query Exception caught");
        }
    }

    public QueuePumpJob() {
        databaseInstance = ServiceDAOMongoDb.getInstance();
    }


    public void execute(JobExecutionContext context) throws JobExecutionException {

        if (qid != null) {
            LOG.info("Publisher sending message");
            //JobDataMap data = context.getJobDetail().getJobDataMap();
            count++;
            try {
                Message message = databaseInstance.queryAll().get(0);
                queueManager.push(qid, message);
            } catch (DatabaseException e) {
                e.printStackTrace();
            } catch (QueueException e) {
                e.printStackTrace();
            }
        } else {
            LOG.info("Publisher cannot send message. Queue not initialized");
        }

    }


}
