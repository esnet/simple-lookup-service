package net.es.lookup.client;

import net.es.lookup.common.ResponseCodes;
import net.es.lookup.common.exception.LSClientException;
import net.es.lookup.common.exception.ParserException;
import net.es.lookup.common.exception.RecordException;
import net.es.lookup.protocol.json.JSONParser;
import net.es.lookup.records.ErrorRecord;
import net.es.lookup.records.Record;
import org.apache.log4j.Logger;

/**
 * Author: sowmya
 * Date: 4/30/13
 * Time: 5:46 PM
 */
public class RegistrationClient {

    private SimpleLS server;
    private Record record;
    private String connectionType = "POST";
    private String relativeUrl = "lookup/records";

    private static Logger LOG = Logger.getLogger(RegistrationClient.class);

    public RegistrationClient(SimpleLS server) throws LSClientException {

        this(server, null);
    }

    public RegistrationClient(SimpleLS server, Record record) throws LSClientException {

       if(server != null){

           LOG.info("net.es.lookup.client.RegistrationClient: Creating RegistrationClient");
           this.server = server;
           this.record = record;
       }else{

           LOG.info("net.es.lookup.client.RegistrationClient: Error creating RegistrationClient. Did not find server");
           throw new LSClientException("Error creating RegistrationClient. Did not find server");
       }

    }

    public SimpleLS getServer() {

        return server;
    }

    public Record getRecord() {

        return record;
    }

    public synchronized void setRecord(Record record) throws LSClientException {

        LOG.info("net.es.lookup.client.RegistrationClient: Setting Record");
        if(record !=null){

            this.record = record;
        }else{

            LOG.info("net.es.lookup.client.RegistrationClient: Error setting Record. Found null values");
            throw new LSClientException("Error setting Record. Found null values");
        }
    }

    public String getRelativeUrl() {

        return relativeUrl;
    }

    public void setRelativeUrl(String relativeUrl) throws LSClientException {

        LOG.info("net.es.lookup.client.RegistrationClient: Setting relative URL");
        if(relativeUrl != null){

            this.relativeUrl = relativeUrl;
        } else {

            LOG.info("net.es.lookup.client.RegistrationClient: Error setting relative URL. URL is null");
            throw new LSClientException("URL is null");
        }
    }

    public synchronized Record register() throws ParserException, LSClientException {

        if(record != null){

            LOG.info("net.es.lookup.client.RegistrationClient: Parsing Record elements");
            server.setData(JSONParser.toString(record));

            LOG.info("net.es.lookup.client.RegistrationClient: Setting request parameters");
            server.setConnectionType(connectionType);
            server.setRelativeUrl(relativeUrl);

            LOG.info("net.es.lookup.client.RegistrationClient: Sending request");
            server.send();

            if (server.getResponseCode() == ResponseCodes.SUCCESS) {

                String response = server.getResponse();
                LOG.info("net.es.lookup.client.RegistrationClient: Parsing response");
                Record record = JSONParser.toRecord(response);
                LOG.info("net.es.lookup.client.RegistrationClient: Successful request");
                return record;
            } else {

                ErrorRecord record = new ErrorRecord();
                record.setErrorCode(server.getResponseCode());
                LOG.info("net.es.lookup.client.RegistrationClient: Unsuccessful request with code: " + server.getResponseCode());
                try {

                    record.setErrorMessage(server.getErrorMessage());
                } catch (RecordException e) {

                    LOG.info("net.es.lookup.client.RegistrationClient: Error in response");
                    throw new LSClientException("Error in response");
                }
                return record;
            }
        } else {
            LOG.info("net.es.lookup.client.RegistrationClient: No records to register");
            throw new LSClientException("No records to register");
        }
    }

}
