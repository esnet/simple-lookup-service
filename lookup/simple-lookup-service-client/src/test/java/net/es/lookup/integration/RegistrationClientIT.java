package net.es.lookup.integration;

import net.es.lookup.client.RegistrationClient;
import net.es.lookup.common.exception.LSClientException;
import net.es.lookup.common.exception.ParserException;
import net.es.lookup.integration.BaseIT;
import net.es.lookup.protocol.json.JSONParser;
import net.es.lookup.records.ErrorRecord;
import net.es.lookup.records.Record;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.fail;

/**
 * Created with IntelliJ IDEA.
 * User: student5
 * Date: 6/5/13
 * Time: 3:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class RegistrationClientIT extends BaseIT {

    private final int NUM_THREADS = 2;
    private final int NUM_RECORDS = 10;

    @Test
    public void registerRecord() throws InterruptedException {


        System.out.println("Testing record registration");

        long start = new Date().getTime();

        String id = UUID.randomUUID().toString();

        try {
            Record rec;
            rec = JSONParser.toRecord(registrationData);
            rec.add("unique-id", id);


            rc = new RegistrationClient(regLS, rec);
            registrationResult = rc.register();

            if (registrationResult instanceof ErrorRecord) {

                fail(regLS.getResponseCode() + ":" + regLS.getErrorMessage());
            } else if (registrationResult instanceof Record) {

                int index = ((Record) registrationResult).getURI().lastIndexOf("/") + 1;
                recordID = ((Record) registrationResult).getURI().substring(index);
                recordIDs.add(recordID);
            }

        } catch (LSClientException e) {

            System.out.println(e.getMessage());
            fail(e.getMessage());
        } catch (ParserException e) {

            System.out.println(e.getMessage());
            fail(e.getMessage());
        }
        Assert.assertTrue(((Record) registrationResult).validate() && regLS.getResponseCode() == 200);


        long end = new Date().getTime();
        long time = end - start;
        System.out.println(time);

    }
}