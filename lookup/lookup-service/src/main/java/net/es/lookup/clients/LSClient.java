//package net.es.lookup.client;

import java.io.*;
import java.net.*;
import java.util.*;
//import net.es.lookup.common.ReservedKeywords;



public class LSClient{
	private String urlStr = null;  
	private String urlStrs = null;
	//	ReservedKeywords keyword = new ReservedKeywords();

	public LSClient(String url,String urls){
		this.urlStr=url;
		this.urlStrs=urls;
	}


	public HttpURLConnection getConnection(URL url){
		HttpURLConnection connection=null;
		try{  
			connection= (HttpURLConnection)url.openConnection();   
			connection.setAllowUserInteraction(true);  
			connection.setRequestMethod("GET");  
			connection.setDoInput(true);
			connection.setDoOutput(true);  
			connection.setUseCaches(false);  
			System.out.println("Msg: "+ connection.getResponseMessage());
			System.out.println("Error code: "+ connection.getResponseCode());

		}catch(Exception e){  
			e.printStackTrace();  
		}  
		return connection;
	}

	public HttpURLConnection postConnection(URL url){
		HttpURLConnection connection=null;
		try{  
			connection= (HttpURLConnection)url.openConnection();   
			connection.setAllowUserInteraction(true);  
			connection.setRequestMethod("POST");  
			connection.setDoInput(true);
			connection.setDoOutput(true);  
			connection.setUseCaches(false);  
			connection.setRequestProperty("Content-type",
					"application/json");
		}catch(Exception e){  
			e.printStackTrace();  
		}  
		return connection;
	}

	public HttpURLConnection deleteConnection(URL url){
		HttpURLConnection connection=null;
		try{  
			connection= (HttpURLConnection)url.openConnection();   
			connection.setAllowUserInteraction(true);  
			connection.setRequestMethod("DELETE");  
			connection.setDoInput(true);
			connection.setDoOutput(true);  
			connection.setUseCaches(false);  
			System.out.println("Msg: "+ connection.getResponseMessage());
			System.out.println("Error code: "+ connection.getResponseCode());
		}catch(Exception e){  
			e.printStackTrace();  
		}  
		return connection;
	}

	public  String getDataOnServer(){
		String returnString="";
		try{  

			URL url = new URL(this.urlStrs);  
			HttpURLConnection connection=getConnection(url);

			DataInputStream in = new DataInputStream (connection.getInputStream ()); 
			String inputLine;
			while (null!=((inputLine = in.readLine()))){
				returnString +=inputLine;
			}
			in.close();

		}catch(Exception e){  
			e.printStackTrace();  
		}  
		System.out.println("============"+returnString);
		return returnString;
	}



	public String getService(String recorduri){
		String returnString="";
		String urlStr=null;
		if (recorduri!=null)
			urlStr = this.urlStr+recorduri;  

		try{  
			URL url = new URL(urlStr);  
			HttpURLConnection connection=getConnection(url);
			DataInputStream in = new DataInputStream (connection.getInputStream ()); 
			String inputLine;
			while (null!=((inputLine = in.readLine()))){
				returnString +=inputLine;
			}
			in.close();
		}catch(Exception e){  
			e.printStackTrace();  
		}  
		System.out.println("============"+returnString);
		return returnString;
	}


	public String getServiceKey(String recorduri, String key){
		String urlStr=null;
		String returnString="";
		if(recorduri!=null){
			if (key!=null){
				urlStr = this.urlStr+recorduri+"/"+key;  
			}
			else
				urlStr=this.urlStr+recorduri;
		} 
		try{  
			URL url = new URL(urlStr);  
			HttpURLConnection connection=getConnection(url);
			DataInputStream in = new DataInputStream (connection.getInputStream ()); 
			String inputLine;
			while (null!=((inputLine = in.readLine()))){
				returnString +=inputLine;
			}
			in.close();
		}catch(Exception e){  
			e.printStackTrace();  
		}  
		System.out.println("============"+returnString);
		return returnString;
	}

	public String deleteService(String recorduri){
		String urlStr=null;
		String returnString="";
		if (recorduri!=null)
			urlStr = this.urlStr+recorduri;  
		try{  
			URL url = new URL(urlStr);  
			HttpURLConnection connection=deleteConnection(url);
			DataInputStream in = new DataInputStream (connection.getInputStream ()); 

			String inputLine;
			while (null!=((inputLine = in.readLine()))){
				returnString +=inputLine;
			}
			in.close();
		}catch(Exception e){  
			e.printStackTrace();  
		}  
		System.out.println("============"+returnString);
		return returnString;
	}

	public void renew(String recorduri,HashMap<String,Object> map){
		String urlStr=null;
		String params="";
		if(recorduri!=null&&!map.isEmpty()){
			if(map.containsKey("record-ttl")&&map.get("record-ttl")!=null&&map.get("record-ttl")!=""){
				urlStr = this.urlStr+recorduri;
			}
			for(String eachKey:map.keySet()){
				params="{\""+eachKey+"\""+":"+"\""+map.get(eachKey)+"\"}";
			}
		}
		renewService(urlStr,recorduri,params);
	}


