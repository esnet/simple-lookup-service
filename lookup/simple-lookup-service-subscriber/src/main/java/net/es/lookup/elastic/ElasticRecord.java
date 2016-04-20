package net.es.lookup.elastic;

import net.sf.json.JSONObject;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Author: sowmya
 * Date: 4/5/16
 * Time: 12:53 PM
 */
public class ElasticRecord {
    private ConcurrentHashMap<String,Object> dataMap;

    public ElasticRecord(){
        dataMap = new ConcurrentHashMap<String, Object>();
    }

    public ElasticRecord(String json){

        JSONObject jsonObject = JSONObject.fromObject(json);
        dataMap = new ConcurrentHashMap<String, Object>();

        for(Object key: jsonObject.keySet()){
            Object value = jsonObject.get(key);

            if(value instanceof List){

            }

        }



    }

}
