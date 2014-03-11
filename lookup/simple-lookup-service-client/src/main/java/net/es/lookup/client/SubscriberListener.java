package net.es.lookup.client;

import net.es.lookup.common.exception.LSClientException;
import net.es.lookup.records.Record;

/**
 * Author: sowmya
 * Date: 4/24/13
 * Time: 4:49 PM
 */
public interface SubscriberListener {
    public void onRecord(Record record) throws LSClientException;
}
