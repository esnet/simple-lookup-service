package net.es.lookup.database;

import net.es.lookup.common.*;
import net.es.lookup.protocol.json.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map;
import com.mongodb.*;
import java.net.UnknownHostException;

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
		Map services = (Map) registerRequest.getMap();
	
		BasicDBObject doc = new BasicDBObject();
		doc.putAll(services);
		
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
	
		BasicDBObject query = new BasicDBObject();
		String uri = deleteRequest.getURI();
		//TODO: add check to see if only one elem is returned
		query.put("uri", uri);
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
		Map serv =  queryRequest.getMap();
		BasicDBObject query = new BasicDBObject();
		BasicDBObject doc = new BasicDBObject();
		
		String op = queryRequest.getOperator();
		String mongoOp = "$and";
		
		if(!op.isEmpty()){
			if(op.equalsIgnoreCase("any")){
				mongoOp = "$or";
			}else if(op.equalsIgnoreCase("all")){
				mongoOp = "$and";
			}
		}
		
		doc.putAll(serv);
	
		query.put(mongoOp, doc);
		
		DBCursor cur = coll.find(query);
		ArrayList <Service> result = new ArrayList<Service>();
		while (cur.hasNext()){
			Service tmpserv = new Service();
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
	
	public Service getServiceByURI(String URI){
		int errorcode;
		String errormsg;
		
		BasicDBObject query = new BasicDBObject();
		query.put("uri", URI);
		DBCursor cur = coll.find(query);
		
		Service result = new Service();
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