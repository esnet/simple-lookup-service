package net.es.lookup.pubsub;

import net.es.lookup.queries.Query;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Author: sowmya
 * Date: 3/5/14
 * Time: 3:02 PM
 */
public class Publisher {


    URI accesspoint;
    List<Map<String,Object>> queries;

    public Publisher(URI accesspoint, List<Map<String,Object>> queries) {

        this.accesspoint = accesspoint;
        this.queries = queries;
    }

    public URI getAccesspoint() {

        return accesspoint;
    }

    public void setAccesspoint(URI accesspoint) {

        this.accesspoint = accesspoint;
    }

    public List<Map<String,Object>> getQueries() {

        return queries;
    }

    public void setQueries(List<Map<String,Object>> queries) {

        this.queries = queries;
    }




}
