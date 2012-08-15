package net.es.lookup.database;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import com.mongodb.*;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

import net.es.lookup.common.Service;
import net.es.lookup.common.Message;
import net.es.lookup.resources.ServicesResource;
import net.es.lookup.common.ReservedKeywords;
import net.es.lookup.common.exception.internal.DatabaseException;

import net.es.lookup.utils.DatabaseConfigReader;

public class ArchiveDAOMongoDb {
	private String dburl="127.0.0.1";
	private int dbport=27017;
	private String dbname="LookupServiceArchive";
	private String collname="archive";

	private Mongo mongo;
	private DB db;
	private DBCollection coll;
    private static ArchiveDAOMongoDb instance = null;
    
	private static Map<String, String> operatorMapping = new HashMap();
	private static Map<String, String> listOperatorMapping = new HashMap();

    public static ArchiveDAOMongoDb getInstance() {
        return ArchiveDAOMongoDb.instance;
    }

	public ArchiveDAOMongoDb() throws DatabaseException{
		DatabaseConfigReader dcfg = DatabaseConfigReader.getInstance();
		this.dburl = dcfg.getArchiveDbUrl();
		this.dbport = dcfg.getArchiveDbPort();
		this.dbname = dcfg.getArchiveDbName();
		this.collname = dcfg.getArchiveDbCollName();
		init();
	}

	private void init() throws DatabaseException {
        if (ArchiveDAOMongoDb.instance != null) {
            // An instance has been already created.
            throw new DatabaseException("Attempt to create a second instance of ArchiveDAOMongoDb");
        }
        ArchiveDAOMongoDb.instance = this;
        try{
        	mongo = new Mongo(dburl,dbport);
        	//System.out.println(mongo.getAddress().toString());
		
        	db = mongo.getDB(dbname);
        	//System.out.println(db.getName());
        	coll = db.getCollection(collname);
        	//System.out.println(coll.getName());
        }catch(UnknownHostException e){
        	throw new DatabaseException(e.getMessage());
        }catch(Exception e){
        	throw new DatabaseException(e.getMessage());
        }
		
        operatorMapping.put(ReservedKeywords.RECORD_OPERATOR_ALL, "$and");
        operatorMapping.put(ReservedKeywords.RECORD_OPERATOR_ANY, "$or");
		
        listOperatorMapping.put(ReservedKeywords.RECORD_OPERATOR_ANY, "$in");
        listOperatorMapping.put(ReservedKeywords.RECORD_OPERATOR_ALL, "$all");
	}
	
	//should use json specific register request and response.
	public void insert(Message message) throws DatabaseException{
		int errorcode;
		String errormsg;
		Message response;
		
		Map<String, Object> services = message.getMap();
		BasicDBObject doc = new BasicDBObject();
		doc.putAll(services);
		
		WriteResult wrt = coll.insert(doc);
		
		CommandResult cmdres = wrt.getLastError();
		if(!cmdres.ok()){
			throw new DatabaseException("Error inserting record");
		}

	}
	
	public void insert(List<Message> messages) throws DatabaseException{
		int errorcode;
		String errormsg;
		Message response;
		
		List<DBObject> dbobj = new ArrayList<DBObject>();
		for (int i=0;i<messages.size();i++){
			Map<String, Object> service = messages.get(i).getMap();
			BasicDBObject doc = new BasicDBObject();
			doc.putAll(service);
			dbobj.add(doc);
		}
		
		WriteResult wrt = coll.insert(dbobj);
		
		CommandResult cmdres = wrt.getLastError();
		if(!cmdres.ok()){
			throw new DatabaseException("Error inserting record");
		}
		
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
						//remove key added by mongodb
						if(!tmpKey.equals("_id")){
							tmpserv.add (tmpKey,tmp.get(tmpKey));
						}
						  
					}
				}
				result.add(tmpserv);
			}
		}catch(MongoException e){
			throw new DatabaseException("Error retrieving results");
		}

		return result;
	}
	
	public List<Service> queryAll() throws DatabaseException{
		Message msg = new Message();
		List <Service> result = query(msg,msg,msg);
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
                 
            	String val = (String) obj;
            	//deal with metacharacter
            	 if(val.endsWith("*")){
            		 val = val.substring(0, val.length()-1);
            		 //System.out.println(val);
            		 Pattern newVal = Pattern.compile("^"+val);
            		 tmpHash.put(newKey, newVal);
            	 }else if(val.startsWith("*")){
            		 val = val.substring(1, val.length());
            		 //System.out.println(val);
            		 Pattern newVal = Pattern.compile(val+"$");
            		 tmpHash.put(newKey, newVal);
            	 }else{
            		 tmpHash.put(newKey, (String) obj);
            	 }
            } else if (obj instanceof List) {
                 List <Object> values = (List<Object>) obj;
                 ArrayList newValues = new ArrayList();
                 if(values.size()>1){
                	 for(int i=0; i<values.size();i++){
                		 String val = (String)values.get(i);
                		 if(val.endsWith("*")){
                    		 val = val.substring(0, val.length()-1);
                    		 //System.out.println(val);
                    		 Pattern newVal = Pattern.compile("^"+val);
                    		 newValues.add(newVal);
                    	 }else if(val.startsWith("*")){
                    		 val = val.substring(1, val.length());
                    		 //System.out.println(val);
                    		 Pattern newVal = Pattern.compile(val+"$");
                    		 newValues.add(newVal);
                    	 }else{
                    		 newValues.add(val);
                    	 }
                		 
                	 }
                	 HashMap<String, Object> listvalues = new HashMap<String, Object>();
                	 if(ops.containsKey(newKey) && this.listOperatorMapping.containsKey(ops.get(newKey))){
                		 
                		 //get the operator
                		 String curop = this.listOperatorMapping.get(ops.get(newKey));
                		 
                		 listvalues.put(curop, newValues);
                		 tmpHash.put(newKey, listvalues);
                	 }else{
                		 tmpHash.put(newKey, newValues);
                	 }  
                	 
                	
                 }else if(values.size()==1){
                	 String val = (String)values.get(0);
                	 if(val.endsWith("*")){
                		 val = val.substring(0, val.length()-1);
                		 //System.out.println(val);
                		 Pattern newVal = Pattern.compile("^"+val);
                		 tmpHash.put(newKey, newVal);
                	 }else if(val.startsWith("*")){
                		 val = val.substring(1, val.length());
                		 //System.out.println(val);
                		 Pattern newVal = Pattern.compile(val+"$");
                		 newValues.add(newVal);
                	 }else{
                		 tmpHash.put(newKey, values.get(0));
                	 }
                        
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
		
		//System.out.println(query);
		return query;
	}

	
}