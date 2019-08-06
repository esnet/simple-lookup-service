package net.es.lookup.common;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;


public class LeaseManager {

    private long defaultLease = 2 * 60 * 60;
    private long maxLease = defaultLease;
    private long minLease = 240;
    private long pruneThreshold = 300;
    private DateTimeFormatter fmt;

    private static LeaseManager instance = null;

    private static Logger LOG = LogManager.getLogger(LeaseManager.class);


    public static LeaseManager getInstance() {

        return LeaseManager.instance;

    }


    public LeaseManager(long maxLease, long minLease, long defaultLease, long pruneThreshold) {

    if (LeaseManager.instance == null) {
      LeaseManager.instance = this;
      fmt = ISODateTimeFormat.dateTime();
      this.maxLease = maxLease;
      this.minLease = minLease;
      this.defaultLease = defaultLease;
      this.pruneThreshold = pruneThreshold;
        }
    }


    public boolean requestLease(Message message) {

        Instant now = new Instant();
        // Retrieve requested TTL
        String requestedTTL = "";

        requestedTTL = message.getTTL();

        long ttl = 0;

        //check if expires field is beyond pruning threshold. If yes, do not give lease. Record needs to be deleted.
        String expires = "";
        if(message.getExpires() != null && !message.getExpires().isEmpty()){
            expires = (String) message.getExpires();
        }


        if (expires != null && !expires.isEmpty()) {

            Instant pTime = now.minus(pruneThreshold);
            DateTime pruneTime = pTime.toDateTime();

            DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
            DateTime dt = fmt.parseDateTime(expires);
            DateTimeComparator dtc = DateTimeComparator.getInstance();

            if (dtc.compare(dt, pruneTime) < 0) {

                LOG.info("Cannot grant lease because record expired more than 5 minutes ago" + dt + "----" + pruneTime);
                return false;

            }

        }


        if (requestedTTL != null && requestedTTL != "") {

            PeriodFormatter fmt = ISOPeriodFormat.standard();

            try {

                Duration duration = fmt.parsePeriod(requestedTTL).toStandardDuration();
                ttl = new Long(duration.getStandardSeconds());

            } catch (IllegalArgumentException e) {

                LOG.info("Cannot grant lease. Wrong TTL format");
                return false;

            }




            if (ttl == 0 || ttl > maxLease || ttl < minLease) {

                ttl = defaultLease;

            }

        } else {

            ttl = defaultLease;

        }

        Instant newExpires = now.plus(ttl * 1000); //this method requires milliseconds
        LOG.info("Lease granted. ttl value: " + ttl);

        String convertedExpires = this.fmt.print(newExpires);
        message.add(net.es.lookup.common.ReservedKeys.RECORD_EXPIRES, convertedExpires);

        LOG.info("Lease granted. expires value: " + convertedExpires);
        return true;

    }


}
