package net.es.lookup.api;


import net.es.lookup.common.exception.api.InternalErrorException;
import net.es.lookup.common.exception.api.NotSupportedException;
import net.es.lookup.service.Invoker;
import net.es.lookup.utils.config.reader.LookupServiceConfigReader;
import org.apache.log4j.Logger;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;


/**
 * Author: sowmya
 * Date: 8/5/13
 * Time: 2:22 PM
 */
public class BootStrapService {

    private static Logger LOG = Logger.getLogger(BootStrapService.class);
    private static LookupServiceConfigReader lsconfig = LookupServiceConfigReader.getInstance();

    public String getServers() {

        if(!lsconfig.isBootstrapserviceOn()){
            throw new NotSupportedException("BootStrap service not supported!");
        }
        BufferedReader br = null;

        String res = "";
        try {

            String sCurrentLine;

            br = new BufferedReader(new FileReader(Invoker.getConfigPath()+Invoker.getBootstrapoutput()));

            while ((sCurrentLine = br.readLine()) != null) {
                res += sCurrentLine;
            }

        } catch (FileNotFoundException e) {
           throw new InternalErrorException("File not found. Contact administrator");
        } catch (IOException e) {
            throw new InternalErrorException("Error processing output. Contact administrator");
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                throw new InternalErrorException("Error processing output. Contact administrator");
            }
        }

        return res;
    }


}
