package net.es.lookup.publish;

import net.es.lookup.common.exception.internal.PubSubQueueException;
import net.es.lookup.publish.rabbitmq.RMQueue;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Date;

/**
 * Author: sowmya
 * Date: 3/8/16
 * Time: 2:00 PM
 */
public class PublishJob implements Job {

    public final static String PUBLISHER= "publisher" ;
    public final static String QUEUE = "queue";
    public final static String MESSAGE = "message";

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        Date now = new Date();

        long start = now.getTime();

        System.out.println("Executing PublishJob"+start);

        JobDataMap data = jobExecutionContext.getJobDetail().getJobDataMap();

        RMQueue queue = (RMQueue)data.get(QUEUE);

        String message = (String)data.get(MESSAGE);
        System.out.println("Sending message"+message);
        try {
            queue.push(message);
        } catch (PubSubQueueException e) {
            e.printStackTrace();
        }

        Date end = new Date();
        long endTime = end.getTime();
        long totalProcessingTime = endTime-start;
        System.out.println("Total time to Execute PublishJob"+jobExecutionContext.getFireInstanceId()+totalProcessingTime);


    }
}
