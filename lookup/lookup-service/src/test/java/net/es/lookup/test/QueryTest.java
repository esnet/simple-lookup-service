//package net.es.lookup.test;
//import net.es.lookup.common.exception.api.BadRequestException;
//import java.io.IOException;
//import java.io.UnsupportedEncodingException;
//import java.util.ArrayList;
//
//import org.junit.Test;
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.fail
//;
//import org.apache.http.client.HttpClient;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.params.BasicHttpParams;
//import org.apache.http.HttpResponse;
//import org.apache.http.StatusLine;
//
//import net.sf.json.JSONObject;
//import net.sf.json.JSONArray;
//import org.apache.http.util.EntityUtils;
//import org.apache.http.HttpEntity;
//
//
//
//
//public class QueryTest {
//
//		@Test
//		public void testSingleValue(){
//			HttpClient httpclient = new DefaultHttpClient();
//			HttpGet httpget = new HttpGet("http://localhost:8080/lookup/services?record-service-locator=tcp://nash-pt1.es.net:4823");
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
//		
//		
//		@Test
//		public void testMultiValue(){
//			HttpClient httpclient = new DefaultHttpClient();
//			HttpGet httpget = new HttpGet("http://localhost:8080/lookup/services?record-service-type=ping&record-service-domain=ESnet&record-service-locator=tcp://nash-pt1.es.net:4823");
//			
////			BasicHttpParams params = new BasicHttpParams();	
////			params.setParameter("record-service-locator","wash-owamp.es.net");		
////			params.setParameter("record-service-type","ping");
////			System.out.println("PARAMS:"+params.getParameter("record-service-locator"));
//	        
//	        httpget.setHeader("Accept", "application/json");
//	        httpget.setHeader("Content-type", "application/json");
//	        
////	        System.out.println("11111111"+httpget.getParams().toString());
//
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
//
//		
//		
//		@Test
//		public void testOperator(){
//			HttpClient httpclient = new DefaultHttpClient();
//			HttpGet httpget = new HttpGet("http://localhost:8080/lookup/services" +
//					"?record-service-type=ping&record-service-domain=ESnet&record-service-locator=tcp://nash-pt1.es.net:4823&record-operator=any");
//			
//			//BasicHttpParams params = new BasicHttpParams();
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
//		
//		//In this case, the key operator does not work. 
//		//When the record-service-domain-operator=all,the lookup service treats it as "any"
//		@Test
//		public void testKeyOperator(){
//			HttpClient httpclient = new DefaultHttpClient();
//			HttpGet httpget = new HttpGet("http://localhost:8080/lookup/services" +
//					"?record-service-type=ping&record-service-domain=ESnet,LHC&record-service-domain-operator=all&record-service-locator=tcp://nash-pt1.es.net:4823&record-operator=any");
//			
//			//BasicHttpParams params = new BasicHttpParams();
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
//		
//		
//		@Test
//		public void testSingleStar(){
//			HttpClient httpclient = new DefaultHttpClient();
//			HttpGet httpget = new HttpGet("http://localhost:8080/lookup/services" +
//					"?record-service-type=ping&record-service-domain=ES*,LHC&record-service-domain-operator=all&record-service-locator=tcp://nash-pt1.es.net:4823&record-operator=any");
//			
//			//BasicHttpParams params = new BasicHttpParams();
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
//
//		//In this test case, the record which is not included in the specified in certain field are also responsed
//		//eg: record-service-domain=ES*,L*, but the response result also incoude "Ecenter";
//		@Test
//		public void testMultiStar(){
//			HttpClient httpclient = new DefaultHttpClient();
//			HttpGet httpget = new HttpGet("http://localhost:8080/lookup/services" +
//					"?record-service-type=ping&record-service-domain=ES*,L*&record-service-domain-operator=all&record-service-locator=tcp://nash-pt1.es.net:4823&record-operator=any");
//			
//			//BasicHttpParams params = new BasicHttpParams();
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
//		
//		
//		
//		@Test
//		public void testWrongKey(){
//			HttpClient httpclient = new DefaultHttpClient();
//			HttpGet httpget = new HttpGet("http://localhost:8080/lookup/services" +
//					"?record-service-type=ping&record-service-domain=ESnet&record-operator=all");
//			
//			//BasicHttpParams params = new BasicHttpParams();
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
//		
//		
//		
//		//typo the value of "Accept", it will return 406 Not Acceptable
//		@Test
//		public void testWrongContentType(){
//			HttpClient httpclient = new DefaultHttpClient();
//			HttpGet httpget = new HttpGet("http://localhost:8080/lookup/services" +
//					"?record-service-type=ping&record-service-domain=ESnet&record-operator=all");
//			
//			//BasicHttpParams params = new BasicHttpParams();
//		    
//	        httpget.setHeader("Accept", "application/json");
//	        httpget.setHeader("Content-type", "");
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
//				assertEquals(400, responseStatus.getStatusCode());
//				
//			}catch(IOException e){
//				fail("Connection error: "+e.getMessage());
//			}
//		}
//		
//		
//		//404 Not Found
//		@Test
//		public void testWrongDirectory(){
//			HttpClient httpclient = new DefaultHttpClient();
//			HttpGet httpget = new HttpGet("http://localhost:8080/lookup" +
//					"?record-service-type=ping&record-service-domain=ESnet&record-operator=all");
//			
//			//BasicHttpParams params = new BasicHttpParams();
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
//				assertEquals(404, responseStatus.getStatusCode());
//				
//			}catch(IOException e){
//				fail("Connection error: "+e.getMessage());
//			}
//		}
//
//		
//		//500 Internal Server Error
//		@Test
//		public void testTurnOffDB(){
//			HttpClient httpclient = new DefaultHttpClient();
//			HttpGet httpget = new HttpGet("http://localhost:8080/lookup/services" +
//					"?record-service-type=ping&record-service-domain=ESnet&record-operator=all");
//			
//			//BasicHttpParams params = new BasicHttpParams();
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
//				assertEquals(500, responseStatus.getStatusCode());
//				
//			}catch(IOException e){
//				fail("Connection error: "+e.getMessage());
//			}
//		}
//
//}