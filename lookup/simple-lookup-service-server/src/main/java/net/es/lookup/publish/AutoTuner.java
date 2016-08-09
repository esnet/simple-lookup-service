package net.es.lookup.publish;

import net.es.lookup.utils.config.reader.AutoTuneConfigReader;
import net.es.lookup.utils.config.reader.QueueServiceConfigReader;

/**
 * Created by Kamala Narayan on 7/20/16.
 */
public class AutoTuner
{

    private static AutoTuner oneInstance;

    //values
    private long pollIntervalMin;
    private long pollIntervalMax;

    private int batchSizeMin;
    private int batchSizeMax;
    private int initialLoad;
    private int loadEstimate;
    private double weight;
    private int prevLoad;

    private boolean isServiceOn;
    private long currentPollInterval;
    private int currentBatchSize;
    private int loadMax;
    private int loadMin;
    private boolean firstRun;


    public boolean getServiceStatus()
    {
        return this.isServiceOn;
    }

    public boolean isFirstRun()
    {
        return firstRun;
    }

    public int getBatchSize()
    {
        return currentBatchSize;
    }

    //values for calculations
    private AutoTuner()
    {

    }

    public static AutoTuner getInstance()
    {
        if(oneInstance==null)
        {
            oneInstance = new AutoTuner();
            oneInstance.initialize();
        }

        return oneInstance;
    }

    private void initialize()
    {
        AutoTuneConfigReader autoTuneConfigReader = AutoTuneConfigReader.getInstance();
        isServiceOn = autoTuneConfigReader.getServiceStatus();
        pollIntervalMin = autoTuneConfigReader.getPollIntervalMin();
        pollIntervalMax = autoTuneConfigReader.getPollIntervalMax();

        initialLoad = autoTuneConfigReader.getIntialLoad();

        weight = autoTuneConfigReader.getWeight();

        loadMax = autoTuneConfigReader.getExpectedLoadMax();
        loadMin = autoTuneConfigReader.getExpectedLoadMin();
        batchSizeMin = autoTuneConfigReader.getBatchSizeMin();
        batchSizeMax = autoTuneConfigReader.getBatchSizeMax();

        loadEstimate = 0;

        isServiceOn = autoTuneConfigReader.getServiceStatus();


        //getting the initial poll interval from queueServiceConfigReader
        currentPollInterval = QueueServiceConfigReader.getInstance().getPollingInterval();
        currentBatchSize = QueueServiceConfigReader.getInstance().getBatchSize();


        //Intialize previous load for calculations.
        prevLoad = initialLoad;

        firstRun = true;
    }



    // two methods for calculation on N and Tpoll
    // poll interval method should be called only after batch size is estimated.
    public long calculateNextPollInterval()
    {
        long nextPollInterval = currentBatchSize / loadEstimate ;
        nextPollInterval = Math.min( Math.max(pollIntervalMin, nextPollInterval), pollIntervalMax );
        currentPollInterval = nextPollInterval; //save state for next calculation
        return nextPollInterval;
    }


    public long calculateNextBatchSize(int currentAggregateLoad)
    {
        firstRun = false;
        loadEstimate = estimateNextLoad(currentAggregateLoad);

        //Linear function
        /**
         *  x-x1    y-y1
         *  ----- = -----
         *  x2-x1   y2-y1
         *
         *  x = Load;
         *  x1 = Load Min;
         *  x2 = Load Max;
         *
         *  y = Batch Size
         *  y1 = Batch Size Min
         *  y2 = Batch Size Max
         *
         *
         *  nextBatchSize  = BSmin + (  ((BSmax - BSmin)/( LOADmax - LOADmin)) * (LOADest - LOADmin)   )
         *
         */

        int batchSizeExtremeDiff = batchSizeMax - batchSizeMin;
        int loadExtremeDiff = loadMax - loadMin;
        int loadEstDiff = loadEstimate - loadMin;
        int estimatedBatchSize  = batchSizeMin +  (loadEstDiff * ((int)(batchSizeExtremeDiff/loadExtremeDiff)));

        currentBatchSize = estimatedBatchSize;
        prevLoad = currentAggregateLoad;
        return estimatedBatchSize;

    }

    private int estimateNextLoad(long currentAggregateLoad)
    {
        //calculate per second load
        long currentLoadPerSecond = currentAggregateLoad / currentPollInterval;
        int nextLoadEstimate = (int) ((1-weight)* currentLoadPerSecond) + (int) (weight* prevLoad);
        return nextLoadEstimate;
    }
}
