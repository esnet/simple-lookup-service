import java.io.*;
import java.net.*;




public class LSClient{
	
	public static void getDataOnServer(){
		String urlStr = "http://localhost:8080/lookup/services";  
        try{  
        URL url = new URL(urlStr);  
        BufferedReader in = new BufferedReader(
		        new InputStreamReader(url.openStream()));

		        String inputLine;
		        while ((inputLine = in.readLine()) != null)
		            System.out.println(inputLine);
		        in.close();
//        HttpURLConnection connection= (HttpURLConnection)url.openConnection();  
//        //connection.connect();  
//        connection.setAllowUserInteraction(true);  
//        connection.setRequestMethod("GET");  
//        connection.setDoInput(true);
//        connection.setDoOutput(true);  
//        connection.setUseCaches(false);  
//        //connection.setRequestProperty("Content-Length","application/x-www-form-urlencoded");  
////        DataInputStream dis = new DataInputStream(connection.getInputStream());
//          
//        System.out.println("*************CONNECTED*************");  
//        //connection.disconnect();  
        }catch(Exception e){  
            e.printStackTrace();  
        }  
	}
	
	
	
	public static void getService(){
		String urlStr = "http://localhost:8080/lookup/service/1fc9ef7a-23ab-4600-afcb-aeecdb2a3fe2";  
        try{  
        URL url = new URL(urlStr);  
       
        HttpURLConnection connection= (HttpURLConnection)url.openConnection();  
        //connection.connect();  
        connection.setAllowUserInteraction(true);  
        connection.setRequestMethod("GET");  
        connection.setDoInput(true);
        connection.setDoOutput(true);  
        connection.setUseCaches(false);  
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
	
	public static void deleteService(){
		String urlStr = "http://localhost:8080/lookup/service/1834bb1e-9de7-433a-a6ce-feddb1d33d00";  
        try{  
        URL url = new URL(urlStr);  
       
        HttpURLConnection connection= (HttpURLConnection)url.openConnection();  
        //connection.connect();  
        connection.setAllowUserInteraction(true);  
        connection.setRequestMethod("DELETE");  
        connection.setDoInput(true);
        connection.setDoOutput(true);  
        connection.setUseCaches(false);  
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
	
	
	
	
	public static void main(String[] args){  
//        getService();
//        getServiceKey();
        deleteService();
		
		
    }  


}
  