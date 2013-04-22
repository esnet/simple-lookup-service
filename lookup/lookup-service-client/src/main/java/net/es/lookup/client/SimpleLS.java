package net.es.lookup.client;

/**
 * User: sowmya
 * Date: 9/25/12
 * Time: 2:46 PM
 */

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;

import net.es.lookup.common.ReservedKeys;
import net.es.lookup.common.exception.LSClientException;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

public class SimpleLS {

    private String host = "localhost";
    private int port = 8090;
    private String connectionUrl;
    private HttpURLConnection connection;
    private String connectionType; //limited to http methods - GET, POST, DELETE
    private String status = ReservedKeys.SERVER_STATUS_UNKNOWN;
    private long latency = 0;
    private String data="";
    private int timeout = 5000;
    private String response;

    public int getTimeout() {

        return timeout;
    }

    public void setTimeout(int timeout) {

        this.timeout = timeout;
    }

    public String getResponse() {


        return response;
    }

    public int getResponseCode() {

        return responseCode;
    }

    public String getErrorMessage() {

        return errorMessage;
    }

    private int responseCode;
    private String errorMessage;


    public SimpleLS() {

        this.connectionUrl = "http://" + host + ":" + port;
        this.connectionType = "GET";
    }

    public SimpleLS(String host, int port, String connectionType) throws LSClientException {

        this.host = host;
        this.port = port;
        this.connectionUrl = "http://" + host + ":" + port;
        if (isValidConnectiontype(connectionType)) {
            this.connectionType = connectionType;
        } else {
            throw new LSClientException("Invalid Connection Type");
        }


    }

    private boolean isValidConnectiontype(String connectionType) {

        if (connectionType.equalsIgnoreCase("GET") || connectionType.equalsIgnoreCase("POST") || connectionType.equalsIgnoreCase("DELETE")) {
            return true;
        } else {
            return false;
        }
    }

    public String getHost() {

        return host;
    }

    public synchronized void setHost(String host) {

        this.host = host;
        this.connectionUrl = "http://" + host + ":" + port;
    }

    public int getPort() {

        return port;
    }

    public void setPort(int port) {

        this.port = port;
        this.connectionUrl = "http://" + host + ":" + port;
    }

    public String getConnectionUrl() {

        return connectionUrl;
    }

    public synchronized void setConnectionUrl(String connectionUrl) throws LSClientException {

        try {
            URL url = new URL(connectionUrl);
            this.host = url.getHost();
            this.port = url.getPort();
            this.connectionUrl = connectionUrl;
        } catch (MalformedURLException e) {
            throw new LSClientException(e.getMessage());
        }

    }

    public HttpURLConnection getConnection() {

        return connection;
    }

    public void setConnection(HttpURLConnection connection) {

        this.connection = connection;
    }

    public String getConnectionType() {

        return connectionType;
    }

    public void setConnectionType(String connectionType) throws LSClientException {

        if (isValidConnectiontype(connectionType)) {
            this.connectionType = connectionType;
        } else {
            throw new LSClientException("Invalid Connection type");
        }

    }

    public String getStatus() {

        return status;
    }

    public void setStatus(String status) {

        this.status = status;
    }

    public long getLatency() {

        return latency;
    }

    public void setLatency(long latency) {

        this.latency = latency;
    }

    public String getData() {

        return data;
    }

    public void setData(String data) {

        this.data = data;
    }

    public void connect() throws LSClientException {

        try {
            long start = System.nanoTime();
            boolean stat = InetAddress.getByName(host).isReachable(5000);
            if (stat) {
                long end = System.nanoTime();
                long lat = end - start;
                this.latency = lat;
                this.status = ReservedKeys.SERVER_STATUS_ALIVE;
            } else {
                this.latency = 0;
                this.status = ReservedKeys.SERVER_STATUS_UNREACHABLE;
            }

        } catch (IOException e) {
            throw new LSClientException(e.getMessage());
        }
        return;
    }

    public void send() throws LSClientException {

        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse httpResponse;
        if (connectionType.equalsIgnoreCase("GET")) {
            HttpGet httpGet = new HttpGet();
            httpGet.setHeader("Accept", "application/json");
            httpGet.setHeader("Content-type", "application/json");
            try {
                httpResponse = httpclient.execute(httpGet);
            } catch (IOException e) {
                throw new LSClientException(e.getMessage());
            }

        } else if (connectionType.equalsIgnoreCase("POST")) {
            System.out.println("Came to execute Post");
            HttpPost httpPost = new HttpPost();
            httpPost.setURI(URI.create(this.connectionUrl));
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            StringEntity se= null;
            try {
                se = new StringEntity(data);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            httpPost.setEntity(se);
            try {
                httpResponse = httpclient.execute(httpPost);
                System.out.println("Executed");
            } catch (IOException e) {
                throw new LSClientException(e.getMessage());
            }

        } else if (connectionType.equalsIgnoreCase("DELETE")) {
            HttpDelete httpDelete = new HttpDelete();
            try {
                httpResponse = httpclient.execute(httpDelete);
            } catch (IOException e) {
                throw new LSClientException(e.getMessage());
            }

        } else {
            throw new LSClientException("Cannot establish connection. Invalid connection type");
        }

        System.out.println("Got a response");

        this.responseCode = httpResponse.getStatusLine().getStatusCode();
        this.errorMessage = httpResponse.getStatusLine().getReasonPhrase();
        HttpEntity entity = httpResponse.getEntity();
        try {
            this.response = EntityUtils.toString(entity);
        } catch (IOException e) {
            throw new LSClientException(e.getMessage());
        }

        return;
    }
}

