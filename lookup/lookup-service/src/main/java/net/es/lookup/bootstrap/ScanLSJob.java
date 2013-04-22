//package net.es.lookup.bootstrap;
//
///**
// * User: sowmya
// * Date: 1/3/13
// * Time: 2:11 PM
// */
//import net.es.lookup.common.Message;
//import net.es.lookup.common.ReservedKeys;
//import net.es.lookup.common.ServiceRecord;
//import net.es.lookup.common.exception.internal.DatabaseException;
//import net.es.lookup.database.ServiceDAOMongoDb;
//import org.apache.log4j.Logger;
//import org.joda.time.DateTime;
//import org.joda.time.DateTimeComparator;
//import org.joda.time.Instant;
//import org.joda.time.format.DateTimeFormatter;
//import org.joda.time.format.ISODateTimeFormat;
//import org.quartz.*;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//
//@DisallowConcurrentExecution
//public class ScanLSJob {
//    private static Logger LOG = Logger.getLogger(ScanLSJob.class);
//    private String filename = "";
//    public static String PRUNE_THRESHOLD = "prune_threshold"; //parameter will be set during run time
//
//
//    public ScanLSJob(String filename) {
//
//        this.filename = filename;
//
//    }
//
//
//    public void execute(JobExecutionContext context) throws JobExecutionException {
//
//        List<ServiceRecord> result = null;
//        LOG.info("Running MongoDBPrune...");
//        JobDataMap data = context.getJobDetail().getJobDataMap();
//        long prune_threshold = data.getLong(PRUNE_THRESHOLD);
//        Instant now = new Instant();
//        Instant pTime = now.minus(prune_threshold);
//        DateTime pruneTime = pTime.toDateTime();
//
//        try {
//
//            result = db.queryAll();
//
//        } catch (DatabaseException e) {
//
//            LOG.error("DBException! Could not query database");
//
//        }
//
//        List<Message> messages = new ArrayList<Message>();
//
//        if (result != null && result.size() > 0) {
//
//            for (int i = 0; i < result.size(); i++) {
//
//                Map m = result.get(i).getMap();
//                DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
//                DateTime dt = fmt.parseDateTime((String) m.get(ReservedKeys.RECORD_EXPIRES));
//                DateTimeComparator dtc = DateTimeComparator.getInstance();
//
//                if (dtc.compare(dt, pruneTime) < 0) {
//
//                    String uri = (String) m.get(ReservedKeys.RECORD_URI);
//
//                    try {
//
//                        messages.add(db.deleteService(uri));
//
//                    } catch (Exception e) {
//
//                        LOG.error("Error pruning DB!!");
//
//                    }
//
//                }
//
//            }
//
//        }
//
//    }
//
//}
