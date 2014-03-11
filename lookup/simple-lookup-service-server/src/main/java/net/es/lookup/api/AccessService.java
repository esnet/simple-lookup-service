package net.es.lookup.api;


import net.es.lookup.common.*;
import net.es.lookup.common.exception.api.InternalErrorException;
import net.es.lookup.common.exception.api.NotFoundException;
import net.es.lookup.common.exception.internal.DataFormatException;
import net.es.lookup.common.exception.internal.DatabaseException;
import net.es.lookup.database.DBPool;
import net.es.lookup.database.ServiceDAOMongoDb;
import net.es.lookup.protocol.json.*;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;


/**
 *
 */

public class AccessService {

    private static Logger LOG = Logger.getLogger(AccessService.class);

    public String getService(String dbname, String serviceid) {

        LOG.info("Processing getService...");
        LOG.info(" serviceid: " + serviceid);

        JSONSubGetResponse response;
        Message serviceRecord;

        try {
            ServiceDAOMongoDb db = DBPool.getDb(dbname);
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


    public String getKeyService(String dbname, String serviceid, String key) {

        LOG.info("Processing getServiceKey...");
        LOG.info(" serviceid: " + serviceid);

        JSONSubGetResponse response;
        Message serviceRecord;

        try {
            ServiceDAOMongoDb db = DBPool.getDb(dbname);
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


}

