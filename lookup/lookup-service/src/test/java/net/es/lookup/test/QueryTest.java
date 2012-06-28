package net.es.lookup.test;
import net.es.lookup.common.exception.api.BadRequestException;
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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;

import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import org.apache.http.util.EntityUtils;
import org.apache.http.HttpEntity;




public class QueryTest {

//		@Test
//		public void testSingleValue(){
//			HttpClient httpclient = new DefaultHttpClient();
//			HttpGet httpget = new HttpGet("http://localhost:8080/lookup/services");
//			
//			//BasicHttpParams params = new BasicHttpParams();
//			
//			JSONObject data=new JSONObject();
//			JSONArray value = new JSONArray();
////			value.add("service");
////			data.put("record-type",value);
////			
////			value.clear();
////			value.add("http://localhost/accesspointa11");
////			data.put("record-service-locator",value);
////		
////			
////			value.clear();
////			value.add("privatekey1");
////			data.put("record-privatekey",value);
////			
////			value.clear();
////			value.add("bwctl");
////			data.put("record-service-type",value);
//			
//			
//			
//			value.clear();
//			value.add("ESnet");
//			value.add("LHC");
//			data.put("record-service-domain",value);
//			
//			System.out.println(data.toString());
////			try{
////				StringEntity se=new StringEntity (data.toString());
////			
////				httpget.setEntity(se);
////				System.out.println(se);
////			}catch(UnsupportedEncodingException e){
////				fail("Connection error: "+e.getMessage());
////			}
//	        
//	        httpget.setHeader("Accept", "application/json");
//	        httpget.setHeader("Content-type", "application/json");
//			
//			try{
//				HttpResponse response = httpclient.execute(httpget);
//				
//				System.out.println(response.getStatusLine());
//				
//				HttpEntity entity = response.getEntity();
//				System.out.println(entity.getContentType());
//				String httpresponse = EntityUtils.toString(entity);
//				System.out.println("Response Content: "+httpresponse);
//				
//				StatusLine responseStatus = response.getStatusLine();
//				assertEquals(200, responseStatus.getStatusCode());
//				
//			}catch(IOException e){
//				fail("Connection error: "+e.getMessage());
//			}
//		}
		
		
		@Test
		public void testMultiValue(){
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet("http://localhost:8080/lookup/services");
			
			BasicHttpParams params = new BasicHttpParams();
			
			params.setParameter("record-service-locator","wash-owamp.es.net");
			
			params.setParameter("record-service-type","ping");
			System.out.println("PARAMS:"+params.getParameter("record-service-type"));
			
//			JSONObject data=new JSONObject();
//			JSONArray value = new JSONArray();
//			value.add("service");
//			data.put("record-type",value);
//			
//			value.clear();
//			value.add("wash-owamp.es.net");
//			data.put("record-service-locator",value);
//		
//			
////			value.clear();
////			value.add("privatekey1");
////			data.put("record-privatekey",value);
////			
//			value.clear();
//			value.add("ping");
//			data.put("record-service-type",value);
//			
//			
//			
//			value.clear();
//			value.add("ESnet");
////			value.add("LHC");
//			data.put("record-service-domain",value);
//			
//			System.out.println("11111111"+params.toString());
//			try{
//				HttpEntity se=data.toString();
			
			httpget.setParams(params);
//				System.out.println(se);
//			}catch(UnsupportedEncodingException e){
//				fail("Connection error: "+e.getMessage());
//			}
	        
	        httpget.setHeader("Accept", "application/json");
	        httpget.setHeader("Content-type", "application/json");
	        
	        System.out.println("11111111"+httpget.getParams().toString());

			
			try{
				HttpResponse response = httpclient.execute(httpget);
				
				System.out.println(response.getStatusLine());
//				
//				HttpEntity entity = response.getEntity();
//				System.out.println(entity.getContentType());
//				String httpresponse = EntityUtils.toString(entity);
//				System.out.println("Response Content: "+httpresponse);
				
				StatusLine responseStatus = response.getStatusLine();
				assertEquals(200, responseStatus.getStatusCode());
				
			}catch(IOException e){
				fail("Connection error: "+e.getMessage());
			}
		}

		
//		
//		@Test
//		public void testOperator(){
//			HttpClient httpclient = new DefaultHttpClient();
//			HttpGet httpget = new HttpGet("http://localhost:8080/lookup/services");
//			
//			//BasicHttpParams params = new BasicHttpParams();
//			
//			JSONObject data=new JSONObject();
//			JSONArray value = new JSONArray();
//			value.add("service");
//			data.put("record-type",value);
//			
//			value.clear();
//			value.add("http://localhost/accesspointa11");
//			data.put("record-service-locator",value);
//		
//				
//			
//			value.clear();
//			value.add("bwctl");
//			data.put("record-service-type",value);
//			
//			
//			value.clear();
//			value.add("ESnet");
//			value.add("LHC");
//			data.put("record-service-domain",value);
//			
//			value.clear();
//			value.add("all");
//			data.put("record-operator",value);
//			
//			System.out.println(data.toString());
////			try{
////				StringEntity se=new StringEntity (data.toString());
////			
////				httpget.setEntity(se);
////				System.out.println(se);
////			}catch(UnsupportedEncodingException e){
////				fail("Connection error: "+e.getMessage());
////			}
//	        
//	        httpget.setHeader("Accept", "application/json");
//	        httpget.setHeader("Content-type", "application/json");
//			
//			try{
//				HttpResponse response = httpclient.execute(httpget);
//				
//				System.out.println(response.getStatusLine());
//				
//				HttpEntity entity = response.getEntity();
//				System.out.println(entity.getContentType());
//				String httpresponse = EntityUtils.toString(entity);
//				System.out.println("Response Content: "+httpresponse);
//				
//				StatusLine responseStatus = response.getStatusLine();
//				assertEquals(200, responseStatus.getStatusCode());
//				
//			}catch(IOException e){
//				fail("Connection error: "+e.getMessage());
//			}
//		}

		
		
		

//	@Test
//	public void testOutPutUri(){
//		HttpClient httpclient = new DefaultHttpClient();
//		HttpPost httppost = new HttpPost("http://localhost:8080/lookup/services");
//
//		//BasicHttpParams params = new BasicHttpParams();
//
//		JSONObject data=new JSONObject();
//		JSONArray value = new JSONArray();
//		value.add("service");
//		data.put("record-type",value);
//
//		value.clear();
//		value.add("http://localhost/accesspointtttt232t2ttt");
//		data.put("record-service-locator",value);
//
//
//		value.clear();
//		value.add("privatekey1");
//		data.put("record-privatekey",value);
//
//		value.clear();
//		value.add("bwctl");
//		data.put("record-service-type",value);
//
//
//
//		value.clear();
//		value.add("ESnet");
//		value.add("LHC");
//		data.put("record-service-domain",value);
//
//		System.out.println(data.toString());
//		try{
//			StringEntity se=new StringEntity (data.toString());
//
//			httppost.setEntity(se);
//			System.out.println(se);
//		}catch(UnsupportedEncodingException e){
//			fail("Connection error: "+e.getMessage());
//		}
//
//		httppost.setHeader("Accept", "application/json");
//		httppost.setHeader("Content-type", "application/json");
//
//		try{
//			HttpResponse response = httpclient.execute(httppost);
//			//			System.out.println("!!!!!!!!"+response.getEntity().getContentType());
//
//			HttpEntity entity = response.getEntity();
//			System.out.println(entity.getContentType());
//			String httpresponse = EntityUtils.toString(entity);
//			System.out.println("Response Content: "+httpresponse);
//			System.out.println(response.getStatusLine());
//			
//			int n=0;
//			String[] words;
//			httpresponse= httpresponse.replace("\"","");
//
//			for(int i=0;i<httpresponse.length();i++){ 
//				if(httpresponse.charAt(i)==',') n++; 
//			} 
//			words=httpresponse.trim().split(","); 
//			
//			for(int i=0; i<words.length-1; i++) { 
//				if(words[i].contains("record-uri"))
//					System.out.print(words[i]+"\n"); 
//			} 
//
//			StatusLine responseStatus = response.getStatusLine();
//			assertEquals(200, responseStatus.getStatusCode());
//			//			assertEquals(200, responseStatus.);
//
//		}catch(IOException e){
//			fail("Connection error: "+e.getMessage());
//		}
//	}




}