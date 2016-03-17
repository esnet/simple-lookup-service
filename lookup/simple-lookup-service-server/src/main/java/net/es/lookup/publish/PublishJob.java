package net.es.lookup.publish;

import net.es.lookup.common.Message;
import net.es.lookup.common.exception.internal.PubSubQueueException;
import net.es.lookup.publish.rabbitmq.RMQueue;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

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
       /* JobDataMap data = jobExecutionContext.getJobDetail().getJobDataMap();

        Publisher publisherObject = (Publisher)data.get(PUBLISHER);

        Set<String> queues =  publisherObject.getAllQueues();

        ServiceDAOMongoDb dbInstance = DBPool.getDb("lookup");
        for(String queueName: queues){
            Date lastPushed = publisherObject.getLastPushed();
            try {
                List<Message> messages = dbInstance.queryRecordsAfterTime(lastPushed);
                publisherObject.publish(queueName,messages);

            } catch (DatabaseException e) {
                e.printStackTrace();
            } catch (PubSubQueueException e) {
                e.printStackTrace();
            }

        }

        publisherObject.setLastPushed(new Date());*/

        JobDataMap data = jobExecutionContext.getJobDetail().getJobDataMap();

        RMQueue queue = (RMQueue)data.get(QUEUE);

        Message message = (Message)data.get(MESSAGE);
        System.out.println("Sending message");
        try {
            queue.push(message);
        } catch (PubSubQueueException e) {
            e.printStackTrace();
        }


    }
}
