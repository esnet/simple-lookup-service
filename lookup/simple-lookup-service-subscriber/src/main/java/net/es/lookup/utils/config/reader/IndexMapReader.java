package net.es.lookup.utils.config.reader;

import org.apache.log4j.Logger;

import java.io.*;
import java.net.URI;

/**
 * Author: sowmya
 * Date: 8/2/16
 * Time: 3:37 PM
 */
public class IndexMapReader {

    private static IndexMapReader instance;
    private static Logger LOG = Logger.getLogger(BaseConfigReader.class);

    public static IndexMapReader getInstance() {

        if (instance == null) {

            instance = new IndexMapReader();

        }

        return instance;

    }

    private IndexMapReader() {

    }


    @SuppressWarnings({"static-access", "unchecked"})
    public String readMapping(String mappingFile) {

        InputStream mappingStream = this.getClass().getClassLoader().getSystemResourceAsStream(mappingFile);

        StringBuilder mapping = new StringBuilder();

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(mappingStream));

            String line = null;

            while ((line = bufferedReader.readLine()) != null){
                mapping.append(line);

            }


        } catch (NullPointerException ex) {

            try {

                mappingStream = new FileInputStream(new File(mappingFile));
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(mappingStream));

                String line = null;

                while ((line = bufferedReader.readLine()) != null){
                    mapping.append(line);

                }



            } catch (FileNotFoundException e) {

                LOG.error("Mapping file not found");
                System.exit(1);

            } catch (IOException e) {
                LOG.error("Error reading mapping file");
                System.exit(1);
            }

        } catch (IOException e) {
            LOG.error("Error reading mapping file");
            System.exit(1);
        }

        return mapping.toString();


    }


    public String getElasticIndex(URI uri){
        String[] segments = uri.getPath().split("/");
        String idStr = segments[segments.length-2];
        return uri.getScheme()+"://"+uri.getHost()+":"+uri.getPort()+"/"+idStr;
    }

}
