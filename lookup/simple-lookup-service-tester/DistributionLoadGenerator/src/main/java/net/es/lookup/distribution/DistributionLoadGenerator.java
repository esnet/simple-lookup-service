/**
 * Distribution Load Generator:
 *
 * This class generates requests based on a given probability distribution in a
 * configurable interval. The present one is Poisson Distribution with a configurable
 * 'MEAN' parameter.
 *
 * It uses threads to generate the requests to the SLS core.
 */
package net.es.lookup.distribution;
import  java.util.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;


import net.es.lookup.rmqmessages.LGMessage;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.math3.distribution.AbstractIntegerDistribution;
import org.apache.commons.math3.distribution.PoissonDistribution;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;

/**
 * Created by kamala on 6/6/16.
 */
public class DistributionLoadGenerator extends RMQEndPoint
{

    /*Member Declaration*/
    private double mean;
    private AbstractIntegerDistribution distribution;


    public ArrayList<HashMap<String,String>> dataList;
    public Random rand;
    /*End - Member Declaration*/

    /*Static members*/
    public static final String RENEW = "RENEW";
    public static final String REGISTER = "REGISTER";
    public static final String QUEUENAME = "Q2";

    public static final int TOTALRANDOMKEYS= 100;
    public static final int URIMAPSIZELIMIT = 1000;

    public static final int VALIDITY = 2; // in hours
    public static ConcurrentLinkedHashMap<Record,Boolean> renewURImap
            = new ConcurrentLinkedHashMap.Builder<Record,Boolean>().maximumWeightedCapacity(10000).build();

    public static int currentRecordIndex=0;
    public static int currentId=0;
    public static int counter = 0;

    public static AtomicInteger keyIndex = new AtomicInteger(0);
    public static AtomicInteger currentEntries = new AtomicInteger(0);
    public static ConcurrentHashMap<Integer,Record> uriMap;
    /*End - Static Members*/

    /**
     * Constructor
     **/
    public DistributionLoadGenerator(double mean)
    {
        super(QUEUENAME);
        this.mean = mean;
        distribution = new PoissonDistribution(mean);
        uriMap = new ConcurrentHashMap<Integer, Record>();
        dataList = new ArrayList<HashMap<String, String>>();
        rand = new Random();
    }

    /**
     * Generates random data
     */
    public void populateDataList()
    {
        for(int index=0; index < TOTALRANDOMKEYS; index++)
        {
            HashMap<String,String> dataMap = new HashMap<String, String>();

            for(int mapKey=1;mapKey<=10;mapKey++)
            {
                dataMap.put(Integer.toString(mapKey),Double.toString(Math.random()));
            }

            dataMap.put("type","Testing");
            dataList.add(dataMap);
        }
    }

    /**
     * Gets the next key.
     * @return the key for the hashmap
     */
    public static int getNextKey()
    {

        int key = keyIndex.getAndIncrement();
        if(key == URIMAPSIZELIMIT)
        {
            keyIndex.set(0);
            key = 0;
        }

        return key % (URIMAPSIZELIMIT);
    }

    /**
     * Gets a random uri in order to renew it with the sLs core node
     * @return
     */
    public static Record getRandomRecord()
    {
        while(uriMap.size()==0)
        {
            /**
             *  Wait till there is atleast one entry in the map.
             *  Due to latency issues for register, this waiting is absolutely
             * required.
             **/
        }

        double key = (Math.random() * 10000) % uriMap.size();

        Double keyD = new Double(key);
        int intKey = keyD.intValue();

        //randomness
        Record gotRecord = uriMap.get(intKey);
        if(gotRecord==null)
        {
            System.err.println("loop here!");
            return null;
        }

        if(renewURImap.get(gotRecord) != null)
        {
            System.err.println("renew map size:" + renewURImap.keySet().size());
            return null;
        }

        renewURImap.put(gotRecord,true);


        return gotRecord;
    }

    /**
     * Store the uri and it's expiry date
     * @param uri
     * @param expiresDate
     * @return
     */
    public static Record putInfo(String uri,String expiresDate)
    {
        int key = getNextKey();
        Record record = new Record(uri,expiresDate);

        if(uriMap.get(key) != null) // will happen in case of Renew thread accessing this.
        {
            uriMap.put(key, record);
            return record;
        }

        int currentNum = currentEntries.get();
        if(currentNum<URIMAPSIZELIMIT)
        {
            uriMap.put(key, record);
            currentEntries.getAndIncrement();
            record.setIsStored(true);
        }
        else
        {
            record.setIsStored(false);
        }

        return record;

    }

    /**
     * Gets the type of request to generate based on a random number
     * @return
     */
    public String getRequestType()
    {
        double rand = Math.random();
        if(rand > Constants.RATIO)
        {
            return RENEW;
        }

        return REGISTER;
    }




    /**
     * Send to Latency Checker
     * @param message
     */
    public synchronized void publish(LGMessage message)
    {
        try
        {
            channel.basicPublish("", QUEUENAME, null, SerializationUtils.serialize(message));
            System.out.println("Counter "+counter +" MessNumber:"+ message.getMessageId()
                                + " type:" + message.getMessageType()
                                +" uri:"+ message.getUri() + " FINISHED");
        }
        catch(Exception e)
        {
            System.err.println("Error in serializing message");
        }
    }

    public static void main(String[] args)
    {
        long requestTime = 0;

        Constants.initializeConstants();
        System.err.println("Mean = "+ Constants.MEAN);
        System.err.println("Runtime = "+ Constants.RUNLIMIT + " seconds");

        DistributionLoadGenerator distributionLoadGenerator = new DistributionLoadGenerator(Constants.MEAN);
        distributionLoadGenerator.populateDataList();


        ExecutorService executorService = Executors.newFixedThreadPool(500);

        while(true)
        {
            /*Sleep if one second hasn't passed */
            long difference = Constants.SLEEPTIME - requestTime;


            // Get the number of requests/sec for  the distribution.
            int numRequests = distributionLoadGenerator.distribution.sample();

            /**
            * numRequests can be changed here for making the number of requests to be a constant.
            **/

            try
            {
                System.out.println("RequestTime:"+requestTime);
                if(difference>0)
                {
                    Thread.sleep(difference);
                }
                else
                {
                    Thread.sleep(Constants.SLEEPTIME);
                }

                counter++;
                System.out.println("Counter:"+counter +" num:"+ numRequests);

                if(counter > Constants.RUNLIMIT)
                {
                    System.exit(10000);
                }
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }


            long startTime = System.currentTimeMillis();


            CountDownLatch totalLatch = new CountDownLatch(numRequests);
            for(int index=0;index<numRequests;index++)
            {
                String requestType = distributionLoadGenerator.getRequestType();
                HashMap<String, String> map = distributionLoadGenerator.dataList.get(currentRecordIndex);
                RequestSenderThread requestSender
                        = new RequestSenderThread(totalLatch, map, distributionLoadGenerator,currentId,requestType);


                executorService.execute(requestSender);
                currentId++;
                currentRecordIndex++;
                currentRecordIndex = currentRecordIndex % TOTALRANDOMKEYS;


            }


            try
            {
                totalLatch.await();
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }

            long endTime = System.currentTimeMillis();
            requestTime = endTime - startTime;
        }
    }



}
