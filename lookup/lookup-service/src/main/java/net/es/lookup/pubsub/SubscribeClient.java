package net.es.lookup.pubsub;

import net.es.lookup.client.SimpleLS;
import net.es.lookup.client.Subscriber;
import net.es.lookup.client.SubscriberListener;
import net.es.lookup.common.Message;
import net.es.lookup.common.ReservedKeys;
import net.es.lookup.common.ReservedValues;
import net.es.lookup.common.exception.LSClientException;
import net.es.lookup.common.exception.ParserException;
import net.es.lookup.common.exception.internal.DatabaseException;
import net.es.lookup.common.exception.internal.DuplicateEntryException;
import net.es.lookup.database.ServiceDAOMongoDb;
import net.es.lookup.queries.Query;
import net.es.lookup.records.Record;

import javax.jms.MessageListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Author: sowmya
 * Date: 4/16/13
 * Time: 6:31 PM
 */
public class SubscribeClient implements SubscriberListener {

    private String url = "";
    private String queue;
    private SimpleLS server;
    private Subscriber s;
    private ServiceDAOMongoDb db = ServiceDAOMongoDb.getInstance();

    public SubscribeClient() throws LSClientException {
        init();
    }

    private void init() throws LSClientException {
        server = new SimpleLS();
        server.connect();
        Query q = new Query();
        s = new Subscriber(server, q);
        s.addListener(this);
        s.startSubscription();
    }

    public void onRecord(Record record) {
        if (record.getRecordState().equals(ReservedValues.RECORD_VALUE_STATE_REGISTER)) {
            Message message = new Message(record.getMap());
            Map<String, Object> keyValues = record.getMap();
            Message operators = new Message();
            Message query = new Message();

            Iterator it = keyValues.entrySet().iterator();

            while (it.hasNext()) {

                Map.Entry<String, Object> pairs = (Map.Entry) it.next();
                operators.add(pairs.getKey(), ReservedValues.RECORD_OPERATOR_ALL);
                query.add(pairs.getKey(), pairs.getValue());

            }

            try {
                db.queryAndPublishService(message, query, operators);
            } catch (DatabaseException e) {
                e.printStackTrace();
            } catch (DuplicateEntryException e) {
                e.printStackTrace();
            }

        }else if(record.getRecordState().equals(ReservedValues.RECORD_VALUE_STATE_RENEW)){
            String recorduri = record.getURI();
            Message message = new Message(record.getMap());
            System.out.println("Got a renew message. Inserting into db");
            try {
                db.updateService(recorduri, message);
            } catch (DatabaseException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }
}
