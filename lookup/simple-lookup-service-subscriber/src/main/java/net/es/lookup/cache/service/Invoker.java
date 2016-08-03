package net.es.lookup.cache.service;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.es.lookup.cache.agent.Destination;
import net.es.lookup.cache.subscriber.SLSSubscriber;
import net.es.lookup.cache.subscriber.Subscriber;
import net.es.lookup.utils.config.reader.IndexMapReader;
import net.es.lookup.utils.config.reader.SubscriberConfigReader;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

/**
 * Author: sowmya
 * Date: 3/29/16
 * Time: 3:05 PM
 */
public class Invoker {

    private static SubscriberConfigReader subscriberConfigReader;

    private static IndexMapReader indexMapReader;

    private static String configPath = "etc";

    private static String subscriberConfigFile = "subscriber.yaml";
    private static String mappingFile = "indexmapping.json";

    private static String logConfig = "./etc/log4j.properties";


    public static void main(String[] args) throws Exception{

        parseArgs(args);

        System.setProperty("log4j.configuration", "file:" + logConfig);

        SubscriberConfigReader.init(configPath+"/"+subscriberConfigFile);
        subscriberConfigReader = SubscriberConfigReader.getInstance();


        String mappingFileLocation = configPath+"/"+mappingFile;
        indexMapReader = IndexMapReader.getInstance();
        String indexMapping = indexMapReader.readMapping(mappingFileLocation);



        List<Map> queues = subscriberConfigReader.getQueues();
        List<Map> destInConfig= subscriberConfigReader.getDestination();

        List<Subscriber> subscribers = new ArrayList<Subscriber>();

        for(Map queue:queues){

            List<Destination> destinations= new ArrayList<Destination>();
            System.out.println("Initializing subscriber...");

            String host = (String)queue.get(SubscriberConfigReader.QUEUE_HOST);
            int port=(Integer)queue.get(SubscriberConfigReader.QUEUE_PORT);
            String exchangeName = (String) queue.get(SubscriberConfigReader.EXCHANGE_NAME);
            System.out.println(exchangeName);

            String userName = (String) queue.get(SubscriberConfigReader.USERNAME);
            String password = (String) queue.get(SubscriberConfigReader.PASSWORD);
            String vhost= (String) queue.get(SubscriberConfigReader.VHOST);
            System.out.println("Using vhost"+vhost);
            List<String> queries = (List<String>) queue.get(SubscriberConfigReader.QUERIES);
            for(String s : queries){
                System.out.println(s);
            }




            for (Map dest: destInConfig){

                String dType = (String) dest.get(SubscriberConfigReader.DESTINATION_TYPE);
                String dUrl = (String) dest.get(SubscriberConfigReader.DESTINATION_URL);
                System.out.println(dType);



                URI destinationAsUrl = new URI(dUrl);

                Destination destination = new Destination(destinationAsUrl,dType);

                //If it is an elastic destination, initialize mapping.
                if(dType.equals(Destination.DESTINATION_ELASTIC)){
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost mapRequest = new HttpPost();

                    String index = IndexMapReader.getInstance().getElasticIndex(destinationAsUrl);
                    System.out.println(index);

                    mapRequest.setURI(new URI(index));

                    mapRequest.setHeader("Content-type", "application/json");

                    StringEntity stringEntity = new StringEntity(indexMapping);
                    mapRequest.setEntity(stringEntity);

                    HttpResponse response = httpClient.execute(mapRequest);

                    if(response.getStatusLine().getStatusCode() ==200){
                        System.out.println("Index created for ");
                    }else{
                        System.out.println("Index already exists");
                    }



                }
                System.out.println(dUrl);
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



    public static void parseArgs(String args[]) throws java.io.IOException {

        OptionParser parser = new OptionParser();
        parser.acceptsAll(asList("h", "?"), "show help then exit");
        OptionSpec<String> CONFIG = parser.accepts("c", "configPath").withRequiredArg().ofType(String.class);
        OptionSpec<String> LOGCONFIG = parser.accepts("l", "logConfig").withRequiredArg().ofType(String.class);

        OptionSet options = parser.parse(args);

        // check for help
        if (options.has("?")) {

            parser.printHelpOn(System.out);
            System.exit(0);

        }

        if (options.has(CONFIG)) {

            configPath = options.valueOf(CONFIG);
            System.out.println("Config files Path:" + configPath);

        }

        if (options.has(LOGCONFIG)) {

            logConfig = options.valueOf(LOGCONFIG);

        }

    }

}
