package net.es.lookup.cache.subscriber;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import net.es.lookup.cache.agent.Destination;
import net.es.lookup.cache.service.ForwardingAgentService;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Author: sowmya
 * Date: 3/29/16
 * Time: 10:47 AM
 */
public class SLSConsumer extends DefaultConsumer {



    private List<Destination> destinations;

    private static Logger LOG = Logger.getLogger(SLSSubscriber.class);

    /**
     * Constructs a new instance and records its association to the passed-in channel.
     *
     * @param channel the channel to which this consumer is attached
     * @param destinations
     */
    public SLSConsumer(Channel channel, List<Destination> destinations) {

        super(channel);

        this.destinations = destinations;
    }

    public List<Destination> getDestinationUrls() {

        return destinations;
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope,
                               AMQP.BasicProperties properties, byte[] body){

        try {

            String message = new String(body, "UTF-8");
            LOG.debug(" [x] Received '" + envelope.getRoutingKey() + "':'" + message + "'");

            JSONObject jsonMessage = JSONObject.fromObject(message);

            ForwardingAgentService agentService = ForwardingAgentService.getInstance();

            for (Destination dest: destinations){
                agentService.schedule(dest, jsonMessage);
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


    }
}
