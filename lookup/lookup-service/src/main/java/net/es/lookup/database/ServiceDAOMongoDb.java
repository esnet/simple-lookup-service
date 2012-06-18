package net.es.lookup.database;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import com.mongodb.*;
import java.net.UnknownHostException;

import net.es.lookup.common.Service;
import net.es.lookup.common.Message;
import net.es.lookup.resources.ServicesResource;
import net.es.lookup.common.ReservedKeywords;
import net.es.lookup.common.exception.internal.DatabaseException;
import net.es.lookup.common.exception.internal.DuplicateKeyException;

public class ServiceDAOMongoDb {
	private String dburl="127.0.0.1";
	private int dbport=27017;
	private String dbname="LookupService";
	private String collname="services";

	private Mongo mongo;
	private DB db;
	private DBCollection coll;
    private static ServiceDAOMongoDb instance = null;
    
	private static Map<String, String> operatorMapping = new HashMap();
	private static Map<String, String> listOperatorMapping = new HashMap();
	

    public static ServiceDAOMongoDb getInstance() {
        return ServiceDAOMongoDb.instance;
    }

	// retrieves default - mongodb running on localhost and default port - 27017 and dbname- "lookupservice", collection name - "services" 
	//creates a new one if it cannot find one 
	public ServiceDAOMongoDb() throws DatabaseException{
		init();
	}
	
	//uses default url and port - mongodb running on localhost and default port - 27017
	//creates a new one if it cannot find one
	public ServiceDAOMongoDb(String dbname, String collname) throws DatabaseException{
		this.dbname = dbname;
		this.collname = collname;
		init();
	}
	
	//retrieves the db and collection(table); creates a new one if it cannot find one
	public ServiceDAOMongoDb (String dburl, int dbport, String dbname, String collname) throws DatabaseException{
		this.dburl = dburl;
		this.dbport = dbport;
		this.dbname = dbname;
		this.collname = collname;
		init();
	}
	
	private void init() throws DatabaseException {
        if (ServiceDAOMongoDb.instance != null) {
            // An instance has been already created.
            throw new DatabaseException("Attempt to create a second instance of ServiceDAOMongoDb");
        }
        ServiceDAOMongoDb.instance = this;
        try{
        	mongo = new Mongo(dburl,dbport);
        	System.out.println(mongo.getAddress().toString());
		
        	db = mongo.getDB(dbname);
        	System.out.println(db.getName());
        	coll = db.getCollection(collname);
        	System.out.println(coll.getName());
        }catch(UnknownHostException e){
        	throw new DatabaseException(e.getMessage());
        }catch(Exception e){
        	throw new DatabaseException(e.getMessage());
        }
		
        operatorMapping.put(ReservedKeywords.RECORD_OPERATOR_ALL, "$and");
        operatorMapping.put(ReservedKeywords.RECORD_OPERATOR_ANY, "$or");
		
        listOperatorMapping.put(ReservedKeywords.RECORD_OPERATOR_ANY, "$in");
	}
	
