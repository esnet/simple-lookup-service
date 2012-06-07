package net.es.lookup.api;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.es.lookup.common.Message;
import net.es.lookup.common.Service;
import net.es.lookup.database.ServiceDAOMongoDb;
import net.es.lookup.protocol.json.JSONMessage;
import net.es.lookup.common.DuplicateKeyException;
import net.es.lookup.resources.ServicesResource;

public class QueryServices {

    private String params;

    //constructs query and operator messages and calls the DB function
    public String query(Message request, int maxResult, int skip) {
    	
        String response;
        
        Map<String, Object> requestMap = request.getMap();
        Map<String, Object> requestMapCopy = request.getMap();
        
        Message queryParameters = new Message();
        Message operators = new Message();
        
        try{    
        	queryParameters.add(ServicesResource.OPERATOR, request.getOperator());
        	int size = requestMap.size();
        	System.out.println("Total number of parameters passed in request="+size);
        
        	for (Map.Entry<String, Object> entry : requestMap.entrySet()) {
        		
        		String key = entry.getKey();
        		Object value = entry.getValue();
		
        		//generate the operator map
        		if (!key.contains(ServicesResource.OPERATOR)){
        			queryParameters.add(key,value);
        			String opKey = key+"-"+ServicesResource.OPERATOR;
        			if(requestMap.containsKey(opKey)){
        				operators.add(key,requestMap.get(opKey));
        			}else{
        				//add default
        				operators.add(key, ServicesResource.DEFAULT_OPERATOR);
        			}
        		}
            
        	}
        
        	// Query DB
        	List<Service> res = ServiceDAOMongoDb.getInstance().query(request, queryParameters, operators, maxResult, skip);
        	// Build response
            response = JSONMessage.toString(res);
            return response;
        }catch(DuplicateKeyException dke){
        	return "Duplicate Key Found"; 
        }
        
    }

}
