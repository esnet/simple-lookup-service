package net.es.lookup.database;

import net.es.lookup.common.DuplicateKeyException;
import net.es.lookup.common.Service;
import net.es.lookup.common.Message;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import com.mongodb.*;
import java.net.UnknownHostException;

public class ServiceDAOMongoDb {
	private String dburl="127.0.0.1";
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
		System.out.println(mongo.getAddress().toString());
		
		db = mongo.getDB(dbname);
		System.out.println(db.getName());
		coll = db.getCollection(collname);
		System.out.println(coll.getName());
	}
	
	//should use json specific register request and response.
	public Message queryAndPublishService(Message message, Message queryRequest){
		int errorcode;
		String errormsg;
		Message response = new Message();
		
		//check for duplicates
		List<Service> dupEntries = this.query(queryRequest);
		
		if(dupEntries.size()>1){
			response.setError(500);
			response.setErrorMessage("Duplicate entries found");
			return response;		
		}
		
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
		query.put(Message.SERVICE_URI, uri);
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
        long ttl = message.getTTL();
		BasicDBObject query = new BasicDBObject();
		//TODO: add check to see if only one elem is returned
		query.put("uri", uri);
		
		
		DBCursor cur = coll.find(query);
		
		if (cur.size() == 1){
			DBObject tmp = cur.next();
			//DBObject tmp = new DBObject();
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
			
			if(cur.size()>1){
				errormsg = "Database corrupted";
			}else{
				errormsg = "Element not found";
			}
			
		}
	
		Message response = new Message();
		response.setError(errorcode);
		response.setErrorMessage(errormsg);
		return response;
	}
	
	
	public List<Service> query(Message queryRequest){
		
		BasicDBObject query = buildQuery(queryRequest);
		
		DBCursor cur = coll.find(query);
		System.out.println(query.toString());
		System.out.println(cur.count());
		
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
	
	
	//Builds the query from the given map
	private BasicDBObject buildQuery(Message queryRequest){
		Map<String, Object> serv =  queryRequest.getMap();
		List <HashMap<String,Object>> keyValueList = new ArrayList<HashMap<String,Object>>();

        for (Map.Entry<String,Object> entry : serv.entrySet()) {
            String newKey = entry.getKey();
            if ( ! newKey.equals(Message.QUERY_OPERATOR)) {
                HashMap<String, Object> tmpHash = new HashMap<String, Object>();
                Object obj = serv.get(newKey);
                if (obj instanceof String) {
                    tmpHash.put(newKey, (String) obj);
                } else if (obj instanceof List) {
                    List <Object> values = (List<Object>) obj;
                    if(values.size()>1){
                        HashMap<String, Object> listvalues = new HashMap<String, Object>();
                        listvalues.put("$in", values);
                        tmpHash.put(newKey, listvalues);
                    }else if(values.size()==1){
                        tmpHash.put(newKey, values.get(0));
                    }
                    keyValueList.add(tmpHash);
                }
            }
        }
		
		BasicDBObject query = new BasicDBObject();
		
		String op = queryRequest.getOperator();
		String mongoOp = "$and";
		
		if(op != null && !op.isEmpty()){
			if(op.equalsIgnoreCase("any")){
				mongoOp = "$or";
			}else if(op.equalsIgnoreCase("all")){
				mongoOp = "$and";
			}
		}

		query.put(mongoOp, keyValueList);
		
		return query;
	}
	
	public Service getServiceByURI(String URI){
		int errorcode;
		String errormsg;
		
		BasicDBObject query = new BasicDBObject();
		query.put("uri", URI);
		DBCursor cur = coll.find(query);
		
		Service result=null;
		if (cur.size() == 1){
			DBObject tmp = cur.next();
			result = new Service(tmp.toMap());
		}			
		return result;
	}
	
}