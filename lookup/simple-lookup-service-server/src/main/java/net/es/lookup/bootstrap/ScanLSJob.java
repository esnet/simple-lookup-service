package net.es.lookup.bootstrap;

import net.es.lookup.client.SimpleLS;
import net.es.lookup.common.Message;
import net.es.lookup.common.ReservedKeys;
import net.es.lookup.common.ReservedValues;
import net.es.lookup.common.Service;
import net.es.lookup.common.exception.LSClientException;
import net.es.lookup.common.exception.internal.ConfigurationException;
import net.es.lookup.common.exception.internal.DataFormatException;
import net.es.lookup.protocol.json.JSONMessage;
import net.es.lookup.protocol.json.JSONParser;
import net.es.lookup.service.Invoker;
import net.es.lookup.utils.BootStrapConfigReader;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@DisallowConcurrentExecution
public class ScanLSJob implements Job {
    BootStrapConfigReader bootStrapConfigReader;
    List<Service> hostStatus = new ArrayList<Service>();
    public ScanLSJob() throws ConfigurationException {
        bootStrapConfigReader = BootStrapConfigReader.getInstance();
    }

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        int count = bootStrapConfigReader.getSourceCount();

        for(int i=0; i<count; i++) {
            String status = ReservedValues.SERVER_STATUS_UNKNOWN;
            Service hostDetails = new Service();

            String hostlocator = null;
            int priority;
            try {
                hostlocator = bootStrapConfigReader.getSourceLocator(i);
                priority = bootStrapConfigReader.getSourcePriority(i);
                hostDetails.add(ReservedKeys.SERVER_LOCATOR, hostlocator);
                hostDetails.add(ReservedKeys.SERVER_PRIORITY, priority);


                URI uri = null;
                try {

                    uri = new URI(hostlocator);
                    System.out.println(uri.getHost() + ":" + uri.getPort());
                    SimpleLS lsclient = null;
                    try {
                        lsclient = new SimpleLS(uri.getHost(), uri.getPort());

                        try{
                            lsclient.connect();
                        } catch (LSClientException e) {
                            //log error and continue since connect() method updates status even when host cannot be reached
                        }

                        status = lsclient.getStatus();

                        hostDetails.add(ReservedKeys.SERVER_STATUS, status);
                    } catch (LSClientException e) {
                        status = ReservedValues.SERVER_STATUS_UNKNOWN;
                    }

                    DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
                    String timestamp = fmt.print(new Date().getTime());
                    hostDetails.add(ReservedKeys.SERVER_TIMESTAMP, timestamp);

                    hostStatus.add(hostDetails);
                } catch (URISyntaxException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }


            } catch (ConfigurationException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }




        }
        String filename = Invoker.getConfigPath()+Invoker.getBootstrapoutput();

        try {
            String res = JSONMessage.toString(hostStatus, ReservedKeys.BOOTSTRAP_HOSTS);
            System.out.println(res);
            PrintWriter writer = new PrintWriter(filename);
            writer.println(res);
            writer.close();
        } catch (DataFormatException e) {
            System.out.println(e.getMessage());
        } catch (FileNotFoundException e) {
            System.out.println("ErROR");
        }
    }
}