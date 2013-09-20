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
import net.es.lookup.service.Invoker;
import net.es.lookup.utils.BootStrapConfigReader;
import org.apache.log4j.Logger;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@DisallowConcurrentExecution
public class ScanLSJob implements Job {
    BootStrapConfigReader bootStrapConfigReader;
    List<Service> hostStatus = new ArrayList<Service>();
    private static Logger LOG = Logger.getLogger(ScanLSJob.class);

    public ScanLSJob() throws ConfigurationException {
        bootStrapConfigReader = BootStrapConfigReader.getInstance();
    }

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        int count = bootStrapConfigReader.getSourceCount();

        for(int i=0; i<count; i++) {
            String status;
            Service hostDetails = new Service();

            String hostlocator;
            int priority;
            try {
                hostlocator = bootStrapConfigReader.getSourceLocator(i);
                priority = bootStrapConfigReader.getSourcePriority(i);
                hostDetails.add(ReservedKeys.SERVER_LOCATOR, hostlocator);
                hostDetails.add(ReservedKeys.SERVER_PRIORITY, priority);


                URI uri;
                try {

                    uri = new URI(hostlocator);
                    LOG.error("net.es.lookup.bootstrap.ScanLS.execute: Bootstrap scanning host-"+ uri.getHost()+" port-"+uri.getPort());
                    SimpleLS lsclient;
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
                        hostDetails.add(ReservedKeys.SERVER_STATUS, status);

                    }

                    DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
                    String timestamp = fmt.print(new Date().getTime());
                    hostDetails.add(ReservedKeys.SERVER_TIMESTAMP, timestamp);

                    hostStatus.add(hostDetails);
                } catch (URISyntaxException e) {
                    LOG.error("net.es.lookup.bootstrap.ScanLS.execute: Bootstrap scanning error - "+e.getMessage());
                }


            } catch (ConfigurationException e) {
                LOG.error("net.es.lookup.bootstrap.ScanLS.execute: Bootstrap scanning error - "+e.getMessage());
            }




        }
        String filename = Invoker.getConfigPath()+Invoker.getBootstrapoutput();

        try {
            String res = JSONMessage.toString(hostStatus, ReservedKeys.BOOTSTRAP_HOSTS);
            LOG.error("net.es.lookup.bootstrap.ScanLS.execute: Bootstrap scanning results - "+res);
            PrintWriter writer = new PrintWriter(filename);
            writer.println(res);
            writer.close();
        } catch (DataFormatException e) {
            LOG.error("net.es.lookup.bootstrap.ScanLS.execute: Bootstrap scanning error - "+e.getMessage());
        } catch (FileNotFoundException e) {
            LOG.error("net.es.lookup.bootstrap.ScanLS.execute: Bootstrap scanning error - "+e.getMessage());
        }
    }
}