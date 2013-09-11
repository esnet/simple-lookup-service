package net.es.lookup.common;

import net.es.lookup.common.exception.internal.DataFormatException;
import net.es.lookup.common.exception.internal.PubSubQueryException;
import net.es.lookup.protocol.json.JSONMessage;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;

/**
 * This class is for normalizing queries. A normalized query has all the keys sorted in alphabetical order.
 *
 * Author: sowmya
 * Date: 3/1/13
 * Time: 4:04 PM
 */
public class QueryNormalizer {

    /**
     * This method normalizes queries and returns a string format of query
     * @param  query     The query that needs to be normalized
     * @return String   Returns the normalized query as a string
     */
    public static String normalize(Message query) throws PubSubQueryException {

        String result;

        Map mmap = query.getMap();

        if(mmap.isEmpty()){
            result= "empty";



        }else if (mmap.size()==1){
            try {
                result = JSONMessage.toString(query);
            } catch (DataFormatException e) {
                throw new PubSubQueryException(e.getMessage());
            }
        }else{
            //Sort the keys
            List<String> keyv = new ArrayList<String>(mmap.keySet());
            Collections.sort(keyv);

            //Put the sorted keys in a LinkedHashMap to preserve insertion order
            LinkedHashMap<String, Object> resultmap = new LinkedHashMap<String, Object>();
            for (String s : keyv) {
                resultmap.put(s, mmap.get(s));
            }

            //Conversion
            try {
                Message res = new Message(resultmap);
                result = JSONMessage.toString(res);
            } catch (DataFormatException e) {
                throw new PubSubQueryException(e.getMessage());
            }
        }

        return result;

    }

}
