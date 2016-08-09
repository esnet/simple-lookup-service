package net.es.lookup.latencycheck;

import com.google.gson.Gson;


import net.es.lookup.rmqmessages.LGMessage;
import net.sf.json.JSONArray;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import net.sf.json.JSONObject;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * Created by kamala on 6/2/16.
 */
public class Checker implements Runnable
{

    private LGMessage message;
    private static final long DROP = -1;
    private int executionCount;


    public static final String REGISTEREDINCACHE = "registered";
    public static final String RENEWEDINCACHE = "renewed";


    public Checker(LGMessage message)
    {
        executionCount= 0;
        this.message = message;
        Log log = LogFactory.getLog(JSONObject.class);
    }

    public void run()
    {

        try
        {
            Thread.sleep(10*1000);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        /*POST JSON
        {
            "query" : {
            "constant_score" : {
                "filter" : {
                    "term" : {
                        "uri" : "lookup/Testing/68d12f2d-283b-4858-be0f-9278a6354caf"
                    }
                }
            }
        }
        }
        **/
        JSONObject uriObject = new JSONObject();
        uriObject.put("uri",message.getUri());

        JSONObject termObject = new JSONObject();
        termObject.put("term", uriObject);


        JSONObject filterObject = new JSONObject();
        filterObject.put("filter",termObject);

        JSONObject csObject = new JSONObject();
        csObject.put("constant_score",filterObject);


        JSONObject queryObject = new JSONObject();
        queryObject.put("query",csObject);
        boolean waitFlag = true;
        boolean errorStatus= false;




        CloseableHttpResponse response = null;

        while (true)
        {

            //Send the query
            String postUrl = Constants.SLSCACHEENDPOINT ;

            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost post = new HttpPost(postUrl);
            post.setHeader("Content-type", "application/json");
            try
            {
                StringEntity stringEntity = new StringEntity(queryObject.toString());
                post.setEntity(stringEntity);
            }
            catch(UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }

            try
            {
                executionCount++;
                response = httpClient.execute(post);

                if (response.getStatusLine().getStatusCode() == 200)
                {

                    String json = EntityUtils.toString(response.getEntity());
                    JSONObject jsonObject = JSONObject.fromObject(json);
                    JSONObject hitsMap = (JSONObject) jsonObject.get("hits");
                    Integer total = (Integer) hitsMap.get("total");

                    if (total.intValue() > 0)
                    {

                        JSONArray hits = (JSONArray) hitsMap.get("hits");
                        if (hits.size() != 0)
                        {
                            int index = 0;
                            while(index<hits.size())
                            {
                                JSONObject hits2 = (JSONObject) hits.get(index);
                                index++;


                                JSONObject sourceHashMap = (JSONObject) hits2.get("_source");

                                String recvURI = (String) sourceHashMap.get("uri");
                                Date creationTime = (Date) JSONObject.toBean((JSONObject) sourceHashMap.get("createdInCache")
                                        , Date.class);

                                TimeZone tz = TimeZone.getTimeZone("UTC");
                                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                dateFormat.setTimeZone(tz);
                                Date recvExpiryDate = dateFormat.parse((String) sourceHashMap.get("expires"));
                                Object expiryString = sourceHashMap.get("expires");
                                String state = (String) sourceHashMap.get("state");

                                if (message.getMessageType().equals(LGMessage.REGISTER))
                                {
                                    if(state.equals(REGISTEREDINCACHE) == false)
                                    {
                                        continue;
                                    }
                                    saveData(expiryString, creationTime, LGMessage.REGISTER);

                                    System.out.println("message:" + message.getMessageId() + " Finished" + "--"
                                            + message.getMessageType() + " uri:" + message.getUri());

                                    waitFlag = false;
                                    break;

                                }
                                else if (message.getMessageType().equals(LGMessage.RENEW))
                                {
                                    if(state.equals(RENEWEDINCACHE) == false)
                                    {
                                        continue;
                                    }

                                    if(message.getExpiresDate().equals(recvExpiryDate))
                                    {
                                        saveData(expiryString, creationTime, LGMessage.RENEW);

                                        System.out.println("message:" + message.getMessageId() + " Finished" + "--"
                                                + message.getMessageType() + " uri:" + message.getUri());

                                        waitFlag = false;
                                        break;
                                    }
                                    else
                                    {
                                        continue;
                                    }

                                }

                            }
                        }
                        else
                        {
                            System.err.println("uri:" + message.getUri() + " type:" + message.getMessageType() + " no hits");
                        }
                    }

                }
                else
                {
                    System.out.println("ERROR:::  Status Code from Cache: " + response.getStatusLine().getStatusCode()
                                                + " uri: " + message.getUri());
                    errorStatus = true;

                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            finally
            {

                try
                {
                    //clean up
                    response.close();
                    httpClient.close();
                }
                catch(Exception e)
                {

                }
            }

            //Sleep:
            try
            {
                if(executionCount < Constants.POLLTOTAL && waitFlag)
                {
                    Thread.sleep(Constants.POLLINTERVAL);
                }
                else
                {
                    if(waitFlag!=false)
                    {
                        System.out.println("message:" + message.getMessageId() + " TimedOut " + "--"
                                + message.getMessageType() + " uri:" + message.getUri() + " errors?:"+ errorStatus );

                        saveData(null, null, message.getMessageType());
                    }

                    return;// exit this thread. we are done.
                }
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }

        }
    }

    /**
     * Saves the date in the final elasticsearch instance
     * @param expiry
     * @param creationTimeStamp
     * @param messageType
     */
    public void saveData(Object expiry, Date creationTimeStamp, String messageType)
    {
        //get all required parameters.
        HashMap<String,Object> resultMap = new HashMap<String,Object>();

        //M
        resultMap.put("M",Constants.M);
        //N
        resultMap.put("N",Constants.N);
        //T
        resultMap.put("T",Constants.T);

        long time = calculateTimeDifference(creationTimeStamp);
        if(time > Constants.POLLINTERVAL* Constants.POLLTOTAL)
        {
            time = DROP;
        }




        if(time != DROP)
        {
            //Response Time
            resultMap.put("latency", time);
            resultMap.put("result","SUCCESS");
        }
        else
        {
            resultMap.put("result","TIMEOUT");
        }

        //uri
        resultMap.put("uri",message.getUri());

        //messageType
        resultMap.put("messageType",messageType);

        //expires TimeStamp
        resultMap.put("expires",expiry);

        //creationTimeStamp
        DateTime dt = new DateTime();
        DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
        String str = fmt.print(dt);


        resultMap.put("creationTime:" , str);

        Gson gson = new Gson();
        String json = gson.toJson(resultMap, HashMap.class);

        //send to ES store
        try
        {

            CloseableHttpClient httpClient    = HttpClients.createDefault();
            HttpPost     post          = new HttpPost(Constants.DATASTOREENDPOINT);
            post.setHeader("Content-type", "application/json");
            StringEntity stringEntity = new StringEntity(json);
            post.setEntity(stringEntity);

            CloseableHttpResponse response = httpClient.execute(post);

            // get back response.
            if(response.getStatusLine().getStatusCode() == 201)
            {

            }
            else
            {
                System.err.println("Status response from Data Store: " + response.getStatusLine().getStatusCode()
                                    + " for storing uri:" + message.getUri());

            }

            //clean up
            response.close();
            post.releaseConnection();
            httpClient.close();

        }
        catch(IOException e)
        {
            System.out.println("There's an error in saving the data in the data store for uri:" + message.getUri());

        }
    }

    /**
     * Calculates the latency
     */
    public long calculateTimeDifference(Date creationTimeStamp)
    {
        //calculate TimeDifference:
        if(creationTimeStamp==null)
        {
            return DROP;
        }
        Date successTime = message.getTimestamp();


        if(creationTimeStamp.getTime()-successTime.getTime() < 0)
        {
            System.out.println("successTime:"+successTime.getTime()+" cTime:"+creationTimeStamp.getTime());
        }
        return  creationTimeStamp.getTime() - successTime.getTime() ;
    }

}

