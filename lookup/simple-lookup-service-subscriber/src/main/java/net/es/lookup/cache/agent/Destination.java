package net.es.lookup.cache.agent;

import java.net.URI;

/**
 * Author: sowmya
 * Date: 4/13/16
 * Time: 3:24 PM
 */
public class Destination {

    public static final String DESTINATION_ELASTIC = "elastic";
    public static final String DESTINATION_DEFAULT = "default";


    private URI endpoint;

    private String type;


    public Destination(URI endpoint, String type) {

        this.endpoint = endpoint;
        this.type = type;
    }

    public URI getEndpoint() {

        return endpoint;
    }

    public String getType() {

        return type;
    }
}
