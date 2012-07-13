package net.es.lookup.api;


import java.util.List;
import java.util.Map;

import net.es.lookup.protocol.json.JSONMessage;
import net.es.lookup.protocol.json.JSONRenewRequest;
import net.es.lookup.protocol.json.JSONRenewResponse;
import net.es.lookup.protocol.json.JSONDeleteRequest;
import net.es.lookup.protocol.json.JSONDeleteResponse;
import net.es.lookup.protocol.json.JSONSubGetRequest;
import net.es.lookup.protocol.json.JSONSubGetResponse;
import net.es.lookup.database.ServiceDAOMongoDb;
import net.es.lookup.common.LeaseManager;
import net.es.lookup.common.Message;
import net.es.lookup.common.Service;
import net.es.lookup.common.ReservedKeywords;
import net.es.lookup.common.exception.api.BadRequestException;
import net.es.lookup.common.exception.api.NotFoundException;
import net.es.lookup.common.exception.api.ForbiddenRequestException;
import net.es.lookup.common.exception.api.InternalErrorException;
import net.es.lookup.common.exception.internal.DatabaseException;
import net.es.lookup.common.exception.internal.DataFormatException;



/**
 *
 */

public class AccessService {

	public String getService(String serviceid, String service) {
		// Return some cliched textual content
		System.out.println("Processing getService");
		JSONSubGetResponse response;
		Service serviceRecord= new Service();
		Message errorResponse = new Message();


		JSONSubGetRequest request = new JSONSubGetRequest(service);
		if (request.getStatus() == JSONSubGetRequest.INCORRECT_FORMAT) {
			System.out.println("INCORRECT FORMAT");
			// TODO: return correct error code
			throw new BadRequestException("Service request format is Incorrect\n");
		}

		// Verify that request is valid and authorized
		if (this.isValid(request) && this.isAuthed(serviceid, request)) {

			try{
				serviceRecord = ServiceDAOMongoDb.getInstance().getServiceByURI(serviceid);

				if(serviceRecord!= null){
					System.out.println("servicerecord not null");
					Map<String, Object> serviceMap = serviceRecord.getMap();	

					Message newRequest = new Message(serviceMap);

					if(newRequest.getError() == 200){
						response = new JSONSubGetResponse (newRequest.getMap());
						try{
							return JSONMessage.toString(response);
						}catch(DataFormatException e){
							throw new InternalErrorException("Data formatting exception");
						}
					}
				}else{
					throw new NotFoundException("Service Not Found in DB\n");
				}
			}catch(DatabaseException e){
				throw new InternalErrorException("Database error\n");
			}
		}else{
			if(!this.isValid(request)){
				throw new BadRequestException("Service Request is invalid\n");
			}else if(!this.isAuthed(serviceid, request)){
				throw new ForbiddenRequestException("The private-key is not authorized to access this service\n");
			}
			try{
				return JSONMessage.toString(errorResponse);    
			}catch(DataFormatException e){
				throw new InternalErrorException("Data formatting exception");
			}
		}

		//		return "\n";
		//		return "/lookup/service/" + serviceRecord.getMap() + "\n";
		return "Service record:  " + serviceRecord.getMap() + "\n";
	}



	public String getKeyService(String serviceid, String service, String key) {
		// Return some cliched textual content
		System.out.println("Processing getService");
		JSONSubGetResponse response;
		Service serviceRecord= new Service();
		Message errorResponse = new Message();


		JSONSubGetRequest request = new JSONSubGetRequest(service);
		if (request.getStatus() == JSONSubGetRequest.INCORRECT_FORMAT) {
			System.out.println("INCORRECT FORMAT");
			// TODO: return correct error code
			throw new BadRequestException("Service request format is Incorrect\n");
		}

		// Verify that request is valid and authorized
		if (this.isValid(request) && this.isAuthed(serviceid, request)) {

			try{
				serviceRecord = ServiceDAOMongoDb.getInstance().getServiceByURI(serviceid);

				if(serviceRecord!= null){
					System.out.println("servicerecord not null");
					Map<String, Object> serviceMap = serviceRecord.getMap();	

					Message newRequest = new Message(serviceMap);

					if(newRequest.getError() == 200){
						response = new JSONSubGetResponse (newRequest.getMap());
						try{
							return JSONMessage.toString(response);
						}catch(DataFormatException e){
							throw new InternalErrorException("Data formatting exception");
						}
					}else if(serviceRecord.getKey(key)==null){
						throw new NotFoundException("The key does not exist\n");
					}
						
				}else{
					throw new NotFoundException("Service Not Found in DB\n");
				}
			}catch(DatabaseException e){
				throw new InternalErrorException("Database error\n");
			}
		}else{
			if(!this.isValid(request)){
				throw new BadRequestException("Service Request is invalid\n");
			}else if(!this.isAuthed(serviceid, request)){
				throw new ForbiddenRequestException("The private-key is not authorized to access this service\n");
			}
			try{
				return JSONMessage.toString(errorResponse);    
			}catch(DataFormatException e){
				throw new InternalErrorException("Data formatting exception");
			}
		}

		//		return "\n";
		//		return "/lookup/service/" + serviceRecord.getMap() + "\n";

		return key + ":" + serviceRecord.getKey(key) + "\n";
	}



