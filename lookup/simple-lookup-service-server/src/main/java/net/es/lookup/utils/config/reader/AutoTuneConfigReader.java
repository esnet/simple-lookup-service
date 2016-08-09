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
    private static final String SERVICE = "service";
    private static final String EXPECTEDLOAD = "expected_load";

    //values
    private String serviceStatus;
    private long pollIntervalMin;
    private long pollIntervalMax;
    private int batchSizeMin;
    private int batchSizeMax;
    private int intialLoad;
    private double weight;

    private int expectedLoadMax;
    private int expectedLoadMin;

    private static Logger LOG = Logger.getLogger(BaseConfigReader.class);

    /*getters for the values*/
    public int getBatchSizeMax()
    {
        return batchSizeMax;
    }

    public int getBatchSizeMin()
    {
        return batchSizeMin;
    }

    public int getIntialLoad()
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

    public int getExpectedLoadMax(){
        return expectedLoadMax;
    }

    public int getExpectedLoadMin(){
        return expectedLoadMin;
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
            batchSizeMax = (Integer) batchSizeMap.get(MAX);
            batchSizeMin = (Integer) batchSizeMap.get(MIN);

            weight = (Double) yamlMap.get(WEIGHT);
            intialLoad = (Integer) yamlMap.get(INTIALLOAD);

            HashMap<String,Object> expectedLoadMap = (HashMap<String,Object>) yamlMap.get(EXPECTEDLOAD);
            expectedLoadMax = (Integer)expectedLoadMap.get(MAX);
            expectedLoadMin = (Integer)expectedLoadMap.get(MIN);
        }
        catch(Exception e)
        {
            LOG.error("Error parsing autotune config file. Please check config parameters " + e.toString());
            System.exit(1);
        }

    }


}
