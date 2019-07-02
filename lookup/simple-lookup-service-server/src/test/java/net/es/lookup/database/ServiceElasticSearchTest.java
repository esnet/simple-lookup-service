package net.es.lookup.database;

import net.es.lookup.common.Message;
import net.es.lookup.common.exception.internal.DuplicateEntryException;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.UUID;

import static org.junit.Assert.fail;

public class ServiceElasticSearchTest {
    ServiceElasticSearch client;

    @Before
    public void setUp() throws Exception {
        client = new ServiceElasticSearch("127.0.0.1", 9200, 9201, "post");
        client.deleteAllRecords();
    }

    @After
    public void tearDown() throws Exception {
        client.closeConnection();
    }

    //@Test
    private void queryAndPublishService() throws IOException, DuplicateEntryException {
        Message message = new Message();
        message.add("type", "test");

        String uuid = UUID.randomUUID().toString();
        message.add("uri", "2"); // 2nd param should be uuid but for testing purposes was assigned a number

        message.add("test-id", String.valueOf(1));

        message.add("ttl", "PT10M");

        DateTime dateTime = new DateTime();
        message.add("expires", dateTime.toString());

        Message addedMessage = client.queryAndPublishService(message);
        //System.out.println(addedMessage.getMap().toString());
    }

    @Test
    public void queryAndPublishSingle() throws IOException, DuplicateEntryException {
        //client.deleteAllRecords();
        queryAndPublishService();
    }

    @Test
    public void queryAndPublishExists() {
        boolean checkSecond = false;
        try {
            //client.deleteAllRecords();
            queryAndPublishService();
            checkSecond = true;
            Thread.sleep(1000);
            queryAndPublishService();
            fail();
        } catch (DuplicateEntryException e) {
            if (checkSecond) {
                System.out.println("Duplicate entry detected. Test passed");
            } else {
                System.out.println("entry already exists before test");
                fail();
            }
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}