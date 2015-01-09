package net.es.lookup.pubsub.amq;

import net.es.lookup.pubsub.QueueDataGenerator;
import net.es.lookup.pubsub.QueueServiceMapping;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;

/**
 * Author: sowmya
 * Date: 1/8/15
 * Time: 4:39 PM
 */
public class PublisherJob implements Job {


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        List<QueueDataGenerator> queueDataGeneratorList = QueueServiceMapping.getAllQueueDataGenerator();
        for(QueueDataGenerator queueDataGenerator: queueDataGeneratorList){
            queueDataGenerator.pushToQueue();
        }
    }
}
