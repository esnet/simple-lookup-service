package net.es.lookup.utils;

import net.es.lookup.common.ReservedValues;

import java.net.URI;

/**
 * Author: sowmya
 * Date: 8/22/16
 * Time: 2:18 PM
 *
 * A helper class to retrieve elastic search urls
 *
 */
public class ElasticUtils {

    /**
     * Returns the absolute(root) path of the given url.
     * */
    public static String getAbsoluteEndPoint(URI uri){
        return uri.getScheme()+"://"+uri.getHost()+":"+uri.getPort()+"/";
    }

    /**
     * Returns the url that is used to modify index aliases for the given elasticsearch url
     * */
    public static String getAliasEndPoint(URI uri){
        String idStr = ReservedValues.ELASTIC_ALIASES_ENDPOINT;
        return uri.getScheme()+"://"+uri.getHost()+":"+uri.getPort()+"/"+idStr;
    }

    /**
     * This method constructs the index url for the given elastic search url and the given index
     * */
    public static String getAbsoluteIndexUrl(URI uri, String index){
        return uri.getScheme()+"://"+uri.getHost()+":"+uri.getPort()+"/"+index;
    }

}
