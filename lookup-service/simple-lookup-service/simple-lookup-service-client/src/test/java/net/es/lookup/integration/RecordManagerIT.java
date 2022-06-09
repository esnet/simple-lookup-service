package net.es.lookup.integration;

import net.es.lookup.client.RecordManager;
import net.es.lookup.common.exception.LSClientException;
import net.es.lookup.common.exception.ParserException;
import net.es.lookup.integration.BaseIT;
import net.es.lookup.protocol.json.JSONParser;
import net.es.lookup.records.ErrorRecord;
import net.es.lookup.records.Record;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created with IntelliJ IDEA.
 * User: student5
 * Date: 6/25/13
 * Time: 4:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class RecordManagerIT extends BaseIT {

    @Test
    public void renewRecord() {

        System.out.println("Testing renewal of an existing record");

        Record rec = null;
        int tmp = rand.nextInt();
        registrationData = registrationData.replace(registrationData.substring(registrationData.indexOf("unique")), "unique\":[\"" + Integer.toString(tmp) + "\"]}");

        try {

            regLS.setData(registrationData);
            regLS.send();
            String response = regLS.getResponse();

            if (regLS.getResponseCode() == 403) {
                fail("Test record is duplicate of existing record");
            }

            if (regLS.getResponseCode() == 200) {

                int index = (JSONParser.toRecord(response)).getURI().lastIndexOf("/") + 1;
                recordID = (JSONParser.toRecord(response)).getURI().substring(index);
                recordIDs.add(recordID);

                rmc = new RecordManager(simpleLS);
                rmc.setRecordUri(type + "/" + recordID);
                rec = rmc.renew();

            } else {

                System.out.println("Test setup failed with error code " + regLS.getResponseCode() + ". Could not register test record with specified LS.");
                fail(regLS.getErrorMessage());
            }

        } catch (LSClientException e) {

            System.out.println(e.getMessage());
            fail(e.getMessage());
        } catch (ParserException e) {

            System.out.println(e.getMessage());
            fail(e.getMessage());
        }
        System.out.println(simpleLS.getResponseCode() + ": " + simpleLS.getErrorMessage());
        assertTrue("Renew attempt did not return expected Record", rec != null && rec.validate());
    }

    @Test
    public void renewNonExistingRecord() {

        System.out.println("Testing renewal attempt for a non-existing record (should fail)");

        Record rec = null;

        try {

            rmc = new RecordManager(simpleLS);
            rmc.setRecordUri(type + "/bogusID");
            rec = rmc.renew();

        } catch (LSClientException e) {

            System.out.println(e.getMessage());
            fail(e.getMessage());
        } catch (ParserException e) {

            System.out.println(e.getMessage());
            fail(e.getMessage());
        }
        System.out.println(simpleLS.getResponseCode() + ": " + simpleLS.getErrorMessage());
        assertTrue("Renew attempt did not return expected ErrorRecord", rec != null && rec instanceof ErrorRecord);
    }

    @Test
    public void getExistingRecord() {

        System.out.println("Testing retrieval of an existing record");

        Record rec = null;
        int tmp = rand.nextInt();
        registrationData = registrationData.replace(registrationData.substring(registrationData.indexOf("unique")), "unique\":[\"" + Integer.toString(tmp) + "\"]}");

        try {

            regLS.setData(registrationData);
            regLS.send();
            String response = regLS.getResponse();

            if (regLS.getResponseCode() == 403) {
                fail("Test record is duplicate of existing record");
            }

            if (regLS.getResponseCode() == 200) {

                int index = (JSONParser.toRecord(response)).getURI().lastIndexOf("/") + 1;
                recordID = (JSONParser.toRecord(response)).getURI().substring(index);
                recordIDs.add(recordID);

                rmc = new RecordManager(simpleLS);
                rmc.setRecordUri(type + "/" + recordID);
                rec = rmc.getRecord();

            } else {

                System.out.println("Test setup failed with error code " + regLS.getResponseCode() + ". Could not register test record with specified LS.");
                fail(regLS.getErrorMessage());
            }

        } catch (LSClientException e) {

            System.out.println(e.getMessage());
            fail(e.getMessage());
        } catch (ParserException e) {

            System.out.println(e.getMessage());
            fail(e.getMessage());
        }
        System.out.println(simpleLS.getResponseCode() + ": " + simpleLS.getErrorMessage());
        assertTrue("Access attempt did not return expected Record", rec != null && rec.validate());
    }

    @Test
    public void getNonExistingRecord() {

        System.out.println("Testing retrieval attempt for a non-existing record (should fail)");

        Record rec = null;

        try {

            rmc = new RecordManager(simpleLS);
            rmc.setRecordUri(type + "/bogusID");
            rec = rmc.getRecord();

        } catch (LSClientException e) {

            System.out.println(e.getMessage());
            fail(e.getMessage());
        } catch (ParserException e) {

            System.out.println(e.getMessage());
            fail(e.getMessage());
        }
        System.out.println(simpleLS.getResponseCode() + ": " + simpleLS.getErrorMessage());
        assertTrue("Access attempt did not return expected ErrorRecord", rec != null && rec instanceof ErrorRecord);
    }

    @Test
    public void getExistingRecordWithKeyValuePair() {

        System.out.println("Testing retrieval of a key/value pair in an existing record");

        Record rec = null;
        int tmp = rand.nextInt();
        registrationData = registrationData.replace(registrationData.substring(registrationData.indexOf("unique")), "unique\":[\"" + Integer.toString(tmp) + "\"]}");

        try {

            regLS.setData(registrationData);
            regLS.send();
            String response = regLS.getResponse();

            if (regLS.getResponseCode() == 403) {
                fail("Test record is duplicate of existing record");
            }

            if (regLS.getResponseCode() == 200) {

                int index = (JSONParser.toRecord(response)).getURI().lastIndexOf("/") + 1;
                recordID = (JSONParser.toRecord(response)).getURI().substring(index);
                recordIDs.add(recordID);

                rmc = new RecordManager(simpleLS);
                rmc.setRecordUri(type + "/" + recordID);
                rec = rmc.getKeyValueInRecord(key1);

            } else {

                System.out.println("Test setup failed with error code " + regLS.getResponseCode() + ". Could not register test record with specified LS.");
                fail(regLS.getErrorMessage());
            }

        } catch (LSClientException e) {

            System.out.println(e.getMessage());
            fail(e.getMessage());
        } catch (ParserException e) {

            System.out.println(e.getMessage());
            fail(e.getMessage());
        }
        System.out.println(simpleLS.getResponseCode() + ": " + simpleLS.getErrorMessage());
        assertTrue("Access attempt did not return expected Record", rec != null && rec.validate());
    }

    @Test
    public void getNonExistingKeyValuePair() {

        System.out.println("Testing retrieval of a key/value pair for a non-existing record (should fail)");

        Record rec = null;

        try {

            rmc = new RecordManager(simpleLS);
            rmc.setRecordUri(type + "/bogusID");
            rec = rmc.getKeyValueInRecord(key1);

        } catch (LSClientException e) {

            System.out.println(e.getMessage());
            fail(e.getMessage());
        } catch (ParserException e) {

            System.out.println(e.getMessage());
            fail(e.getMessage());
        }
        System.out.println(simpleLS.getResponseCode() + ": " + simpleLS.getErrorMessage());
        assertTrue("Access attempt did not return expected ErrorRecord", rec != null && rec instanceof ErrorRecord);
    }

    @Test
    public void deleteExistingRecord() {

        System.out.println("Testing deletion of an existing record");

        Record rec = null;
        int tmp = rand.nextInt();
        registrationData = registrationData.replace(registrationData.substring(registrationData.indexOf("unique")), "unique\":[\"" + Integer.toString(tmp) + "\"]}");

        try {

            regLS.setData(registrationData);
            regLS.send();
            String response = regLS.getResponse();

            if (regLS.getResponseCode() == 403) {
                fail("Test record is duplicate of existing record");
            }

            if (regLS.getResponseCode() == 200) {

                int index = (JSONParser.toRecord(response)).getURI().lastIndexOf("/") + 1;
                recordID = (JSONParser.toRecord(response)).getURI().substring(index);
                recordIDs.add(recordID);

                rmc = new RecordManager(simpleLS);
                rmc.setRecordUri(type + "/" + recordID);
                rec = rmc.delete();

            } else {

                System.out.println("Test setup failed with error code " + regLS.getResponseCode() + ". Could not register test record with specified LS.");
                fail(regLS.getErrorMessage());
            }

        } catch (LSClientException e) {

            System.out.println(e.getMessage());
            fail(e.getMessage());
        } catch (ParserException e) {

            System.out.println(e.getMessage());
            fail(e.getMessage());
        }
        System.out.println(simpleLS.getResponseCode() + ": " + simpleLS.getErrorMessage());
        assertTrue("Delete attempt did not return expected Record", rec != null && rec.validate());
    }

    @Test
    public void deleteNonExistingRecord() {

        System.out.println("Testing delete attempt for a non-existing record (should fail)");

        Record rec = null;

        try {

            rmc = new RecordManager(simpleLS);
            rmc.setRecordUri(type + "/bogusID");
            rec = rmc.delete();

        } catch (LSClientException e) {

            System.out.println(e.getMessage());
            fail(e.getMessage());
        } catch (ParserException e) {

            System.out.println(e.getMessage());
            fail(e.getMessage());
        }
        System.out.println(simpleLS.getResponseCode() + ": " + simpleLS.getErrorMessage());
        assertTrue("Delete attempt did not return expected ErrorRecord", rec != null && rec instanceof ErrorRecord);
    }
}


