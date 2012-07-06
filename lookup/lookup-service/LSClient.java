import java.io.*;
import java.net.*;






	
	
	


public class LSClient{
	public static getDataOnServer(){
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
	
	
	
	
	
	
	public static void main(String[] args){  
        
		
		
    }  


}
  