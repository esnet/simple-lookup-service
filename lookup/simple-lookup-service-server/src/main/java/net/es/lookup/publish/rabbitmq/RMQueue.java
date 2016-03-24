package net.es.lookup.publish.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import net.es.lookup.common.Message;
import net.es.lookup.common.exception.internal.DataFormatException;
import net.es.lookup.common.exception.internal.PubSubQueueException;
import net.es.lookup.protocol.json.JSONMessage;
import net.es.lookup.publish.Queue;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * Author: sowmya
 * Date: 3/3/16
 * Time: 11:17 AM
 */
public class RMQueue extends Queue {

    private ConnectionFactory factory;
    private Connection connection;

    /**
     * Default constructor with max Events=25 and timeInterval between push set to 60s
     *
     * @throws PubSubQueueException
     */

    public RMQueue() throws PubSubQueueException {

        this("localhost", 25, 60);
    }

    public RMQueue(String host, int maxPushEvents, long timeInterval) throws PubSubQueueException {

        factory = new ConnectionFactory();
        factory.setHost(host);

        this.setMaxPushEvents(maxPushEvents);
        this.setTimeInterval(timeInterval);

        this.setLastPushed(new Date());

        try {
            connection = factory.newConnection();
        } catch (IOException e) {
            throw new PubSubQueueException("net.es.lookup.publish.rabbitmq.RMQueue.RMQueue()"+e.getMessage());
        } catch (TimeoutException e) {
            throw new PubSubQueueException("net.es.lookup.publish.rabbitmq.RMQueue.RMQueue()"+e.getMessage());
        }

    }


    @Override
    public void push(List<Message> messages) throws PubSubQueueException {

        //TODO: get list of message and push them to queue

    }

    public void push(Message message) throws PubSubQueueException {

        if(!connection.isOpen()) {
            try {
                connection = factory.newConnection();
            } catch (IOException e) {
                throw new PubSubQueueException("net.es.lookup.publish.rabbitmq.RMQueue.RMQueue()"+e.getMessage());
            } catch (TimeoutException e) {
                throw new PubSubQueueException("net.es.lookup.publish.rabbitmq.RMQueue.RMQueue()"+e.getMessage());
            }

        }

        Channel channel = null;
        try {
            channel = connection.createChannel();

        } catch (IOException e) {
            throw new PubSubQueueException("net.es.lookup.publish.rabbitmq.RMQueue.push - Error closing connection"+e.getMessage());
        }

        if (channel != null) {
            try {
                channel.exchangeDeclare("sls_exchange", "direct");
            } catch (IOException e) {
                throw new PubSubQueueException("net.es.lookup.publish.rabbitmq.RMQueue.push - Error creating exchange"+e.getMessage());
            }
        }
        //String message = "Hello";

        //JSONObject jsonObject = new JSONObject();
        //jsonObject.accumulateAll(message.getMap());
        //jsonObject.put("timestamp",(new Date()).toString());
        String jsonMessage="";

        try {
            message.add("timestamp",(new Date()).toString());
            jsonMessage = JSONMessage.toString(message);
        } catch (DataFormatException e) {
            e.printStackTrace();
        }

        String rmqmessage= jsonMessage.toString();
        if (channel != null) {
            try {
                channel.basicPublish("sls_exchange", "all", null, rmqmessage.getBytes());
            } catch (IOException e) {
                throw new PubSubQueueException("net.es.lookup.publish.rabbitmq.RMQueue.push - Error publishing messages"+e.getMessage());
            }
        }

        try {
            channel.close();

        } catch (IOException e) {
            throw new PubSubQueueException("net.es.lookup.publish.rabbitmq.RMQueue.push - Error closing connection"+e.getMessage());
        } catch (TimeoutException e) {
            throw new PubSubQueueException("net.es.lookup.publish.rabbitmq.RMQueue.push - Error closing connection"+e.getMessage());
        }


    }


    public void push(String message) throws PubSubQueueException {

        if(!connection.isOpen()) {
            try {
                connection = factory.newConnection();
            } catch (IOException e) {
                throw new PubSubQueueException("net.es.lookup.publish.rabbitmq.RMQueue.RMQueue()"+e.getMessage());
            } catch (TimeoutException e) {
                throw new PubSubQueueException("net.es.lookup.publish.rabbitmq.RMQueue.RMQueue()"+e.getMessage());
            }

        }

        Channel channel = null;
        try {
            channel = connection.createChannel();

        } catch (IOException e) {
            throw new PubSubQueueException("net.es.lookup.publish.rabbitmq.RMQueue.push - Error closing connection"+e.getMessage());
        }

        if (channel != null) {
            try {
                channel.exchangeDeclare("sls_exchange", "direct");
            } catch (IOException e) {
                throw new PubSubQueueException("net.es.lookup.publish.rabbitmq.RMQueue.push - Error creating exchange"+e.getMessage());
            }
        }

        if (channel != null) {
            try {
                channel.basicPublish("sls_exchange", "all", null, message.getBytes());
            } catch (IOException e) {
                throw new PubSubQueueException("net.es.lookup.publish.rabbitmq.RMQueue.push - Error publishing messages"+e.getMessage());
            }
        }

        try {
            channel.close();

        } catch (IOException e) {
            throw new PubSubQueueException("net.es.lookup.publish.rabbitmq.RMQueue.push - Error closing connection"+e.getMessage());
        } catch (TimeoutException e) {
            throw new PubSubQueueException("net.es.lookup.publish.rabbitmq.RMQueue.push - Error closing connection"+e.getMessage());
        }


    }


    @Override
    public String getQid() {

        return null;
    }

    @Override
    public String getQAccessPoint() {

        return null;
    }
}
