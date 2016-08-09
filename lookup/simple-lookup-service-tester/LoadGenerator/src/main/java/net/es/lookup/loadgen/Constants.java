package net.es.lookup.loadgen;

import net.es.lookup.loadgen.BaseConfigReader;

import java.util.HashMap;

/**
 * Created by kamala on 8/4/16.
 */
public class Constants
{

    public static int NUMTHREADS = -1;
    public static String SLSCOREHOSTNAME  = "";
    public static String SLSCOREENDPOINT = "";
    public static final String configFile = "etc/loadgenerator.yaml";
    public static final String PATHNAME =  "/lookup/records";

    public static void initializeConstants()
    {
        // read from yaml file and assign values.
        HashMap<String,Object> constantMap = (HashMap<String, Object>) BaseConfigReader.getInstance().getConfiguration(configFile);
        NUMTHREADS = (Integer) constantMap.get("number_of_threads");
        SLSCOREHOSTNAME = (String) constantMap.get("sls_core_host");

        SLSCOREENDPOINT = "http://"+ SLSCOREHOSTNAME +":8090";



    }
}
