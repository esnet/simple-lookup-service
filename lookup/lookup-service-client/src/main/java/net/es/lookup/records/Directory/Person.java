package net.es.lookup.records.Directory;

import net.es.lookup.common.ReservedKeywords;
import net.es.lookup.records.Record;

/**
 * User: sowmya
 * Date: 12/25/12
 * Time: 1:18 PM
 */
public class Person extends Record {

    public Person(){
        super(ReservedKeywords.RECORD_VALUE_TYPE_PERSON);
    }

}
