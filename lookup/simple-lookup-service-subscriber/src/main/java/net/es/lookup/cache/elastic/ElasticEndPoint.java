package net.es.lookup.cache.elastic;

import net.es.lookup.cache.dispatch.EndPoint;
import net.es.lookup.utils.ElasticUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Author: sowmya
 * Date: 8/8/16
 * Time: 4:21 PM
 */
public class ElasticEndPoint implements EndPoint,Runnable {

    public static final long INDEX_INTERVAL = 3600;
    public static final long MAX_INDEX = 24;
    public static final String INDEX_PREFIX = "perfsonar_";
    public static final String ALIAS_ACTION_ADD = "add";
    public static final String ALIAS_ACTION_REMOVE = "remove";
    private static final String HTTP_GET = "GET" ;
    private URI location;
    JSONObject mapping;

    private String writeIndex;
    private String searchIndex;
    private String documentType;
    private static Logger LOG = Logger.getLogger(ElasticEndPoint.class);


    /**
     * Constructors
     * */
    public ElasticEndPoint(URI location) {
        this.location = location;
    }

    public ElasticEndPoint(URI location, JSONObject mapping){
        this.location = location;
        this.mapping = mapping;
        init();

    }

    /**
     * Getters and setters
     * */

    public URI getLocation() {
        return location;
    }

    public String getWriteIndex() {

        return writeIndex;
    }

    public void setWriteIndex(String writeIndex) {

        this.writeIndex = writeIndex;
    }

    public String getSearchIndex() {

        return searchIndex;
    }

    public void setSearchIndex(String searchIndex) {

        this.searchIndex = searchIndex;
    }

    public String getDocumentType() {

        return documentType;
    }

    public void setDocumentType(String documentType) {

        this.documentType = documentType;
    }

    /**
     * Initializes the elastic search instance. Creates an executor service to run the reindex()
     * every hour
     * */
    private void init() {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(this, 2, INDEX_INTERVAL, TimeUnit.SECONDS);
    }


    /**
     * This method creates an index in the elastic search instance using HTTP POST operation
     * @param index Data Index to be created
     *
     * */
    public void createIndex(String index){
        String indexUrl = ElasticUtils.getAbsoluteIndexUrl(location,index);
        try {
            URI uri = new URI(indexUrl);
            set(mapping, uri);
        }catch (URISyntaxException e) {
            LOG.error(e.getClass().getCanonicalName()+e.getMessage());
        }
    }


    /**
     *
     * This method performs a HTTP POST operation on the given alias
     * @param aliasName Alias name to be created or deleted
     * @param indexName The index name for which the alias is being created/deleted
     * @param action "delete" or "add" operation to be performed on the alias
     *
     * */
    public void modifyAlias(String aliasName, String indexName, String action){
        String aliasEndPoint = ElasticUtils.getAliasEndPoint(location);
        JSONObject aliases = new JSONObject();

        JSONArray aliasActions = new JSONArray();
        JSONObject addSearchIndex = new JSONObject();
        addSearchIndex.put("alias", aliasName);
        addSearchIndex.put("index", indexName);

        JSONObject actionsObject = new JSONObject();
        if(action.equals(ALIAS_ACTION_ADD)){
            actionsObject.put(ALIAS_ACTION_ADD, addSearchIndex);
        }else if(action.equals(ALIAS_ACTION_REMOVE)){
            actionsObject.put(ALIAS_ACTION_REMOVE, addSearchIndex);
        }
        aliasActions.add(actionsObject);

        JSONObject actions = new JSONObject();
        actions.put("actions", aliasActions);
        LOG.debug("Alias data: "+actions.toString());
        try {
            URI uri = new URI(aliasEndPoint);
            LOG.debug("URI to post Alias to: "+aliasEndPoint);
            send(actions, uri);
        } catch (URISyntaxException e) {
            LOG.error(this.getClass().getCanonicalName()+": "+e.getMessage());
        }
    }


