package net.es.lookup.pubsub.client.failover;

import net.es.lookup.client.Subscriber;
import net.es.lookup.common.exception.LSClientException;
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

    private List<ConnectionFailure> failedConnections = null;

    public FailureRecovery(){
        failedConnections = new LinkedList<ConnectionFailure>();
    }

    public FailureRecovery(List<ConnectionFailure> failedConnectionList){
        failedConnections = failedConnectionList;
    }

    public void addFailedConnection(ConnectionFailure connectionFailure){
        failedConnections.add(connectionFailure);
    }

    public ConnectionFailure removeFailedConnection(ConnectionFailure connectionFailure){
        if(failedConnections.contains(connectionFailure)){
            failedConnections.remove(connectionFailure);
            return connectionFailure;
        }

        return null;
    }

    public ConnectionFailure removeFailedConnection(Subscriber subscriber){
        int index=-1;
        for(ConnectionFailure cf: failedConnections){
            if(cf.getSubscriber().equals(subscriber)){
                index = failedConnections.indexOf(cf);
            }

        }

        if(index>=0){
            ConnectionFailure cf = failedConnections.get(index);
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

        for (ConnectionFailure connectionFailure: failedConnections){

            //if time period is within the aggressive ping thresold
            if((connectionFailure.getTimeOfInitialFailure()-now) <= THRESHOLD){
                boolean res = connectionFailure.reconnect();

                if(res){
                    connectionIndex.add(failedConnections.indexOf(connectionFailure));
                }
            }else{
                if(connectionFailure.getTimeOfLastFailure() >= REGULAR_PING_PERIOD){
                    boolean res = connectionFailure.reconnect();
                    if(res){
                        connectionIndex.add(failedConnections.indexOf(connectionFailure));
                    }
                }
            }

            for (Integer index: connectionIndex){
                failedConnections.remove(index);
            }


        }

    }
}
