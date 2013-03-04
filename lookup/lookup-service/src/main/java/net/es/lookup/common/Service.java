package net.es.lookup.common;


import java.util.Map;

/**
 * The Service class implements a Service entry. It encapsulates the set of key/value pairs that makes
 * an entry in the Lookup Service. It also provides useful helper methods for key/value pair manipulation.
 */
public class Service extends Message {

    private Message message = null;

    /*
     * Creates an empty Service
     */

    public Service() {

        super();

    }

    public Service(Map<String, Object> map) {

        super(map);

    }

}
