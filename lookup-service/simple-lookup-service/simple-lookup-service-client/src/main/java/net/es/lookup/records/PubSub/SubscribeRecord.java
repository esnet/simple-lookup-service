package net.es.lookup.records.PubSub;

import net.es.lookup.common.ReservedKeys;
import net.es.lookup.common.ReservedValues;
import net.es.lookup.records.Record;
import org.joda.time.Instant;

import java.util.List;

/**
 * Author: sowmya
 * Date: 4/16/13
 * Time: 4:36 PM
 */
public class SubscribeRecord extends Record {


    public SubscribeRecord() {
        super(ReservedValues.RECORD_VALUE_TYPE_SUBSCRIBE);
    }

    public List<String> getLocator(){

        return (List<String>)this.getMap().get(ReservedKeys.RECORD_SUBSCRIBE_LOCATOR);

    }


    public void setLocator(List<String> locator){

        this.add(ReservedKeys.RECORD_SUBSCRIBE_LOCATOR, locator);

    }

    public List<String> getQueues(){
        return (List<String>)this.getMap().get(ReservedKeys.RECORD_SUBSCRIBE_QUEUE);
    }

    public void setQueues(List<String> queues){
        this.add(ReservedKeys.RECORD_SUBSCRIBE_QUEUE, queues);
    }

    public String getQueueState(){
        return (String) this.getMap().get(ReservedKeys.RECORD_SUBSCRIBE_QUEUE_STATE);
    }

    public void setQueueState(String queueState){
        this.add(ReservedKeys.RECORD_SUBSCRIBE_QUEUE_STATE, queueState);
    }


    public Instant getQueueCreationTime() {

        String time = (String) this.getMap().get(ReservedKeys.RECORD_SUBSCRIBE_QUEUE_TIMESTAMP);
        Instant ntime = new Instant(time);
        return ntime;

    }
}
