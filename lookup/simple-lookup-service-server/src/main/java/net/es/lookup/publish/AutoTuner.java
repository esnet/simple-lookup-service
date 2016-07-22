package net.es.lookup.publish;

import net.es.lookup.utils.config.reader.AutoTuneConfigReader;

/**
 * Created by Kamala Narayan on 7/20/16.
 */
public class AutoTuner
{

    private static AutoTuner oneInstance;

    //values
    private boolean isServiceOn;
    private long pollIntervalMin;
    private long pollIntervalMax;
    private long batchSizeMin;
    private long batchSizeMax;
    private long intialLoad;
    private double weight;


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
        batchSizeMin = autoTuneConfigReader.getBatchSizeMin();
        batchSizeMax = autoTuneConfigReader.getBatchSizeMax();
        intialLoad = autoTuneConfigReader.getIntialLoad();
        weight = autoTuneConfigReader.getWeight();
    }
}