	public String renewService(String serviceid, String service){
		System.out.println("Processing renewService");
		JSONRenewResponse response;

		Message errorResponse = new Message();

		JSONRenewRequest request = new JSONRenewRequest(service);
		//renew can be empty for now. next version will require the privatekey
		if (!service.isEmpty() && request.getStatus() == JSONRenewRequest.INCORRECT_FORMAT) {
			System.out.println("INCORRECT FORMAT");
			// TODO: return correct error code
			throw new BadRequestException("Service request format is Incorrect\n");
		}

		// Verify that request is valid and authorized
		if (this.isValid(request) && this.isAuthed(serviceid, request)) {

			try{
				Service serviceRecord = ServiceDAOMongoDb.getInstance().getServiceByURI(serviceid);

				if(serviceRecord!= null){
					System.out.println("servicerecord not null");
					Map<String, Object> serviceMap = serviceRecord.getMap();
					if(request.getTTL() != null && request.getTTL() != ""){
						serviceMap.put(ReservedKeywords.RECORD_TTL, request.getTTL());
					}else{
						serviceMap.put(ReservedKeywords.RECORD_TTL, "");
					}

					if(serviceMap.containsKey(ReservedKeywords.RECORD_EXPIRES)){
						serviceMap.remove(ReservedKeywords.RECORD_EXPIRES);
					}

					Message newRequest = new Message(serviceMap);

					boolean gotLease = LeaseManager.getInstance().requestLease(newRequest);
					if(gotLease){
						System.out.println("gotLease for "+serviceid);
						Message res = ServiceDAOMongoDb.getInstance().updateService(serviceid,newRequest);

						if(res.getError() == 200){
							response = new JSONRenewResponse (res.getMap());
							try{
								return JSONMessage.toString(response);
							}catch(DataFormatException e){
								throw new InternalErrorException("Data formatting exception");
							}
						}else{

						}
					}	
				}else{
					throw new NotFoundException("Service Not Found in DB\n");
				}
			}catch(DatabaseException e){
				throw new InternalErrorException("Database error\n");
			}
		}else{
			if(!this.isValid(request)){
				throw new BadRequestException("Request is invalid\n");
			}else if(!this.isAuthed(serviceid, request)){
				throw new ForbiddenRequestException("The private-key is not authorized to access this service\n");
			}
			try{
				return JSONMessage.toString(errorResponse);    
			}catch(DataFormatException e){
				throw new InternalErrorException("Data formatting exception");
			}
		}

		return "\n";

	}

	public String deleteService(String serviceid, String service){
		System.out.println("Processing deleteService");
		JSONDeleteResponse response;

		Message errorResponse = new Message();
		JSONDeleteRequest request = new JSONDeleteRequest(service);
		if (request.getStatus() == JSONDeleteRequest.INCORRECT_FORMAT) {
			System.out.println("INCORRECT FORMAT");
			// TODO: return correct error code
			throw new BadRequestException("Service request format is Incorrect\n");
		}

		// Verify that request is valid and authorized
		if (this.isValid(request) && this.isAuthed(serviceid, request)) {
			try{
				Service serviceRecord = ServiceDAOMongoDb.getInstance().getServiceByURI(serviceid);

				if(serviceRecord!= null){
					System.out.println("servicerecord not null");
					Map<String, Object> serviceMap = serviceRecord.getMap();


					if(serviceMap.containsKey(ReservedKeywords.RECORD_EXPIRES)){
						serviceMap.remove(ReservedKeywords.RECORD_EXPIRES);
					}
					Message newRequest = new Message(serviceMap);

					Message res = ServiceDAOMongoDb.getInstance().deleteService(newRequest);
					if(res.getError() == 200){
						response = new JSONDeleteResponse (res.getMap());
						try{
							return JSONMessage.toString(response);
						}catch(DataFormatException e){
							throw new InternalErrorException("Data formatting exception");
						}
					}
					
				}else{
					throw new NotFoundException("Service Not Found in DB\n");
				}
			}
			catch(DatabaseException e){
				throw new InternalErrorException("Database error\n");
			}
		}
		else{
			if(!this.isValid(request)){
				throw new BadRequestException("Service Request is invalid\n");
			}else if(!this.isAuthed(serviceid, request)){
				throw new ForbiddenRequestException("The private-key is not authorized to access this service\n");
			}
			try{
				return JSONMessage.toString(errorResponse);   
			}catch(DataFormatException e){
				throw new InternalErrorException("Data formatting exception");
			}
		}
		return "\n";

	}

	private boolean isAuthed(String serviceid, JSONSubGetRequest request) {

		// TODO: needs to be implemented. Check if client uuid matches
		return true;
	}


	private boolean isValid(JSONSubGetRequest request) {
		// TODO: needs to be implemented. Check for client-uuid    
		boolean res = request.validate();

		return res;
		//		return true;
	}


	private boolean isAuthed(String serviceid, JSONRenewRequest request) {
		// TODO: needs to be implemented. Check if client uuid matches
		return true;
	}


	private boolean isValid(JSONRenewRequest request) {
		//TODO: add privatekey as mandatory key-value
		System.out.println("Request's TTL= "+request.getTTL());
		boolean res;
		if(request != null){
			res = ((request.validate()));
		}else{
			//can be empty for renew
			res = true;
		}
		

		return res;  
	}

	private boolean isAuthed(String serviceid, JSONDeleteRequest request) {

		// TODO: needs to be implemented. Check if client uuid matches
		boolean res = request.validate();

		return res;
		//		return true;
	}


	private boolean isValid(JSONDeleteRequest request) {
		// TODO: needs to be implemented. Check for client-uuid     
		return true;
	}


}

