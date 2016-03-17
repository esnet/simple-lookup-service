package net.es.lookup.publish;

import net.es.lookup.common.Message;
import org.quartz.*;

import java.util.Collection;

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

        System.out.println(queues.size());

        for (Queue queue: queues){
            Message message = new Message();
            message.add("type","test");
            message.add("test-name","rabbitmq");



            JobDetail publishInvoker = newJob(PublishJob.class)
                    .withIdentity("publish", "pubsub")
                    .build();


            SimpleTrigger publishTrigger = (SimpleTrigger)newTrigger().withIdentity("publish trigger", "pubsub")
                    .startNow()
                    .build();
            publishInvoker.getJobDataMap().put(PublishJob.MESSAGE, message);
            publishInvoker.getJobDataMap().put(PublishJob.QUEUE, queue);

            net.es.lookup.timer.Scheduler.getInstance().schedule(publishInvoker, publishTrigger);




        }


    }

}
