package net.es.lookup.service;

import static java.util.Arrays.asList;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.es.lookup.database.ServiceDAOMongoDb;
import net.es.lookup.common.Message;
import net.es.lookup.common.Service;
import java.util.List;



public class Invoker {

    private static String port = "8080";
    private static LookupService lookupService = null;
    private static ServiceDAOMongoDb dao = null;

    /**
     * Main program to start the Lookup Service
     * @param args [-h, ?] for help
     *              [-p server-port
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        parseArgs( args );

        System.out.println("starting ServiceDAOMongoDb");
        Invoker.dao = new ServiceDAOMongoDb();

        System.out.println("starting Lookup Service");
        // Create the REST service
        Invoker.lookupService = new LookupService(Integer.parseInt(Invoker.port));
        // Start the service
        Invoker.lookupService.startService();
        
        
        //Insert values
        //Message ms = new Message();
        //ms.add(Message.SERVICE_NAME, "test");
       // ms.add(Message.SERVICE_URI, "http://test");
        //ms.add(Message.SERVICE_DOMAIN, "es.net");
        //Invoker.dao.publishService(ms);
        
        //ms = new Message();
        //ms.add(Message.SERVICE_NAME, "test1");
        //ms.add(Message.SERVICE_URI, "http://test1");
        //ms.add(Message.SERVICE_DOMAIN, "lbl");
        //Invoker.dao.publishService(ms);
        
        //ms = new Message();
        //ms.add(Message.SERVICE_NAME, "test");
        //Invoker.dao.publishService(ms);
        
        //Query test
        Message msg = new Message();
        msg.add(Message.SERVICE_NAME, "test");
        msg.add(Message.SERVICE_URI, "http://test");
        msg.add(Message.SERVICE_DOMAIN, "es.net");
        //msg.add(Message.QUERY_OPERATOR, "all");
        
        List<Service> response = Invoker.dao.query(msg);
        for (Service x:response){
        	System.out.println(x);
        }
        
        //Delete values
        Message delmsg = new Message();
        delmsg.add(Message.SERVICE_NAME, "test");
        delmsg.add(Message.SERVICE_URI, "http://test");
        delmsg.add(Message.SERVICE_DOMAIN, "es.net");
        Invoker.dao.deleteService();
        
        // Block forever
        Object blockMe = new Object();
        synchronized (blockMe) {
            blockMe.wait();
        }
    }

    public static void parseArgs(String args[])  throws java.io.IOException {

        OptionParser parser = new OptionParser();
        parser.acceptsAll( asList( "h", "?" ), "show help then exit" );
        OptionSpec<String> PORT = parser.accepts("p", "server port").withRequiredArg().ofType(String.class);
        OptionSet options = parser.parse( args );

        // check for help
        if ( options.has( "?" ) ) {
            parser.printHelpOn( System.out );
            System.exit(0);
        }
        if (options.has(PORT) ){
            port = options.valueOf(PORT);
        }
   }

}