package net.es.lookup.utils.config.reader;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: Kamala Narayan
 * Date: 19th July 2016
 * Time: 3:00 pm
 */
public class AutoTuneConfigReader {


    private static AutoTuneConfigReader instance;


    //file Info
    private static final String DEFAULT_FILE = "autotune.yaml";
    private static final String DEFAULT_PATH = "etc";
    private static String configFile = DEFAULT_PATH + "/" + DEFAULT_FILE;

    //file parameter label info
    private static final String BATCHSIZE = "batch_size";
    private static final String POLLINTERVAL= "poll_interval";
    private static final String MAX = "max";
    private static final String MIN = "min";
    private static final String WEIGHT = "weight";
    private static final String INTIALLOAD = "initial_load";
    private static final String SERVICE = "Service";

    //values
    private String serviceStatus;
    private long pollIntervalMin;
    private long pollIntervalMax;
    private long batchSizeMin;
    private long batchSizeMax;
    private long intialLoad;
    private double weight;


    private static Logger LOG = Logger.getLogger(BaseConfigReader.class);

    /*getters for the values*/
    public long getBatchSizeMax()
    {
        return batchSizeMax;
    }

    public long getBatchSizeMin()
    {
        return batchSizeMin;
    }

    public long getIntialLoad()
    {
        return intialLoad;
    }

    public long getPollIntervalMax()
    {
        return pollIntervalMax;
    }

    public long getPollIntervalMin()
    {
        return pollIntervalMin;
    }

    public boolean getServiceStatus()
    {
        if(serviceStatus.equals("on"))
        {
            return true;
        }

        return false;
    }

    public double getWeight()
    {
        return weight;
    }



    /*private constructor*/
    private AutoTuneConfigReader()
    {

    }

    /*Singleton*/
    public static AutoTuneConfigReader getInstance()
    {
        if(instance==null)
        {
            instance = new AutoTuneConfigReader();
            instance.setInfo(configFile);
        }

        return instance;
    }



    private void setInfo(String configPath)
    {

        BaseConfigReader cfg = BaseConfigReader.getInstance();
        Map yamlMap = cfg.getConfiguration(configPath);
        assert yamlMap != null : "Could not load configuration file from " +
                "file: ${basedir}/" + configPath;

        try
        {
            //service status
            serviceStatus = (String) yamlMap.get(SERVICE);

            HashMap<String,Object> pollIntervalMap = (HashMap<String,Object>) yamlMap.get(POLLINTERVAL);
            pollIntervalMax = (Long) pollIntervalMap.get(MAX);
            pollIntervalMin = (Long) pollIntervalMap.get(MIN);

            HashMap<String,Object> batchSizeMap = (HashMap<String,Object>) yamlMap.get(BATCHSIZE);
            batchSizeMax = (Long) batchSizeMap.get(MAX);
            batchSizeMin = (Long) batchSizeMap.get(MIN);

            weight = (Double) yamlMap.get(WEIGHT);
            intialLoad = (Long) yamlMap.get(INTIALLOAD);
        }
        catch(Exception e)
        {
            LOG.error("Error parsing config file. Please check config parameters " + e.toString());
            System.exit(1);
        }

    }


}
