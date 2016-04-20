package net.es.lookup.cache.agent;

import net.es.lookup.common.ReservedKeys;
import net.es.lookup.common.exception.ParserException;
import net.es.lookup.protocol.json.JSONParser;
import net.es.lookup.records.Record;
import net.sf.json.JSONObject;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

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
        deleteOldEntries(); send();
    }

    private void deleteOldEntries(){

        HttpClient httpclient = new DefaultHttpClient();

        HttpDelete httpDelete = new HttpDelete();
        httpDelete.setHeader("Accept", "application/json");
        httpDelete.setHeader("Content-type", "application/json");

        try {
            Record record = JSONParser.toRecord(data.toString());
            String uriValue = record.getURI();
            String uriKey = ReservedKeys.RECORD_URI;

            String delUriString= destination.toString()+"/_search?q="+uriKey+":%22"+uriValue+"%22";
            System.out.println(delUriString);
            URI deleteURI = new URI(delUriString);

            httpDelete.setURI(deleteURI);


            httpclient.execute(httpDelete);

        } catch (ParserException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


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
