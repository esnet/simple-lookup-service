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

import org.apache.log4j.Logger;



public class LeaseManager {
	private static long DEFAULT_LEASE=2*60*60;
    private static long MAX_LEASE=DEFAULT_LEASE;
    private static long MIN_LEASE = DEFAULT_LEASE;
    private static LeaseManager instance = null;
    private DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
    private LookupServiceConfigReader lcfg;
    
    private static Logger LOG = Logger.getLogger(LeaseManager.class);
    
    static {
        LeaseManager.instance = new LeaseManager();
    }

    public static LeaseManager getInstance() {
        return LeaseManager.instance;
    }

    private LeaseManager () {
    	 lcfg = LookupServiceConfigReader.getInstance();
    	 MAX_LEASE = lcfg.getMaxLease();
    	 MIN_LEASE = lcfg.getMinLease();
    	 DEFAULT_LEASE = lcfg.getDefaultLease();
    }

    public boolean requestLease (Message message) {
        Instant now = new Instant();
        // Retrieve requested TTL
        String requestedTTL = message.getTTL();
        long ttl = 0;
        
        //check if expires field is beyond pruning threshold. If yes, do not give lease. Record needs to be deleted.
        String expires = message.getExpires();
        if(expires != null && expires != ""){
        	Instant pTime = now.minus(lcfg.getPruneThreshold());
			DateTime pruneTime = pTime.toDateTime();
			
			DateTimeFormatter fmt =  ISODateTimeFormat.dateTime();
			DateTime dt = fmt.parseDateTime(expires);
			DateTimeComparator dtc =  DateTimeComparator.getInstance();
			if(dtc.compare(dt,pruneTime)<0){
				LOG.info("Cannot grant lease because record expired more than 5 minutes ago"+dt+"----"+pruneTime);
				return false;
			}
        }
        if(requestedTTL != null && requestedTTL != ""){
        	PeriodFormatter fmt = ISOPeriodFormat.standard();
        	
            try{
            	Duration duration = fmt.parsePeriod(requestedTTL).toStandardDuration();
                ttl  = new Long(duration.getStandardSeconds());
            }catch(IllegalArgumentException e){
            	LOG.info("Cannot grant lease. Wrong TTL format");
            	return false;
            }
            if (ttl ==0 || ttl > LeaseManager.MAX_LEASE || ttl < LeaseManager.MIN_LEASE ) {
                ttl = LeaseManager.DEFAULT_LEASE;
            }
           
        }else {
        	ttl = LeaseManager.DEFAULT_LEASE;
        }
        
        Instant newExpires = now.plus(ttl*1000); //this method requires milliseconds
        LOG.info("Lease granted. ttl value: "+ttl);
        //System.out.println(expires.toString());
        // Add expires key/value in the message
        message.add(ReservedKeywords.RECORD_EXPIRES, this.fmt.print(newExpires));
        LOG.info("Lease granted. expires value: "+newExpires);
        return true;
 
    }
}
