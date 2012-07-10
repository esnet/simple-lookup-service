import java.io.*;
import java.net.*;
import java.util.*;
import org.apache.http.client.HttpClient;



public class LSClient{
//	private String urlStr = "http://localhost:8080/lookup/services";  
//	private String urLStrs = "";
	
//	public LSClient(String url,String urls){
//		this.urlStr=url;
//		this.urlStrs=urls;
//	}

	public static void getDataOnServer(){
		final String urlStr = "http://localhost:8080/lookup/services";  
		try{  
			URL url = new URL(urlStr);  
			HttpURLConnection connection= (HttpURLConnection)url.openConnection();  
			//connection.connect();  
			connection.setAllowUserInteraction(true);  
			connection.setRequestMethod("GET");  
			connection.setDoInput(true);
			connection.setDoOutput(true);  
			connection.setUseCaches(false);  
			System.out.println("Msg: "+ connection.getResponseMessage());
			System.out.println("Error code: "+ connection.getResponseCode());
			//connection.setRequestProperty("Content-Length","application/x-www-form-urlencoded");  
			//      DataInputStream dis = new DataInputStream(connection.getInputStream());

			//      System.out.println("*************CONNECTED*************");  
			//connection.disconnect(); 
			BufferedReader in = new BufferedReader(
					new InputStreamReader(connection.getInputStream()));

			String inputLine;
			while ((inputLine = in.readLine()) != null)
				System.out.println(inputLine);
			in.close();

		}catch(Exception e){  
			e.printStackTrace();  
		}  
	}



	public static void getService(String recorduri){
		//		String recorduri="62cd211e-ccf8-40e6-b875-56fe18d635c3";
		final String parturl = "http://localhost:8080/lookup/service/";
		String urlStr=null;
		if (recorduri!=null)
			urlStr = parturl+recorduri;  

		try{  
			URL url = new URL(urlStr);  

			HttpURLConnection connection= (HttpURLConnection)url.openConnection();  
			//connection.connect();  
			connection.setAllowUserInteraction(true);  
			connection.setRequestMethod("GET");  
			connection.setDoInput(true);
			connection.setDoOutput(true);  
			connection.setUseCaches(false);
			System.out.println("Msg: "+ connection.getResponseMessage());
			System.out.println("Error code: "+ connection.getResponseCode());
			//connection.setRequestProperty("Content-Length","application/x-www-form-urlencoded");  
			//        DataInputStream dis = new DataInputStream(connection.getInputStream());

			BufferedReader in = new BufferedReader(
					new InputStreamReader(connection.getInputStream()));

			String inputLine;
			while ((inputLine = in.readLine()) != null)
				System.out.println(inputLine);
			in.close();

		}catch(Exception e){  
			e.printStackTrace();  
		}  
	}


	public static void getServiceKey(String recorduri, String key){
		String urlStr=null;
		final String parturl = "http://localhost:8080/lookup/service/";
		if(recorduri!=null){
			if (key!=null){
				if(key.equals("record-service-domain")||
						key.equals("record-service-locator")||
						key.equals("record-service-type")||
						key.equals("record-type"))
					urlStr = parturl+recorduri+"/"+key;  
			}
			else
				urlStr=parturl+recorduri;
		}

//		String urlStr = "http://localhost:8080/lookup/service/1fc9ef7a-23ab-4600-afcb-aeecdb2a3fe2/record-service-domain";  
		try{  
			URL url = new URL(urlStr);  

			HttpURLConnection connection= (HttpURLConnection)url.openConnection();  
			//connection.connect();  
			connection.setAllowUserInteraction(true);  
			connection.setRequestMethod("GET");  
			connection.setDoInput(true);
			connection.setDoOutput(true);  
			connection.setUseCaches(false);  
			//			System.out.println("Msg: "+ connection.getResponseBody());
			System.out.println("Msg: "+ connection.getResponseMessage());
			System.out.println("Error code: "+ connection.getResponseCode());

			BufferedReader in = new BufferedReader(
					new InputStreamReader(connection.getInputStream()));

			String inputLine;
			while ((inputLine = in.readLine()) != null)
				System.out.println(inputLine);
			in.close();

		}catch(Exception e){  
			e.printStackTrace();  
		}  
	}

	public static void deleteService(String recorduri){
//		String urlStr = "http://localhost:8080/lookup/service/faa2dfab-2fe9-4e48-a444-cb87f27779d3";  
		
		final String parturl = "http://localhost:8080/lookup/service/";
		String urlStr=null;
		if (recorduri!=null)
			urlStr = parturl+recorduri;  
		try{  
			URL url = new URL(urlStr);  

			HttpURLConnection connection= (HttpURLConnection)url.openConnection();  
			//connection.connect();  
			connection.setAllowUserInteraction(true);  
			connection.setRequestMethod("DELETE");  
			connection.setDoInput(true);
			connection.setDoOutput(true);  
			connection.setUseCaches(false);  

			System.out.println("Msg: "+ connection.getResponseMessage());
			System.out.println("Error code: "+ connection.getResponseCode());
			BufferedReader in = new BufferedReader(
					new InputStreamReader(connection.getInputStream()));

			String inputLine;
			while ((inputLine = in.readLine()) != null)
				System.out.println(inputLine);
			in.close();

		}catch(Exception e){  
			e.printStackTrace();  
		}  
	}

