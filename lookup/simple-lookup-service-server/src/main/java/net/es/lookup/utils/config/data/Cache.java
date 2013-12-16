package net.es.lookup.utils.config.data;

import java.util.List;

/**
 * Author: sowmya
 * Date: 11/6/13
 * Time: 2:59 PM
 */
public class Cache {

    private String name;
    private String type;
    private List<SubscriberSource> sources;

    public Cache(String name, String type, List<SubscriberSource> sources) {

        this.name = name;
        this.type = type;
        this.sources = sources;
    }

    public String getName() {

        return name;
    }

    public String getType() {

        return type;
    }

    public List<SubscriberSource> getSources() {

        return sources;
    }
}
