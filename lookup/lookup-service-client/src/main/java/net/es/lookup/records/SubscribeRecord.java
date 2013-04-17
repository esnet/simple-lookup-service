package net.es.lookup.records;

import net.es.lookup.common.ReservedKeywords;

import java.util.List;

/**
 * Author: sowmya
 * Date: 4/16/13
 * Time: 4:36 PM
 */
public class SubscribeRecord extends Record {


    public SubscribeRecord() {
        super(ReservedKeywords.RECORD_VALUE_SUBSCRIBE);
    }

    public List<String> getLocator(){

        return (List<String>)this.getMap().get(ReservedKeywords.RECORD_SUBSCRIBE_LOCATOR);

    }


    public void setLocator(List<String> locator){

        this.add(ReservedKeywords.RECORD_SUBSCRIBE_LOCATOR, locator);

    }

    public List<String> getQueues(){
        return (List<String>)this.getMap().get(ReservedKeywords.RECORD_SUBSCRIBE_QUEUE);
    }

    public void setQueues(List<String> queues){
        this.add(ReservedKeywords.RECORD_SUBSCRIBE_QUEUE, queues);
    }


}
