package net.es.lookup.api;

import net.es.lookup.common.Message;
import net.es.lookup.common.exception.api.InternalErrorException;
import net.es.lookup.common.exception.api.NotFoundException;
import net.es.lookup.common.exception.internal.DataFormatException;
import net.es.lookup.database.ServiceElasticSearch;
import net.es.lookup.database.connectDB;
import net.es.lookup.protocol.json.JSONGetServiceResponse;
import net.es.lookup.protocol.json.JSONMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class AccessService {

  private static Logger LOG = LogManager.getLogger(AccessService.class);

  /**
   * Method to retrieve the record.
   *
   * @param serviceid id of the record (uri)
   * @return String Json message of the record as string
   */
  public String getService(String serviceid) {

    LOG.info("Processing getService...");
    LOG.info(" serviceid: " + serviceid);

    JSONGetServiceResponse response;
    Message serviceRecord;
    try {
      connectDB connect = new connectDB();
      ServiceElasticSearch db = connect.connect();
      serviceRecord = db.getRecordByURI(serviceid);
      db.closeConnection();

      if (serviceRecord != null) {

        LOG.debug("servicerecord not null");
        Map<String, Object> serviceMap = serviceRecord.getMap();

        response = new JSONGetServiceResponse(serviceMap);
        try {

          LOG.info("GetService status: SUCCESS; exiting ");
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

    } catch (URISyntaxException | IOException e) {

      LOG.fatal("DatabaseException: The database is out of service." + e.getMessage());
      LOG.info("GetService status: FAILED; exiting");
      throw new InternalErrorException("Database error\n");
    }
  }

  /**
   * Method to retrieve a particular key and value from the record.
   *
   * @param serviceid id of the record (uri)
   * @param key the key of the key-value to be retrieved
   * @return String Json message of the key-value expressed as string
   */
  public String getKeyService(String serviceid, String key) {

    LOG.info("Processing getServiceKey...");
    LOG.info(" serviceid: " + serviceid);

    JSONGetServiceResponse response;
    Message serviceRecord;

    try {
      connectDB connect = new connectDB();
      ServiceElasticSearch db = connect.connect();
      serviceRecord = db.getRecordByURI(serviceid);
      db.closeConnection();
      if (serviceRecord != null) {

        if (serviceRecord.getKey(key) == null) {

          LOG.error("The key does not exist.");
          LOG.info("GetServiceKey status: FAILED; exiting");
          throw new NotFoundException("The key does not exist\n");
        }

        LOG.info("GetServiceKey status: SUCCESS");
        Map<String, Object> keyValueMap = new HashMap<String, Object>();
        keyValueMap.put(key, serviceRecord.getKey(key));
        response = new JSONGetServiceResponse(keyValueMap);

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

    } catch (URISyntaxException e) {

      LOG.fatal("DatabaseException: The database is out of service." + e.getMessage());
      LOG.info("GetServiceKey status: FAILED; exiting");
      throw new InternalErrorException("Database error\n");
    } catch (IOException e) {
      LOG.error("unable to find record");
      throw new InternalErrorException("Record URI not found");
    }
  }

}
