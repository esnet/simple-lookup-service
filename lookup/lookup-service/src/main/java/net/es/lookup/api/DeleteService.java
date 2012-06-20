package net.es.lookup.api;

import net.es.lookup.common.Message;
import net.es.lookup.protocol.json.JSONDeleteRequest;
import net.es.lookup.protocol.json.JSONDeleteResponse;
import net.es.lookup.database.ServiceDAOMongoDb;
import net.es.lookup.protocol.json.JSONMessage;


public class DeleteService {
	private String params;

    public String delete(String serviceid) {
    	
        // this.params = params;
        // Return some cliched textual content
    	JSONDeleteResponse response;

        // TODO: Needs rework
        /****
        Message res = ServiceDAOMongoDb.getInstance().deleteService(serviceid);

        // Build response
        response = new JSONDeleteResponse(res.getMap());
        return JSONMessage.toString(response);
        ****/
        return "Not implemented yet\n";

    }

    private boolean isAuthed(JSONDeleteRequest request) {
        // The only case where a service registration is denied is when client-uuid does not match: this ensures that a service entry with a specified client-uuid cannot be deleted accidentally.
        // TODO: needs to be implemented
        return true;
    }


    private boolean isValid(JSONDeleteRequest request) {
        // All mandatory key/value must be present
        boolean res = true;
        return res;
    }
}