package net.es.lookup.client;

import net.es.lookup.common.ReservedKeys;
import net.es.lookup.common.ReservedValues;
import net.es.lookup.common.ResponseCodes;
import net.es.lookup.common.exception.LSClientException;
import net.es.lookup.common.exception.ParserException;
import net.es.lookup.common.exception.RecordException;
import net.es.lookup.protocol.json.JSONParser;
import net.es.lookup.records.ErrorRecord;
import net.es.lookup.records.Record;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: luke
 * Date: 6/21/13
 * Time: 3:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class RecordManager {

    private SimpleLS server;
    private String recordUri;
    private String baseUrl = "lookup/";
    private String relativeUrl = baseUrl;

    private static Logger LOG = Logger.getLogger(RecordManager.class);

    public RecordManager(SimpleLS server) throws LSClientException {

        this(server, null);
    }

    public RecordManager(SimpleLS server, String recordUri) throws LSClientException {

        if (server != null) {

            this.server = server;
            this.recordUri = recordUri;
            LOG.info("net.es.lookup.client.RecordManager: Creating RecordManager");
        } else {

            LOG.info("net.es.lookup.client.RecordManager: Error creating RecordManager. Did not find server");
            throw new LSClientException("Error creating RecordManager. Did not find server");
        }
    }

    public SimpleLS getServer() {

        return server;
    }

    public String getRecordUri() {

        return recordUri;
    }

    private void setRelativeUrl() throws LSClientException {

        if (recordUri != null && !recordUri.isEmpty()) {

            if (recordUri.startsWith("/")) {
                recordUri = recordUri.replace("/", "");
            }
            this.relativeUrl = baseUrl + recordUri;
        } else {

            LOG.info("net.es.lookup.client.RecordManager: Could not set URL. Record URI is empty");
            throw new LSClientException("Record URI is empty");
        }
    }

    public void setRecordUri(String relativeUrl) throws LSClientException {

        if (relativeUrl != null && !relativeUrl.isEmpty()) {

            this.recordUri = relativeUrl;
        } else {

            LOG.info("net.es.lookup.client.RecordManager: Could not set record URI. Record type and service ID missing");
            throw new LSClientException("Record type and service ID missing");
        }
    }

    public synchronized Record renew() throws ParserException, LSClientException {

        LOG.info("net.es.lookup.client.RecordManager: Initializing record renewal");
        if (recordUri != null) {

            LOG.info("net.es.lookup.client.RecordManager: Setting relative URL");
            this.setRelativeUrl();

            LOG.info("net.es.lookup.client.RecordManager: Setting request parameters");
            server.setData("");
            server.setConnectionType("POST");
            server.setRelativeUrl(relativeUrl);

            return commitOperation();
        } else {

            LOG.info("net.es.lookup.client.RecordManager: Could not renew record. Record type and service ID missing");
            throw new LSClientException("Record type and service ID missing.");
        }
    }

    public synchronized Record getRecord() throws ParserException, LSClientException {

        LOG.info("net.es.lookup.client.RecordManager: Initializing record retrieval");
        if (recordUri != null) {

            LOG.info("net.es.lookup.client.RecordManager: Setting relative URL");
            this.setRelativeUrl();

            LOG.info("net.es.lookup.client.RecordManager: Setting request parameters");
            server.setConnectionType("GET");
            server.setRelativeUrl(relativeUrl);

            return commitOperation();
        } else {

            LOG.info("net.es.lookup.client.RecordManager: Could not retrieve record. Record type and service ID missing");
            throw new LSClientException("Record type and service ID missing.");
        }
    }

    public synchronized Record getKeyValueInRecord(String key) throws ParserException, LSClientException {

        LOG.info("net.es.lookup.client.RecordManager: Initializing key/value pair retrieval");
        if (recordUri != null) {

            LOG.info("net.es.lookup.client.RecordManager: Setting relative URL");
            this.setRelativeUrl();

            LOG.info("net.es.lookup.client.RecordManager: Setting request parameters");
            server.setConnectionType("GET");
            relativeUrl += (relativeUrl.endsWith("/")) ? "" : "/";
            relativeUrl += key;
            server.setRelativeUrl(relativeUrl);

            LOG.info("net.es.lookup.client.RecordManager: Sending query request");
            server.send();
            if (server.getResponseCode() == ResponseCodes.SUCCESS) {

                String response = server.getResponse();
                LOG.info("net.es.lookup.client.RecordManager: Parsing response");
                JSONObject jsonObject = JSONObject.fromObject(response);
                jsonObject.put(ReservedKeys.RECORD_TYPE, ReservedValues.RECORD_VALUE_TYPE_KEY_VALUE_PAIR);
                Record record = JSONParser.toRecord(jsonObject.toString());
                LOG.info("net.es.lookup.client.RecordManager: Successful request");
                return record;
            } else {

                ErrorRecord record = new ErrorRecord();
                record.setErrorCode(server.getResponseCode());
                try {

                    record.setErrorMessage(server.getErrorMessage());
                } catch (RecordException e) {

                    LOG.info("net.es.lookup.client.RecordManager: Error in response");
                    throw new LSClientException("Error in response");
                }
                return record;
            }
        } else {

            LOG.info("net.es.lookup.client.RecordManager: Could not retrieve key/value pair. Record type and service ID missing");
            throw new LSClientException("Record type and service ID missing.");
        }
    }

    public synchronized Record delete() throws ParserException, LSClientException {

        LOG.info("net.es.lookup.client.RecordManager: Initializing record deletion");
        if (recordUri != null) {

            LOG.info("net.es.lookup.client.RecordManager: Setting relative URL");
            this.setRelativeUrl();

            LOG.info("net.es.lookup.client.RecordManager: Setting request parameters");
            server.setConnectionType("DELETE");
            server.setRelativeUrl(relativeUrl);

            return commitOperation();
        } else {

            LOG.info("net.es.lookup.client.RecordManager: Could not delete record. Record type and service ID missing");
            throw new LSClientException("Record type and service ID missing.");
        }
    }

    private Record commitOperation() throws LSClientException, ParserException {

        LOG.info("net.es.lookup.client.RecordManager: Sending query request");
        server.send();
        if (server.getResponseCode() == ResponseCodes.SUCCESS) {

            String response = server.getResponse();
            LOG.info("net.es.lookup.client.RecordManager: Parsing response");
            Record record = JSONParser.toRecord(response);
            LOG.info("net.es.lookup.client.RecordManager: Successful request");
            return record;
        } else {
            ErrorRecord record = new ErrorRecord();
            record.setErrorCode(server.getResponseCode());
            try {

                record.setErrorMessage(server.getErrorMessage());
            } catch (RecordException e) {

                LOG.info("net.es.lookup.client.RecordManager: Error in response");
                throw new LSClientException("Error in response");
            }

            LOG.info("net.es.lookup.client.RecordManager: Unsuccessful request with code: " + server.getResponseCode());
            return record;
        }
    }
}
