package net.es.lookup.database;

import net.es.lookup.common.DuplicateKeyException;
import net.es.lookup.common.Service;
import net.es.lookup.common.Message;

import java.util.*;

import com.mongodb.*;
import java.net.UnknownHostException;

public class ServiceDAOMongoDb {
	private String dburl="localhost";
	private int dbport=27017;
	private String dbname="LookupService";
	private String collname="services";

	private Mongo mongo;
	private DB db;
	private DBCollection coll;
    private static ServiceDAOMongoDb instance = null;

    public static ServiceDAOMongoDb getInstance() {
        return ServiceDAOMongoDb.instance;
    }

	// retrieves default - mongodb running on localhost and default port - 27017 and dbname- "lookupservice", collection name - "services" 
	//creates a new one if it cannot find one 
	public ServiceDAOMongoDb() throws UnknownHostException{
		init();
	}
	
	//uses default url and port - mongodb running on localhost and default port - 27017
	//creates a new one if it cannot find one
	public ServiceDAOMongoDb(String dbname, String collname) throws UnknownHostException{
		this.dbname = dbname;
		this.collname = collname;
		init();
	}
	
	//retrieves the db and collection(table); creates a new one if it cannot find one
	public ServiceDAOMongoDb (String dburl, int dbport, String dbname, String collname)  throws UnknownHostException{
		this.dburl = dburl;
		this.dbport = dbport;
		this.dbname = dbname;
		this.collname = collname;
		init();
	}
	
	private void init() throws UnknownHostException {
        if (ServiceDAOMongoDb.instance != null) {
            // An instance has been already created.
            throw new RuntimeException("Attempt to create a second instance of ServiceDAOMongoDb");
        }
        ServiceDAOMongoDb.instance = this;

		mongo = new Mongo(dburl,dbport);
		db = mongo.getDB(dbname);
		coll = db.getCollection(collname);
	}
	
	//should use json specific register request and response.
	public Message publishService(Message message){
		int errorcode;
		String errormsg;
		Map services = (Map) message.getMap();
	
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
		
		Message response = new Message();
		response.setError(errorcode);
		response.setErrorMessage(errormsg);
		return response;
	}
	
	
	public Message deleteService(Message message){
		int errorcode;
		String errormsg;
	
		BasicDBObject query = new BasicDBObject();
		String uri = message.getURI();
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
		
		Message response = new Message();
		response.setError(errorcode);
		response.setErrorMessage(errormsg);
		return response;
	}
	
	public Message renewService(Message message){
		
		int errorcode;
		String errormsg;
		
		String uri = message.getURI();
        int ttl = message.getTTL();
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
	
		Message response = new Message();
		response.setError(errorcode);
		response.setErrorMessage(errormsg);
		return response;
	}
	
	
	
	public List<Service> query(Message queryRequest){
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
                    try {
					    tmpserv.add (tmpKey,tmp.get(tmpKey));
                    } catch (DuplicateKeyException e) {
                        // Since the key/value pairs are coming from the database, we are guaranteed to be valid
                        // therefore, any DuplicateKeyException would indicate a bug in the code
                        // TODO: better error handling
                        Thread.dumpStack();
                    }
				}
			}
			result.add(tmpserv);
		}
		return result;
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
					try {
					    result.add (tmpKey,tmp.get(tmpKey));
                    } catch (DuplicateKeyException e) {
                        // Since the key/value pairs are coming from the database, we are guaranteed to be valid
                        // therefore, any DuplicateKeyException would indicate a bug in the code
                        // TODO: better error handling
                        Thread.dumpStack();
                    }
				}
			}
		}
			
		return result;
	}
	
}