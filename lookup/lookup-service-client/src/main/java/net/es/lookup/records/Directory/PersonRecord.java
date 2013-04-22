package net.es.lookup.records.Directory;

import net.es.lookup.common.ReservedKeys;
import net.es.lookup.common.ReservedValues;
import net.es.lookup.records.Record;

/**
 * User: sowmya
 * Date: 12/25/12
 * Time: 1:18 PM
 */
public class PersonRecord extends Record {

    public PersonRecord(){
        super(ReservedValues.RECORD_VALUE_TYPE_PERSON);
    }

}
