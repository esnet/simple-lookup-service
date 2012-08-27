package net.es.lookup.common;

import org.joda.time.Instant;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import net.es.lookup.common.ReservedKeywords;

import org.joda.time.Duration;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.DateTimeComparator;
import org.joda.time.DateTime;

import net.es.lookup.utils.LookupServiceConfigReader;
import net.es.lookup.utils.DatabaseConfigReader;



public class LeaseManager {
	private static long DEFAULT_MAX_LEASE=2*60*60;
    private static long MAX_LEASE=DEFAULT_MAX_LEASE;
    private static LeaseManager instance = null;
    private DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
    private LookupServiceConfigReader lcfg;
    private DatabaseConfigReader dcfg;

    static {
        LeaseManager.instance = new LeaseManager();
    }

    public static LeaseManager getInstance() {
        return LeaseManager.instance;
    }

    private LeaseManager () {
    	 lcfg = LookupServiceConfigReader.getInstance();
    	 dcfg = DatabaseConfigReader.getInstance();
    	 MAX_LEASE = lcfg.getMaxleasetime();
    }

    public boolean requestLease (Message message) {
        Instant now = new Instant();
        // Retrieve requested TTL
        String requestedTTL = message.getTTL();
        long ttl = 0;
        
        //check if expires field is beyond pruning threshold. If yes, do not give lease. Record needs to be deleted.
        String expires = message.getExpires();
        if(expires != null && expires != ""){
        	Instant pTime = now.minus(dcfg.getPruneThreshold());
			DateTime pruneTime = pTime.toDateTime();
			
			DateTimeFormatter fmt =  ISODateTimeFormat.dateTime();
			DateTime dt = fmt.parseDateTime(expires);
			DateTimeComparator dtc =  DateTimeComparator.getInstance();
			if(dtc.compare(dt,pruneTime)<0){
				return false;
			}
        }
        if(requestedTTL != null && requestedTTL != ""){
        	PeriodFormatter fmt = ISOPeriodFormat.standard();
        	
            try{
            	Duration duration = fmt.parsePeriod(requestedTTL).toStandardDuration();
                ttl  = new Long(duration.getStandardSeconds());
            }catch(IllegalArgumentException e){
            	return false;
            }
            if (ttl ==0 || ttl > LeaseManager.MAX_LEASE ) {
                ttl = LeaseManager.MAX_LEASE;
            }
           
        }else {
        	ttl = LeaseManager.MAX_LEASE;
        }
        
        Instant newExpires = now.plus(ttl);
        //System.out.println(expires.toString());
        // Add expires key/value in the message
        message.add(ReservedKeywords.RECORD_EXPIRES, this.fmt.print(newExpires));
        return true;
 
    }
}
