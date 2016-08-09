package net.es.lookup.loadgen; /**
 * Created by kamala on 6/7/16.
 * Used to create a channel for rabbitmq for the latency checker queue.
 */
import com.rabbitmq.client.Channel;

public class MessageSender extends RMQEndPoint
{
    public static Object lock;
    public static MessageSender object;
    static
    {
        object = new MessageSender();
    }

    public MessageSender()
    {
        super(LoadGenerator.PUBLISHQUEUE);
    }

    public static Channel getChannel()
    {
        Channel newChannel = null;
        synchronized(object)
        {
            try {


                newChannel = object.connection.createChannel();

                //declaring a queue for this channel. If queue does not exist,
                //it will be created on the server.
                newChannel.queueDeclare(LoadGenerator.PUBLISHQUEUE, false, false, false, null);
            }
            catch(Exception e)
            {

            }
        }

        return newChannel;

    }
}
