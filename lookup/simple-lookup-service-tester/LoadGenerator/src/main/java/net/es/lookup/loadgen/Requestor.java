package net.es.lookup.loadgen; /**
 * This class sends the request to the sls core and sends the
 * expires field and uri to the latency checker.
 */
import java.io.*;
import java.net.*;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.LogManager;

import com.rabbitmq.client.Channel;

import com.google.gson.Gson;
import org.apache.commons.lang.SerializationUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;

import org.apache.http.impl.client.CloseableHttpClient;

import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;


import net.es.lookup.rmqmessages.KVGMessage;
import net.es.lookup.rmqmessages.LGMessage;


/**
 * Created by kamala on 5/31/16.
 */
public class Requestor implements Runnable
{
    private KVGMessage message;


    public static final int VALIDITY = 2;

    public Requestor(KVGMessage message)
    {

        LogManager.getLogManager().reset();

        this.message = message;

    }

    public KVGMessage getMessage()
    {
        return message;
    }

    /*Override*/
    public void run()
    {

        URI address = null;


        if(message.getMessageType().equals(KVGMessage.REGISTER))
        {
            //handle register
            HashMap<String,String> msgDataMap =  message.getMap();

            HttpURLConnection httpcon = null;
            String url = Constants.SLSCOREENDPOINT + Constants.PATHNAME;

            Gson gson = new Gson();

            String data = gson.toJson(msgDataMap,HashMap.class);
            String result = null;


            try {
                //Connect
                httpcon = (HttpURLConnection) ((new URL(url).openConnection()));
                httpcon.setDoOutput(true);
                httpcon.setRequestProperty("Content-Type", "application/json");
                httpcon.setRequestProperty("Accept", "*/*");
                httpcon.setRequestProperty("Content-Length", Integer.toString(data.length()));
                httpcon.setRequestMethod("POST");
                httpcon.connect();


                //Write
                OutputStream os = httpcon.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(data);
                writer.close();
                os.close();

                if (httpcon.getResponseCode() == 200)
                {

                    //Read
                    BufferedReader br = new BufferedReader(new InputStreamReader(httpcon.getInputStream(), "UTF-8"));

                String line = null;
                StringBuilder sb = new StringBuilder();

                while ((line = br.readLine()) != null) {
                    sb.append(line);

                }

                br.close();
                result = sb.toString();

                HashMap<String, String> map = gson.fromJson(result, HashMap.class);
                Record record = LoadGenerator.putInfo(map.get("uri"), map.get("expires"));

                //calculate created Time:
                Date expiryDate = record.getExpiresDate();
                Calendar cal = Calendar.getInstance();
                cal.setTime(expiryDate);
                cal.add(Calendar.HOUR, -1 * VALIDITY);
                Date successTime = cal.getTime();


                //publish to queue for latencyChecker to consume
                LGMessage lgMessage = new LGMessage();
                lgMessage.setMessageId(message.getMessageId());
                lgMessage.setTimestamp(successTime);
                lgMessage.setUri(map.get("uri"));
                lgMessage.setMessageType(LGMessage.REGISTER);
                lgMessage.setExpiresDate(record.getExpiresDate());
                lgMessage.setIsStored(record.getIsStored());


                publish(lgMessage);
                }
                else
                {
                    System.err.println("error code:" + httpcon.getResponseCode() + " for id:" + message.getMessageId() +"   " + httpcon.getResponseMessage());
                }


            }
            catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            finally
            {

                if(httpcon!=null)
                {
                    httpcon.disconnect();
                }

            }

        }

        else if(message.getMessageType().equals(KVGMessage.RENEW))
        {
            CloseableHttpClient   httpClient = null;
            CloseableHttpResponse  response =null;
            try
            {
                // handle renew
                Record record = LoadGenerator.getRandomRecord();
                String uri = record.getUri();

                // get connections from database
                // get random db record
                // send renew record.
                String       postUrl       = Constants.SLSCOREENDPOINT +"/" + uri;// put in your url
                Gson         gson          = new Gson();
                 httpClient   = HttpClients.createDefault();
                HttpPost     post          = new HttpPost(postUrl);
                post.setHeader("Content-type", "application/json");

                response = httpClient.execute(post);

                // get back response.
                if (response.getStatusLine().getStatusCode() == 200)
                {

                }
                else
                {
                    System.err.println("Status response from core for renew :: " + response.getStatusLine().getStatusCode()
                            + " uri: "+ record.getUri());
                    return;
                }

                String responseJson=  EntityUtils.toString(response.getEntity(), "UTF-8");
                HashMap<String, String> responseMap = gson.fromJson(responseJson, HashMap.class);

                Record storedRecord
                        = LoadGenerator.putInfo(responseMap.get("uri"), responseMap.get("expires"));

                //calculate created Time:
                Date expiryDate = storedRecord.getExpiresDate();
                Calendar cal = Calendar.getInstance();
                cal.setTime(expiryDate);
                cal.add(Calendar.HOUR, -1 * VALIDITY);
                Date successTime = cal.getTime();


                //send to tier 2
                //publish to queue for latencyChecker to consume
                LGMessage lgMessage = new LGMessage();
                lgMessage.setMessageId(message.getMessageId());
                lgMessage.setTimestamp(successTime);
                lgMessage.setUri(storedRecord.getUri());
                lgMessage.setMessageType(LGMessage.RENEW);
                lgMessage.setExpiresDate(storedRecord.getExpiresDate());




            }
            catch(IOException e)
            {
                System.out.println("There's an error in the sending the http renew request");
                e.printStackTrace();

            }
            finally {

                try {
                    //cleanup
                    if (response != null) {
                        response.close();
                    }
                    if (httpClient != null) {
                        httpClient.close();
                    }
                }
                catch(IOException e)
                {

                }
            }



        }



    }




    public void publish(LGMessage message)
    {

        try
        {
            Channel channel = MessageSender.getChannel();
            channel.basicPublish("", LoadGenerator.PUBLISHQUEUE, null, SerializationUtils.serialize(message));
            System.out.println("message:"+message.getMessageId() +" FINISHED  -- " + message.getMessageType()
                    +" uri:" + message.getUri()  );
            channel.close();
        }
        catch(Exception e)
        {
            System.err.println("Error in serializing message");
        }

    }

}
