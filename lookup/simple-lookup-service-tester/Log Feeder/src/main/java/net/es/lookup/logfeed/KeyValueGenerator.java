package net.es.lookup.logfeed; /**
 * KeyValueGenerator:
 * Sends a Key Value message to the rabbitmq server for consumption
 * by the tier 2 services.
 */
import net.es.lookup.rmqmessages.KVGMessage;
import org.apache.commons.lang.SerializationUtils;


import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Reads the number of register/renew messages from the Feeder
 * and sends that number of messages accordingly to the
 * RabbitMQ message queue.
 */
public class KeyValueGenerator extends RMQEndPoint implements Runnable {


    public static final int TOTALENTRIES=1000; // for NOW
    public ArrayList<HashMap<String,String>> dataList;
    public int currentRecordIndex;
    public int currentId;
    public static final String QUEUENAME = "Q1";

    public boolean firstRun;

    public KeyValueGenerator()
    {

        super(QUEUENAME);
        dataList = new ArrayList<HashMap<String,String>>();
        currentRecordIndex=0;
        populateDataList();
        currentId = 1;
        firstRun = true;
    }


    /**
     * Generates random numbers
     */
    public void populateDataList()
    {
        for(int index=0;index<TOTALENTRIES;index++)
        {
            HashMap<String,String> dataMap = new HashMap<String, String>();

            for(int mapKey=1;mapKey<=10;mapKey++)
            {
                dataMap.put(Integer.toString(mapKey),Double.toString(Math.random()));
            }

            //requiored
            dataMap.put("type","test");
            dataList.add(dataMap);

        }
    }


    public void run()
    {

        while( true )
        {
            try
            {
                Runner.kvgSemaphore.acquire();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            long numRenewRequests= Runner.currentRenewRequests;
            long numRegisterRequests = Runner.currentRegisterRequests;


            //ignore renew requests, as we have no data
            for( long count=0; count<numRegisterRequests; count++ )
            {
                // send as many messages to RabbitMQ server.
                KVGMessage message = new KVGMessage();

                HashMap<String,String> map = dataList.get(currentRecordIndex);

                //set Message parameters
                map.put("Id",Integer.toString(currentId));
                message.setMap(map);
                message.setMessageId(currentId);
                message.setMessageType(KVGMessage.REGISTER);

                currentId++;
                currentRecordIndex++;
                firstRun=false;

                if(currentRecordIndex==dataList.size())
                {
                    currentRecordIndex=0;
                }

                try
                {
                    sendMessage(message);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    System.err.println("Error in sending message Id" + (currentRecordIndex-1));
                }
            }


            if(firstRun==false) // ignore renew for first run as we have no data.
            {

                for(long count=0; count < numRenewRequests;count++)
                {

                    KVGMessage message = new KVGMessage();

                    message.setMessageId(currentId);
                    message.setMessageType(KVGMessage.RENEW);

                    currentId++;
                    currentRecordIndex++;
                    if(currentRecordIndex==dataList.size())
                    {
                        currentRecordIndex=0;
                    }

                    try
                    {
                        sendMessage(message);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                        System.err.println("Error in sending message Id" + (currentRecordIndex-1));
                    }

                }

            }

            //release the feederSemaphore
            Runner.feederSemaphore.release();
        }
    }


    public void sendMessage(Serializable object) throws IOException
    {

        channel.basicPublish("",QUEUENAME, null, SerializationUtils.serialize(object));
    }

}
