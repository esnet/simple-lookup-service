package net.es.lookup.common;

import java.util.ArrayList;
import java.util.List;

/**
 * The Service class implements a Service entry. It encapsulates the set of key/value pairs that makes
 * an entry in the Lookup Service. It also provides useful helper methods for key/value pair manipulation.
 */
public class Service {
    private ArrayList<KeyValue> keyValues = null;

    /*
     * Creates an empty Service
     */
    public Service() {
        this.keyValues = new ArrayList<KeyValue>();
    }

    /*
     * @returns an ArrayList<KeyValue> with the KeyValue's of the Service.
     */
    public synchronized List<KeyValue> getKeyValues() {
        return new ArrayList<KeyValue>(keyValues);
    }

    /*
     * Adds a KeyValue to the Service
     */
    public synchronized void addKeyValue (KeyValue keyValue) {
        this.keyValues.add(keyValue);
    }

    /*
     * Remove a KeyValue from the Service.
     * @returns true if this list contained the specified element (or equivalently, if this list changed as a result of the call).
     */
    public synchronized boolean removeKeyValue (KeyValue keyValue) {
        return this.keyValues.remove(keyValue);
    }


    /*
     * @returns a List<KeyValue> containing all the KeyValue exactly matching the provided key.
     */
    public synchronized List<KeyValue> getKeyValues (String key) {
        ArrayList<KeyValue> results = new ArrayList<KeyValue>();
        for (KeyValue keyValue : this.keyValues) {
            if (keyValue.equals(key)) {
                results.add(keyValue);
            }
        }
        return results;
    }

    public synchronized String toString() {
        StringBuffer buffer = new StringBuffer();
        for (KeyValue keyValue : this.keyValues) {
            buffer.append(keyValue.getKey() + ":" + keyValue.getValue().toString());
        }
        return buffer.toString();
    }

}
