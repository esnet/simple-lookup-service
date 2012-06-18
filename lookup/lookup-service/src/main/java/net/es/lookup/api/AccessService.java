package net.es.lookup.api;


import java.util.List;
import java.util.Map;

import net.es.lookup.protocol.json.JSONMessage;
import net.es.lookup.protocol.json.JSONRenewRequest;
import net.es.lookup.protocol.json.JSONRenewResponse;
import net.es.lookup.database.ServiceDAOMongoDb;
import net.es.lookup.common.LeaseManager;
import net.es.lookup.common.Message;
import net.es.lookup.common.exception.internal.DuplicateKeyException;
import net.es.lookup.common.exception.internal.DatabaseException;
import net.es.lookup.common.Service;
import net.es.lookup.common.ReservedKeywords;
import net.es.lookup.common.exception.api.BadRequestException;
import net.es.lookup.common.exception.api.NotFoundException;
import net.es.lookup.common.exception.api.ForbiddenRequestException;
import net.es.lookup.common.exception.api.InternalErrorException;



/**
 *
 */

public class AccessService {


    public String getService(String service) {
        // Return some cliched textual content
        return "/lookup/service/" + service + "\n";
    }
    
    public String renewService(String serviceid, String service){
    	System.out.println("Processing renewService");
    	JSONRenewResponse response;
    	
    	Message errorResponse = new Message();
    	
    	try{
        JSONRenewRequest request = new JSONRenewRequest(service);
        if (request.getStatus() == JSONRenewRequest.INCORRECT_FORMAT) {
                System.out.println("INCORRECT FORMAT");
                // TODO: return correct error code
                throw new BadRequestException("Service request format is Incorrect\n");
            }
            
            // Verify that request is valid and authorized
            if (this.isValid(request) && this.isAuthed(serviceid, request)) {
            	
            	try{
            		Service serviceRecord = ServiceDAOMongoDb.getInstance().getServiceByURI(serviceid);
            	
            			if(serviceRecord!= null){
            				System.out.println("servicerecord not null");
            				Map<String, Object> serviceMap = serviceRecord.getMap();
            			if(request.getTTL()>(long)0){
            				serviceMap.put(ReservedKeywords.RECORD_TTL, request.getTTL());
            			}else{
            				serviceMap.put(ReservedKeywords.RECORD_TTL, (long)0);
            			}
            		
            			if(serviceMap.containsKey(ReservedKeywords.RECORD_EXPIRES)){
            				serviceMap.remove(ReservedKeywords.RECORD_EXPIRES);
            			}
            		
            			Message newRequest = new Message(serviceMap);
            		
            			boolean gotLease = LeaseManager.getInstance().requestLease(newRequest);
            			if(gotLease){
            				System.out.println("gotLease for "+serviceid);
            				Message res = ServiceDAOMongoDb.getInstance().updateService(serviceid,newRequest);
            				
            				if(res.getError() == 200){
            					response = new JSONRenewResponse (res.getMap());
            					return JSONMessage.toString(response);
            				}else{
            					
            				}
            				
            				
            			}	
            			}else{
            				throw new NotFoundException("Service Not Found in DB\n");
            			}
            	}catch(DatabaseException e){
            		throw new InternalErrorException("Database error\n");
            	}
            }else{
            	if(!this.isValid(request)){
            		throw new BadRequestException("Service Request is invalid\n");
            	}else if(!this.isAuthed(serviceid, request)){
            		throw new ForbiddenRequestException("The private-key is not authorized to access this service\n");
            	}
            	return JSONMessage.toString(errorResponse);        	
            }
    	}catch(DuplicateKeyException e){
    		throw new BadRequestException("Duplicate Keys Found");
    	}
        return "\n";
    	
    }
    
    
    private boolean isAuthed(String serviceid, JSONRenewRequest request) {
   
        // TODO: needs to be implemented. Check if client uuid matches
        return true;
    }


    private boolean isValid(JSONRenewRequest request) {
        // TODO: needs to be implemented. Check for client-uuid     
        return true;
    }
}

