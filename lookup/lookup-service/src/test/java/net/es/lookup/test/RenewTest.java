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




public class RenewTest {

		@Test
		public void testSimpleRenew(){
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://localhost:8080/lookup/service/78986dd0-f41b-4568-bfe7-14340ac736bf");
			
			//BasicHttpParams params = new BasicHttpParams();
			
			JSONObject data=new JSONObject();
//			JSONArray value = new JSONArray();
			String value="PT2H5M2S";
//			value.add("PT2H5M2S");
			data.put("record-ttl",value);
//			
//			value.clear();
//			value.add("http://localhost/accesspointa11");
//			data.put("record-service-locator",value);
//		
//			
//			value.clear();
//			value.add("privatekey1");
//			data.put("record-privatekey",value);
//			
//			value.clear();
//			value.add("bwctl");
//			data.put("record-service-type",value);
//			
//			
//			
//			value.clear();
//			value.add("ESnet");
//			value.add("LHC");
//			data.put("record-service-domain",value);
//			
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

				HttpEntity entity = response.getEntity();
				System.out.println(entity.getContentType());
				String httpres = EntityUtils.toString(entity);
				System.out.println("Response Content: "+ httpres);
				


				
				
				StatusLine responseStatus = response.getStatusLine();
				assertEquals(200, responseStatus.getStatusCode());
	//			assertEquals(200, responseStatus.);
				
			}catch(IOException e){
				fail("Connection error: "+e.getMessage());
			}
		}


		
		@Test
		public void testBlankTTL(){
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://localhost:8080/lookup/service/78986dd0-f41b-4568-bfe7-14340ac736bf");
			
			//BasicHttpParams params = new BasicHttpParams();
			
			JSONObject data=new JSONObject();

//			String value="PT2H5M2S";
//			data.put("record-ttl",value);
//			
//			value.clear();
//			value.add("http://localhost/accesspointa11");
//			data.put("record-service-locator",value);
//		
//			
//			value.clear();
//			value.add("privatekey1");
//			data.put("record-privatekey",value);
//			
//			value.clear();
//			value.add("bwctl");
//			data.put("record-service-type",value);
//			
//			
//			
//			value.clear();
//			value.add("ESnet");
//			value.add("LHC");
//			data.put("record-service-domain",value);
//			
//			System.out.println(data.toString());
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
				
				

				HttpEntity entity = response.getEntity();
				System.out.println(entity.getContentType());
				String httpresponse = EntityUtils.toString(entity);
				System.out.println("Response Content: "+httpresponse);
				System.out.println(response.getStatusLine());
				
				
				StatusLine responseStatus = response.getStatusLine();
				assertEquals(400, responseStatus.getStatusCode());
	
				
			}catch(IOException e){
				fail("Connection error: "+e.getMessage());
			}
		}
		
		@Test
		public void testTTLisNull(){
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://localhost:8080/lookup/service/78986dd0-f41b-4568-bfe7-14340ac736bf");
			
			//BasicHttpParams params = new BasicHttpParams();
			
			JSONObject data=new JSONObject();
			String value="";
			data.put("record-ttl",value);
//			
//			value.clear();
//			value.add("http://localhost/accesspointa11");
//			data.put("record-service-locator",value);
//		
//			
//			value.clear();
//			value.add("privatekey1");
//			data.put("record-privatekey",value);
//			
//			value.clear();
//			value.add("bwctl");
//			data.put("record-service-type",value);
//			
//			
//			
//			value.clear();
//			value.add("ESnet");
//			value.add("LHC");
//			data.put("record-service-domain",value);
//			
//			System.out.println(data.toString());
			try{
				StringEntity se=new StringEntity (data.toString());
			
				httppost.setEntity(se);
//				System.out.println(se);
			}catch(UnsupportedEncodingException e){
				fail("Connection error: "+e.getMessage());
			}
	        
	        httppost.setHeader("Accept", "application/json");
	        httppost.setHeader("Content-type", "application/json");
			
			try{
				HttpResponse response = httpclient.execute(httppost);
				
				System.out.println(response.getStatusLine());
				
				

				HttpEntity entity = response.getEntity();
//				System.out.println(entity.getContentType());
				String httpresponse = EntityUtils.toString(entity);
				System.out.println("Response Content: "+httpresponse);
//				System.out.println(response.getStatusLine());
				
				
				StatusLine responseStatus = response.getStatusLine();
				assertEquals(200, responseStatus.getStatusCode());
	
				
			}catch(IOException e){
				fail("Connection error: "+e.getMessage());
			}
		}
		
		
		@Test
		public void testtestWrongUri(){
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://localhost:8080/lookup/service/9a113c60-1f28-4e3a-9b7b-073955d842");
			
			//BasicHttpParams params = new BasicHttpParams();
			
			JSONObject data=new JSONObject();
			String value="PT2H5M2S";
			data.put("record-ttl",value);
//			
//			value.clear();
//			value.add("http://localhost/accesspointa11");
//			data.put("record-service-locator",value);
//		
//			
//			value.clear();
//			value.add("privatekey1");
//			data.put("record-privatekey",value);
//			
//			value.clear();
//			value.add("bwctl");
//			data.put("record-service-type",value);
//			
//			
//			
//			value.clear();
//			value.add("ESnet");
//			value.add("LHC");
//			data.put("record-service-domain",value);
//			
//			System.out.println(data.toString());
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

				HttpEntity entity = response.getEntity();
				System.out.println(entity.getContentType());
				String httpresponse = EntityUtils.toString(entity);
				System.out.println("Response Content: "+httpresponse);
				System.out.println(response.getStatusLine());
				
				
				StatusLine responseStatus = response.getStatusLine();
				assertEquals(404, responseStatus.getStatusCode());
	
				
			}catch(IOException e){
				fail("Connection error: "+e.getMessage());
			}
		}
		
		
		
		@Test
		public void testtestWrongPath(){
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://localhost:8080/lookup/service/9a113c60-1f28-4e3a-9b7b-073955d842");
			
			//BasicHttpParams params = new BasicHttpParams();
			
			JSONObject data=new JSONObject();
			String value="PT2H5M2S";
			data.put("record-ttl",value);
//			
//			value.clear();
//			value.add("http://localhost/accesspointa11");
//			data.put("record-service-locator",value);
//		
//			
//			value.clear();
//			value.add("privatekey1");
//			data.put("record-privatekey",value);
//			
//			value.clear();
//			value.add("bwctl");
//			data.put("record-service-type",value);
//			
//			
//			
//			value.clear();
//			value.add("ESnet");
//			value.add("LHC");
//			data.put("record-service-domain",value);
//			
//			System.out.println(data.toString());
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
				
				HttpEntity entity = response.getEntity();
				System.out.println(entity.getContentType());
				String httpresponse = EntityUtils.toString(entity);
				System.out.println("Response Content: "+httpresponse);
				System.out.println(response.getStatusLine());
				
				
				StatusLine responseStatus = response.getStatusLine();
				assertEquals(404, responseStatus.getStatusCode());
	
				
			}catch(IOException e){
				fail("Connection error: "+e.getMessage());
			}
		}
		
		
		
		
		@Test
		public void testtestWrongContentType(){
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://localhost:8080/lookup/service/9a113c60-1f28-4e3a-9b7b-073955d84219");
			
			//BasicHttpParams params = new BasicHttpParams();
			
			JSONObject data=new JSONObject();
			String value="PT2H5M2S";
			data.put("record-ttl",value);
//			
//			value.clear();
//			value.add("http://localhost/accesspointa11");
//			data.put("record-service-locator",value);
//		
//			
//			value.clear();
//			value.add("privatekey1");
//			data.put("record-privatekey",value);
//			
//			value.clear();
//			value.add("bwctl");
//			data.put("record-service-type",value);
//			
//			
//			
//			value.clear();
//			value.add("ESnet");
//			value.add("LHC");
//			data.put("record-service-domain",value);
//			
//			System.out.println(data.toString());
			try{
				StringEntity se=new StringEntity (data.toString());
			
				httppost.setEntity(se);
				System.out.println(se);
			}catch(UnsupportedEncodingException e){
				fail("Connection error: "+e.getMessage());
			}
	        
	        httppost.setHeader("Accept", "application/json");
	        httppost.setHeader("Content-type", "applicat");
			
			try{
				HttpResponse response = httpclient.execute(httppost);
				
				System.out.println(response.getStatusLine());
				
				

				HttpEntity entity = response.getEntity();
				System.out.println(entity.getContentType());
				String httpresponse = EntityUtils.toString(entity);
				System.out.println("Response Content: "+httpresponse);
				System.out.println(response.getStatusLine());
				
				
				StatusLine responseStatus = response.getStatusLine();
				assertEquals(400, responseStatus.getStatusCode());
	
				
			}catch(IOException e){
				fail("Connection error: "+e.getMessage());
			}
		}
		
		
		@Test
		public void testtestNoneContentType(){
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://localhost:8080/lookup/service/9a113c60-1f28-4e3a-9b7b-073955d84219");
			
			//BasicHttpParams params = new BasicHttpParams();
			
			JSONObject data=new JSONObject();
			String value="PT2H5M2S";
			data.put("record-ttl",value);
			
//			value.clear();
//			value.add("http://localhost/accesspointa11");
//			data.put("record-service-locator",value);
//		
//			
//			value.clear();
//			value.add("privatekey1");
//			data.put("record-privatekey",value);
//			
//			value.clear();
//			value.add("bwctl");
//			data.put("record-service-type",value);
//			
//			
//			
//			value.clear();
//			value.add("ESnet");
//			value.add("LHC");
//			data.put("record-service-domain",value);
//			
//			System.out.println(data.toString());
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
				
				

				HttpEntity entity = response.getEntity();
				System.out.println(entity.getContentType());
				String httpresponse = EntityUtils.toString(entity);
				System.out.println("Response Content: "+httpresponse);
				System.out.println(response.getStatusLine());
				
				
				StatusLine responseStatus = response.getStatusLine();
				assertEquals(200, responseStatus.getStatusCode());
	
				
			}catch(IOException e){
				fail("Connection error: "+e.getMessage());
			}
		}
		
		
		
		
	
	
		//leave the value of the "Content-type" as blank
		@Test
		public void testBlankContentTypeValue(){
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://localhost:8080/lookup/service/9a113c60-1f28-4e3a-9b7b-073955d84219");
			
			//BasicHttpParams params = new BasicHttpParams();
			
			JSONObject data=new JSONObject();
			String value="PT2H5M2S";
			data.put("record-ttl",value);
			
//			value.clear();
//			value.add("http://localhost/accesspointa11");
//			data.put("record-service-locator",value);
//		
//			
//			value.clear();
//			value.add("privatekey1");
//			data.put("record-privatekey",value);
//			
//			value.clear();
//			value.add("bwctl");
//			data.put("record-service-type",value);
//			
//			
//			
//			value.clear();
//			value.add("ESnet");
//			value.add("LHC");
//			data.put("record-service-domain",value);
//			
//			System.out.println(data.toString());
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
				
				

				HttpEntity entity = response.getEntity();
				System.out.println(entity.getContentType());
				String httpresponse = EntityUtils.toString(entity);
				System.out.println("Response Content: "+httpresponse);
				System.out.println(response.getStatusLine());
				
				
				StatusLine responseStatus = response.getStatusLine();
				assertEquals(200, responseStatus.getStatusCode());
	
				
			}catch(IOException e){
				fail("Connection error: "+e.getMessage());
			}
		}
	



}