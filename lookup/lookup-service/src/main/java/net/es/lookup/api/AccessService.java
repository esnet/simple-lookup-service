package net.es.lookup.api;


/**
 *
 */

public class AccessService {


    public String getService(String service) {
        // Return some cliched textual content
        return "/lookup/service/" + service + "\n";
    }
}

