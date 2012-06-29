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




public class GetKeyServiceTest {

	
	//200 OK
	@Test
	public void testSimpleGetKeyService(){
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet("http://localhost:8080/lookup/service/81c8089b-bd61-4ff3-a966-d8a6affa2599/site_location");

		httpget.setHeader("Accept", "application/json");
		httpget.setHeader("Content-type", "application/json");

		try{
			HttpResponse response = httpclient.execute(httpget);

			System.out.println(response.getStatusLine());

			HttpEntity entity = response.getEntity();
			System.out.println(entity.getContentType());
			String httpresponse = EntityUtils.toString(entity);
			System.out.println("Response Content: "+httpresponse);

			StatusLine responseStatus = response.getStatusLine();
			assertEquals(200, responseStatus.getStatusCode());

		}catch(IOException e){
			fail("Connection error: "+e.getMessage());
		}
	}
	
	
	@Test
	public void testWrongUri(){
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet("http://localhost:8080/lookup/service/81c8089b-bd61-4ff3-a966-d8a6affa/site_lacation");

		httpget.setHeader("Accept", "application/json");
		httpget.setHeader("Content-type", "application/json");

		try{
			HttpResponse response = httpclient.execute(httpget);

			System.out.println(response.getStatusLine());

			HttpEntity entity = response.getEntity();
			System.out.println(entity.getContentType());
			String httpresponse = EntityUtils.toString(entity);
			System.out.println("Response Content: "+httpresponse);

			StatusLine responseStatus = response.getStatusLine();
			assertEquals(404, responseStatus.getStatusCode());

		}catch(IOException e){
			fail("Connection error: "+e.getMessage());
		}
	}

	@Test
	public void testBlankKey(){
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet("http://localhost:8080/lookup/service/81c8089b-bd61-4ff3-a966-d8a6affa2599/");

		httpget.setHeader("Accept", "application/json");
		httpget.setHeader("Content-type", "application/json");

		try{
			HttpResponse response = httpclient.execute(httpget);

			System.out.println(response.getStatusLine());

			HttpEntity entity = response.getEntity();
			System.out.println(entity.getContentType());
			String httpresponse = EntityUtils.toString(entity);
			System.out.println("Response Content: "+httpresponse);

			StatusLine responseStatus = response.getStatusLine();
			assertEquals(200, responseStatus.getStatusCode());

		}catch(IOException e){
			fail("Connection error: "+e.getMessage());
		}
	}
	
	
	@Test
	public void testWrongKey(){
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet("http://localhost:8080/lookup/service/81c8089b-bd61-4ff3-a966-d8a6affa2599/site_locator");

		httpget.setHeader("Accept", "application/json");
		httpget.setHeader("Content-type", "application/json");

		try{
			HttpResponse response = httpclient.execute(httpget);

			System.out.println(response.getStatusLine());

			HttpEntity entity = response.getEntity();
			System.out.println(entity.getContentType());
			String httpresponse = EntityUtils.toString(entity);
			System.out.println("Response Content: "+httpresponse);

			StatusLine responseStatus = response.getStatusLine();
			assertEquals(404, responseStatus.getStatusCode());

		}catch(IOException e){
			fail("Connection error: "+e.getMessage());
		}
	}
	
	
	@Test
	public void testWrongDirectory(){
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet("http://localhost:8080/lookup/81c8089b-bd61-4ff3-a966-d8a6affa2599/site_location");

		httpget.setHeader("Accept", "application/json");
		httpget.setHeader("Content-type", "application/json");

		try{
			HttpResponse response = httpclient.execute(httpget);

			System.out.println(response.getStatusLine());

			HttpEntity entity = response.getEntity();
			System.out.println(entity.getContentType());
			String httpresponse = EntityUtils.toString(entity);
			System.out.println("Response Content: "+httpresponse);

			StatusLine responseStatus = response.getStatusLine();
			assertEquals(404, responseStatus.getStatusCode());

		}catch(IOException e){
			fail("Connection error: "+e.getMessage());
		}
	}
	
	
	@Test
	public void testWrongContentType(){
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet("http://localhost:8080/lookup/service/81c8089b-bd61-4ff3-a966-d8a6affa2599/site_location");

		httpget.setHeader("Accept", "application/json");
		httpget.setHeader("Content-type", "application/");

		try{
			HttpResponse response = httpclient.execute(httpget);

			System.out.println(response.getStatusLine());

			HttpEntity entity = response.getEntity();
			System.out.println(entity.getContentType());
			String httpresponse = EntityUtils.toString(entity);
			System.out.println("Response Content: "+httpresponse);

			StatusLine responseStatus = response.getStatusLine();
			assertEquals(400, responseStatus.getStatusCode());

		}catch(IOException e){
			fail("Connection error: "+e.getMessage());
		}
	}
	
	
	
	@Test
	public void testNoneContentType(){
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet("http://localhost:8080/lookup/service/81c8089b-bd61-4ff3-a966-d8a6affa2599/site_location");

		httpget.setHeader("Accept", "application/json");
//		httpget.setHeader("Content-type", "");

		try{
			HttpResponse response = httpclient.execute(httpget);

			System.out.println(response.getStatusLine());

			HttpEntity entity = response.getEntity();
			System.out.println(entity.getContentType());
			String httpresponse = EntityUtils.toString(entity);
			System.out.println("Response Content: "+httpresponse);

			StatusLine responseStatus = response.getStatusLine();
			assertEquals(200, responseStatus.getStatusCode());

		}catch(IOException e){
			fail("Connection error: "+e.getMessage());
		}
	}
	

	
}