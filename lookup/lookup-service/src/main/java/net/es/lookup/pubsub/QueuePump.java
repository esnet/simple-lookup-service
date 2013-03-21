package net.es.lookup.pubsub;

import net.es.lookup.common.Message;
import net.es.lookup.common.exception.internal.QueryException;
import net.es.lookup.common.exception.internal.QueueException;

import java.util.List;

/**
 * Author: sowmya
 * Date: 3/19/13
 * Time: 1:33 PM
 */
public interface QueuePump {

    public void fillQueues(List<Message> messageList) throws QueueException, QueryException;


}
