package net.es.lookup.cache.service;

import net.es.lookup.cache.agent.Destination;
import net.es.lookup.cache.subscriber.SLSSubscriber;
import net.es.lookup.cache.subscriber.Subscriber;
import net.es.lookup.utils.config.reader.SubscriberConfigReader;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Author: sowmya
 * Date: 3/29/16
 * Time: 3:05 PM
 */
public class Invoker {

    private static SubscriberConfigReader subscriberConfigReader;

    private static String subscriberConfigFile = "etc/subscriber.yml";

    private static String logConfig = "./etc/log4j.properties";

    public static void main(String[] args) throws Exception{

        System.setProperty("log4j.configuration", "file:" + logConfig);

        SubscriberConfigReader.init(subscriberConfigFile);
        subscriberConfigReader = SubscriberConfigReader.getInstance();



        List<Map> queues = subscriberConfigReader.getQueues();
        List<Map> destInConfig= subscriberConfigReader.getDestination();

        List<Subscriber> subscribers = new ArrayList<Subscriber>();

        for(Map queue:queues){

            List<Destination> destinations= new ArrayList<Destination>();
            System.out.println("Initializing subscriber...");

            String host = (String)queue.get(SubscriberConfigReader.QUEUE_HOST);
            System.out.println(host);
            int port=(Integer)queue.get(SubscriberConfigReader.QUEUE_PORT);
            System.out.println(port);
            String exchangeName = (String) queue.get(SubscriberConfigReader.EXCHANGE_NAME);
            System.out.println(exchangeName);

            String userName = (String) queue.get(SubscriberConfigReader.USERNAME);
            System.out.println(userName);
            String password = (String) queue.get(SubscriberConfigReader.PASSWORD);
            System.out.println(password);
            String vhost= (String) queue.get(SubscriberConfigReader.VHOST);
            System.out.println(vhost);
            List<String> queries = (List<String>) queue.get(SubscriberConfigReader.QUERIES);
            for(String s : queries){
                System.out.println(s);
            }

            for (Map dest: destInConfig){

                String dType = (String) dest.get(SubscriberConfigReader.DESTINATION_TYPE);
                System.out.println(dType);
                String dUrl = (String) dest.get(SubscriberConfigReader.DESTINATION_URL);
                System.out.println(dUrl);

                URI destinationAsUrl = new URI(dUrl);

                Destination destination = new Destination(destinationAsUrl,dType);
                destinations.add(destination);


                Subscriber subscriber = new SLSSubscriber(host,port,userName,password,vhost,queries,exchangeName, SLSSubscriber.QueueType.TOPIC,destinations);
                subscriber.init();

                subscriber.start();
                subscribers.add(subscriber);

            }

        }
        System.out.println("Initialized subscriber...");
        Object blockingObject = new Object();
        synchronized (blockingObject){
            blockingObject.wait();
        }
    }

}
