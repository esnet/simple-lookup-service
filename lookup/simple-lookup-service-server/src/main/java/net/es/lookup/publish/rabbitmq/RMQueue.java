package net.es.lookup.publish.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import net.es.lookup.common.Message;
import net.es.lookup.common.exception.internal.DataFormatException;
import net.es.lookup.common.exception.internal.PubSubQueueException;
import net.es.lookup.protocol.json.JSONMessage;
import net.es.lookup.publish.Queue;
import org.apache.log4j.Logger;

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

    private String exchangeName;
    private String exchangeType;
    private boolean exchangeDurability;

    public static final String QUERY = "all";

    private static Logger LOG = Logger.getLogger(RMQueue.class);

    /**
     * Default constructor with max Events=25 and timeInterval between push set to 60s
     *
     * @throws PubSubQueueException
     */

    public RMQueue() throws PubSubQueueException {

        this("localhost", 5672,"guest","","/", "test_exchange","direct",false);
    }

    public RMQueue(String host, int port, String username, String password, String vhost, String exchangeName, String exchangeType, boolean exchangeDurability) throws PubSubQueueException {

        factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setVirtualHost(vhost);

        this.exchangeName = exchangeName;
        this.exchangeType = exchangeType;
        this.exchangeDurability = exchangeDurability;



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
                channel.exchangeDeclare(exchangeName, exchangeType, exchangeDurability);
            } catch (IOException e) {
                LOG.debug(this.getClass().getName()+" Error creating channel"+e.getMessage());
                throw new PubSubQueueException(this.getClass().getName()+" - Error creating exchange"+e.getMessage());
            }
        }
        String jsonMessage="";

        try {
            message.add("timestamp",(new Date()).toString());
            jsonMessage = JSONMessage.toString(message);
        } catch (DataFormatException e) {
            throw new PubSubQueueException("net.es.lookup.publish.rabbitmq.RMQueue.push - Error creating exchange"+e.getMessage());
        }

        String rmqmessage= jsonMessage.toString();
        if (channel != null) {
            try {

                channel.basicPublish(exchangeName, QUERY, null, rmqmessage.getBytes());
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

                channel.exchangeDeclare(exchangeName, exchangeType);
            } catch (IOException e) {
                throw new PubSubQueueException("net.es.lookup.publish.rabbitmq.RMQueue.push - Error creating exchange"+e.getMessage());
            }
        }

        if (channel != null) {
            try {
                channel.basicPublish(exchangeName, QUERY, null, message.getBytes());
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
