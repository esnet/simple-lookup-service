import net.es.lookup.client.RegistrationClient;
import net.es.lookup.common.exception.LSClientException;
import net.es.lookup.common.exception.ParserException;
import net.es.lookup.protocol.json.JSONParser;
import net.es.lookup.records.ErrorRecord;
import net.es.lookup.records.Record;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: student5
 * Date: 6/5/13
 * Time: 3:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class RegistrationClientTest extends BaseTest {
    private final int NUM_THREADS = 1;
    private final int NUM_RECORDS = 25000;
    @Test
    public void registerRecord() throws InterruptedException {


        System.out.println("Testing record registration");



        Thread threads[] = new Thread[NUM_THREADS];

        for (int j=0;j<NUM_THREADS;j++){
            System.out.println(j);
            rand.setSeed(j);

            threads[j] = new Thread(){

                public void run(){
                    //rand.setSeed(new Random().nextLong()*10);

                    for(int i=0; i<NUM_RECORDS;i++){

                        int tmp = rand.nextInt();
                       // System.out.println(tmp);
                        int tmp1 = rand.nextInt();
                        //registrationData = registrationData.replace(registrationData.substring(registrationData.indexOf("unique")), "unique\":[\"" + Integer.toString(tmp) + Integer.toString(tmp1)+ "\"]}");


                        try {
                            Record rec;
                            rec = JSONParser.toRecord(registrationData);
                            List<String> randomval = new ArrayList<String>();
                            randomval.add(UUID.randomUUID().toString());
                            rec.add("random", randomval);

                            rc = new RegistrationClient(regLS, rec);
                            registrationResult = rc.register();

                            if (registrationResult instanceof ErrorRecord) {

                                Assert.fail(regLS.getResponseCode()+":"+regLS.getErrorMessage());
                            } else if (registrationResult instanceof Record) {

                                int index = ((Record) registrationResult).getURI().lastIndexOf("/") + 1;
                                recordID = ((Record) registrationResult).getURI().substring(index);
                                recordIDs.add(recordID);
                            }

                        } catch (LSClientException e) {

                            System.out.println(e.getMessage());
                            Assert.fail(e.getMessage());
                        } catch (ParserException e) {

                            System.out.println(e.getMessage());
                            Assert.fail(e.getMessage());
                        }
                        //System.out.println(regLS.getResponseCode() + ": " + regLS.getErrorMessage());
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