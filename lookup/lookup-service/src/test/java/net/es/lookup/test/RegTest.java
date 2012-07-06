package net.es.lookup.test;
import net.es.lookup.common.exception.api.BadRequestException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.HttpEntity;

import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import org.apache.http.util.EntityUtils;
import org.apache.http.HttpEntity;



//
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
			value.add("http://localhost/accesspointa11");
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
	//			assertEquals(200, responseStatus.);
				
			}catch(IOException e){
				fail("Connection error: "+e.getMessage());
			}
		}

	@Test
	public void testOutPutUri(){
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://localhost:8080/lookup/services");

		//BasicHttpParams params = new BasicHttpParams();

		JSONObject data=new JSONObject();
		JSONArray value = new JSONArray();
		value.add("service");
		data.put("record-type",value);

		value.clear();
		value.add("http://localhost/accesspointtttt232t2ssttddt");
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
			//			System.out.println("!!!!!!!!"+response.getEntity().getContentType());

			HttpEntity entity = response.getEntity();
			System.out.println(entity.getContentType());
			String httpresponse = EntityUtils.toString(entity);
			System.out.println("Response Content: "+httpresponse);
			System.out.println(response.getStatusLine());
			
			int n=0;
			String[] words;
			httpresponse= httpresponse.replace("\"","");

			for(int i=0;i<httpresponse.length();i++){ 
				if(httpresponse.charAt(i)==',') n++; 
			} 
			words=httpresponse.trim().split(","); 
			
			for(int i=0; i<words.length-1; i++) { 
				if(words[i].contains("record-uri"))
					System.out.print(words[i]+"\n"); 
			} 

			StatusLine responseStatus = response.getStatusLine();
			assertEquals(200, responseStatus.getStatusCode());
			//			assertEquals(200, responseStatus.);

		}catch(IOException e){
			fail("Connection error: "+e.getMessage());
		}
	}



		//double entry, data.put() will overwirte the existing value
		@Test
		public void testDoubleEntry(){
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://localhost:8080/lookup/services");
			
			//BasicHttpParams params = new BasicHttpParams();
			
			JSONObject data=new JSONObject();
			JSONArray value = new JSONArray();
			value.add("service");
			data.put("record-type",value);
			
			value.clear();
			value.add("http://localhost/accesspointb1");
			data.put("record-service-locator",value);
		
			
			value.clear();
			value.add("privatekey1");
			data.put("record-privatekey",value);
			
			value.clear();
			value.add("owamp");
			data.put("record-service-type",value);
			
			value.clear();//
			value.add("ping");
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
		
		//typo of the key: "service-type"->"service-typo"
		@Test
		public void testTypoServiceType(){
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://localhost:8080/lookup/services");
			
			//BasicHttpParams params = new BasicHttpParams();
			
			JSONObject data=new JSONObject();
			JSONArray value = new JSONArray();
			value.add("service");
			data.put("record-typo",value);
			
			value.clear();
			value.add("http://localhost/accesspointc1");
			data.put("record-service-locator",value);
		
			
			value.clear();
			value.add("privatekey1");
			data.put("record-privatekey",value);
			
			value.clear();
			value.add("owamp");
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
	
				assertEquals(400, responseStatus.getStatusCode());
			}catch(IOException e){
				fail("Connection error: "+e.getMessage());
			}
		}
		
		//typo of the value of the key "service type"
		@Test
		public void testWrongServiceType(){
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://localhost:8080/lookup/services");
			
			//BasicHttpParams params = new BasicHttpParams();
			
			JSONObject data=new JSONObject();
			JSONArray value = new JSONArray();
			value.add("serv");
			data.put("record-type",value);
			
			value.clear();
			value.add("http://localhost/accesspointd1");
			data.put("record-service-locator",value);
		
			
			value.clear();
			value.add("privatekey1");
			data.put("record-privatekey",value);
			
			value.clear();
			value.add("owamp");
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
		
		
		// Leave the “service-type” field as blank
		@Test
		public void testBlankServiceType(){
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://localhost:8080/lookup/services");
			
			//BasicHttpParams params = new BasicHttpParams();
			
			JSONObject data=new JSONObject();
			JSONArray value = new JSONArray();
	
			
			value.clear();
			value.add("http://localhost/accesspointe1");
			data.put("record-service-locator",value);
		
			
			value.clear();
			value.add("privatekey1");
			data.put("record-privatekey",value);
			
			value.clear();
			value.add("owamp");
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
				assertEquals(400, responseStatus.getStatusCode());
			}catch(IOException e){
				fail("Connection error: "+e.getMessage());
			}
		}
		
		//Typo any key besides “service-type”
		@Test
		public void testTypoAnyKey(){
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://localhost:8080/lookup/services");
			
			//BasicHttpParams params = new BasicHttpParams();
			
			JSONObject data=new JSONObject();
			JSONArray value = new JSONArray();
			value.add("service");
			data.put("record-type",value);
			
			value.clear();
			value.add("http://localhost/accesspointf1");
			data.put("record-service-locator",value);
		
			
			value.clear();
			value.add("privatekey1");
			data.put("record-privatekey",value);
			
			value.clear();
			value.add("owamp");
			data.put("record--type",value);
			
			
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
	
				assertEquals(400, responseStatus.getStatusCode());
			}catch(IOException e){
				fail("Connection error: "+e.getMessage());
			}
		}
		
		
		//leave any value besides "record-type" as blank
		@Test
		public void testBlankAnyValue(){
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://localhost:8080/lookup/services");
			
			//BasicHttpParams params = new BasicHttpParams();
			
			JSONObject data=new JSONObject();
			JSONArray value = new JSONArray();
			value.add("service");
			data.put("record-type",value);
			
			value.clear();
			value.add("http://localhost/accesspointg1");
			data.put("record-service-locator",value);
		
			
			value.clear();
			value.add("privatekey1");
			data.put("record-privatekey",value);
			
			value.clear();
			value.add("");
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
		
		//leave any field besides "record-type" as blank
		@Test
		public void testBlankAnyField(){
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://localhost:8080/lookup/services");
			
			//BasicHttpParams params = new BasicHttpParams();
			
			JSONObject data=new JSONObject();
			JSONArray value = new JSONArray();
			value.add("service");
			data.put("record-type",value);
			
			value.clear();
			value.add("http://localhost/accesspointh1");
			data.put("record-service-locator",value);
		
			
			value.clear();
			value.add("privatekey1");
			data.put("record-privatekey",value);
			
			value.clear();
			value.add("owamp");
			data.put("record-service-type",value);
			
			
	//		value.clear();
	//		value.add("ESnet");
	//		value.add("LHC");
	//		data.put("record-service-domain",value);
			
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
		
		
		//typo the value of  "record-service-type"
		@Test
		public void testTypoAnyValue(){
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://localhost:8080/lookup/services");
			
			//BasicHttpParams params = new BasicHttpParams();
			
			JSONObject data=new JSONObject();
			JSONArray value = new JSONArray();
			value.add("service");
			data.put("record-type",value);
			
			value.clear();
			value.add("http://localhost/accesspointi1");
			data.put("record-service-locator",value);
		
			
			value.clear();
			value.add("privatekey1");
			data.put("record-privatekey",value);
			
			value.clear();
			value.add("owp");
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
		
		//Typo the value of “Content-type”
		@Test
		public void testWrongContentTypeValue(){
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://localhost:8080/lookup/services");
			
			//BasicHttpParams params = new BasicHttpParams();
			
			JSONObject data=new JSONObject();
			JSONArray value = new JSONArray();
			value.add("service");
			data.put("record-type",value);
			
			value.clear();
			value.add("http://localhost/accesspointj1");
			data.put("record-service-locator",value);
		
			
			value.clear();
			value.add("privatekey1");
			data.put("record-privatekey",value);
			
			value.clear();
			value.add("owamp");
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
	        httppost.setHeader("Content-type", "application/js");
	    
			
			try{
				HttpResponse response = httpclient.execute(httppost);
				
				System.out.println(response.getStatusLine());
				
				StatusLine responseStatus = response.getStatusLine();
	
				assertEquals(415, responseStatus.getStatusCode());
			}catch(IOException e){
				fail("Connection error: "+e.getMessage());
			}
		}
		
		
		//leave the value of the "Content-type" as blank
		@Test
		public void testBlankContentTypeValue(){
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://localhost:8080/lookup/services");
			
			//BasicHttpParams params = new BasicHttpParams();
			
			JSONObject data=new JSONObject();
			JSONArray value = new JSONArray();
			value.add("service");
			data.put("record-type",value);
			
			value.clear();
			value.add("http://localhost/accesspointk1");
			data.put("record-service-locator",value);
		
			
			value.clear();
			value.add("privatekey1");
			data.put("record-privatekey",value);
			
			value.clear();
			value.add("owamp");
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
	        httppost.setHeader("Content-type", "");
	    
			
			try{
				HttpResponse response = httpclient.execute(httppost);
				
				System.out.println(response.getStatusLine());
				
				StatusLine responseStatus = response.getStatusLine();
	
				assertEquals(400, responseStatus.getStatusCode());
			}catch(IOException e){
				fail("Connection error: "+e.getMessage());
			}
		}
		
		
		// write a wrong directory
		@Test
		public void testWrongDirectory(){
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://localhost:8080/services");
			
			//BasicHttpParams params = new BasicHttpParams();
			
			JSONObject data=new JSONObject();
			JSONArray value = new JSONArray();
			value.add("service");
			data.put("record-type",value);
			
			value.clear();
			value.add("http://localhost/accesspointl1");
			data.put("record-service-locator",value);
		
			
			value.clear();
			value.add("privatekey1");
			data.put("record-privatekey",value);
			
			value.clear();
			value.add("owamp");
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
	
				assertEquals(404, responseStatus.getStatusCode());
			}catch(IOException e){
				fail("Connection error: "+e.getMessage());
			}
		}
		
		//Remove the “Content-Type” field
		@Test
		public void testRemoveContentType(){
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://localhost:8080/lookup/services");
			
			//BasicHttpParams params = new BasicHttpParams();
			
			JSONObject data=new JSONObject();
			JSONArray value = new JSONArray();
			value.add("service");
			data.put("record-type",value);
			
			value.clear();
			value.add("http://localhost/accesspointm1");
			data.put("record-service-locator",value);
		
			
			value.clear();
			value.add("privatekey1");
			data.put("record-privatekey",value);
			
			value.clear();
			value.add("owamp");
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
	        
	    
			
			try{
				HttpResponse response = httpclient.execute(httppost);
				
				System.out.println(response.getStatusLine());
				
				StatusLine responseStatus = response.getStatusLine();
	
				assertEquals(415, responseStatus.getStatusCode());
			}catch(IOException e){
				fail("Connection error: "+e.getMessage());
			}
		}

		@Test
		public void testRegistration012(){
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://localhost:8080/lookup/services");
	//		HttpGet httppost = new HttpGet("http://localhost:8080/lookup/services");
			
			//BasicHttpParams params = new BasicHttpParams();
			
			JSONObject data=new JSONObject();
			JSONArray value = new JSONArray();
			value.add("service");
			data.put("record-type",value);
			
			value.clear();
			value.add("http://localhost/accesspoint000000000000");
			data.put("record-service-locator",value);
		
			
			value.clear();
			value.add("privatekey1");
			data.put("record-privatekey",value);
			
			value.clear();
			value.add("owamp");
			data.put("record-service-type",value);
			value.clear();
			value.add("ping");
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