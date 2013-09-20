package net.es.lookup.bootstrap;

import net.es.lookup.client.SimpleLS;
import net.es.lookup.common.ReservedKeys;
import net.es.lookup.common.ReservedValues;
import net.es.lookup.common.exception.BootStrapException;
import net.es.lookup.common.exception.LSClientException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.net.URI;
import java.util.Iterator;

/**
 * Author: sowmya
 * Date: 8/13/13
 * Time: 3:21 PM
 */
public class BootStrapClient {

    private URI source;
    private static final int MAX_PRIORITY = 100;
    private static final int MIN_PRIORITY = 0;
    private String lsUrl = "";

    public BootStrapClient(URI source) throws BootStrapException {
        this.source = source;

        SimpleLS client;
        try {
            client = new SimpleLS(source.getHost(), source.getPort());

            client.setRelativeUrl(source.getPath());

            client.setConnectionType("GET");

            client.send();

            String jsonResp = client.getResponse();

            if(client.getResponseCode() == 200){
                JSONObject jsonObject = JSONObject.fromObject(jsonResp);


                JSONArray hosts = (JSONArray)(jsonObject.get("hosts"));

                Iterator hostIterator = hosts.iterator();

                int curMax = 0;
                String url = "";
                while(hostIterator.hasNext()){
                    JSONObject hostDetails = (JSONObject) hostIterator.next();
                    if(hostDetails != null && !hostDetails.isEmpty()){
                        if(hostDetails.get(ReservedKeys.SERVER_STATUS).equals(ReservedValues.SERVER_STATUS_ALIVE)){
                            int priority = 0;
                            if(hostDetails.get(ReservedKeys.SERVER_PRIORITY) instanceof String){
                                priority = Integer.parseInt((String)hostDetails.get(ReservedKeys.SERVER_PRIORITY));
                            }else{
                                priority = (Integer)hostDetails.get(ReservedKeys.SERVER_PRIORITY);
                            }
                            if( priority > curMax && priority >= MIN_PRIORITY && priority <= MAX_PRIORITY ){
                                curMax = priority;
                                url = (String)hostDetails.get(ReservedKeys.SERVER_LOCATOR);
                            }
                        }

                    }
                }


                lsUrl = url;


            }

        } catch (LSClientException e) {
           throw new BootStrapException(e.getMessage());
        }



    }

    public String getLsUrl() {

        return lsUrl;
    }
}
