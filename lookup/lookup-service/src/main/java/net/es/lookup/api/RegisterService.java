package net.es.lookup.api;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.es.lookup.common.LeaseManager;
import net.es.lookup.common.Message;
import net.es.lookup.database.ServiceDAOMongoDb;
import net.es.lookup.protocol.json.JSONMessage;
import net.es.lookup.protocol.json.JSONRegisterRequest;
import net.es.lookup.protocol.json.JSONRegisterResponse;
import net.es.lookup.service.LookupService;
import net.es.lookup.common.ReservedKeywords;
import net.es.lookup.common.exception.api.BadRequestException;
import net.es.lookup.common.exception.api.InternalErrorException;
import net.es.lookup.common.exception.api.ConflictException;
import net.es.lookup.common.exception.internal.DuplicateKeyException;
import net.es.lookup.common.exception.internal.DataFormatException;
import net.es.lookup.common.exception.internal.DatabaseException;

/**
 *
 */
public class RegisterService {

    private String params;

    public String registerService(String message) {
    	
        // this.params = params;
        // Return some cliched textual content
        JSONRegisterResponse response;
        try {
            JSONRegisterRequest request = new JSONRegisterRequest(message);
            if (request.getStatus() == JSONRegisterRequest.INCORRECT_FORMAT) {
                System.out.println("INCORRECT FORMAT");
                // TODO: return correct error code
                throw new BadRequestException("Error in JSON data");
               
            }
            // Verify that request is valid and authorized
            if (this.isValid(request) && this.isAuthed(request)) {
                // Generate a new URI for this service and add it to the service key/value pairs
                String uri = this.newURI (); 
                request.add (ReservedKeywords.RECORD_URI, uri);
                // Request a lease
                boolean gotLease = LeaseManager.getInstance().requestLease(request);
                if (gotLease) {
                    // Build the matching query request that must fail for the service to be published
                    Message query = new Message();
                    Message operators = new Message();
                    
                    List<String> list;
                    list=(List)request.getAccessPoint();
                    query.add(ReservedKeywords.RECORD_SERVICE_LOCATOR,list);
                    operators.add(ReservedKeywords.RECORD_SERVICE_LOCATOR, ReservedKeywords.RECORD_OPERATOR_ALL);
                    
                    //list = new ArrayList<String>();
                    list=null;
                    list=(List)request.getClientUUID();
                    query.add(ReservedKeywords.RECORD_PRIVATEKEY,list);
                    operators.add(ReservedKeywords.RECORD_PRIVATEKEY, ReservedKeywords.RECORD_OPERATOR_ALL);
                    
                    //list = new ArrayList<String>();
                    list=null;
                    list=(List)request.getServiceType();
                    query.add(ReservedKeywords.RECORD_SERVICE_TYPE,list);
                    operators.add(ReservedKeywords.RECORD_SERVICE_TYPE, ReservedKeywords.RECORD_OPERATOR_ALL);
                    
                    //list = new ArrayList<String>();
                    list=null;
                    list=(List)request.getServiceDomain();
                    query.add(ReservedKeywords.RECORD_SERVICE_DOMAIN,list);
                    operators.add(ReservedKeywords.RECORD_SERVICE_DOMAIN, ReservedKeywords.RECORD_OPERATOR_ALL);
                    
                    System.out.println(request.getServiceDomain());
                   try{
                	    Message res = ServiceDAOMongoDb.getInstance().queryAndPublishService(request,query,operators);

                    	response = new JSONRegisterResponse (res.getMap());
                    	System.out.println(JSONMessage.toString(response));
                    	return JSONMessage.toString(response);
                    }catch(DataFormatException e){
                    	throw new InternalErrorException("Data formatting exception");
                    }catch(DatabaseException e){
                    	throw new InternalErrorException(e.getMessage());
                    }
                }else{
                    // Build response
                   // response = new JSONRegisterResponse (request.getMap());
                   throw new InternalErrorException("Failed to secure lease for the registration record");
                }

    
            }
        } catch (DuplicateKeyException e) {
            throw new ConflictException("Service record contains duplicate keys");
        }
        return "\n";
    }

    private boolean isAuthed(JSONRegisterRequest request) {
        // The only case where a service registration is denied is when a service with the same name, same type with
        // the same client-uuid: this ensures that a service entry with a specified client-uuid cannot be overwritten.
        // TODO: needs to be implemented
        return true;
    }


    private boolean isValid(JSONRegisterRequest request) {
        // All mandatory key/value must be present
        boolean res = false;

        res = ! (((request.getRecordType() == null) || request.getRecordType().isEmpty()));

        return res;
    }

    private String newURI() {
        String uri = LookupService.SERVICE_URI_PREFIX + "/" + UUID.randomUUID().toString();
        return uri;
    }

}

