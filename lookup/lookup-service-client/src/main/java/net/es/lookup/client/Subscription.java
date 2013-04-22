package net.es.lookup.client;

import net.es.lookup.common.ReservedKeys;
import net.es.lookup.common.exception.ParserException;
import net.es.lookup.protocol.json.JSONParser;
import net.es.lookup.queries.Query;
import net.es.lookup.common.exception.LSClientException;
import net.es.lookup.records.Record;
import net.es.lookup.records.PubSub.SubscribeRecord;

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


        if (server != null && server.getStatus().equals(ReservedKeys.SERVER_STATUS_ALIVE)) {
            String queryString = "";
            //  if(query != null){
            //    try {
            //queryString = JSONParser.toString(query);
            //  } catch (ParserException e) {
            //    throw new LSClientException(e.getMessage());
            //}
            // }
            server.setPort(8090);

            String url = server.getConnectionUrl();
            String fullurl = url.concat(relativeUrl);
            server.setConnectionUrl(fullurl);
            server.setConnectionType("POST");
            server.setData(queryString);
            server.send();

            if (server.getResponseCode() == 200) {

                String response = server.getResponse();
                SubscribeRecord record = (SubscribeRecord)JSONParser.toRecord(response);
                return record;
            } else {


                throw new LSClientException("Error in response. Response code: " + server.getResponseCode() + ". Error Message: " + server.getErrorMessage());
            }

        } else {
            throw new LSClientException("Server Initialization Error");
        }



    }


}
