package net.es.lookup.pubsub.client.failover;

import net.es.lookup.client.Subscriber;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Author: sowmya
 * Date: 12/19/13
 * Time: 9:45 PM
 */
public class FailureRecovery implements Job {

    private static final int THRESHOLD = 900;
    private static final int AGGRESSIVE_PING_PERIOD = 120;
    private static final int REGULAR_PING_PERIOD=5*AGGRESSIVE_PING_PERIOD;
    private static final int MAX_RECONNECTION_ATTEMPTS=10;

    private List<FailedConnection> failedConnections = null;
    private static Logger LOG = Logger.getLogger(FailureRecovery.class);

    public FailureRecovery(){
        failedConnections = new LinkedList<FailedConnection>();
    }

    public FailureRecovery(List<FailedConnection> failedConnectionList){
        failedConnections = failedConnectionList;
    }

    public void addFailedConnection(FailedConnection failedConnection){
        failedConnections.add(failedConnection);
    }

    public FailedConnection removeFailedConnection(FailedConnection failedConnection){
        if(failedConnections.contains(failedConnection)){
            failedConnections.remove(failedConnection);
            return failedConnection;
        }

        return null;
    }

    public FailedConnection removeFailedConnection(Subscriber subscriber){
        int index=-1;
        for(FailedConnection cf: failedConnections){
            if(cf.getSubscriber().equals(subscriber)){
                index = failedConnections.indexOf(cf);
            }

        }

        if(index>=0){
            FailedConnection cf = failedConnections.get(index);
            failedConnections.remove(index);
            return cf;
        }

        return null;
    }

    public static int getAggressivePingPeriod() {

        return AGGRESSIVE_PING_PERIOD;
    }

    public static int getRegularPingPeriod() {

        return REGULAR_PING_PERIOD;
    }

    public static int getMaxReconnectionAttempts() {

        return MAX_RECONNECTION_ATTEMPTS;
    }

    public static int getThreshold() {

        return THRESHOLD;
    }

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Date date = new Date();
        long now = date.getTime();

        List<Integer> connectionIndex = new LinkedList<Integer>();

        LOG.debug("Executing failure recovery");
        for (FailedConnection failedConnection : failedConnections){

            //if time period is within the aggressive ping thresold
            if((failedConnection.getTimeOfInitialFailure()-now) <= THRESHOLD){
                boolean res = failedConnection.reconnect();

                if(res){
                    connectionIndex.add(failedConnections.indexOf(failedConnection));
                }
            }else{
                if(failedConnection.getTimeOfLastFailure() >= REGULAR_PING_PERIOD){
                    boolean res = failedConnection.reconnect();
                    if(res){
                        connectionIndex.add(failedConnections.indexOf(failedConnection));
                    }
                }
            }

            for (Integer index: connectionIndex){
                failedConnections.remove(index);
            }


        }

    }
}
