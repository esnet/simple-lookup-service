package net.es.lookup.api;

import net.es.lookup.common.exception.api.NotSupportedException;
import net.es.lookup.protocol.json.JSONSubRequest;
import org.apache.log4j.Logger;


public class SubscribeService {

    private static Logger LOG = Logger.getLogger(SubscribeService.class);

    public String subscribe(String serviceName, String message) {
        throw new NotSupportedException("Will be supported soon");

    }


    private boolean isAuthed(JSONSubRequest request) {

        // The only case where a service registration is denied is when a service with the same name, same type with
        // the same client-uuid: this ensures that a service entry with a specified client-uuid cannot be overwritten.
        // TODO: needs to be implemented
        return true;

    }

    /**
     * Checks if the message is valid
     */
    private boolean isValid(JSONSubRequest request) {

        // Can contain 0 or 1 key-value pair
        boolean res = true;
        if (request.getMap().size() > 1) {
            res = false;
        }
        LOG.debug("net.es.lookup.api.SubscribeService.isValid: Query size - " + request.getMap().size());
        LOG.debug("net.es.lookup.api.SubscribeService.isValid: Return Value - " + res);
        return res;

    }
}
