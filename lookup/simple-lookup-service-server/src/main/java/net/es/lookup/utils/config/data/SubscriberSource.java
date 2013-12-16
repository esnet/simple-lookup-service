package net.es.lookup.utils.config.data;

import net.es.lookup.queries.Query;

import java.util.List;

/**
 * Author: sowmya
 * Date: 11/6/13
 * Time: 2:53 PM
 */
public class SubscriberSource {


    private String accessPoint;
    private List<Query> queries;

    public SubscriberSource(String accessPoint, List<Query> queries) {

        this.accessPoint = accessPoint;
        this.queries = queries;
    }

    public String getAccessPoint() {

        return accessPoint;
    }

    public List<Query> getQueries() {

        return queries;
    }


}
