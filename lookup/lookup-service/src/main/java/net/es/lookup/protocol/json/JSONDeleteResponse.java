package net.es.lookup.protocol.json;

import java.util.Map;
import net.es.lookup.common.Service;
import net.es.lookup.common.DeleteResponse;

public class JSONDeleteResponse implements DeleteResponse{
    public Service getContent(){
    	return null;
    }
    
	public int getStatus(){
		return 0;
	}
	
	public int getError(){
		return 0;
	}
	public String getErrorMessage(){
		return null;
	}
	public void setError(int code){
		
	}
	public void setErrorMessage(String s){
		
	}
	
	public Map getMap(){
		return null;
	}
	
}