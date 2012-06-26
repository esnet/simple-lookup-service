package net.es.lookup.test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail
;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;

import net.sf.json.JSONObject;
import net.sf.json.JSONArray;


public class RegistrationTest {
	
	@Test
	public void testSimpleRegistration(){
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://localhost:8080/lookup/services");
		
		//BasicHttpParams params = new BasicHttpParams();
		
		JSONObject data=new JSONObject();
		JSONArray value = new JSONArray();
		value.add("service");
		data.put("record-type",value);
		
		value.clear();
		value.add("http://localhost/accesspoint3");
		data.put("record-service-locator",value);
	
		
		value.clear();
		value.add("privatekey1");
		data.put("record-privatekey",value);
		
		value.clear();
		value.add("bwctl");
		data.put("record-service-type",value);
		
		value.clear();
		value.add("ESnet");
		value.add("LHC");
		data.put("record-service-domain",value);
		
		System.out.println(data.toString());
		try{
			StringEntity se=new StringEntity (data.toString());
		
		
			httppost.setEntity(se);
			System.out.println(se);
		}catch(UnsupportedEncodingException e){
			fail("Connection error: "+e.getMessage());
		}
        
        
        httppost.setHeader("Accept", "application/json");
        httppost.setHeader("Content-type", "application/json");
		
		try{
			HttpResponse response = httpclient.execute(httppost);
			
			System.out.println(response.getStatusLine());
			
			StatusLine responseStatus = response.getStatusLine();
			assertEquals(200, responseStatus.getStatusCode());
		}catch(IOException e){
			fail("Connection error: "+e.getMessage());
		}
		
		

	}
	
}