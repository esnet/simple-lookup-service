//package net.es.lookup.clients;

import java.io.*;
import java.net.*;
import java.util.*;


//import net.es.lookup.clients.ServiceKeywords;



public class LSClientTest{



	public static void main(String[] args){  
		String urls="http://localhost:8080/lookup/services";
		String url="http://localhost:8080/lookup/service/";

		String recorduri= "19afe8f8-5efc-4dff-82e8-11b451004ca2";
		String key= "record-service-domain";

		HashMap<String, Object> renewmap = new HashMap();
		renewmap.put("record-ttl","PT2H5M2S");

		HashMap<String, Object> regmap = new HashMap();
		regmap.put("record-type","service");
		regmap.put("record-service-locator","http://localhost/accesspointadfsddsdfrddgt");
		regmap.put("record-service-type","owamp");
		regmap.put("record-service-domain","es.net");

		HashMap<String, Object> querymap = new HashMap();
		querymap.put("record-type","service");
//		querymap.put("record-service-locator","tcp://nash-pt1.es.net:4823");
		querymap.put("record-service-type","ping");
		querymap.put("record-service-domain","es.net,L*");
		querymap.put("record-service-domain-operator","any");


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

