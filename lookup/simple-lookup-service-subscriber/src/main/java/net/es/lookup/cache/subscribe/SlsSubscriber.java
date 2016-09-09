package net.es.lookup.cache.subscribe;

import com.rabbitmq.client.*;
import net.es.lookup.cache.dispatch.DispatchService;
import net.es.lookup.cache.dispatch.EndPoint;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * Author: sowmya
 * Date: 8/17/16
 * Time: 3:31 PM
 *
 * This class defines the  sLS subscriber. It implements the Subscriber interface.
 *
 */
public class SlsSubscriber implements Subscriber {

    //General subscriber fields
    private String host;
    private int port;
    private List<String> queries;
    private List<EndPoint> endpoints;
    private static Logger LOG = Logger.getLogger(SlsSubscriber.class);

    //RMQ Connection fields
    private Connection connection;
    private Channel channel;

    //RMQ Exchange fields
    private String exchangeName;
    private String rmqExchangeType;
    private QueueType queueType;
    private String queueName;
    private String userName;
    private String password;
    private String vhost;

    //Enum to define the different Rabbitmq queue types
    public enum QueueType{
        DIRECT, TOPIC, HEADERS, FANOUT;
    }

    /**
     * Returns the rabbitmq connection
     * */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Returns the rabbitmq channel. Channel is a virtual connection on the Rabbitmq connection.
     * Channel provides isolation.
     * Multiple channels can exist on a connection.
     * */
    public Channel getChannel() {
        return channel;
    }

    /**
     * Returns the rabbitmq exchange name
     * */
    public String getExchangeName() {
        return exchangeName;
    }

    /**
     * Returns the rabbitmq exchange type
     * */
    public String getRmqExchangeType() {
        return rmqExchangeType;
    }

    /**
     * Returns the rabbitmq queue type
     * */
    public QueueType getQueueType() {
        return queueType;
    }

    /**
     * Returns the rabbitmq queue name
     * */
    public String getQueueName() {
        return queueName;
    }

    /**
     * This constructor gets the general subscriber parameters and the rabbitmq parameters and creates the
     * SlsSubscriber instance out of it
     * */
    public SlsSubscriber(String host, int port, String username, String password, String vhost, List<String> queries, String exchangeName, QueueType queueType, List<EndPoint> endpoints) {
        this.host= host;
        this.port=port;
        this.queries = queries;
        this.exchangeName = exchangeName;
        this.queueType = queueType;
        this.userName = username;
        this.password = password;
        this.vhost = vhost;
        this.endpoints = endpoints;
    }


    /**
     * Setter method to set the rabbitmq exhange according to queuetype
     * */
    private void setRmqExchange() {

        switch (queueType) {
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

    /**
     * Returns the rabbitmq host
     * */
    public String getHost() {
        return host;
    }

    /**
     * Returns the rabbitmq port
     * */
    public int getPort() {
        return port;
    }

    /**
     * Returns the list of queries subscribed.
     * */
    public List<String> getQueries() {
        return queries;
    }


   /**
    * This method initializes the rabbitmq connection
    * */
    @Override
    public void init() {

        LOG.debug(this.getClass().getCanonicalName()+": Initializing SLS subscriber");

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(this.getHost());
        factory.setPort(this.getPort());
        factory.setUsername(this.userName);
        factory.setPassword(this.password);
        factory.setVirtualHost(this.vhost);

        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            setRmqExchange();
        } catch (TimeoutException e) {
            LOG.error(this.getClass().getCanonicalName()+": Error initializing subscriber"+e.getMessage());
        } catch (IOException e) {
            LOG.error(this.getClass().getCanonicalName()+": Error initializing subscriber"+e.getMessage());
        }
        LOG.debug(this.getClass().getCanonicalName()+": Initialized SLS subscriber");
    }


    /**
     * This method starts the rabbitmq connection and binds a queue to the consumer. The init() method must
     * be called before this method is called.
     *
     * */
    @Override
    public void start() throws SubscriberException {
        if(connection == null || channel == null){
            throw new SubscriberException("cannot start without initializing");
        }

        LOG.debug(this.getClass().getCanonicalName()+": Starting SLS Subscriber");
        try {
            channel.exchangeDeclare(exchangeName, rmqExchangeType);
            queueName = channel.queueDeclare().getQueue();

            List<String> queries = getQueries();
            for(String q:queries) {
                channel.queueBind(queueName, exchangeName, q);

            }

            LOG.info(this.getClass().getCanonicalName()+": Starting to listen"+queueName);

            channel.basicConsume(queueName, true, "", this);
        } catch (IOException e) {
            LOG.error(this.getClass().getCanonicalName()+": Error starting subscriber"+e.getMessage());
        }

    }

    /**
     * This method terminates the rabbitmq connection
     * */
    @Override
    public void stop() {

        try {
            channel.close();
            connection.close();
        } catch (IOException e) {
            LOG.error(this.getClass().getCanonicalName()+": Error stopping subscriber"+e.getMessage());
        } catch (TimeoutException e) {
            LOG.error(this.getClass().getCanonicalName()+": Error stopping subscriber"+e.getMessage());
        }
    }


    /**
     * The following methods are defined by the Consumer class.
     * */
    @Override
    public void handleConsumeOk(String consumerTag) {
        LOG.info(this.getClass().getCanonicalName()+": Subscriber connected");
    }

    @Override
    public void handleCancelOk(String consumerTag) {
        LOG.info(this.getClass().getCanonicalName()+": Subscriber disconnected gracefully");
    }

    @Override
    public void handleCancel(String consumerTag) throws IOException {
        LOG.info(this.getClass().getCanonicalName()+": Subscriber disconnected");
    }

    @Override
    public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
        LOG.info(this.getClass().getCanonicalName()+": Subscriber received shutdown signal");
    }

    @Override
    public void handleRecoverOk(String consumerTag) {
        LOG.info(this.getClass().getCanonicalName()+": Subscriber reconnected");
    }


    /**
     * This method defines how to handle a delivered message. The message is handed over to DispatchService and is forwarded
     * to the endpoint
     * */
    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

        try {

            String message = new String(body, "UTF-8");
            LOG.debug(this.getClass().getCanonicalName()+" [x] Received '" + envelope.getRoutingKey() + "':'" + message + "'");

            JSONObject jsonMessage = JSONObject.fromObject(message);
            LOG.debug(this.getClass().getCanonicalName()+": JSON Message: "+jsonMessage);

            DispatchService dispatchService = DispatchService.getInstance();

            for (EndPoint endpoint : endpoints) {
                dispatchService.schedule(endpoint, jsonMessage);
            }
        } catch (UnsupportedEncodingException e) {
           LOG.error(this.getClass().getCanonicalName()+": "+e.getMessage());
        }
    }

}