    /**
     *
     * This method performs a HTTP PUT operation.
     * @param data Data to be sent in the put request
     * @param location The URI for the put request
     * */
    public void set(JSONObject data, URI location) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPut httpPut = new HttpPut();
        httpPut.setURI(location);
        httpPut.setHeader("Accept", "application/json");
        httpPut.setHeader("Content-type", "application/json");
        try {
            LOG.debug(this.getClass().getCanonicalName()+": URL to set"+location);
            LOG.debug(this.getClass().getCanonicalName()+": Data to set"+data.toString());

            StringEntity se = new StringEntity(data.toString());
            httpPut.setEntity(se);
            HttpResponse response = httpclient.execute(httpPut);
            int statusCode = response.getStatusLine().getStatusCode();
            LOG.info("Status code: "+ statusCode);
            if(statusCode < 200 || statusCode > 299){
                LOG.error(this.getClass().getCanonicalName()+": Error setting data in elasticsearch");
                LOG.error(this.getClass().getCanonicalName()+":"+response.getStatusLine().getReasonPhrase());
            }else{
                LOG.debug(this.getClass().getCanonicalName()+": Success setting data in elasticsearch");
            }
        } catch (UnsupportedEncodingException e) {
            LOG.error(this.getClass().getCanonicalName()+": "+e.getMessage());
        } catch (ClientProtocolException e) {
            LOG.error(this.getClass().getCanonicalName()+": "+e.getMessage());
        } catch (IOException e) {
            LOG.error(this.getClass().getCanonicalName()+": "+e.getMessage());
        }
    }


    /**
     *
     * This method performs a HTTP POST operation. The data is sent to the
     * write alias defined for this elasticsearch instance
     * @param data Data to be sent in the post request
     *
     * */
    public void send(JSONObject data) {
            String writeLocation = location.toString()+"/"+writeIndex+"/"+documentType;
        try {
            URI writeUri = new URI(writeLocation);
            send(data,writeUri);
        } catch (URISyntaxException e) {
            LOG.error(this.getClass().getCanonicalName()+": "+e.getMessage());
        }


    }

    /**
     * This method performs a HTTP POST operation on the given URI
     * @param data Data to be sent in the post request
     * @param location The URI for the post request
     * */
    public void send(JSONObject data, URI location){
        HttpClient httpclient = new DefaultHttpClient();

        HttpPost httpPost = new HttpPost();
        httpPost.setURI(location);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        try {
            LOG.debug(this.getClass().getCanonicalName()+": URL to send"+location);
            LOG.debug(this.getClass().getCanonicalName()+": Data to send"+data.toString());

            StringEntity se = new StringEntity(data.toString());
            httpPost.setEntity(se);
            HttpResponse response = httpclient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            LOG.info("Status code: "+ statusCode);
            if(statusCode < 200 || statusCode > 299){
                LOG.error(this.getClass().getCanonicalName()+": Error sending data to elasticsearch");
                LOG.error(this.getClass().getCanonicalName()+":"+response.getStatusLine().getReasonPhrase());
            }else{
                LOG.debug(this.getClass().getCanonicalName()+": Success sending data to elasticsearch");
            }
        } catch (UnsupportedEncodingException e) {
            LOG.error(this.getClass().getCanonicalName()+": "+e.getMessage());
        } catch (ClientProtocolException e) {
            LOG.error(this.getClass().getCanonicalName()+": "+e.getMessage());
        } catch (IOException e) {
            LOG.error(this.getClass().getCanonicalName()+": "+e.getMessage());
        }
    }

    /**
     * This method performs a HTTP Delete operation on the given URI
     * @param deleteUri The URI of the resource to be deleted
     * */
    public void deleteIndex(URI deleteUri){
        HttpClient httpclient = new DefaultHttpClient();
        HttpDelete httpDelete = new HttpDelete();
        httpDelete.setURI(deleteUri);
        httpDelete.setHeader("Accept", "application/json");
        httpDelete.setHeader("Content-type", "application/json");
        try {
            LOG.debug(this.getClass().getCanonicalName()+": URL to delete: "+deleteUri);

            HttpResponse response = httpclient.execute(httpDelete);
            int statusCode = response.getStatusLine().getStatusCode();
            LOG.info("Status code: "+ statusCode);
            if(statusCode < 200 || statusCode > 299){
                LOG.error(this.getClass().getCanonicalName()+": Error deleting index in elasticsearch: "+deleteUri);
                LOG.error(this.getClass().getCanonicalName()+":"+response.getStatusLine().getReasonPhrase());
            }else{
                LOG.debug(this.getClass().getCanonicalName()+": Success deleting data in elastinsearch: "+deleteUri);
            }
        } catch (UnsupportedEncodingException e) {
            LOG.error(this.getClass().getCanonicalName()+": "+e.getMessage());
        } catch (ClientProtocolException e) {
            LOG.error(this.getClass().getCanonicalName()+": "+e.getMessage());
        } catch (IOException e) {
            LOG.error(this.getClass().getCanonicalName()+": "+e.getMessage());
        }
    }

    @Override
    public void run() {
        LOG.info(this.getClass().getCanonicalName()+": Executing index task");
        reindex();
    }


    /**
     * This method defines how to reindex the elastic search instance every hour.
     * It creates a new index for the current hour, adds it to the searcha nd write alias
     * and cleans up the previous index.
     * */
    public void reindex(){

        //Get index for this hour and previous hour indices
        Calendar today = Calendar.getInstance();
        int hour = today.get(Calendar.HOUR_OF_DAY);
        int prev_3hour = hour - 2;
        int prev_hour = hour - 1;

        if(prev_3hour<0){
            prev_3hour += MAX_INDEX;
        }

        if(prev_hour<0){
            prev_hour += MAX_INDEX;
        }

        // Create index
        String index = INDEX_PREFIX+hour;
        LOG.info(this.getClass().getCanonicalName()+"Index to be created--"+index);
        createIndex(index);

        // Add index to write alias
        modifyAlias(writeIndex,index,ALIAS_ACTION_ADD);

        // Remove prev_index from write alias
        String prev_index = INDEX_PREFIX+prev_hour;
        modifyAlias(writeIndex,prev_index,ALIAS_ACTION_REMOVE);

        //Add index to search alias
        modifyAlias(searchIndex,index,ALIAS_ACTION_ADD);

        // Remove prev_index from search alias
        String prev_3hour_index = INDEX_PREFIX+prev_3hour;
        modifyAlias(searchIndex,prev_3hour_index,ALIAS_ACTION_REMOVE);

        //delete the 2 hour old index
        String prev_3hour_uri = ElasticUtils.getAbsoluteEndPoint(location)+prev_3hour_index;
        try {
            URI prev_3hour_Index = new URI(prev_3hour_uri);
            deleteIndex(prev_3hour_Index);
        } catch (URISyntaxException e) {
            LOG.info(this.getClass().getCanonicalName()+": Error creating delete uri");
        }
    }
}
