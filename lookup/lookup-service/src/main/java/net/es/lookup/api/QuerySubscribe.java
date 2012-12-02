package net.es.lookup.api;

import net.es.lookup.protocol.json.JSONSubRequest;

public class QuerySubscribe {

    private String params;

    public String subscribe(String message) {

        /*  String response;
        try {
            JSONSubRequest request = new JSONSubRequest(message);
            if (request.getStatus() == JSONSubRequest.INCORRECT_FORMAT) {
                System.out.println("INCORRECT FORMAT");
                // TODO: return correct error code
                return "402\n";
            }
            // Verify that request is valid and authorized
            if (this.isValid(request) && this.isAuthed(request)) {
                List<Service> res = ServiceDAOMongoDb.getInstance().query(request);

                // Build response
                response = JSONMessage.toString(res);
                return response;
            }
        } catch (DuplicateKeyException e) {
            Thread.dumpStack();
            // TODO: Handle error
        }
        return "\n";
        */

        return "Not yet implemented";

    }


    private boolean isAuthed(JSONSubRequest request) {

        // The only case where a service registration is denied is when a service with the same name, same type with
        // the same client-uuid: this ensures that a service entry with a specified client-uuid cannot be overwritten.
        // TODO: needs to be implemented
        return true;

    }


    private boolean isValid(JSONSubRequest request) {

        // All mandatory key/value must be present
        boolean res = true;
        return res;

    }

}
