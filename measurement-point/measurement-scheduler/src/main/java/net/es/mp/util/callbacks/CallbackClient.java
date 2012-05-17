package net.es.mp.util.callbacks;

import java.net.URI;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.log4j.Logger;

import net.es.mp.measurement.types.Measurement;
import net.es.mp.scheduler.NetLogger;
import net.es.mp.scheduler.types.Schedule;

/**
 * Handles callbacks to clients
 * 
 * @author Andy Lake<alake@es.net>
 *
 */
public class CallbackClient{
    private Logger log = Logger.getLogger(CallbackClient.class);
    private Logger netLogger = Logger.getLogger("netLogger");
    Protocol httpsProtocol;

    
    public CallbackClient(String keystore, String keystorePassword){
        if(keystore == null){
            return;
        }
        /*
         * Set https handler when client created. This allows program to 
         * use specific stores without affecting JVM-wide environment
         */
        this.httpsProtocol =  new Protocol("https", 
                new CustomSSLSocketFactory(keystore, keystorePassword), 443);
    }
    
    public void callback(Measurement measurement, Schedule schedule) {
        NetLogger netLog = NetLogger.getTlogger();
        
        //if null then done
        if(schedule.getCallbackURIs() == null){
            return;
        }
        
        //callback URI
        for(String callbackURI : schedule.getCallbackURIs()){
            this.netLogger.info(netLog.start("mp.util.callbacks.CallbackClient.callback", null, callbackURI));
            try {
                HttpClient client = new HttpClient();
                URI uri = new URI(callbackURI);
                PostMethod postMethod = null;
                if(this.httpsProtocol.getScheme().equals(uri.getScheme())){
                    client.getHostConfiguration().setHost(uri.getHost(), 
                            (uri.getPort() < 0 ? this.httpsProtocol.getDefaultPort() : uri.getPort()), 
                            this.httpsProtocol);
                    postMethod =  new PostMethod(uri.getPath());
                }else{
                    postMethod = new PostMethod(callbackURI);
                }
                 
                StringRequestEntity requestEntity = new StringRequestEntity(measurement.toJSONString(), "application/json", "UTF-8");
                postMethod.setRequestEntity(requestEntity);
                client.executeMethod(postMethod);
                this.netLogger.info(netLog.end("mp.util.callbacks.CallbackClient.callback", null, callbackURI));
            } catch (Exception e) {
                this.log.error("Error contacting " + callbackURI + ": " + e.getMessage());
                this.netLogger.info(netLog.error("mp.util.callbacks.CallbackClient.callback", e.getMessage(), callbackURI));
            }
        }
    }

}
