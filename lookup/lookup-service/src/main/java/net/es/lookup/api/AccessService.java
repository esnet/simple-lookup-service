package net.es.lookup.api;


import net.es.lookup.common.LeaseManager;
import net.es.lookup.common.Message;
import net.es.lookup.common.ReservedKeywords;
import net.es.lookup.common.Service;
import net.es.lookup.common.exception.api.BadRequestException;
import net.es.lookup.common.exception.api.ForbiddenRequestException;
import net.es.lookup.common.exception.api.InternalErrorException;
import net.es.lookup.common.exception.api.NotFoundException;
import net.es.lookup.common.exception.internal.DataFormatException;
import net.es.lookup.common.exception.internal.DatabaseException;
import net.es.lookup.database.ServiceDAOMongoDb;
import net.es.lookup.protocol.json.*;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 */

public class AccessService {

    private static Logger LOG = Logger.getLogger(AccessService.class);

    public String getService(String serviceid) {

        LOG.info("Processing getService...");
        LOG.info(" serviceid: " + serviceid);

        JSONSubGetResponse response;
        Service serviceRecord;

        try {
            ServiceDAOMongoDb db = ServiceDAOMongoDb.getInstance();
            if(db != null){
                serviceRecord =db.getServiceByURI(serviceid);

                if (serviceRecord != null) {

                    LOG.debug("servicerecord not null");
                    Map<String, Object> serviceMap = serviceRecord.getMap();

                    response = new JSONSubGetResponse(serviceMap);
                    try {

                        LOG.info("GetService status: SUCCESS; exiting");
                        return JSONMessage.toString(response);

                    } catch (DataFormatException e) {

                        LOG.error("Data formating exception.");
                        LOG.info("GetService status: FAILED; exiting");
                        throw new InternalErrorException("Data formatting exception");

                    }


                } else {

                    LOG.error("ServiceRecord Not Found in DB.");
                    LOG.info("GetService status: FAILED; exiting");
                    throw new NotFoundException("ServiceRecord Not Found in DB\n");

                }
            } else{
                LOG.error("DB could not be accessed.");
                throw new InternalErrorException("Cannot access database");
            }


        } catch (DatabaseException e) {

            LOG.fatal("DatabaseException: The database is out of service." + e.getMessage());
            LOG.info("GetService status: FAILED; exiting");
            throw new InternalErrorException("Database error\n");

        }


    }


    public String getKeyService(String serviceid, String key) {

        LOG.info("Processing getServiceKey...");
        LOG.info(" serviceid: " + serviceid);

        JSONSubGetResponse response;
        Service serviceRecord;

        try {
            ServiceDAOMongoDb db = ServiceDAOMongoDb.getInstance();
            if(db!=null){
                serviceRecord = db.getServiceByURI(serviceid);

                if (serviceRecord != null) {

                    if (serviceRecord.getKey(key) == null) {

                        LOG.error("The key does not exist.");
                        LOG.info("GetServiceKey status: FAILED; exiting");
                        throw new NotFoundException("The key does not exist\n");

                    }

                    LOG.info("GetServiceKey status: SUCCESS");
                    Map<String, Object> keyValueMap = new HashMap<String, Object>();
                    keyValueMap.put(key,serviceRecord.getKey(key));
                    response = new JSONSubGetResponse(keyValueMap);

                    try {

                        return JSONMessage.toString(response);

                    } catch (DataFormatException e) {

                        LOG.error("Data formating exception.");
                        LOG.info("GetServiceKey status: FAILED; exiting");
                        throw new InternalErrorException("Data formatting exception");

                    }


                } else {

                    LOG.error("ServiceRecord Not Found in DB.");
                    LOG.info("GetServiceKey status: FAILED; exiting");
                    throw new NotFoundException("ServiceRecord Not Found in DB\n");

                }
            }else{
                LOG.fatal("DatabaseException: The database is out of service.");
                LOG.info("GetServiceKey status: FAILED; exiting");
                throw new InternalErrorException("Database error\n");
            }


        } catch (DatabaseException e) {

            LOG.fatal("DatabaseException: The database is out of service." + e.getMessage());
            LOG.info("GetServiceKey status: FAILED; exiting");
            throw new InternalErrorException("Database error\n");

        }


    }




