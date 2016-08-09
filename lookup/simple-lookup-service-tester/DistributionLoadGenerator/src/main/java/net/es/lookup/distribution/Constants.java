package net.es.lookup.distribution;

import java.util.HashMap;

/**
 * Created by kamala on 8/1/16.
 */
public class Constants
{

    public static final String pathName =  "/lookup/records";
    public static final int SLEEPTIME = 1000; // 1 second

    public static String sLSCoreHostName = "";
    public static int RUNLIMIT = -1; // runs the loop for this many units of sleeptime.
    public static int MEAN = -1; // mean number of requests.
    public static double RATIO = -1; //80% register and 20% renew


    private static final String configFile = "etc/distributionloadgenerator.yaml";


    public static void initializeConstants()
    {
        HashMap<String,Object> constantsMap = (HashMap<String, Object>) BaseConfigReader.getInstance().getConfiguration(configFile);

        sLSCoreHostName = "http://"+ (String) constantsMap.get("sls_core_host_name") + ":8090";

        RUNLIMIT = (Integer) constantsMap.get("run_time");

        MEAN = (Integer) constantsMap.get("mean");

        RATIO = (Double) constantsMap.get("ratio");



    }
}


