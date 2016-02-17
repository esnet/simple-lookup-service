import net.es.lookup.client.RegistrationClient;
import net.es.lookup.common.exception.LSClientException;
import net.es.lookup.common.exception.ParserException;
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
public class RegistrationClientTest extends BaseTest {
    private final int NUM_THREADS = 4;
    private final int NUM_RECORDS = 5000;
    @Test
    public void registerRecord() throws InterruptedException {


        System.out.println("Testing record registration");

        final int bucket = Math.round(NUM_RECORDS/NUM_THREADS);
        System.out.println("Num records in each thread="+bucket);

        final Thread threads[] = new Thread[NUM_THREADS];

        for (int j=0;j<NUM_THREADS;j++){

            final int start = bucket*j;
            System.out.println(j+"---"+start);

            threads[j] = new Thread(){

                public void run(){

                    for(int i=0; i<bucket;i++){

                        int id = start+i;

                        try {
                            Record rec;
                            rec = JSONParser.toRecord(registrationData);
                            rec.add("unique-id", String.valueOf(id));


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

                    }


                }



            };

        }

        long start = new Date().getTime();

        for (int j=0;j<threads.length;j++){
            threads[j].start();
            Thread.sleep(5000);

        }


        for (int j=0;j<threads.length;j++){
            try {
                threads[j].join();
            } catch (InterruptedException e) {
                System.out.println(e.getStackTrace());
            }

        }

        long end = new Date().getTime();
        long time = end-start;
        System.out.println(time);

    }
}