package net.es.lookup.cache.service;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.es.lookup.cache.dispatch.EndPoint;
import net.es.lookup.cache.elastic.ElasticEndPoint;
import net.es.lookup.cache.subscribe.SlsSubscriber;
import net.es.lookup.cache.subscribe.Subscriber;
import net.es.lookup.common.ReservedValues;
import net.es.lookup.utils.config.reader.IndexMapReader;
import net.es.lookup.utils.config.reader.SubscriberConfigReader;
import net.sf.json.JSONObject;

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


    public static void main(String[] args) throws Exception {

        parseArgs(args);

        System.setProperty("log4j.configuration", "file:" + logConfig);

        SubscriberConfigReader.init(configPath + "/" + subscriberConfigFile);
        subscriberConfigReader = SubscriberConfigReader.getInstance();

        String mappingFileLocation = configPath + "/" + mappingFile;
        indexMapReader = IndexMapReader.getInstance();
        String indexMapping = indexMapReader.readMapping(mappingFileLocation);
        JSONObject jsonIndexMapping = JSONObject.fromObject(indexMapping);


        List<Map> queues = subscriberConfigReader.getQueues();
        List<Map> destInConfig = subscriberConfigReader.getDestination();

        List<Subscriber> subscribers = new ArrayList<Subscriber>();

        for (Map queue : queues) {

            List<EndPoint> endPoints = new ArrayList<EndPoint>();
            System.out.println("Initializing subscriber...");

            String host = (String) queue.get(SubscriberConfigReader.QUEUE_HOST);
            int port = (Integer) queue.get(SubscriberConfigReader.QUEUE_PORT);
            String exchangeName = (String) queue.get(SubscriberConfigReader.EXCHANGE_NAME);
            String exchangeType = (String) queue.get(SubscriberConfigReader.EXCHANGE_TYPE);

            String userName = (String) queue.get(SubscriberConfigReader.USERNAME);
            String password = (String) queue.get(SubscriberConfigReader.PASSWORD);
            String vhost = (String) queue.get(SubscriberConfigReader.VHOST);

            String queueName = (String) queue.get(SubscriberConfigReader.QUEUE_NAME);
            boolean queueDurable = (Boolean) queue.get(SubscriberConfigReader.QUEUE_DURABILITY);
            boolean queueExclusive = (Boolean) queue.get(SubscriberConfigReader.QUEUE_EXCLUSIVE);
            boolean queueAutoDelete = (Boolean) queue.get(SubscriberConfigReader.QUEUE_AUTODELETE);

            List<String> queries = (List<String>) queue.get(SubscriberConfigReader.QUERIES);

            for (Map dest : destInConfig) {

                String dType = (String) dest.get(SubscriberConfigReader.DESTINATION_TYPE);
                String dUrl = (String) dest.get(SubscriberConfigReader.DESTINATION_URL);
                System.out.println(dType);

                URI destinationAsUrl = new URI(dUrl);
                if (dType.equals(ReservedValues.RECORD_SUBSCRIBER_ENDPOINT_ELASTIC)) {
                    String dWriteIndex = (String) dest.get(SubscriberConfigReader.DESTINATION_ELASTIC_WRITEINDEX);
                    String dSearchIndex = (String) dest.get(SubscriberConfigReader.DESTINATION_ELASTIC_SEARCHINDEX);
                    String dDocumentType = (String) dest.get(SubscriberConfigReader.DESTINATION_ELASTIC_DOCUMENTTYPE);
                    System.out.println(dSearchIndex);

                    ElasticEndPoint endPoint = new ElasticEndPoint(destinationAsUrl, jsonIndexMapping);
                    endPoint.setWriteIndex(dWriteIndex);
                    endPoint.setSearchIndex(dSearchIndex);
                    endPoint.setDocumentType(dDocumentType);
                    endPoints.add(endPoint);
                }
            }

            SlsSubscriber subscriber = new SlsSubscriber();
            subscriber.setHost(host);
            subscriber.setPort(port);

            subscriber.setUserName(userName);
            subscriber.setPassword(password);
            subscriber.setVhost(vhost);

            subscriber.setExchangeName(exchangeName);
            subscriber.setExchangeType(exchangeType);

            subscriber.setQueueName(queueName);
            subscriber.setQueueExclusive(queueExclusive);
            subscriber.setQueueDurability(queueDurable);
            subscriber.setQueueAutoDelete(queueAutoDelete);

            subscriber.setQueries(queries);
            subscriber.setEndpoints(endPoints);

            subscriber.init();

            subscriber.start();
            subscribers.add(subscriber);


        }
        System.out.println("Initialized subscriber...");
        Object blockingObject = new Object();
        synchronized (blockingObject) {
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
