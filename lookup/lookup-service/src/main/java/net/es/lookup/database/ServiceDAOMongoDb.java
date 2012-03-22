package net.es.lookup.database;

import net.es.lookup.common.LookupService;
import net.es.lookup.common.Service;
import net.es.lookup.common.KeyValue;
import net.es.lookup.common.QueryRequest;
import net.es.lookup.common.QueryResponse;
import net.es.lookup.common.RegisterRequest;
import net.es.lookup.common.RegisterResponse;
import net.es.lookup.common.DeleteRequest;
import net.es.lookup.common.DeleteResponse;
import net.es.lookup.common.RenewRequest;
import net.es.lookup.common.RenewResponse;
import java.util.List;


import com.mongodb.Mongo;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBCursor;

public class ServiceDAOMongoDb implements LookupService{
	private String dburl;
	private int dbport;
	private DB db;
	private DBCollection coll;

	// retrieves default - mongodb running on localhost and default port - 27017 and dbname- "lookupservice", collection name - "services" 
	//creates a new one if it cannot find one 
	ServiceDAOMongoDb(){
		
	}
	
	//uses default url and port - mongodb running on localhost and default port - 27017
	//creates a new one if it cannot find one
	ServiceDAOMongoDb(String dbname, String collName){
		
	}
	
	//retrieves the db and collection(table); creates a new one if it cannot find one
	ServiceDAOMongoDb(String dburl, int dbport, String dbname, String collName){
			
	}
	
	//should use json specific register request and response.
	public RegisterResponse publishService(RegisterRequest registerRequest){
		return null;
	}
	
	
	public DeleteResponse deleteService(DeleteRequest deleteRequest){
		return null;
	}
	
	public RenewResponse renewService(RenewRequest renewRequest){
		return null;
	}
	
	public QueryResponse query(QueryRequest queryRequest){
		return null;
	}
	
	public Service getServiceByURI(String URI){
		return null;
	}
	
}