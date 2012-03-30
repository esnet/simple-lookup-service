package net.es.lookup.common;

import org.joda.time.Instant;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


public class LeaseManager {

    private static long MAX_LEASE;
    private static LeaseManager instance = null;
    private DateTimeFormatter fmt = ISODateTimeFormat.dateTime();

    static {
        LeaseManager.instance = new LeaseManager();
    }

    public static LeaseManager getInstance() {
        return LeaseManager.instance;
    }

    private LeaseManager () {

    }

    public boolean requestLease (Message message) {
        Instant now = new Instant();
        // Retrieve requested TTL
        long requestedTTL = message.getTTL();
        long ttl = requestedTTL;
        if (requestedTTL > LeaseManager.MAX_LEASE) {
            ttl = LeaseManager.MAX_LEASE;
        }
        Instant expires = now.plus(ttl);
        // Add expires key/value in the message
        try {
            message.add(Message.EXPIRES, this.fmt.print(expires));
            return true;
        } catch (DuplicateKeyException e) {
            return false;
        }
    }
}
