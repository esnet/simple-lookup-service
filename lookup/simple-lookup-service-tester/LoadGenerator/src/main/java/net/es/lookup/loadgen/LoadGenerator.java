package net.es.lookup.loadgen; /**
 * This class receives the data from the log feeder through rabbitmq
 * and sends the request to the sls Core
 */

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.SerializationUtils;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;

import net.es.lookup.rmqmessages.KVGMessage;


/**
 * Created by kamala on 5/27/16.
 */
public class LoadGenerator extends RMQEndPoint implements Consumer
{

    public static final String SUBSCRIBEQUEUE = "Q1";
    public static final String PUBLISHQUEUE = "Q2";

    public static ConcurrentHashMap<Integer,Record> uriMap = new ConcurrentHashMap<Integer, Record>();
    public static AtomicInteger keyIndex = new AtomicInteger(0);
    public static final int HASHMAPSIZELIMIT = 50000;
    public static AtomicInteger currentEntries = new AtomicInteger(0);


    private RequestorThreadPool threadPool;

    /**
     * Constructor with queue name
     */
    public LoadGenerator (String queueName)
    {
        super(queueName);
        threadPool = new RequestorThreadPool();
    }

    /**
     * Gets the next key.
     * @return the key for the hashmap
     */
    public static int getNextKey()
    {

        int key = keyIndex.getAndIncrement();

        if(key == HASHMAPSIZELIMIT)
        {
            keyIndex.set(0);
            key = 0;
        }

        return key % (HASHMAPSIZELIMIT);
    }


    /**
     * Stores the result uri against the specified key.
     * @param uri
     */
    public static synchronized Record putInfo(String uri,String expiresDate)
    {
        int key = getNextKey();
        Record record = new Record(uri,expiresDate);

        int currentNum = currentEntries.get();

        if(currentNum < HASHMAPSIZELIMIT)
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

        Random generator = new Random();
        Object[] values = uriMap.values().toArray();
        Object randomValue = values[generator.nextInt(values.length)];

        return (Record) randomValue;

    }

    /**
     * Registers this object with rabbitmq as a consumer
     */
    public void startConsumer()
    {
        try
        {

            // start consuming messages. Auto acknowledge messages.
            channel.basicConsume(endPointName, true, this);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Called when consumer is registered.
     */
    public void handleConsumeOk(String consumerTag)
    {
        System.out.println("Consumer "+consumerTag +" registered");
    }

    /**
     * Called when new message is available.
     */
    public void handleDelivery(String consumerTag, Envelope env,
                               BasicProperties props, byte[] body) throws IOException
    {
        KVGMessage message = (KVGMessage) SerializationUtils.deserialize(body);
        System.out.println("message:"+message.getMessageId() +" received -- " + message.getMessageType());
        threadPool.sendRequest(message);

    }

    public void handleCancel(String consumerTag) {}
    public void handleCancelOk(String consumerTag) {}
    public void handleRecoverOk(String consumerTag) {}
    public void handleShutdownSignal(String consumerTag, ShutdownSignalException arg1) {}


    public static void main(String[] args)
    {
        Constants.initializeConstants();
        System.err.println("Sls Core: " + Constants.SLSCOREHOSTNAME);

        LoadGenerator loadGenerator = new LoadGenerator(SUBSCRIBEQUEUE);
        loadGenerator.startConsumer();
    }

}
