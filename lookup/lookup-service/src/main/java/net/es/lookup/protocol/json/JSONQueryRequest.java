package net.es.lookup.protocol.json;

import net.es.lookup.common.QueryRequest;
import net.es.lookup.common.Service;
import java.util.Map;

public class JSONQueryRequest implements QueryRequest {

    public Service getContent(){
    	return null;
    }
    
	public int getStatus(){
		return 0;
	}
	
	public String getOperator(){
		return null;
	}
	
	public Map getMap(){
		return null;
	}
}