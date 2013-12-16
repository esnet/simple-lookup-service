package net.es.lookup.client;

/**
 * User: sowmya
 * Date: 9/25/12
 * Time: 2:46 PM
 */

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;

import net.es.lookup.common.ReservedValues;
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

    private String protocol = "http";
    private URI connectionUrl;

    private String host = "localhost";
    private int port = 8090;
    private String relativeUrl;

    private String connectionType; //limited to http methods - GET, POST, DELETE
    private String status = ReservedValues.SERVER_STATUS_UNKNOWN;
    private long latency = 0;
    private String data;
    private int timeout = 5000;
    private String response;

    private int responseCode;
    private String errorMessage;

    public SimpleLS(String host, int port) throws LSClientException {

        this(host, port, "GET");

    }

    public SimpleLS(String host, int port, String connectionType) throws LSClientException {

        if (host != null && !host.isEmpty()) {
            this.host = host;
        } else {
            throw new LSClientException("Please enter valid host name");
        }

        this.port = port;

        try {
            this.connectionUrl = createAbsoluteUrl("");
        } catch (URISyntaxException e) {
            throw new LSClientException(e.getMessage());
        }
        if (isValidConnectionType(connectionType)) {
            this.connectionType = connectionType;
        } else {
            throw new LSClientException("Invalid Connection Type");
        }


    }

    public SimpleLS(URI url) throws LSClientException {

        this(url.getHost(),url.getPort(), "GET");

    }


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


    public String getHost() {

        return host;
    }


    public int getPort() {

        return port;
    }

    public String getRelativeUrl() {

        return relativeUrl;
    }

    public synchronized void setRelativeUrl(String relativeUrl) throws LSClientException {

        if (relativeUrl != null) {
            this.relativeUrl = relativeUrl;
            try {
                this.connectionUrl = createAbsoluteUrl(relativeUrl);
            } catch (URISyntaxException e) {
                throw new LSClientException(e.getMessage());
            }

        } else {
            throw new LSClientException("Empty parameter in setRelativeUrl");
        }

    }

    private URI createAbsoluteUrl(String relativeUrl) throws URISyntaxException {

        return new URI(protocol + "://" + host + ":" + port + "/" + relativeUrl);
    }

    public String getConnectionUrl() {

        return connectionUrl.toString();
    }

    public String getConnectionType() {

        return connectionType;
    }

    public synchronized void setConnectionType(String connectionType) throws LSClientException {

        if (isValidConnectionType(connectionType)) {
            this.connectionType = connectionType;
        } else {
            throw new LSClientException("Invalid Connection type");
        }

    }

    public String getStatus() {

        return status;
    }

    public long getLatency() {

        return latency;
    }

    public String getData() {

        return data;
    }

    public synchronized void setData(String data) {

        this.data = data;
    }

    public synchronized void connect() throws LSClientException {
        Socket socket = null;
        try {
            long start = System.nanoTime();
            socket = new Socket(host, port);
            long end = System.nanoTime();
            long lat = end - start;
            this.latency = lat;
            this.status = ReservedValues.SERVER_STATUS_ALIVE;
        }catch (IOException e){
            this.latency = 0;
            this.status = ReservedValues.SERVER_STATUS_UNREACHABLE;
            throw new LSClientException(e.getMessage());
        }finally {
            if (socket != null) try { socket.close(); } catch(IOException e) {}
        }
        return;
    }

    public synchronized void send() throws LSClientException {

        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse httpResponse;
        if (connectionType.equalsIgnoreCase("GET")) {
            HttpGet httpGet = new HttpGet();
            httpGet.setURI(connectionUrl);
            httpGet.setHeader("Accept", "application/json");
            httpGet.setHeader("Content-type", "application/json");
            try {
                httpResponse = httpclient.execute(httpGet);
            } catch (IOException e) {
                throw new LSClientException(e.getMessage());
            }

        } else if (connectionType.equalsIgnoreCase("POST")) {
            HttpPost httpPost = new HttpPost();
            httpPost.setURI(connectionUrl);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            StringEntity se;
            try {
                se = new StringEntity(data);
            } catch (UnsupportedEncodingException e) {
                throw new LSClientException(e.getMessage());
            }
            httpPost.setEntity(se);
            try {
                httpResponse = httpclient.execute(httpPost);
            } catch (IOException e) {
                throw new LSClientException(e.getMessage());
            }

        } else if (connectionType.equalsIgnoreCase("DELETE")) {
            HttpDelete httpDelete = new HttpDelete();
            httpDelete.setURI(connectionUrl);
            try {
                httpResponse = httpclient.execute(httpDelete);
            } catch (IOException e) {
                throw new LSClientException(e.getMessage());
            }

        } else {
            throw new LSClientException("Cannot establish connection. Invalid connection type");
        }


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


    private boolean isValidConnectionType(String connectionType) {

        if (connectionType.equalsIgnoreCase("GET") || connectionType.equalsIgnoreCase("POST") || connectionType.equalsIgnoreCase("DELETE")) {
            return true;
        } else {
            return false;
        }
    }
}

