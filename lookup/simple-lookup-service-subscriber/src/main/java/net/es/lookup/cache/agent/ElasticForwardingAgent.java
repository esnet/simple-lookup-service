package net.es.lookup.cache.agent;

import net.es.lookup.common.ReservedValues;
import net.es.lookup.common.exception.ParserException;
import net.es.lookup.protocol.json.JSONParser;
import net.es.lookup.records.Record;
import net.sf.json.JSONObject;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

/**
 * Author: sowmya
 * Date: 4/13/16
 * Time: 3:06 PM
 */
public class ElasticForwardingAgent extends ForwardingAgent {

    Record record;
    private static Logger LOG = Logger.getLogger(ElasticForwardingAgent.class);

    public ElasticForwardingAgent(URI destination, JSONObject data) {

        super(destination, data);

    }

    @Override
    public void run() {
        parseRecord(); send();
    }

    @Override
    public void send(){
        HttpClient httpclient = new DefaultHttpClient();

        HttpPut httpPut = new HttpPut();
        String uriValue = record.getURI();
        uriValue = uriValue.replaceAll(ReservedValues.RECORD_VALUE_FORWARD_SLASH, ReservedValues.RECORD_VALUE_UNDERSCORE);

        String mainUrl = this.getDestination().toString();

        String putIndex = mainUrl+ReservedValues.RECORD_VALUE_FORWARD_SLASH+uriValue;

        try {
            URI esPutUri = new URI(putIndex);
            httpPut.setURI(esPutUri);
            httpPut.setHeader("Accept", "application/json");
            httpPut.setHeader("Content-type", "application/json");


            JSONObject putData = this.getData();
            putData.put("createdInCache", new Date());
            StringEntity se = new StringEntity(putData.toString());
            httpPut.setEntity(se);
            httpclient.execute(httpPut);

        } catch (URISyntaxException e) {
            LOG.error("net.es.lookup.cache.agent.ElasticForwardingAgent"+e.getMessage());
        } catch (UnsupportedEncodingException e) {
            LOG.error("net.es.lookup.cache.agent.ElasticForwardingAgent"+e.getMessage());
        } catch (ClientProtocolException e) {
            LOG.error("net.es.lookup.cache.agent.ElasticForwardingAgent"+e.getMessage());
        } catch (IOException e) {
            LOG.error("net.es.lookup.cache.agent.ElastcForwardingAgent"+e.getMessage());
        }


    }

    private void parseRecord(){

        try {
            record = JSONParser.toRecord(this.getData().toString());
        } catch (ParserException e) {
            LOG.error("net.es.lookup.cache.agent.ElastcForwardingAgent"+e.getMessage());
        }

    }


}
