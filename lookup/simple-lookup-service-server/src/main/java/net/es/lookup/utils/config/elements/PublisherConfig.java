package net.es.lookup.utils.config.elements;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Author: sowmya
 * Date: 3/6/14
 * Time: 1:36 PM
 */
public class PublisherConfig {

    private URI locator;
    private List<Map<String, Object>> queries;

    public PublisherConfig(URI locator, List<Map<String, Object>> queries) {

        this.locator = locator;
        this.queries = queries;
    }

    public URI getLocator() {

        return locator;
    }

    public List<Map<String, Object>> getQueries() {

        return queries;
    }
}
