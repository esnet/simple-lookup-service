package net.es.mp.util;

import javax.ws.rs.core.UriBuilder;

public class IDUtil {

    static public String generateURI(String baseURI, String path, String id){
        UriBuilder ub = UriBuilder.fromUri(baseURI);
        ub.path(path);
        ub.path(id);
        
        return ub.build().toASCIIString();
    }
}
