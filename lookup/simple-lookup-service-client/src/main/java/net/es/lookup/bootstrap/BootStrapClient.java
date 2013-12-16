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
import java.util.LinkedList;
import java.util.List;

/**
 * Author: sowmya
 * Date: 8/13/13
 * Time: 3:21 PM
 */
public class BootStrapClient {

    private URI source;
    private SimpleLS bootstrapclient;
    private static final int MAX_PRIORITY = 100;
    private static final int MIN_PRIORITY = 0;
    private String lsUrl = "";
    private List<String> lsList;

    public BootStrapClient(URI source) throws BootStrapException {
        this.source = source;
        lsList = new LinkedList<String>();
        try {
            bootstrapclient = new SimpleLS(source.getHost(), source.getPort());

            bootstrapclient.setRelativeUrl(source.getPath());

            bootstrapclient.setConnectionType("GET");

            retrieveLsList();

        } catch (LSClientException e) {
           throw new BootStrapException(e.getMessage());
        }



    }

    public String getLsUrl() {

        return lsUrl;
    }

    public List<String> getAllUrls(){
        return lsList;
    }

    public void refresh() throws BootStrapException {
        retrieveLsList();
    }


    private void retrieveLsList() throws BootStrapException {

        try {
            bootstrapclient.send();
        } catch (LSClientException e) {
            throw new BootStrapException(e.getMessage());
        }

        String jsonResp = bootstrapclient.getResponse();

        if(bootstrapclient.getResponseCode() == 200){
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
                        String tmpurl = (String)hostDetails.get(ReservedKeys.SERVER_LOCATOR);
                        lsList.add(tmpurl);
                        if(hostDetails.get(ReservedKeys.SERVER_PRIORITY) instanceof String){
                            priority = Integer.parseInt((String)hostDetails.get(ReservedKeys.SERVER_PRIORITY));
                        }else{
                            priority = (Integer)hostDetails.get(ReservedKeys.SERVER_PRIORITY);
                        }
                        if( priority > curMax && priority >= MIN_PRIORITY && priority <= MAX_PRIORITY ){
                            curMax = priority;
                            url = tmpurl;
                        }
                    }

                }
            }


            lsUrl = url;


        }
    }
}
