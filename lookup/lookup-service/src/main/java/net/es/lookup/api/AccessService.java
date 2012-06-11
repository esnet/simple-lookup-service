package net.es.lookup.api;


import java.util.List;
import java.util.Map;

import net.es.lookup.protocol.json.JSONMessage;
import net.es.lookup.protocol.json.JSONRenewRequest;
import net.es.lookup.protocol.json.JSONRenewResponse;
import net.es.lookup.common.LeaseManager;
import net.es.lookup.common.Message;
import net.es.lookup.database.ServiceDAOMongoDb;
import net.es.lookup.common.DuplicateKeyException;
import net.es.lookup.common.Service;
import net.es.lookup.common.ReservedKeywords;



/**
 *
 */

public class AccessService {


    public String getService(String service) {
        // Return some cliched textual content
        return "/lookup/service/" + service + "\n";
    }
    
    public String renewService(String serviceid, String service){
    	//return "Renew functionality coming soon!!!\n" + service + "\n";
    	System.out.println("Came inside renewService");
    	JSONRenewResponse response;
        try {
            JSONRenewRequest request = new JSONRenewRequest(service);
            if (request.getStatus() == JSONRenewRequest.INCORRECT_FORMAT) {
                System.out.println("INCORRECT FORMAT");
                // TODO: return correct error code
                return "402\n";
            }
            
            // Verify that request is valid and authorized
            if (this.isValid(request) && this.isAuthed(serviceid, request)) {
            	//TODO: change to getService
            	Service serviceRecord = ServiceDAOMongoDb.getInstance().getServiceByURI(serviceid);
            	
            	if(serviceRecord!= null){
            		System.out.println("Came here: servicerecord not null ");
            		Map<String, Object> serviceMap = serviceRecord.getMap();
            		if(request.getTTL()>(long)0){
            			serviceMap.put(ReservedKeywords.TTL, request.getTTL());
            		}else{
            			serviceMap.put(ReservedKeywords.TTL, (long)0);
            		}
            		
            		if(serviceMap.containsKey(ReservedKeywords.EXPIRES)){
            			serviceMap.remove(ReservedKeywords.EXPIRES);
            		}
            		
            		Message newRequest = new Message(serviceMap);
            		
                	boolean gotLease = LeaseManager.getInstance().requestLease(newRequest);
                	if(gotLease){
                		System.out.println("Came here: gotLease for "+serviceid);
                		Message res = ServiceDAOMongoDb.getInstance().updateService(serviceid,newRequest);

                        response = new JSONRenewResponse (res.getMap());
                        return JSONMessage.toString(response);
                	}	
            	}

            }
        }catch (DuplicateKeyException e) {
            Thread.dumpStack();
            // TODO: Handle error
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

