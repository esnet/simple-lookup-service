package net.es.lookup.latencycheck; /**
 * Created by Kamala Narayan on 6/1/16.
 *
 * This class is used for calculating the overall latency it takes for a message
 * to reach the sLS cache from the core via the message queue.
 *
 * This latency measurement is done by calculating the difference between the
 * created in Cache timestamps and the expires field (T- tn hours). This T-tn is
 * assumed to be the time at which a successful response was received for that particular
 * message.
 *
 * The check to see if the record has been reached is made once in every 30 seconds
 */


import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;

import net.es.lookup.rmqmessages.LGMessage;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.rabbitmq.client.Consumer;
import org.apache.commons.lang.SerializationUtils;

import java.io.IOException;

import java.util.Date;


import java.util.concurrent.ConcurrentHashMap;

public class LatencyChecker extends RMQEndPoint implements Consumer
{
    private CheckerThreadPool checkerThreadPool;
    public static final String SUBSCRIBEQUEUE = "Q2";
    public static ConcurrentHashMap<String,Date> uriExpiryMap = new ConcurrentHashMap<String, Date>();

    public static final String  mappingJSONString=  "{\"mappings\": " +
        "{\"latency\": " +
        "{\"properties\":         " +
        "{\"latency\":  {\"type\":\"long\"},       " +
        "\"M\":{\"type\":\"long\"}," +
        "\"N\":{\"type\":\"long\"}," +
        "\"T\":{\"type\":\"long\"}," +
        "\"creationTime:\":{\"type\":\"date\",\"format\":\"strict_date_optional_time||epoch_millis\"}," +
        "\"expires\":{\"type\":\"date\",\"format\":\"strict_date_optional_time||epoch_millis\"}," +
        "\"messageType\":{\"type\":\"string\",\"index\":\"not_analyzed\"}," +
        "\"uri\":{\"type\":\"string\",\"index\":\"not_analyzed\"}," +
        "\"result\":{\"type\":\"string\",\"index\":\"not_analyzed\"}" +
        "}}}}";

    static
    {
        System.setProperty("org.apache.commons.logging.Log",
                "org.apache.commons.logging.impl.NoOpLog");
    }



    /*Constructor*/
    public LatencyChecker()
    {
        super(SUBSCRIBEQUEUE);
        checkerThreadPool = new CheckerThreadPool();
    }



    /**
     * Registers this object with rabbitmq as a consumer
     */
    public void startConsumer()
    {
        try
        {
            // start consuming messages. Auto acknowledge messages.
            channel.basicConsume(SUBSCRIBEQUEUE, true, this);
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
                               AMQP.BasicProperties props, byte[] body) throws IOException
    {
        LGMessage message = (LGMessage) SerializationUtils.deserialize(body);


        //System.err.println("messsage:" + message.getMessageId() + "uri:" + message.getUri() + " REC");
        if(message.getMessageType().equals("REGISTER"))
        {
            if(message.getIsStored()== true && LatencyChecker.uriExpiryMap.get(message.getUri())==null)
            {
                LatencyChecker.uriExpiryMap.put(message.getUri() , message.getExpiresDate());

            }

        }





        checkerThreadPool.checkLatency(message);
    }

    public void handleCancel(String consumerTag) {}
    public void handleCancelOk(String consumerTag) {}
    public void handleRecoverOk(String consumerTag) {}
    public void handleShutdownSignal(String consumerTag, ShutdownSignalException arg1) {}


    /**
     * Prints the welcome message:
     */
    public static void printInfo()
    {

        System.out.println("M:"+ Constants.M +" N: "+ Constants.N + " T:" + Constants.T);
        System.out.println("Cache: "+ Constants.SLSCACHEENDPOINT);
        System.out.println("DataStore:"+ Constants.DATASTOREENDPOINT);
    }

    /**
     * Creates an index and mapping for further use.
     * Ignores if the index already exists.
     */
    public static void initializeElasticSearch()
    {



        Gson gson = new Gson();
        String json = mappingJSONString;

        //send to ES store
        try
        {

            CloseableHttpClient httpClient    = HttpClients.createDefault();
            HttpPost post          = new HttpPost(Constants.MAPPINGENDPOINT);
            post.setHeader("Content-type", "application/json");
            StringEntity stringEntity = new StringEntity(json);
            post.setEntity(stringEntity);

            CloseableHttpResponse response = httpClient.execute(post);

            // get back response.
            if(response.getStatusLine().getStatusCode() == 200)
            {
                System.out.println("Index Created in Data Store: " + Constants.INDEX);
            }
            else
            {
                System.err.println("Index already exists in Data Store:" + Constants.INDEX);
            }

            //clean up
            response.close();
            post.releaseConnection();
            httpClient.close();

        }
        catch(IOException e)
        {
            System.out.println("There's an error in the sending the mapping request");
            System.exit(-1);

        }
    }


    public static void main(String[] args)
    {

        Constants.intitializeConstants();
        printInfo();

        LatencyChecker latencyChecker = new LatencyChecker();
        latencyChecker.initializeElasticSearch();
        latencyChecker.startConsumer();
    }
}
