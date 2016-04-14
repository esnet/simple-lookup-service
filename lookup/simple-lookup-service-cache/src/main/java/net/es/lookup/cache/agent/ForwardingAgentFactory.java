package net.es.lookup.cache.agent;

import net.sf.json.JSONObject;

/**
 * Author: sowmya
 * Date: 4/13/16
 * Time: 3:07 PM
 */
public class ForwardingAgentFactory {

    public static ForwardingAgent createForwardingAgent(Destination destination, JSONObject data){
        if (destination.getType().equals(Destination.DESTINATION_ELASTIC)){
            return new ElasticForwardingAgent(destination.getEndpoint(),data);
        }else if(destination.getType().equals(Destination.DESTINATION_DEFAULT)){
            return new ForwardingAgent(destination.getEndpoint(),data);
        }
        return null;
    }

}
