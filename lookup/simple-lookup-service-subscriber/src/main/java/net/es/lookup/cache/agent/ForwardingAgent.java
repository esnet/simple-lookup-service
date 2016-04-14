package net.es.lookup.cache.agent;

import net.sf.json.JSONObject;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;

/**
 * Author: sowmya
 * Date: 4/1/16
 * Time: 1:24 PM
 *
 * A forwarding agent that uses HTTP POST to send the data to the destination url
 */
public class ForwardingAgent implements Runnable {


    private JSONObject data;
    private URI destination;


    public ForwardingAgent(URI destination, JSONObject data) {

        this.destination = destination;
        this.data = data;
    }

    public JSONObject getData() {

        return data;
    }

    public URI getDestination() {

        return destination;
    }

    @Override
    public void run() {
      send();
    }

    public void send(){
        HttpClient httpclient = new DefaultHttpClient();

        HttpPost httpPost = new HttpPost();
        httpPost.setURI(destination);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");


        try {
            StringEntity se = new StringEntity(data.toString());
            httpPost.setEntity(se);
            httpclient.execute(httpPost);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
