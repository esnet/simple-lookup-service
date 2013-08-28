package net.es.lookup.common;


import java.util.Map;

/**
 * The ServiceRecord class implements a ServiceRecord entry. It encapsulates the set of key/value pairs that makes
 * an entry in the Lookup ServiceRecord. It also provides useful helper methods for key/value pair manipulation.
 */
public class Service extends Message {

    private Message message = null;

    /*
     * Creates an empty ServiceRecord
     */

    public Service() {

        super();

    }

    public Service(Map<String, Object> map) {

        super(map);

    }

}
