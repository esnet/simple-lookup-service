package net.es.lookup.client;

import net.es.lookup.common.ResponseCodes;
import net.es.lookup.common.exception.LSClientException;
import net.es.lookup.common.exception.ParserException;
import net.es.lookup.common.exception.RecordException;
import net.es.lookup.protocol.json.JSONParser;
import net.es.lookup.records.ErrorRecord;
import net.es.lookup.records.Record;

import java.io.StringReader;

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



    public RegistrationClient(SimpleLS server) throws LSClientException {
        this(server, null);
    }

    public RegistrationClient(SimpleLS server, Record record) throws LSClientException {

       if(server != null){
           this.server = server;
           this.record = record;
       }else{
           throw new LSClientException("Error creating Registration client. Did not find server");
       }

    }

    public SimpleLS getServer() {

        return server;
    }

    public Record getRecord() {

        return record;
    }

    public synchronized void setRecord(Record record) throws LSClientException {
        if(record !=null){
            this.record = record;
        }else{
            throw new LSClientException("Error setting Record. Found null values");
        }
    }

    public String getRelativeUrl() {

        return relativeUrl;
    }

    public void setRelativeUrl(String relativeUrl) throws LSClientException {
        if(relativeUrl != null){
            this.relativeUrl = relativeUrl;
        } else {
            throw new LSClientException("URL is null");
        }

    }

    public synchronized Record register() throws ParserException, LSClientException {
        if(record != null){
            server.setData(JSONParser.toString(record));

                server.setConnectionType(connectionType);
            server.setRelativeUrl(relativeUrl);
            server.send();
            if(server.getResponseCode() == ResponseCodes.SUCCESS){
                String response = server.getResponse();

                Record record = JSONParser.toRecord(response);
                return record;
            }else{
                ErrorRecord record = new ErrorRecord();
                record.setErrorCode(server.getResponseCode());
                try {
                    record.setErrorMessage(server.getErrorMessage());
                } catch (RecordException e) {
                    throw new LSClientException("Error in response");
                }
                return record;
            }


        }else{
            throw new LSClientException("No records to register");
        }
    }

}
