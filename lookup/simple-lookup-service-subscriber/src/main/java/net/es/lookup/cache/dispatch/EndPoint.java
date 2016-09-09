package net.es.lookup.cache.dispatch;

import net.sf.json.JSONObject;

/**
 * Author: sowmya
 * Date: 8/8/16
 * Time: 4:15 PM
 *
 * This interface defines a destination. It has a single method send which is used
 * to send messages to the endpoint.
 */
public interface EndPoint {
    /**
     * This method is used to deliver messages to the endpoint.
     * @param data  The message to be delivered expressed as a JSONObject
     * */
    public void send(JSONObject data);
}
