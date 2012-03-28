package net.es.lookup.protocol.json;

import net.es.lookup.common.DeleteRequest;
import net.es.lookup.common.Service;
import java.util.Map;

public class JSONDeleteRequest implements DeleteRequest{
    public Service getContent(){
    	return null;
    }
    
	public int getStatus(){
		return 0;
	}
	
	public String getURI(){
		return null;
	}
	
	public Map getMap(){
		return null;
	}
	
}