	//should use json specific register request and response.
	public Message queryAndPublishService(Message message, Message queryRequest, Message operators) throws DatabaseException{
		int errorcode;
		String errormsg;
		Message response = new Message();
		
		//check for duplicates
		List<Service> dupEntries = this.query(message,queryRequest,operators);
		System.out.println("Duplicate Entries: "+dupEntries.size());
		if(dupEntries.size()>0){
			response.setError(500);
			response.setErrorMessage("Duplicate entries found");
			return response;		
		}
		
		Map<String, Object> services = message.getMap();
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
		query.put(ReservedKeywords.RECORD_URI, uri);
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
	
	public Message updateService(String serviceid, Message updateRequest) throws DatabaseException{
		
		int errorcode;
		String errormsg;
        
        
        if(serviceid != null && !serviceid.isEmpty()){
        	
        	BasicDBObject query = new BasicDBObject();
        	query.put(ReservedKeywords.RECORD_URI, serviceid);
        	
        	System.out.println(query);
        	
        	BasicDBObject updateObject = new BasicDBObject();
        	updateObject.putAll(updateRequest.getMap());
        	
        	System.out.println(updateObject);
        	
        	try{
        		WriteResult wrt = coll.update(query, updateObject);
        		CommandResult cmdres = wrt.getLastError();
        		System.out.println(cmdres.ok());
        	
        		if(cmdres.ok()){
        			errorcode=200;
        			errormsg="SUCCESS";
        		}else{
        			errorcode=500;
        			errormsg=cmdres.getErrorMessage();
        		}
        	}catch(MongoException e){
        		throw new DatabaseException(e.getMessage());
        	}
    		
        }else{
        	errorcode=500;
        	errormsg = "Record URI not specified!!!";
        }
		
        Message response = new Message();
        response.setError(errorcode);
        response.setErrorMessage(errormsg);
        System.out.println("came here");
		return response;
	}

    public List<Service> query(Message message, Message queryRequest, Message operators) throws DatabaseException{
        return this.query (message, queryRequest, operators, 0, 0);
    }
	
	public List<Service> query(Message message, Message queryRequest, Message operators, int maxResults, int skip) throws DatabaseException{
		
		BasicDBObject query = buildQuery(queryRequest, operators);
		
		ArrayList <Service> result = new ArrayList<Service>();
		
		try{
			DBCursor cur = coll.find(query);	
			
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
		}catch(MongoException e){
			throw new DatabaseException(e.getMessage());
		}

		return result;
	}
	
	
	
	//Builds the query from the given map
	private BasicDBObject buildQuery(Message queryRequest, Message operators){
		Map<String, Object> serv =  queryRequest.getMap();
		
		Map<String, String> ops = operators.getMap();
		
	
		
		List <HashMap<String,Object>> keyValueList = new ArrayList<HashMap<String,Object>>();
		
        for (Map.Entry<String,Object> entry : serv.entrySet()) {
            String newKey = entry.getKey();
            HashMap<String, Object> tmpHash = new HashMap<String, Object>();
            Object obj = serv.get(newKey);
            if (obj instanceof String) {
                 tmpHash.put(newKey, (String) obj);
            } else if (obj instanceof List) {
                 List <Object> values = (List<Object>) obj;
                 if(values.size()>1){
                	 HashMap<String, Object> listvalues = new HashMap<String, Object>();
                	 if(ops.containsKey(newKey) && this.listOperatorMapping.containsKey(ops.get(newKey))){ 
                		 //get the operator
                		 String curop = this.listOperatorMapping.get(ops.get(newKey));
                		 
                		 listvalues.put(curop, values);
                		 tmpHash.put(newKey, listvalues);
                	 }else{
                		 tmpHash.put(newKey, values);
                	 }                   
                 }else if(values.size()==1){
                        tmpHash.put(newKey, values.get(0));
                 }
                    
             }
            
            if(!tmpHash.isEmpty()){
            	keyValueList.add(tmpHash);
            }
             
            
           
        }
		
		BasicDBObject query = new BasicDBObject();
		ArrayList queryOp = (ArrayList)operators.getOperator();
		
		String op=null;
		if( queryOp != null && !queryOp.isEmpty()){
			op = (String)queryOp.get(0);
		}else{
			op = ReservedKeywords.RECORD_OPERATOR_DEFAULT;
		}
		
		String mongoOp = "$and";
		
		if(op != null && !op.isEmpty()){
			if(op.equalsIgnoreCase(ReservedKeywords.RECORD_OPERATOR_ANY)){
				mongoOp = "$or";
			}else if(op.equalsIgnoreCase(ReservedKeywords.RECORD_OPERATOR_ALL)){
				mongoOp = "$and";
			}
		}
		
		if(!keyValueList.isEmpty()){
			query.put(mongoOp, keyValueList);
		}
		
		System.out.println(query);
		return query;
	}
	
	public Service getServiceByURI(String URI) throws DatabaseException{
		int errorcode;
		String errormsg;
		
		BasicDBObject query = new BasicDBObject();
		query.put(ReservedKeywords.RECORD_URI, URI);
		Service result=null;
		
		try{
			DBCursor cur = coll.find(query);
		
			System.out.println("Came inside getServiceByURI");
			
			if (cur.size() == 1){
				DBObject tmp = cur.next();
				result = new Service(tmp.toMap());
			}
		}catch(MongoException e){
			throw new DatabaseException(e.getMessage());
		}
			return result;
	}
	
}