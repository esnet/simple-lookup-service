package net.es.lookup.client;

import net.es.lookup.common.ReservedKeywords;
import net.es.lookup.common.exception.ParserException;
import net.es.lookup.protocol.json.JSONParser;
import net.es.lookup.queries.Query;
import net.es.lookup.common.exception.LSClientException;
import net.es.lookup.records.Record;
import net.es.lookup.records.SubscribeRecord;

import java.util.List;

/**
 * Author: sowmya
 * Date: 4/16/13
 * Time: 3:42 PM
 */
public class Subscription {

    private SimpleLS server;
    private Query query;
    private String relativeUrl = "/lookup/subscribe";

    public Subscription(SimpleLS server) throws LSClientException {

        this.server = server;
        this.server.connect();
    }

    public SimpleLS getServer() {

        return server;
    }

    public synchronized void setServer(SimpleLS server) {

        this.server = server;
    }

    public Query getQuery() {

        return query;
    }

    public synchronized void setQuery(Query query) {

        this.query = query;
    }

    public String getRelativeUrl() {

        return relativeUrl;
    }

    public synchronized void setRelativeUrl(String relativeUrl) {

        this.relativeUrl = relativeUrl;
    }

    public synchronized SubscribeRecord request() throws LSClientException, ParserException {
        SubscribeRecord record = new SubscribeRecord();
        System.out.println("Debugging: Came here");
        if( server != null && server.getStatus().equals(ReservedKeywords.SERVER_STATUS_ALIVE)){
            System.out.println("Debugging: Came here 1");
            String queryString = "";
          //  if(query != null){
            //    try {
                    //queryString = JSONParser.toString(query);
              //  } catch (ParserException e) {
                //    throw new LSClientException(e.getMessage());
                //}
           // }

            System.out.println("Debugging: Came here 2");
            server.setPort(8080);

            String url = server.getConnectionUrl();
            String fullurl = url.concat(relativeUrl);

            System.out.println(fullurl);
            server.setConnectionUrl(fullurl);
            server.setConnectionType("POST");
            server.setData(queryString);
            server.send();
            System.out.println("Debugging: Came here 3");
            System.out.println(server.getResponseCode());
            if(server.getResponseCode() == 200){
                System.out.println("Debugging: Came here 4");
                String response = server.getResponse();
                List<Record> recordList = JSONParser.toRecord(response);
                if(recordList != null && !recordList.isEmpty()){
                    record = (SubscribeRecord) recordList.get(0);
                }
            }else{
                throw new LSClientException("Error in response. Response code: "+server.getResponseCode()+ ". Error Message: "+server.getErrorMessage());
            }

        }else{
            throw new LSClientException("Server Initialization Error");
        }

        return record;

    }


}
