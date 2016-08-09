package net.es.lookup.loadgen;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * Represents a connection with a queue
 * @author Kamal
 */
public abstract class RMQEndPoint
{

    protected Channel channel;
    protected Connection connection;
    protected String endPointName;

    public RMQEndPoint()
    {

    }

    public RMQEndPoint(String endpointName)
    {
        this.endPointName = endpointName;

        //Create a connection factory
        ConnectionFactory factory = new ConnectionFactory();

        //hostname of your rabbitmq server
        factory.setHost("localhost");
        factory.setUsername(ConnectionFactory.DEFAULT_USER);
        factory.setPassword(ConnectionFactory.DEFAULT_PASS);
        factory.setVirtualHost(ConnectionFactory.DEFAULT_VHOST);


        //getting a connection
        try
        {
            connection = factory.newConnection();

            //creating a channel
            channel = connection.createChannel();

            //declaring a queue for this channel. If queue does not exist,
            //it will be created on the server.
            channel.queueDeclare(endpointName, false, false, false, null);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.err.println("Error in creating RMQ End Point");
            System.err.println("Exiting....");
            System.exit(-1);

        }
    }


    /**
     * Close channel and connection. Not necessary as it happens implicitly any way. 
     * @throws IOException
     */
    public void close() throws Exception
    {
        this.channel.close();
        this.connection.close();
    }
}