    public String renewService(String serviceid, String service) {

        LOG.info("Processing renewService...");
        LOG.info(" serviceid: " + serviceid);

        JSONRenewResponse response;
        Message errorResponse = new Message();
        JSONRenewRequest request = new JSONRenewRequest(service);

        //renew can be empty for now. next version will require the privatekey
        if (!service.isEmpty() && request.getStatus() == JSONRenewRequest.INCORRECT_FORMAT) {

            LOG.error("requestStatus:" + request.getStatus() + " ServiceRecord requestUrl format is Incorrect");
            LOG.info("RenewService status: FAILED; exiting");
            throw new BadRequestException("ServiceRecord requestUrl format is Incorrect\n");

        }

        // Verify that requestUrl is valid and authorized
        LOG.debug("Is the requestUrl valid?" + this.isValid(request));

        if (this.isValid(request) && this.isAuthed(serviceid, request)) {

            try {
                ServiceDAOMongoDb db = ServiceDAOMongoDb.getInstance();
                if(db == null){
                    LOG.error(("Error accessing database object"));
                    throw new InternalErrorException("Error accessing database");
                }
                Service serviceRecord = db.getServiceByURI(serviceid);

                if (serviceRecord != null) {

                    LOG.debug("servicerecord not null");
                    Map<String, Object> serviceMap = serviceRecord.getMap();

                    if (request.getTTL() != null && !request.getTTL().isEmpty()) {

                        serviceMap.put(ReservedKeys.RECORD_TTL, request.getTTL());

                    } else {

                        serviceMap.put(ReservedKeys.RECORD_TTL, new ArrayList());

                    }

                    Message newRequest = new Message(serviceMap);
                    boolean gotLease = LeaseManager.getInstance().requestLease(newRequest);

                    if (gotLease) {

                        LOG.debug("gotLease for " + serviceid);
                        Message res = db.updateService(serviceid, newRequest);


                        response = new JSONRenewResponse(res.getMap());

                        try {
                            return JSONMessage.toString(response);

                        } catch (DataFormatException e) {

                            LOG.error("Data formatting exception");
                            LOG.info("RenewService status: FAILED; exiting");
                            throw new InternalErrorException("Data formatting exception");

                        }

                    } else {

                        LOG.fatal("Failed to secure lease for the renew record");
                        LOG.info("Renew status: FAILED; exiting");
                        throw new ForbiddenRequestException("Failed to secure lease for the renewal record");

                    }

                } else {

                    LOG.error("ServiceRecord Not Found in DB.");
                    LOG.info("RenewService status: FAILED; exiting");
                    throw new NotFoundException("ServiceRecord Not Found in DB\n");

                }

            } catch (DatabaseException e) {

                LOG.fatal("DatabaseException: The database is out of service." + e.getMessage());
                LOG.info("RenewService status: FAILED; exiting");
                throw new InternalErrorException("Database error\n");

            }

        } else {

            if (!this.isValid(request)) {

                LOG.error("ServiceRecord Request is invalid");
                LOG.info("RenewService status: FAILED; exiting");
                throw new BadRequestException("Request is invalid\n");

            } else if (!this.isAuthed(serviceid, request)) {

                LOG.error("The private-key is not authorized to access this service");
                LOG.info("RenewService status: FAILED; exiting");
                throw new ForbiddenRequestException("The private-key is not authorized to access this service\n");

            }

            try {

                LOG.info("RenewService status: SUCCESS");
                return JSONMessage.toString(errorResponse);

            } catch (DataFormatException e) {

                LOG.error("Data formatting exception");
                LOG.info("RenewService status: FAILED; exiting");
                throw new InternalErrorException("Data formatting exception");

            }

        }

    }


    public String deleteService(String dbname, String serviceid, String service) {

        LOG.info("Processing deleteService...");
        LOG.info("serviceid: " + serviceid);
        JSONDeleteResponse response;

        JSONDeleteRequest request = new JSONDeleteRequest(service);

        if (request.getStatus() == JSONDeleteRequest.INCORRECT_FORMAT) {

            LOG.debug("INCORRECT FORMAT");
            LOG.error("requestStatus:" + request.getStatus() + " ServiceRecord requestUrl format is Incorrect");
            LOG.info("DeleteService status: FAILED; exiting");
            throw new BadRequestException("ServiceRecord requestUrl format is Incorrect\n");

        }

        // Verify that requestUrl is valid and authorized
        LOG.debug("Is the requestUrl valid?" + this.isValid(request));

        if (this.isValid(request) && this.isAuthed(serviceid, request)) {

            try {
                ServiceDAOMongoDb db = ServiceDAOMongoDb.getInstance();

                if(db == null){
                    LOG.error("Error accessing database");
                    throw new InternalErrorException("Error accessing database");
                }
                Message serviceRecord = db.deleteService(serviceid);

                if (serviceRecord == null) {

                    LOG.error("ServiceRecord Not found");
                    LOG.info("DeleteService status: FAILED; exiting");
                    throw new NotFoundException("ServiceRecord not found in DB\n");

                } else {
                    response = new JSONDeleteResponse(serviceRecord.getMap());


                    try {

                        LOG.info("ServiceRecord Deleted");
                        LOG.info("DeleteService status: SUCCESS; exiting");
                        return JSONMessage.toString(response);

                    } catch (DataFormatException e) {

                        LOG.error("Data formatting exception");
                        LOG.info("DeleteService status: FAILED; exiting");
                        throw new InternalErrorException("Database error\n");

                    }

                }

            } catch (DatabaseException e) {

                LOG.fatal("DatabaseException: The database is out of service." + e.getMessage());
                LOG.info("DeleteService status: FAILED; exiting");
                throw new InternalErrorException("Database error\n");

            }

        } else {
            //fails either because it is not valid or not authorized
            if (!this.isValid(request)) {

                LOG.error("ServiceRecord Request is invalid");
                LOG.info("DeleteService status: FAILED; exiting");
                throw new BadRequestException("ServiceRecord Request is invalid\n");

            } else  {

                LOG.error("The private-key is not authorized to access this service");
                LOG.info("DeleteService status: FAILED; exiting");
                throw new ForbiddenRequestException("The private-key is not authorized to access this service\n");

            }

        }

    }



    private boolean isAuthed(String serviceid, Message request) {

        // TODO: needs to be implemented. Check if client uuid matches
        return true;

    }


    private boolean isValid(Message request) {

        //TODO: add privatekey as mandatory key-value
        LOG.debug("Request's TTL= " + request.getTTL());
        boolean res;

        if (request != null) {

            res = ((request.validate()));

        } else {

            //can be empty for renew
            res = true;
        }

        return res;

    }


}

