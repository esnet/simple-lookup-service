package net.es.lookup.publish;

import net.es.lookup.common.exception.internal.PubSubQueueException;
import net.es.lookup.publish.rabbitmq.RMQueue;
import org.apache.log4j.Logger;

import java.util.Date;

/**
 * Author: sowmya
 * Date: 3/8/16
 * Time: 2:00 PM
 */
class PublishTask implements Runnable {

    public final static String PUBLISHER= "publisher" ;
    public final static String QUEUE = "queue";
    public final static String MESSAGE = "message";
    private static Logger LOG = Logger.getLogger(PublishTask.class);

    private RMQueue queue;

    private String message;

    public PublishTask(Queue queue, String message){
        this.queue = queue;
        this.message = message;

    }


    public void run() {

        LOG.debug("net.es.lookup.publish.PublishJob: Pushing to queue. Thread id: "+ Thread.currentThread().getId());
        Date now = new Date();

        long start = now.getTime();

        LOG.debug("Sending message"+message);
        try {
            queue.push(message);
        } catch (PubSubQueueException e) {
            e.printStackTrace();
        }

        Date end = new Date();
        long endTime = end.getTime();
        long totalProcessingTime = endTime-start;
        LOG.debug("Total time to Execute PublishJob: "+totalProcessingTime);

    }
}
