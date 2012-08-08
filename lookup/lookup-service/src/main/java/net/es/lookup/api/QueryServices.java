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
import org.apache.log4j.Logger;





public class QueryServices {
	private static Logger LOG = Logger.getLogger(QueryServices.class);
    private String params;

    //constructs query and operator messages and calls the DB function
    public String query(Message request, int maxResult, int skip) {
    LOG.info("Processing queryService...");
    LOG.info("Received message: "+request.getMap());
    String response;
    
    Map<String, Object> requestMap = request.getMap();
    Map<String, Object> requestMapCopy = request.getMap();
    
    Message queryParameters = new Message();
    Message operators = new Message();
    
    int size = requestMap.size();
	
	LOG.debug("Total number of parameters passed in request="+size); 
	
	LOG.info("request:"+requestMap.toString());
    	if(request.getOperator() != null){ 		
    		List mainOp = request.getOperator();
        	operators.add(ReservedKeywords.RECORD_OPERATOR, mainOp);
    	}else{
    		List mainOp = new ArrayList();
    		mainOp.add(ReservedKeywords.RECORD_OPERATOR_DEFAULT);
    		operators.add(ReservedKeywords.RECORD_OPERATOR, mainOp);
    	}
    	
    	for (Map.Entry<String, Object> entry : requestMap.entrySet()) {
    		
    		String key = entry.getKey();
    		Object value = entry.getValue();
    		
    		LOG.debug("key= "+key);
	
    		//generate the operator map
    		if (!key.contains(ReservedKeywords.RECORD_OPERATOR_SUFFIX)){
    			queryParameters.add(key,value);
    			String opKey = key+"-"+ReservedKeywords.RECORD_OPERATOR_SUFFIX;
    			if(requestMap.containsKey(opKey)){
    				operators.add(key,requestMap.get(opKey));

    			}else{
    				//add default
    				operators.add(key, ReservedKeywords.RECORD_OPERATOR_DEFAULT);
    				
    			}
    			LOG.debug("operators::"+operators.getMap());////
    		}
    	}   

        	// Query DB
        	try{
        		List<Service> res = ServiceDAOMongoDb.getInstance().query(request, queryParameters, operators, maxResult, skip);
            	// Build response
                response = JSONMessage.toString(res);
        		//response = res;
                LOG.info("Query status: SUCCESS;");
                LOG.debug("Response:"+response);
                return response;
        	}catch(DatabaseException e){
        		LOG.fatal("Error retrieving results:" +e.getMessage());
        		LOG.info("Query status: FAILED; exiting");
        		throw new InternalErrorException("Error retrieving results");
        	}catch(DataFormatException e){
				LOG.error("Data formatting exception");
				LOG.info("Query status: FAILED; exiting");
        		throw new InternalErrorException("Error formatting data");
        	}  
    }

}
