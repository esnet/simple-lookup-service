package net.es.lookup.publish;

import net.es.lookup.common.Message;
import net.es.lookup.common.exception.internal.DataFormatException;
import net.es.lookup.common.exception.internal.DatabaseException;
import net.es.lookup.database.DBPool;
import net.es.lookup.database.ServiceDAOMongoDb;
import net.es.lookup.protocol.json.JSONMessage;
import org.quartz.*;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Author: sowmya
 * Date: 3/1/16
 * Time: 2:52 PM
 */
public class PublisherScheduler implements Job {

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        System.out.println("Executing Publisher Scheduler");

        Publisher publisher = Publisher.getInstance();
        Collection<Queue> queues = publisher.getAllQueues();

        Date now = new Date();

        //TODO: Get it as config parameter
        long threshold = 120000;

        Date lastOneMinute = new Date(now.getTime() - threshold);

        ServiceDAOMongoDb db = DBPool.getDb("lookup");


        for (Queue queue : queues) {

            if (queue.getLastPushed().before(lastOneMinute)) {
                List<Message> messages = null;
                try {
                    messages = db.findRecordsInTimeRange(queue.getLastPushed(), now);
                    System.out.println("Querying time range:"+queue.getLastPushed().toString()+"---"+now.toString());
                } catch (DatabaseException e) {
                    e.printStackTrace();
                }

                //Push to queue only if there are messages to send

                if (messages != null && !messages.isEmpty()) {
                    try {
                        String jsonMessage = JSONMessage.toString(messages);
                        System.out.println(jsonMessage);


                        JobDetail publishInvoker = newJob(PublishJob.class)
                                .withIdentity("publish", "pubsub")
                                .build();


                        SimpleTrigger publishTrigger = (SimpleTrigger) newTrigger().withIdentity("publish trigger", "pubsub")
                                .startNow()
                                .build();
                        publishInvoker.getJobDataMap().put(PublishJob.MESSAGE, jsonMessage);
                        publishInvoker.getJobDataMap().put(PublishJob.QUEUE, queue);

                        net.es.lookup.timer.Scheduler.getInstance().schedule(publishInvoker, publishTrigger);
                        queue.setLastPushed(now);
                    } catch (DataFormatException e) {
                        e.printStackTrace();
                    }


                }else{
                    System.out.println("No messages to send");
                }




            }

        }


    }

}
