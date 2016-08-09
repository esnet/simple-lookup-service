package net.es.lookup.distribution;
import com.google.gson.Gson;

import net.es.lookup.rmqmessages.LGMessage;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

/**
 * Created by kamala on 7/12/16.
 */
public class RequestSenderThread implements Runnable
{
    private CountDownLatch latch;
    private DistributionLoadGenerator distributionLoadGenerator;
    private HashMap<String,String> map;
    private int messageId;
    private String requestType;

    public RequestSenderThread(CountDownLatch latch
                ,HashMap<String,String> map, DistributionLoadGenerator dist, int id, String requestType)
    {
        this.latch = latch;
        this.distributionLoadGenerator = dist;
        this.requestType = requestType;
        this.map = map;
        this.messageId = id;

    }

    public void run()
    {
        sendRequest();
    }

    public void sendRequest()
    {
        if (requestType.equals(DistributionLoadGenerator.REGISTER))
        {

            /**/
            //set Message parameters
            map.put("Id", Integer.toString(messageId));

            //handle register
            HashMap<String, String> msgDataMap = map;

            HttpURLConnection httpcon = null;
            String url = Constants.sLSCoreHostName + Constants.pathName;
            Gson gson = new Gson();

            String data = gson.toJson(msgDataMap, HashMap.class);
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

                //Read

                if(httpcon.getResponseCode()== 200) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(httpcon.getInputStream(), "UTF-8"));

                    String line = null;
                    StringBuilder sb = new StringBuilder();

                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }

                    br.close();
                    result = sb.toString();

                    HashMap<String, String> responseMap = gson.fromJson(result, HashMap.class);
                    Record record = DistributionLoadGenerator.putInfo(responseMap.get("uri"), responseMap.get("expires"));

                    //calculate created Time:
                    Date expiryDate = record.getExpiresDate();
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(expiryDate);
                    cal.add(Calendar.HOUR, -1 * DistributionLoadGenerator.VALIDITY);
                    Date successTime = cal.getTime();
                    //
                    //publish to queue for latencyChecker to consume
                    LGMessage lgMessage = new LGMessage();
                    lgMessage.setMessageId(messageId);
                    lgMessage.setTimestamp(successTime);
                    lgMessage.setUri(responseMap.get("uri"));
                    lgMessage.setMessageType(LGMessage.REGISTER);
                    lgMessage.setExpiresDate(record.getExpiresDate());
                    lgMessage.setIsStored(record.getIsStored());

                    distributionLoadGenerator.publish(lgMessage);
                }
                else
                {
                    System.err.println(httpcon.getResponseCode() + " " + httpcon.getResponseMessage() + " messId:" +
                            messageId );
                }


            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("REGISTER:" + map);
                e.printStackTrace();
            } finally {

                if (httpcon != null)
                {
                    httpcon.disconnect();
                }

            }

            //increment record indices.



        }
        else
        {
            if (DistributionLoadGenerator.uriMap.size() == 0)
            {
                //count down the latch
                latch.countDown();
                return;
            }

            CloseableHttpResponse response = null;
            CloseableHttpClient httpClient = null;
            try
            {
                // handle renew

                Record record = null;
                while(record==null)
                {
                   record = DistributionLoadGenerator.getRandomRecord();
                }
                String uri = record.getUri();

                // get connections from database
                // get random db record
                // send renew record.
                String postUrl = Constants.sLSCoreHostName + "/" + uri;// put in your url
                Gson gson = new Gson();
                httpClient = HttpClients.createDefault();
                HttpPost post = new HttpPost(postUrl);
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
                        = DistributionLoadGenerator.putInfo(responseMap.get("uri"), responseMap.get("expires"));

                //calculate created Time:
                Date expiryDate = storedRecord.getExpiresDate();
                Calendar cal = Calendar.getInstance();
                cal.setTime(expiryDate);
                cal.add(Calendar.HOUR, -1 * DistributionLoadGenerator.VALIDITY);
                Date successTime = cal.getTime();


                //send to tier 2
                //publish to queue for latencyChecker to consume
                LGMessage lgMessage = new LGMessage();
                lgMessage.setMessageId(messageId);
                lgMessage.setTimestamp(successTime);
                lgMessage.setUri(storedRecord.getUri());
                lgMessage.setMessageType(LGMessage.RENEW);
                lgMessage.setExpiresDate(storedRecord.getExpiresDate());

                //publish
                distributionLoadGenerator.publish(lgMessage);


            }
            catch (IOException e)
            {
                System.out.println("There's an error in the sending the http renew request");
                e.printStackTrace();

            }
            finally
            {
                //cleanup
                try
                {
                    response.close();
                    httpClient.close();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

        //count down the latch
        latch.countDown();


    }
}