	public static void renewService(){
//		Hashtable h = new Hashtable();
//        h.put("record-ttl", "PT2H5M2S");
		String urlStr = "http://localhost:8080/lookup/service/78986dd0-f41b-4568-bfe7-14340ac736bf";  
		
		try{  
			String params = "{\"record-ttl\":\"PT2H5M2S\",\"client-uuid\":[\"myuuid\"]}";
			URL url = new URL(urlStr);  

			HttpURLConnection connection= (HttpURLConnection)url.openConnection();  
			//connection.connect();  
			connection.setAllowUserInteraction(true);  
			connection.setRequestMethod("POST");  
			connection.setDoInput(true);
			connection.setDoOutput(true);  
			connection.setUseCaches(false);  
		
			
			DataOutputStream out=new DataOutputStream (connection.getOutputStream ()); 

			out.writeBytes(params);
			out.close();
			
			System.out.println("Msg: "+ connection.getResponseMessage());
//			System.out.println("urlstr: "+ urlStr);
			System.out.println("Error code: "+ connection.getResponseCode());
			BufferedReader in = new BufferedReader(
					new InputStreamReader(connection.getInputStream()));

			String inputLine;
			while ((inputLine = in.readLine()) != null)
				System.out.println(inputLine);
			in.close();

		}catch(Exception e){  
			e.printStackTrace();  
		}  
	}

		public static void registerService(){
		
	         
			String urlStr = "http://localhost:8080/lookup/services";  
			try{  
				String params = "{\"record-type\":[\"service\"]}";
				System.out.println("param:"+params);
//				String params = null;
				URL url = new URL(urlStr); 
				HttpURLConnection connection= (HttpURLConnection)url.openConnection();  
				//connection.connect();  
				connection.setAllowUserInteraction(true);  
				connection.setRequestMethod("POST");  
				connection.setDoInput(true);
				connection.setDoOutput(true);  
				connection.setUseCaches(false);  
			
				
				DataOutputStream out=new DataOutputStream (connection.getOutputStream ()); 

				out.writeChars(params);
				out.close();
				
				System.out.println("Msg: "+ connection.getResponseMessage());
				System.out.println("Error code: "+ connection.getResponseCode());
				BufferedReader in = new BufferedReader(
						new InputStreamReader(connection.getInputStream()));
	
				String inputLine;
				while ((inputLine = in.readLine()) != null)
					System.out.println(inputLine);
				in.close();
	
			}catch(Exception e){  
				e.printStackTrace();  
			}  
		}

	

	public static void queryService(String message){
//		String urlStr = "http://localhost:8080/lookup/services?record-service-type=ping&record-service-type-operator=any&record-service-domain=ESnet&record-service-locator=tcp://nash-pt1.es.net:4823&record-operator=any";  
		final String parturl="http://localhost:8080/lookup/services";
		String[] words;
		String []eachkey=new String[2];
		StringBuilder mes = new StringBuilder();
		String urlStr=null;
//		String [] operators={"record-operator","record-service-type-operator","record-service-domain-operator","record-service-locator-operator","record-privatekey-operator"};
//		String [] keys={"record-type","record-service-type","record-service-domain","record-service-locator","record-privatekey"};

		if(message!=null){
			words=message.trim().split("&");
//			for(int i =0;i<words.length;i++)
//			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!worsd"+words[i]);
			
			
			for(int i =0;i<words.length;i++){
				eachkey=words[i].trim().split("=");
				System.out.println("eachkey:"+eachkey[0]);
				if(eachkey[0].equals("record-type")||
						eachkey[0].equals("record-service-type")||
						eachkey[0].equals("record-service-domain")||
						eachkey[0].equals("record-service-locator")||
						eachkey[0].equals("record-privatekey")||
						eachkey[0].equals("record-operator")||
						eachkey[0].equals("record-service-type-operator")||
						eachkey[0].equals("record-service-domain-operator")||
						eachkey[0].equals("record-service-locator-operator")||
						eachkey[0].equals("record-privatekey-operator")&&
						eachkey[1]!=null
						){
					if(eachkey[0].contains("operator")&&eachkey[1]=="any"||eachkey[1]=="all"){
						mes.append(eachkey[0]+"="+eachkey[1]+"&");
					}
					mes.append(eachkey[0]+"="+eachkey[1]+"&");
				}
			}
			System.out.println("MESSAGE:"+mes);
			urlStr=parturl+"?"+mes;
			System.out.println("url:"+urlStr);
		}
		try{  
			URL url = new URL(urlStr);  

			HttpURLConnection connection= (HttpURLConnection)url.openConnection();  
			//connection.connect();  
			connection.setAllowUserInteraction(true);  
			connection.setRequestMethod("GET");  
			connection.setDoInput(true);
			connection.setDoOutput(true);  
			connection.setUseCaches(false);  

			System.out.println("Msg: "+ connection.getResponseMessage());
			System.out.println("Error code: "+ connection.getResponseCode());
			BufferedReader in = new BufferedReader(
					new InputStreamReader(connection.getInputStream()));

			String inputLine;
			while ((inputLine = in.readLine()) != null)
				System.out.println(inputLine);
			in.close();

		}catch(Exception e){  
			e.printStackTrace();  
		}  
	}


	public static void main(String[] args){  
		//		getDataOnServer();
//		getService("62cd211e-ccf8-40e6-b875-56fe18d635c3");
//				getServiceKey("62cd211e-ccf8-40e6-b875-56fe18d635c3","record-service-locator");
//				deleteService("62cd211e-ccf8-40e6-b875-56fe18d635c3");
//				queryService("record-service-type=ping&record-service-domain=ESnet,L*&record-service-locator=tcp://nash-pt1.es.net:4823&record-operator=all");
//				renewService();
				registerService();

	}  


}
