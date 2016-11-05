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
    private String exchangeType;
    private boolean queueDurability;
    private boolean queueExclusive;
    private boolean queueAutoDelete;
    private String queueName;
    private String userName;
    private String password;
    private String vhost;
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
    public String getExchangeType() {
        return exchangeType;
    }


    /**
     * Returns the rabbitmq queue name
     * */
    public String getQueueName() {
        return queueName;
    }

    /**
     * Default constructor**/
    public SlsSubscriber(){

    }

    /**
     * This constructor gets the general subscriber parameters and the rabbitmq parameters and creates the
     * SlsSubscriber instance out of it
     * */
    public SlsSubscriber(String host, int port, String username, String password, String vhost, List<String> queries, String exchangeName, String exchangeType, String queueName, boolean queueDurability, boolean queueExclusive, boolean queueAutoDelete, List<EndPoint> endpoints) {
        this.host= host;
        this.port=port;
        this.queries = queries;
        this.exchangeName = exchangeName;
        this.exchangeType = exchangeType;
        this.queueDurability = queueDurability;
        this.queueExclusive = queueExclusive;
        this.queueAutoDelete = queueAutoDelete;
        this.queueName = queueName;

        this.userName = username;
        this.password = password;
        this.vhost = vhost;
        this.endpoints = endpoints;
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


    public void setExchangeType(String exchangeType) {

        this.exchangeType = exchangeType;
    }

    /**
     * Returns whether the queue is durable or not.
     * */
    public boolean isQueueDurability() {

        return queueDurability;
    }


    /**
     * Sets the queue durability
     * */
    public void setQueueDurability(boolean queueDurability) {

        this.queueDurability = queueDurability;
    }

    public boolean isQueueExclusive() {

        return queueExclusive;
    }

    public void setQueueExclusive(boolean queueExclusive) {

        this.queueExclusive = queueExclusive;
    }

    public boolean isQueueAutoDelete() {

        return queueAutoDelete;
    }

    public void setQueueAutoDelete(boolean queueAutoDelete) {

        this.queueAutoDelete = queueAutoDelete;
    }

    public void setHost(String host) {

        this.host = host;
    }

    public void setPort(int port) {

        this.port = port;
    }

    public void setQueries(List<String> queries) {

        this.queries = queries;
    }

    public List<EndPoint> getEndpoints() {

        return endpoints;
    }

    public void setEndpoints(List<EndPoint> endpoints) {

        this.endpoints = endpoints;
    }

    public void setExchangeName(String exchangeName) {

        this.exchangeName = exchangeName;
    }

    public void setQueueName(String queueName) {

        this.queueName = queueName;
    }

    public String getUserName() {

        return userName;
    }

    public void setUserName(String userName) {

        this.userName = userName;
    }

    public String getPassword() {

        return password;
    }

    public void setPassword(String password) {

        this.password = password;
    }

    public String getVhost() {

        return vhost;
    }

    public void setVhost(String vhost) {

        this.vhost = vhost;
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
            channel.exchangeDeclare(exchangeName, exchangeType, true);
            channel.queueDeclare(queueName, queueDurability, queueExclusive, queueAutoDelete, null);

            List<String> queries = getQueries();
            for(String q:queries) {
                channel.queueBind(queueName, exchangeName, q);

            }

            LOG.info(this.getClass().getCanonicalName()+": Starting to listen"+queueName);

            channel.basicConsume(queueName, true, "", this);
        } catch (IOException e) {
            e.printStackTrace();
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
