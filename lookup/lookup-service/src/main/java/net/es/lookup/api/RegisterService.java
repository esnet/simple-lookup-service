package net.es.lookup.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
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

import org.apache.log4j.Logger;

/**
 *
 */
public class RegisterService {
	private static Logger LOG = Logger.getLogger(RegisterService.class);
    private String params;

    public String registerService(String message) {
    	LOG.info("intializing registerService...");
        // this.params = params;
        // Return some cliched textual content
        JSONRegisterResponse response;
        JSONRegisterRequest request = new JSONRegisterRequest(message);
        if (request.getStatus() == JSONRegisterRequest.INCORRECT_FORMAT) {
            System.out.println("INCORRECT FORMAT");
            LOG.error("INCORRECR FORMAT OF JSON DATA");
            // TODO: return correct error code
            throw new BadRequestException("Error in JSON data");
           
        }
        // Verify that request is valid and authorized
        System.out.println(this.isValid(request));
        LOG.debug("valid?"+this.isValid(request));
        if (this.isValid(request) && this.isAuthed(request)) {
            // Generate a new URI for this service and add it to the service key/value pairs
            String uri = this.newURI (); 
            request.add (ReservedKeywords.RECORD_URI, uri);
            // Request a lease
            boolean gotLease = LeaseManager.getInstance().requestLease(request);
            if (gotLease) {
            	List<String> recordType = request.getRecordType();
                // Build the matching query request that must fail for the service to be published
                Message query = new Message();
                Message operators = new Message();
                
                List<String> list;
                
                
                
                List<String> queryKeyList = new ArrayList();
                
                if(recordType.get(0).equals(ReservedKeywords.RECORD_VALUE_DEFAULT)){
                	queryKeyList = getServiceRecordQueryKeys();
                	
                	for(int i =0; i<queryKeyList.size(); i++){
                		list=(List)request.getKey(queryKeyList.get(i));
                		query.add(queryKeyList.get(i),list);
                        operators.add(queryKeyList.get(i), ReservedKeywords.RECORD_OPERATOR_ALL);
                	}
                	
                }else{
                	
                	Map<String, Object> keyValues = request.getMap();
                	Iterator it = keyValues.entrySet().iterator();
                	
                    while (it.hasNext()) {
                        Map.Entry<String,Object> pairs = (Map.Entry)it.next();
                        if(!isIgnoreKey(pairs.getKey())){
                        	System.out.println(pairs.getKey() + " = " + pairs.getValue());
                        	LOG.debug("key-value pair:"+ pairs.getKey() + "=" + pairs.getValue());
                        	operators.add(pairs.getKey(), ReservedKeywords.RECORD_OPERATOR_ALL);
                        	query.add(pairs.getKey(),pairs.getValue());
                        	
                        }
                    }
                }
 
               try{
            	    Message res = ServiceDAOMongoDb.getInstance().queryAndPublishService(request,query,operators);

                	response = new JSONRegisterResponse (res.getMap());
                	System.out.println(JSONMessage.toString(response));
                	LOG.debug("response:"+ JSONMessage.toString(response));
                	return JSONMessage.toString(response);
                }catch(DataFormatException e){
                	LOG.fatal("Data formating exception");
                	throw new InternalErrorException("Data formatting exception");
                }catch(DuplicateEntryException e){
                	LOG.error("FobiddenRequestException:"+e.getMessage());
                	throw new ForbiddenRequestException(e.getMessage());
                }catch(DatabaseException e){
                	LOG.fatal("DatabaseException:" +e.getMessage());
                	throw new InternalErrorException(e.getMessage());
                }
            }else{
                // Build response
               // response = new JSONRegisterResponse (request.getMap());
            	LOG.fatal("Failed to secure lease for the registration record");
               throw new InternalErrorException("Failed to secure lease for the registration record");
            }


        }else{
        	if(!this.isValid(request)){
        		LOG.error("Invalid request:");
        		throw new BadRequestException("Invalid request");
        	}else if(!this.isAuthed(request)){
        		LOG.error("Not authorized to perform the request");
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
    
    private List<String> getServiceRecordQueryKeys(){
    	List<String> qList = new ArrayList();
    	qList.add(ReservedKeywords.RECORD_TYPE);
    	qList.add(ReservedKeywords.RECORD_SERVICE_LOCATOR);
    	qList.add(ReservedKeywords.RECORD_PRIVATEKEY);
    	qList.add(ReservedKeywords.RECORD_SERVICE_TYPE);
    	qList.add(ReservedKeywords.RECORD_SERVICE_DOMAIN);
    	return qList;
    }
    
    private boolean isIgnoreKey(String key){
    	if (key.equals(ReservedKeywords.RECORD_TTL) || key.equals(ReservedKeywords.RECORD_EXPIRES) || key.equals(ReservedKeywords.RECORD_URI)){
    		return true;
    	}else{
    		return false;
    	}
    }

}

