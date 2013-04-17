package net.es.lookup.records.Network;

import net.es.lookup.common.ReservedKeywords;
import net.es.lookup.records.Record;

/**
 * User: sowmya
 * Date: 12/25/12
 * Time: 1:17 PM
 */
public class Host extends Record {
    public Host(){
        super(ReservedKeywords.RECORD_VALUE_TYPE_HOST);
    }
}
