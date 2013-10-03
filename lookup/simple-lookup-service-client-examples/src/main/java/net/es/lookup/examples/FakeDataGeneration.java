package net.es.lookup.examples;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.es.lookup.client.QueryClient;
import net.es.lookup.client.RegistrationClient;
import net.es.lookup.client.SimpleLS;
import net.es.lookup.common.exception.LSClientException;
import net.es.lookup.common.exception.ParserException;
import net.es.lookup.common.exception.QueryException;
import net.es.lookup.queries.Query;
import net.es.lookup.records.Record;

import java.io.IOException;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Author: sowmya
 * Date: 10/1/13
 * Time: 3:26 PM
 * <p/>
 * Generates 100,000 records and registers them with the specified lookup service one after another
 */
public class FakeDataGeneration {


    public static void main(String args[]) throws IOException {

        String host = "sowmya-dev-vm.es.net";
        int port = 8090;

   /*     OptionParser parser = new OptionParser();
        parser.acceptsAll(asList("h", "?"), "show help then exit");
        OptionSpec<String> PORT = parser.accepts("u", "url").withRequiredArg().ofType(String.class);
        OptionSpec<String> HOST = parser.accepts("h", "host").withRequiredArg().ofType(String.class);

        OptionSet options = parser.parse(args);

        // check for help
        if (options.has("?")) {

            parser.printHelpOn(System.out);
            System.exit(0);

        }

        if (!options.has(HOST) || !options.has(PORT)) {
            System.out.println("Please specify host and port");
            System.exit(0);


        } else {
            host = options.valueOf(HOST);
            port = Integer.parseInt(options.valueOf(PORT));
        }
*/

        SimpleLS server = null;


        try {
            server = new SimpleLS(host, port);
        } catch (LSClientException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        RegistrationClient registrationClient = null;
        try {
            registrationClient = new RegistrationClient(server);
        } catch (LSClientException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


        SimpleLS queryserver = null;
        try {
            queryserver = new SimpleLS("sowmya-dev-vm.es.net", 8090);
        } catch (LSClientException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        QueryClient queryClient = null;
        try {
            queryClient = new QueryClient(queryserver);
        } catch (LSClientException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        Query query = new Query();
        if (queryClient != null) {
            try {
                queryClient.setQuery(query);
                List<Record> results = null;
                try {
                    results = queryClient.query();
                } catch (ParserException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (LSClientException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (QueryException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

                System.out.println("Number of records: " + results.size());
       /*         for (Record record : results) {
                    registrationClient.setRecord(record);
                    try {
                        registrationClient.register();

                    } catch (ParserException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }*/
            } catch (LSClientException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }


    }
}