	public String renewService(String urlString, String recorduri,String params){
		String returnString="";
		System.out.println("urlstr"+urlString);
		System.out.println("params"+params);
		try{  
			URL url = new URL(urlString);  
			HttpURLConnection connection=postConnection(url);
			DataOutputStream out=new DataOutputStream (connection.getOutputStream ()); 
			out.writeBytes(params);
			out.close();
			System.out.println("Msg: "+ connection.getResponseMessage());
			System.out.println("Error code: "+ connection.getResponseCode());
			DataInputStream in = new DataInputStream (connection.getInputStream ()); 

			String inputLine;
			while (null!=((inputLine = in.readLine()))){
				returnString +=inputLine;
			}
			in.close();
		}catch(Exception e){  
			e.printStackTrace();  
		}  
		System.out.println("============"+returnString);
		return returnString;
	}



	public void register(HashMap<String,Object> map){
		String params="";
		if(!map.isEmpty()){
			if(map.containsKey("record-type")&&map.get("record-type")!=null&&
					map.containsKey("record-service-locator")&&map.get("record-service-locator")!=null&&
					map.containsKey("record-service-type")&&map.get("record-service-type")!=null){
				for(String eachKey:map.keySet()){
					params+="\""+eachKey+"\""+":"+"[\""+map.get(eachKey)+"\"],";				
				}
				params=params.substring(0,params.length()-1);
				params="{"+params+"}";
			}
		}
		registerService(params);
	}


	public String registerService(String params){
		String returnString="";
		System.out.println("params"+params);

		try{  	
			URL url = new URL(this.urlStrs); 
			HttpURLConnection connection=postConnection(url);
			DataOutputStream out=new DataOutputStream (connection.getOutputStream ()); 
			out.writeBytes(params);
			out.close();
			System.out.println("Msg: "+ connection.getResponseMessage());
			System.out.println("Error code: "+ connection.getResponseCode());
			DataInputStream in = new DataInputStream (connection.getInputStream ()); 

			String inputLine;
			while (null!=((inputLine = in.readLine()))){
				returnString +=inputLine;
			}
			in.close();
		}catch(Exception e){  
			e.printStackTrace();  
		}  
		System.out.println("============"+returnString);
		return returnString;
	}

	public void query(HashMap<String,Object> map){
		String params = "";
		if(!map.isEmpty()){
			for(String eachKey:map.keySet()){
				if(eachKey.contains("operator")&&map.get(eachKey)=="all"||map.get(eachKey)=="any"){
					params+= eachKey+"="+map.get(eachKey)+"&";	
				}else{
					params+= eachKey+"="+map.get(eachKey)+"&";
				}
			}
			params=params.substring(0,params.length()-1);
		}
		queryService(params);
	}

	public String queryService(String message){
		String returnString="";
		System.out.println("MESSAGE:"+message);
		urlStr=this.urlStrs+"?"+message;
		System.out.println("url:"+urlStr);
		try{  
			URL url = new URL(urlStr);  
			HttpURLConnection connection=getConnection(url);
			DataInputStream in = new DataInputStream (connection.getInputStream ()); 

			String inputLine;
			while (null!=((inputLine = in.readLine()))){
				returnString +=inputLine;
			}
			in.close();

		}catch(Exception e){  
			e.printStackTrace();  
		}  
		System.out.println("============"+returnString);
		return returnString;
	}


	public static void main(String[] args){  
		String urls="http://localhost:8080/lookup/services";
		String url="http://localhost:8080/lookup/service/";

		String recorduri= "19afe8f8-5efc-4dff-82e8-11b451004ca2";
		String key= "record-service-domain";

		HashMap<String, Object> renewmap = new HashMap();
		renewmap.put("record-ttl","PT2H5M2S");

		HashMap<String, Object> regmap = new HashMap();
		regmap.put("record-type","service");
		regmap.put("record-service-locator","http://localhost/accesspointadfsdddgt");
		regmap.put("record-service-type","owamp");
		regmap.put("record-service-domain","es.net");

		HashMap<String, Object> querymap = new HashMap();
		querymap.put("record-type","service");
		querymap.put("record-service-locator","tcp://nash-pt1.es.net:4823");
		querymap.put("record-service-type","owamp");
		querymap.put("record-service-domain","es.net,L*");
		querymap.put("record-service-domain-operator","all");


		String renewparams = "{\"record-ttl\":\"PT2H5M2S\",\"client-uuid\":[\"myuuid\"]}";
		String message = "record-service-type=ping&record-service-domain=ESnet,L*&record-service-locator=tcp://nash-pt1.es.net:4823&record-operator=any";
		String regparams="{\"record-type\":[\"service\"],\"record-service-locator\":[\"http://localhost/accesspointasjdfoddddi\"],\"record-privatekey\":[\"myuuid\"],\"record-service-type\":[\"owamp\"],\"record-service-domain\":[\"es.net\"]}";

		LSClient client = new LSClient(url,urls);
		//		client.getDataOnServer();
		//		client.getService(recorduri);
		//		client.getServiceKey(recorduri,key);
		//		client.deleteService(recorduri);
		//		client.renew(recorduri,renewmap);
		//		client.register(regmap);
		client.query(querymap);
	}  


}
