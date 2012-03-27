package net.es.lookup.database;

import net.es.lookup.common.*;
import net.es.lookup.protocol.json.*;

import java.security.Provider.Service;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.net.*;
import com.mongodb.*;

public class ServiceDAOMongoDb implements LookupService{
	private String dburl="localhost";
	private int dbport=27017;
	private String dbname="LookupService";
	private String collname="services";

	private Mongo mongo;
	private DB db;
	private DBCollection coll;
	// retrieves default - mongodb running on localhost and default port - 27017 and dbname- "lookupservice", collection name - "services" 
	//creates a new one if it cannot find one 
	ServiceDAOMongoDb() throws UnknownHostException{
		init();
	}
	
	//uses default url and port - mongodb running on localhost and default port - 27017
	//creates a new one if it cannot find one
	ServiceDAOMongoDb(String dbname, String collname) throws UnknownHostException{
		this.dbname = dbname;
		this.collname = collname;
		init();
	}
	
	//retrieves the db and collection(table); creates a new one if it cannot find one
	ServiceDAOMongoDb (String dburl, int dbport, String dbname, String collname)  throws UnknownHostException{
		this.dburl = dburl;
		this.dbport = dbport;
		this.dbname = dbname;
		this.collname = collname;
		init();
	}
	
	private void init() throws UnknownHostException{
		mongo = new Mongo(dburl,dbport);
		db = mongo.getDB(dbname);
		coll = db.getCollection(collname);
	}
	
	//should use json specific register request and response.
	public RegisterResponse publishService(RegisterRequest registerRequest){
		int errorcode;
		String errormsg;
		net.es.lookup.common.Service services = (net.es.lookup.common.Service) registerRequest.getContent();
		ArrayList keyvalues = (ArrayList)services.getKeyValues();
		BasicDBObject doc = new BasicDBObject();
		
		for(int i=0; i< keyvalues.size(); i++){
			KeyValue tmp = (KeyValue)keyvalues.get(i);		
			String tmpKey = tmp.getKey();
			doc.put(tmpKey, tmp.getValue());					
		}
		
		WriteResult wrt = coll.insert(doc);
		CommandResult cmdres = wrt.getLastError();
		if(cmdres.ok()){
			errorcode = 200;
			errormsg = "SUCCESS";
		}else{
			errorcode = 500;
			errormsg = cmdres.getErrorMessage();
		}
		
		RegisterResponse response = new JSONRegisterResponse();
		response.setError(errorcode);
		response.setErrorMessage(errormsg);
		return response;
	}
	
	
	public DeleteResponse deleteService(DeleteRequest deleteRequest){
		int errorcode;
		String errormsg;
		net.es.lookup.common.Service serv = (net.es.lookup.common.Service) deleteRequest.getContent();
		BasicDBObject query = new BasicDBObject();
		ArrayList uri = (ArrayList)serv.getKeyValues("uri");
		//TODO: add check to see if only one elem is returned
		query.put("uri", uri.get(0));
		WriteResult wrt = coll.remove(query);
		
		CommandResult cmdres = wrt.getLastError();
		
		if(cmdres.ok()){
			errorcode = 200;
			errormsg = "SUCCESS";
		}else{
			errorcode = 500;
			errormsg = cmdres.getErrorMessage();
		}
		
		DeleteResponse response = new JSONDeleteResponse();
		response.setError(errorcode);
		response.setErrorMessage(errormsg);
		return response;
	}
	
	public RenewResponse renewService(RenewRequest renewRequest){
		
		int errorcode;
		String errormsg;
		
		String uri = renewRequest.getURI();
        int ttl = renewRequest.getTTL();
		BasicDBObject query = new BasicDBObject();
		//TODO: add check to see if only one elem is returned
		query.put("uri", uri);
		
		
		DBCursor cur = coll.find(query);
		
		if (cur.length() == 1){
			DBObject tmp = cur.next();
			tmp.put("ttl", ttl);
			WriteResult wrt = coll.save(tmp);
			
			CommandResult cmdres = wrt.getLastError();
			
			if(cmdres.ok()){
				errorcode = 200;
				errormsg = "SUCCESS";
			}else{
				errorcode = 500;
				errormsg = cmdres.getErrorMessage();
			}
			
		}else{
			errorcode = 500;
			errormsg = "Database corrupted";
		}
	
		RenewResponse response = new JSONRenewResponse();
		response.setError(errorcode);
		response.setErrorMessage(errormsg);
		return response;
	}
	
	public QueryResponse query(QueryRequest queryRequest){
		net.es.lookup.common.Service serv = (net.es.lookup.common.Service) queryRequest.getContent();
		BasicDBObject query = new BasicDBObject();
		ArrayList<net.es.lookup.common.KeyValue> keyvalues = (ArrayList<net.es.lookup.common.KeyValue>)serv.getKeyValues();
		
		for (int i=0; i<keyvalues.size();i++){
			KeyValue kv = keyvalues.get(i);
			query.put(kv.getKey(), kv.getValue());
		}
		
		DBCursor cur = coll.find(query);
		ArrayList <net.es.lookup.common.Service> result = new ArrayList<net.es.lookup.common.Service>();
		while (cur.hasNext()){
			net.es.lookup.common.Service tmpserv = new net.es.lookup.common.Service();
			DBObject tmp = cur.next();
			Set<String> keys = tmp.keySet();
			if (!keys.isEmpty()){
				Iterator<String> it = keys.iterator();
				while(it.hasNext()){	
					String tmpKey = it.next();
					KeyValue kv = new KeyValue(tmpKey,tmp.get(tmpKey));
					tmpserv.addKeyValue(kv);
				}
			}
			result.add(tmpserv);
		}
		
		QueryResponse response = new JSONQueryResponse();
		response.setResult(result);
		return response;
	}
	
	public net.es.lookup.common.Service getServiceByURI(String URI){
		int errorcode;
		String errormsg;
		
		BasicDBObject query = new BasicDBObject();
		query.put("uri", URI);
		DBCursor cur = coll.find(query);
		
		net.es.lookup.common.Service result = new net.es.lookup.common.Service();
		if (cur.length() == 1){
			DBObject tmp = cur.next();
			Set<String> keys = tmp.keySet();
			if (!keys.isEmpty()){
				Iterator<String> it = keys.iterator();
				while(it.hasNext()){
					
					String tmpKey = it.next();
					KeyValue kv = new KeyValue(tmpKey,tmp.get(tmpKey));
					result.addKeyValue(kv);
				}
			}
		}
			
		return result;
	}
	
}