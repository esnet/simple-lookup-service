package net.es.lookup.pubsub;

import net.es.lookup.client.SimpleLS;
import net.es.lookup.client.Subscription;
import net.es.lookup.common.exception.LSClientException;
import net.es.lookup.common.exception.ParserException;
import net.es.lookup.queries.Query;
import net.es.lookup.records.PubSub.SubscribeRecord;

import java.util.ArrayList;

/**
 * Author: sowmya
 * Date: 4/16/13
 * Time: 6:31 PM
 */
public class SubscribeClient {

    private String url = "";
    private String queue;
    SimpleLS server;
    Subscription s;

    public SubscribeClient() throws LSClientException {
        server = new SimpleLS();
        server.connect();
        s = new Subscription(server);
    }

    public void initiate() throws LSClientException, ParserException {
        Query q = new Query();
        ArrayList<String>  type = new ArrayList<String>();
        type.add("service");
        q.setRecordType(type);
        s.setQuery(q);
        SubscribeRecord response = s.request();
        System.out.println("Executed subscribe request");
        if(response != null){
            System.out.println(response.getLocator());
        }else{
            System.out.println("null response");
        }

    }

}
