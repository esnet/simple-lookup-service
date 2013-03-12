package net.es.lookup.pubsub;

import net.es.lookup.common.Message;
import net.es.lookup.common.exception.internal.QueryException;
import net.es.lookup.common.exception.internal.QueueException;
import net.es.lookup.pubsub.amq.AMQueueManager;
import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


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

    static{
        try {
            queueManager = AMQueueManager.getInstance();
            Message query = new Message();
            query.add("operator","all");
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

    }


    public void execute(JobExecutionContext context) throws JobExecutionException {

        if (qid != null) {
            LOG.info("Publisher sending message");
            //JobDataMap data = context.getJobDetail().getJobDataMap();
            count++;
            Message message = new Message();
            message.add("key1", "value1");
            message.add("key2", "value2");
            message.add("key3", "" + count);

            try {
                queueManager.push(qid, message);
            } catch (QueueException e) {
                e.printStackTrace();
            }
        } else {
            LOG.info("Publisher cannot send message. Queue not initialized");
        }

    }


}
