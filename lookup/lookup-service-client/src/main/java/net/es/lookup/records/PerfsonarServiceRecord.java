package net.es.lookup.records;

import net.es.lookup.common.ReservedKeywords;

/**
 * User: sowmya
 * Date: 9/25/12
 * Time: 2:33 PM
 */
public class PerfsonarServiceRecord extends Record {

    public PerfsonarServiceRecord() {

    }

    public PerfsonarServiceRecord(String recordType, String serviceType, String serviceEventtype, String serviceDomains, String serviceLocator) {

        super(recordType);
        this.add(ReservedKeywords.RECORD_TYPE, recordType);
        this.add(ReservedKeywords.RECORD_SERVICE_TYPE, serviceType);
        this.add(ReservedKeywords.RECORD_SERVICE_EVENTTYPE, serviceEventtype);
        this.add(ReservedKeywords.RECORD_SERVICE_DOMAINS, serviceDomains);
        this.add(ReservedKeywords.RECORD_SERVICE_LOCATOR, serviceLocator);

    }

}
