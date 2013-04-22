package net.es.lookup.records.Network;


import net.es.lookup.common.ReservedValues;
import net.es.lookup.records.Record;

/**
 * User: sowmya
 * Date: 12/25/12
 * Time: 1:17 PM
 */
public class HostRecord extends Record {
    public HostRecord(){
        super(ReservedValues.RECORD_VALUE_TYPE_HOST);
    }
}
