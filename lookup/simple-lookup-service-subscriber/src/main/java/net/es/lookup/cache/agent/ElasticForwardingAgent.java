package net.es.lookup.cache.agent;

import net.es.lookup.common.ReservedKeys;
import net.es.lookup.common.exception.ParserException;
import net.es.lookup.protocol.json.JSONParser;
import net.es.lookup.records.Record;
import net.sf.json.JSONObject;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Author: sowmya
 * Date: 4/13/16
 * Time: 3:06 PM
 */
public class ElasticForwardingAgent extends ForwardingAgent {

    public ElasticForwardingAgent(URI destination, JSONObject data) {

        super(destination, data);

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
            Record record = JSONParser.toRecord(this.getData().toString());
            String uriValue = record.getURI();
            String uriKey = ReservedKeys.RECORD_URI;

            String delUriString= this.getDestination().toString()+"/_query?q="+uriKey+":%22"+uriValue+"%22";
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

}
