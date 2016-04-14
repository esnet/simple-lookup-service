package net.es.lookup.cache.subscriber;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import net.es.lookup.cache.agent.Destination;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * Author: sowmya
 * Date: 3/29/16
 * Time: 3:16 PM
 */
public class SLSSubscriber extends Subscriber {

    //RMQ Connection fields
    private Connection connection;
    private Channel channel;

    private static Logger LOG = Logger.getLogger(SLSSubscriber.class);

    //RMQ Exchange fields
    private String exchangeName;
    private String rmqExchangeType;
    private QueueType queueType;
    private String queueName;



    private String userName;
    private String password;
    private String vhost;

    //SLSConsumer
    private SLSConsumer SLSConsumer;

    //Destination

    private List<Destination> destinations;

    public enum QueueType{
        DIRECT, TOPIC, HEADERS, FANOUT;

    }

    public Connection getConnection() {

        return connection;
    }

    public Channel getChannel() {

        return channel;
    }

    public String getExchangeName() {

        return exchangeName;
    }

    public String getRmqExchangeType() {

        return rmqExchangeType;
    }

    public QueueType getQueueType() {

        return queueType;
    }

    public String getQueueName() {

        return queueName;
    }

    public SLSConsumer getSLSConsumer() {

        return SLSConsumer;
    }

    public SLSSubscriber(String host, int port, String username, String password, String vhost, List<String> query, String exchangeName, QueueType queueType, List<Destination> destinations) {
        super(host,port,query);
        this.exchangeName = exchangeName;
        this.queueType = queueType;
        this.destinations = destinations;
        this.userName = username;
        this.password = password;
        this.vhost = vhost;

    }


    private void setRmqExchange(){

        switch(queueType){
            case DIRECT:
                rmqExchangeType = "direct";
                break;
            case TOPIC:
                rmqExchangeType = "topic";
                break;
            case HEADERS:
                rmqExchangeType = "headers";
                break;
            case FANOUT:
                rmqExchangeType = "fanout";
                break;
            default:
                rmqExchangeType = "direct";
                break;
        }


    }

    public void init(){

        LOG.debug("net.es.lookup.cache.subscriber.SLSSubscriber.init: Initializing SLS subscriber");

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(this.getHost());
        factory.setPort(this.getPort());
        factory.setUsername(this.userName);
        factory.setPassword(this.password);
        factory.setVirtualHost(this.vhost);

        try {

            connection = factory.newConnection();
            channel = connection.createChannel();
            SLSConsumer = new SLSConsumer(channel,destinations);

            setRmqExchange();

        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        LOG.debug("net.es.lookup.cache.subscriber.SLSSubscriber.init: Initialized SLS subscriber");

    }

    @Override
    public void start() throws SubscriberException {

        if(connection == null || channel == null){
            throw new SubscriberException("cannot start without initializing");
        }

        LOG.debug("net.es.lookup.cache.subscriber.SLSSubscriber.init: Starting SLS Subscriber");
        try {
            channel.exchangeDeclare(exchangeName, rmqExchangeType);
            queueName = channel.queueDeclare().getQueue();

            List<String> queries = getQueries();
            for(String q:queries) {
                channel.queueBind(queueName, exchangeName, q);

            }

            LOG.info("net.es.lookup.cache.subscriber.SLSSubscriber.init: Starting to listen"+queueName);

            channel.basicConsume(queueName, SLSConsumer);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void stop() {

        try {
            channel.close();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

}
