package net.es.lookup.utils.config.elements;

import com.sun.jersey.core.util.StringIgnoreCaseKeyComparator;

import java.util.List;

/**
 * Author: sowmya
 * Date: 3/6/14
 * Time: 1:34 PM
 */
public class CacheConfig {

    private String name;
    private String type;
    private List<PublisherConfig> publishers;

    public CacheConfig(String name, String type, List<PublisherConfig> publishers) {

        this.name = name;
        this.type = type;
        this.publishers = publishers;
    }

    public String getName() {

        return name;
    }

    public String getType() {

        return type;
    }

    public List<PublisherConfig> getPublishers() {

        return publishers;
    }
}
