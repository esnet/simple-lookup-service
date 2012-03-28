package net.es.lookup.protocol.json;


import net.es.lookup.common.RenewResponse;
import net.es.lookup.common.Service;
import java.util.ArrayList;
import java.util.Map;

public class JSONRenewResponse implements RenewResponse {

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
	
	public void setResult(ArrayList<Service> s){
		
	}
	
	public Map getMap(){
		return null;
	}
}