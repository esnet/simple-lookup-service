package net.es.lookup.publish;

import net.es.lookup.common.Message;
import net.es.lookup.common.exception.internal.DatabaseException;
import net.es.lookup.database.ServiceDaoMongoDb;
import net.es.lookup.utils.config.reader.QueueServiceConfigReader;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Author: sowmya
 * Date: 3/1/16
 * Time: 2:52 PM
 */
public class PublisherScheduler implements Job {
    private static Logger LOG = Logger.getLogger(PublisherScheduler.class);

    private static final int DEFAULT_BATCHSIZE = 100;


    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Date now = new Date();

        long start = now.getTime();

        LOG.info("Executing Publisher Scheduler");
        LOG.debug("Start time: "+start);

        Publisher publisher = Publisher.getInstance();
        Collection<Queue> queues = publisher.getAllQueues();

        int batchSize = DEFAULT_BATCHSIZE;
        if (QueueServiceConfigReader.getInstance().getBatchSize()>0){
            batchSize = QueueServiceConfigReader.getInstance().getBatchSize();
        }

        ExecutorService executorService = Executors.newFixedThreadPool(batchSize);


        ServiceDaoMongoDb db = ServiceDaoMongoDb.getInstance();

        for (Queue queue : queues) {

            long interval = publisher.getMaxPushInterval();

            Date nextPushTime = new Date(publisher.getLastPushed().getTime() + interval);

            //Push to queue if "x" minutes have passed since last push or if the max Events threshold has reached
            if (nextPushTime.before(now) || (publisher.getCurrentPushEvents() >= publisher.getMaxPushEvents())) {

                LOG.debug("Time to send messages to Queue: "+nextPushTime.before(now));
                LOG.debug("Messages within max events? "+(publisher.getCurrentPushEvents() >= publisher.getMaxPushEvents()));

                List<Message> messages = null;
                try {
                    //DB Optimization - Query only if there are any events
                    if(publisher.getCurrentPushEvents()>0) {
                        LOG.debug("Querying time range:" + publisher.getLastPushed().toString() + "---" + now.toString());
                        messages = db.findRecordsInTimeRange(publisher.getLastPushed(), now);

                    }

                } catch (DatabaseException e) {
                    LOG.error(this.getClass().getName()+"Database error");

                }

                //Push to queue only if there are messages to send

                if (messages != null && !messages.isEmpty()) {
                    Date overhead = new Date();
                    long overheadTime = overhead.getTime();
                    long overheadProcessingTime = overheadTime-start;
                    LOG.debug("Overhead processing in Publisher scheduler"+overheadProcessingTime);


                        for(Message message: messages){

                            PublishTask publish = new PublishTask(queue, message);

                            executorService.execute(publish);

                        }

                    publisher.setLastPushed(now);
                    publisher.setCurrentPushEvents(0);


                }else{
                    LOG.info("net.es.lookup.publish.PublisherScheduler:No messages to send");
                }

            }



        }

        executorService.shutdown();


        Date end = new Date();
        long endTime = end.getTime();
        long totalProcessingTime = endTime-start;
        LOG.debug("net.es.lookup.publish.PublisherScheduler: Total time to Execute Publisher Scheduler "+totalProcessingTime);
    }

}
