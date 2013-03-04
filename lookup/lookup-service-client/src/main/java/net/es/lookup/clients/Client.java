package net.es.lookup.clients;

/**
 * User: sowmya
 * Date: 9/25/12
 * Time: 2:46 PM
 */

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import net.es.lookup.common.exception.LSClientException;

public class Client {

    private String connectionUrl;
    private HttpURLConnection connection;
    private String connectionType; //limited to http methods


    public Client() {

    }


    public Client(String url, String connectionType) {

        this.connectionUrl = url;
        this.connectionType = connectionType;

    }


    public String getConnectionUrl() {

        return connectionUrl;
    }


    public void setConnectionUrl(String connectionUrl) {

        this.connectionUrl = connectionUrl;
        this.connection = null;   //reset connection if url is modified

    }


    public String getConnectionType() {

        return connectionType;
    }


    public void setConnectionType(String connectionType) {

        this.connectionType = connectionType;
        this.connection = null;   //reset connection if connectionType is modified
    }


    protected HttpURLConnection getConnection() throws LSClientException {

        if (connectionUrl != null && !connectionUrl.isEmpty()) {

            URL tmpurl = null;

            try {

                tmpurl = new URL(connectionUrl);
                connection = (HttpURLConnection) tmpurl.openConnection();
                connection.setRequestMethod(connectionType);
                connection.setUseCaches(false);


            } catch (ProtocolException e){

                throw new LSClientException(e.getMessage());

            } catch (MalformedURLException e) {

                throw new LSClientException(e.getMessage());

            } catch (IOException e) {

                throw new LSClientException(e.getMessage());

            }


        }

        return connection;

    }


}

