import java.io.*;
import java.net.*;




public class LSClient{

	public static void getDataOnServer(){
		String urlStr = "http://localhost:8080/lookup/services";  
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



	public static void getService(){
		String urlStr = "http://localhost:8080/lookup/service/62cd211e-ccf8-40e6-b875-56fe18d635c3";  
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


	public static void getServiceKey(){
		String urlStr = "http://localhost:8080/lookup/service/1fc9ef7a-23ab-4600-afcb-aeecdb2a3fe2/record-service-domain";  
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

	public static void deleteService(){
		String urlStr = "http://localhost:8080/lookup/service/faa2dfab-2fe9-4e48-a444-cb87f27779d3";  
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
		String urlStr = "http://localhost:8080/lookup/service/16aa7325-a90f-4951-866e-01fcf241ca4d";  
		try{  
			URL url = new URL(urlStr);  

			HttpURLConnection connection= (HttpURLConnection)url.openConnection();  
			//connection.connect();  
			connection.setAllowUserInteraction(true);  
			connection.setRequestMethod("POST");  
			connection.setDoInput(true);
			connection.setDoOutput(true);  
			connection.setUseCaches(false);  
			connection.setRequestProperty("record-ttl","PT2H5M2S");

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

	public static void registerService(){
		String urlStr = "http://localhost:8080/lookup/services";  
		try{  
			URL url = new URL(urlStr);  

			HttpURLConnection connection= (HttpURLConnection)url.openConnection();  
			//connection.connect();  
			connection.setAllowUserInteraction(true);  
			connection.setRequestMethod("POST");  
			connection.setDoInput(true);
			connection.setDoOutput(true);  
			connection.setUseCaches(false);  
			OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            
            //write parameters
            writer.write(data);
//			connection.setRequestProperty("record-type","service");
//			connection.setRequestProperty("record-service-locator","http://localhost/accesspointa11");
//			connection.setRequestProperty("record-privatekey","privatekey1");
//			connection.setRequestProperty("record-service-type","bwctl");
//			connection.setRequestProperty("record-service-domain","ESnet");

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
	
	
	public static void queryService(){
		String urlStr = "http://localhost:8080/lookup/services?record-service-type=ping&record-service-domain=ESnet&record-service-locator=tcp://nash-pt1.es.net:4823&record-operator=any";  
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
//		getService();
//		getServiceKey();
//		deleteService();
//		queryService();
//		renewService();
		registerService();

	}  


}
