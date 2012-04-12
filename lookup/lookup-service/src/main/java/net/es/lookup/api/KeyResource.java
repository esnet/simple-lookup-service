package net.es.lookup.api;

/**
 *
 */
public class KeyResource {

    public String getService(String service, String key) {
        // Return some cliched textual content
        return "/lookup/services/" + service + "/" + key + "\n";
    }
}

