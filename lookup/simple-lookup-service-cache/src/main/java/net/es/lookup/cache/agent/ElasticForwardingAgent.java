package net.es.lookup.cache.agent;

import net.sf.json.JSONObject;

import java.net.URI;

/**
 * Author: sowmya
 * Date: 4/13/16
 * Time: 3:06 PM
 */
public class ElasticForwardingAgent extends ForwardingAgent {

    public ElasticForwardingAgent(URI destination, JSONObject data) {

        super(destination, data);

    }
}
