package net.es.lookup.protocol.json;

import net.es.lookup.common.RegisterResponse;

public class JSONRegisterResponse implements RegisterResponse {
	public JSONRegisterResponse(){
		
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
	
	public Object getContent(){
		return null;
	}
	
	public int getStatus(){
		return 0;
	}
}