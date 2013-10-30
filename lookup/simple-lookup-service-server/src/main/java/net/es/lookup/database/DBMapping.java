package net.es.lookup.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Author: sowmya
 * Date: 10/29/13
 * Time: 7:48 PM
 */
public class DBMapping {
    private static HashMap<String, ServiceDAOMongoDb> dbHashMap = new HashMap<String, ServiceDAOMongoDb>();

    public static void addDb(String dbname, ServiceDAOMongoDb daoMongoDb){
        dbHashMap.put(dbname, daoMongoDb);
    }


    public static ServiceDAOMongoDb getDb(String dbname){
        return dbHashMap.get(dbname);
    }

    public static List<String> getKeys(){

        List<String> keylist = new ArrayList<String>(dbHashMap.keySet());
        return keylist;
    }

    public static boolean containsKey(String key){
        return dbHashMap.containsKey(key);
    }

}
