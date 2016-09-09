package net.es.lookup.utils.config.reader;

import org.apache.log4j.Logger;

import java.io.*;

/**
 * Author: sowmya
 * Date: 8/2/16
 * Time: 3:37 PM
 *
 * This class reads the json mapping file for elastic search index.
 * This is a singleton instance.
 */
public class IndexMapReader {

    private static IndexMapReader instance;
    private static Logger LOG = Logger.getLogger(BaseConfigReader.class);

    /**
     * Returns the instance of this class. If there isn't one, it creates a new instance
     * */
    public static IndexMapReader getInstance() {
        if (instance == null) {
            instance = new IndexMapReader();
        }
        return instance;
    }

    // private constructor as it is a singleton instance
    private IndexMapReader() {

    }


    /**
     * Reads the json mapping and returns it as a String
     * */
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

}
