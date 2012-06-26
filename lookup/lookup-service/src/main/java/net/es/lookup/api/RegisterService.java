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
import net.es.lookup.common.exception.api.ForbiddenRequestException;
import net.es.lookup.common.exception.api.UnauthorizedException;
import net.es.lookup.common.exception.internal.DataFormatException;
import net.es.lookup.common.exception.internal.DatabaseException;
import net.es.lookup.common.exception.internal.DuplicateEntryException;

/**
 *
 */
public class RegisterService {

    private String params;

    public String registerService(String message) {
    	
        // this.params = params;
        // Return some cliched textual content
        JSONRegisterResponse response;
        JSONRegisterRequest request = new JSONRegisterRequest(message);
        if (request.getStatus() == JSONRegisterRequest.INCORRECT_FORMAT) {
            System.out.println("INCORRECT FORMAT");
            // TODO: return correct error code
            throw new BadRequestException("Error in JSON data");
           
        }
        // Verify that request is valid and authorized
        System.out.println(this.isValid(request));
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
                }catch(DuplicateEntryException e){
                	throw new ForbiddenRequestException(e.getMessage());
                }catch(DatabaseException e){
                	throw new InternalErrorException(e.getMessage());
                }
            }else{
                // Build response
               // response = new JSONRegisterResponse (request.getMap());
               throw new InternalErrorException("Failed to secure lease for the registration record");
            }


        }else{
        	if(!this.isValid(request)){
        		throw new BadRequestException("Invalid request");
        	}else if(!this.isAuthed(request)){
        		throw new UnauthorizedException("Not authorized to perform the request");
        	}
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
        boolean res = request.validate();
        
        if(res){
            List<String> recordType = request.getRecordType();
          
            if((recordType == null) || recordType.isEmpty()){
            	return false;
            }
            
            if(recordType.size() > 1){
            	return false;
            }
            
        	if(recordType.get(0).equals(ReservedKeywords.RECORD_VALUE_DEFAULT)){
            	res = !((request.getAccessPoint() == null) || (request.getServiceType() == null));
            }else{
            	res=true;
            }
        
        }

        return res;
    }

    private String newURI() {
        String uri = LookupService.SERVICE_URI_PREFIX + "/" + UUID.randomUUID().toString();
        return uri;
    }

}

