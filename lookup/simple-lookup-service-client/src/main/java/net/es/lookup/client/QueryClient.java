package net.es.lookup.client;

import net.es.lookup.common.ResponseCodes;
import net.es.lookup.common.exception.LSClientException;
import net.es.lookup.common.exception.ParserException;
import net.es.lookup.common.exception.QueryException;
import net.es.lookup.protocol.json.JSONParser;
import net.es.lookup.queries.Query;
import net.es.lookup.records.Record;
import net.sf.json.JSONArray;
import org.apache.log4j.Logger;

import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: luke
 * Date: 6/21/13
 * Time: 3:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class QueryClient {

    private SimpleLS server;
    private Query query;
    private String connectionType = "GET";
    private String baseUrl = "lookup/records/";
    private String relativeUrl;

    private static Logger LOG = Logger.getLogger(QueryClient.class);

    public QueryClient(SimpleLS server) throws LSClientException {

        if (server != null) {

            this.server = server;
            this.query = null;
            relativeUrl = baseUrl;
            LOG.info("net.es.lookup.client.QueryClient: Creating QueryClient");
        } else {

            LOG.info("net.es.lookup.client.QueryClient: Error creating QueryClient. Did not find server");
            throw new LSClientException("Error creating QueryClient. Did not find server");
        }
    }

    public QueryClient(SimpleLS server, Query query) throws LSClientException {

        if (server != null) {

            this.server = server;
            this.query = query;
            LOG.info("net.es.lookup.client.QueryClient: Creating QueryClient");
        } else {

            LOG.info("net.es.lookup.client.QueryClient: Error creating QueryClient. Did not find server");
            throw new LSClientException("Error creating QueryClient. Did not find server");
        }
    }

    public SimpleLS getServer() {

        return server;
    }

    public Query getQuery() {

        return query;
    }

    public synchronized void setQuery(Query query) throws LSClientException {
        if (query != null) {

            LOG.info("net.es.lookup.client.QueryClient: Setting query");
            this.query = query;
        } else {

            LOG.info("net.es.lookup.client.QueryClient: Error setting Query. Found null values");
            throw new LSClientException("Error setting Query. Found null values");
        }
    }

    public String getRelativeUrl() throws QueryException {

        relativeUrl = baseUrl;
        relativeUrl += (query != null) ? query.toURL() : "";

        return relativeUrl;
    }

    public synchronized List query() throws ParserException, LSClientException, QueryException {

        LOG.info("net.es.lookup.client.QueryClient: Initiating query");
        LinkedList<Record> result = new LinkedList<Record>();

        LOG.info("net.es.lookup.client.QueryClient: Configuring relative URL");
        relativeUrl = baseUrl;
        relativeUrl += (query != null) ? query.toURL() : "";

        LOG.info("net.es.lookup.client.QueryClient: Setting request parameters");
        server.setConnectionType(connectionType);
        server.setRelativeUrl(relativeUrl);

        LOG.info("net.es.lookup.client.QueryClient: Sending query request");
        server.send();

        if (server.getResponseCode() == ResponseCodes.SUCCESS) {
            String response = server.getResponse();

            LOG.info("net.es.lookup.client.QueryClient: Parsing response");
            JSONArray ja = JSONArray.fromObject(response);
            for (Object o : ja) {

                result.add(JSONParser.toRecord(o.toString()));
            }

            return result;
        } else {
            LOG.info("net.es.lookup.client.QueryClient: Unsuccessful query attempt; failed with code \" + server.getResponseCode()");
            throw new LSClientException("Unsuccessful query attempt; failed with code " + server.getResponseCode());
        }
    }
}
