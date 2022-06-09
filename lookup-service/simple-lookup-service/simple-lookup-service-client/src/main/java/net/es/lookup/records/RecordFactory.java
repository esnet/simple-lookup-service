package net.es.lookup.records;

import net.es.lookup.common.ReservedValues;
import net.es.lookup.records.Directory.PersonRecord;
import net.es.lookup.records.Network.HostRecord;
import net.es.lookup.records.Network.InterfaceRecord;
import net.es.lookup.records.Network.PSMetadataRecord;
import net.es.lookup.records.Network.ServiceRecord;
import net.es.lookup.records.PubSub.SubscribeRecord;

/**
 * Author: sowmya
 * Date: 4/22/13
 * Time: 12:44 PM
 */
public class RecordFactory {

    public static Record getRecord(String type){

       if(type.equalsIgnoreCase(ReservedValues.RECORD_VALUE_TYPE_SERVICE)){
           return new ServiceRecord();
       }else if(type.equalsIgnoreCase(ReservedValues.RECORD_VALUE_TYPE_HOST)){
           return new HostRecord();
       } else if(type.equalsIgnoreCase(ReservedValues.RECORD_VALUE_TYPE_INTERFACE)){
           return new InterfaceRecord();
       }else if(type.equalsIgnoreCase(ReservedValues.RECORD_VALUE_TYPE_PERSON)){
           return new PersonRecord();
       } else if(type.equalsIgnoreCase(ReservedValues.RECORD_VALUE_TYPE_SUBSCRIBE)){
           return new SubscribeRecord();
       } else if(type.equalsIgnoreCase(ReservedValues.RECORD_VALUE_TYPE_PSMETADATA)){
           return new PSMetadataRecord();
       }else{
           return new Record(type);
       }

    }

}
