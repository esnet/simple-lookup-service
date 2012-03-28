package net.es.lookup.common;

import java.util.Map;

/**
 * The Service class implements a Service entry. It encapsulates the set of key/value pairs that makes
 * an entry in the Lookup Service. It also provides useful helper methods for key/value pair manipulation.
 */
public class Service {

    private Message message = null;

    /*
     * Creates an empty Service
     */
    public Service(Message message) {
        this.message = message;
    }

    public Service() {
        this.message = new Message();
    }

    public synchronized String toString() {
        StringBuffer buffer = new StringBuffer();

        return buffer.toString();
    }

    public Object getObject (String key) {
        Object obj = this.message.getMap().get(key);
        // TODO: add log here
        return obj;
    }

    public Map<String, Object> getMap() {
        return this.message.getMap();
    }

    public void add (String key, Object obj) throws DuplicateKeyException {
        this.message.add(key,obj);
    }


    public String getURI() {
        return (String) this.getMap().get(Message.SERVICE_URI);
    }

    public int getTTL() {
        return ((Integer) this.getMap().get(Message.TTL)).intValue();
    }
}
