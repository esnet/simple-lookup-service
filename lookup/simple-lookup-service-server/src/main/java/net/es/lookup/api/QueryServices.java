package net.es.lookup.api;

import java.util.List;
import java.util.Map;
import net.es.lookup.common.Message;
import net.es.lookup.common.ReservedKeys;
import net.es.lookup.common.ReservedValues;
import net.es.lookup.common.exception.api.InternalErrorException;
import net.es.lookup.common.exception.api.NotFoundException;
import net.es.lookup.common.exception.api.ServiceUnavailableTemporarilyException;
import net.es.lookup.common.exception.internal.DataFormatException;
import net.es.lookup.common.exception.internal.DatabaseException;
import net.es.lookup.database.ServiceDaoMongoDb;
import net.es.lookup.protocol.json.JSONMessage;
import org.apache.log4j.Logger;

public class QueryServices {

  private static Logger LOG = Logger.getLogger(QueryServices.class);
  private String params;

  public static boolean QUERY_ALL_FLAG = false;

  /**
   * Method to query for records using the request.
   * @param request Request containing keywords and operators
   * @param maxResult maxResults to be returned .not yet implemeted
   * */
  public String query(Message request, int maxResult) {

    // TODO: Implement maxResult and skip
    LOG.info("Processing queryService...");
    LOG.info("Received message: " + request.getMap());
    String response;

    Message queryParameters = getQueryParameters(request);
    Message operators = getOperators(request, queryParameters);

    // Query DB
    try {
      ServiceDaoMongoDb db = ServiceDaoMongoDb.getInstance();

      if (db != null) {

        List<Message> res = db.query(request, queryParameters, operators, maxResult);
        // Build response
        response = JSONMessage.toString(res);
        res = null;
        LOG.info("Query status: SUCCESS;");

        if (queryParameters.getMap().size() == 0) {
          QUERY_ALL_FLAG = true;
        }
        LOG.debug("Sending response");
        return response;
      } else {
        throw new NotFoundException("Cannot access database");
      }

    } catch (DatabaseException e) {

      LOG.fatal("Error retrieving results:" + e.getMessage());
      LOG.info("Query status: FAILED; exiting");
      throw new InternalErrorException("Error retrieving results");

    } catch (DataFormatException e) {

      LOG.error("Data formatting exception");
      LOG.info("Query status: FAILED; exiting");
      throw new InternalErrorException("Error formatting elements");

    } catch (OutOfMemoryError e) {

      LOG.error("The response was too large so ran out of memory");
      LOG.info("Query status: FAILED; exiting");
      throw new ServiceUnavailableTemporarilyException(
          "Server is unable to process large query requests at this time. Please try later");

    } catch (Exception e) {

      LOG.error("Unexpected exception: " + e.getMessage());
      LOG.info("Query status: FAILED; exiting");
      throw new ServiceUnavailableTemporarilyException(
          "Server is unable to process the request at this time. Please try later");
    }
  }

  private Message getQueryParameters(Message request) {

    Map<String, Object> requestMap = request.getMap();
    int size = requestMap.size();
    LOG.debug("Total number of parameters passed in requestUrl=" + size);
    LOG.info("requestUrl:" + request.getMap().toString());
    Message queryParameters = new Message();

    for (Map.Entry<String, Object> entry : requestMap.entrySet()) {

      String key = entry.getKey();
      Object value = entry.getValue();

      LOG.debug("key= " + key);

      // generate the operator map
      if (!key.contains(ReservedKeys.RECORD_OPERATOR_SUFFIX)) {
        queryParameters.add(key, value);
      }
    }

    return queryParameters;
  }

  private Message getOperators(Message request, Message queryParameters) {

    Message operators = new Message();
    Map<String, Object> queryParametersMap = queryParameters.getMap();
    Map<String, Object> requestMap = request.getMap();

    if (request.getOperator() != null) {

      String mainOp = request.getOperator();
      operators.add(ReservedKeys.RECORD_OPERATOR, mainOp);

    } else {

      String mainOp = ReservedValues.RECORD_OPERATOR_DEFAULT;
      operators.add(ReservedKeys.RECORD_OPERATOR, mainOp);
    }

    for (Map.Entry<String, Object> entry : queryParametersMap.entrySet()) {

      String key = entry.getKey();
      Object value = entry.getValue();

      LOG.debug("key= " + key);
      String opKey = key + "-" + ReservedKeys.RECORD_OPERATOR_SUFFIX;

      if (requestMap.containsKey(opKey)) {

        operators.add(key, requestMap.get(opKey));

      } else {

        // add default
        operators.add(key, ReservedValues.RECORD_OPERATOR_DEFAULT);
      }

      LOG.debug("operators::" + operators.getMap());
    }

    return operators;
  }
}
