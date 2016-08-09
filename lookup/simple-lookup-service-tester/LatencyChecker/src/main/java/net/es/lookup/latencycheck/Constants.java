package net.es.lookup.latencycheck;

import java.util.HashMap;

/**
 * Created by Kamala Narayan on 8/2/16.
 *
 * This class contains all the constants that are used by the
 * latency checker and related classes. These constants are obtained
 * from a the latency Checker yaml file and are initialized.
 */
public class Constants
{

    private static final String configFile = "etc/latencychecker.yaml";
    private static final String QUERY= "/perfsonar/records/_search";

    public static long N = -1 ;
    public static long M = -1 ;
    public static double T = -1;

    public static  int POLLINTERVAL = -1;
    public static int POLLTOTAL = -1;

    public static  String INDEX= "" ;
    public static  String INDEXENDPOINT = "" ;
    public static String SLSCOREHOST = "";
    public static String DATASTOREHOST = "";


    public static String SLSCACHEENDPOINT = "";
    public static String MAPPINGENDPOINT = "";
    public static String DATASTOREENDPOINT = "";

    public static String MAXTHREADWAITTIME = "";


    public static void intitializeConstants()
    {
        // read from yaml file and assign values.
        HashMap<String,Object> constantMap = (HashMap<String, Object>) BaseConfigReader.getInstance().getConfiguration(configFile);

        //From User
        M = (Integer) constantMap.get("M");
        N = (Integer) constantMap.get("N");
        T = (Integer) constantMap.get("T");

        INDEX = (String) constantMap.get("data_store_index");

        INDEXENDPOINT = (String) constantMap.get("data_store_type");

        SLSCOREHOST = (String) constantMap.get("sls_cache_host_name");

        DATASTOREHOST = (String) constantMap.get("latency_data_store");

        MAXTHREADWAITTIME = (String) constantMap.get("maximum_thread_waiting_time");

        POLLTOTAL = (Integer) constantMap.get("number_of_polls");


        //Derived
        SLSCACHEENDPOINT = "http://"+ Constants.SLSCOREHOST+":9200"+ QUERY;
        MAPPINGENDPOINT = "http://"+Constants.DATASTOREHOST+":9200"+ Constants.INDEX;
        DATASTOREENDPOINT = "http://"+Constants.DATASTOREHOST+":9200"+ Constants.INDEX+ Constants.INDEXENDPOINT;
        POLLINTERVAL = (Integer) constantMap.get("cache_poll_interval") * 1000;





    }
}
