package net.es.lookup.api;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import net.es.lookup.common.Message;
import net.es.lookup.common.Service;
import net.es.lookup.database.ServiceDAOMongoDb;
import net.es.lookup.protocol.json.JSONMessage;
import net.es.lookup.common.exception.internal.DatabaseException;
import net.es.lookup.common.exception.internal.DataFormatException;
import net.es.lookup.common.exception.api.InternalErrorException;
import net.es.lookup.common.ReservedKeywords;

public class QueryServices {

    private String params;

    //constructs query and operator messages and calls the DB function
    public String query(Message request, int maxResult, int skip) {
	
    String response;
    
    Map<String, Object> requestMap = request.getMap();
    Map<String, Object> requestMapCopy = request.getMap();
    
    Message queryParameters = new Message();
    Message operators = new Message();
    
    int size = requestMap.size();
	System.out.println("Total number of parameters passed in request="+size);
	  
	System.out.println("request:"+requestMap.toString());
    	if(request.getOperator() != null){
    		System.out.println("ddddd"+request.getOperator());
    		List mainOp = request.getOperator();
        	operators.add(ReservedKeywords.RECORD_OPERATOR, mainOp);
    	}else{
    		List mainOp = new ArrayList();
    		mainOp.add(ReservedKeywords.RECORD_OPERATOR_DEFAULT);
    		operators.add(ReservedKeywords.RECORD_OPERATOR, mainOp);
    	}
    	System.out.println("operator"+operators.getMap());
    	for (Map.Entry<String, Object> entry : requestMap.entrySet()) {
    		
    		String key = entry.getKey();
    		Object value = entry.getValue();
    		
    		System.out.println("key= "+key);
	
    		//generate the operator map
    		if (!key.contains(ReservedKeywords.RECORD_OPERATOR_SUFFIX)){
    			queryParameters.add(key,value);
    			String opKey = key+"-"+ReservedKeywords.RECORD_OPERATOR_SUFFIX;
    			if(requestMap.containsKey(opKey)){
    				operators.add(key,requestMap.get(opKey));
//    				System.out.println("operators::"+operators.getMap());////
    			}else{
    				//add default
    				operators.add(key, ReservedKeywords.RECORD_OPERATOR_DEFAULT);
    				
    			}
    			System.out.println("operators::"+operators.getMap());////
    		}
    	}   

        	// Query DB
        	try{
        		List<Service> res = ServiceDAOMongoDb.getInstance().query(request, queryParameters, operators, maxResult, skip);
            	// Build response
                response = JSONMessage.toString(res);
                System.out.println(response);
                return response;
        	}catch(DatabaseException e){
        		throw new InternalErrorException("Error retrieving results");
        	}catch(DataFormatException e){
        		throw new InternalErrorException("Error formatting data");
        	}  
    }

}
