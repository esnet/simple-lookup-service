package net.es.lookup.database;

import net.es.lookup.common.Service;
import net.es.lookup.common.ReservedKeywords;
import net.es.lookup.common.exception.internal.DatabaseException;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.DateTimeComparator;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import java.util.List;
import java.util.Map;

public class MongoDBMaintenance implements Runnable{
	private ServiceDAOMongoDb db;
	private static long prune_threshold = 60*1000; //in milliseconds
	private static long maintenanceInterval = 60*60*1000;//in milliseconds
	private static boolean archive = false; //default is false
	private static String archiveDBHost = "";
	private static String archiveDBPort = "";
	private static String archiveDBName = "";
	private static String archiveDBCollectionName = "";
	
	
	
	public static long getPruneThreshold(){
		return prune_threshold;
	}
	
	public MongoDBMaintenance(ServiceDAOMongoDb db){
		this.db = db;
	}
	
	public MongoDBMaintenance(String configFile){
		this.db = db;
	}
	
	public void run() {
		while(true){
			List<Service> result;
			System.out.println("Hello from a thread!");
			Instant now = new Instant();
			Instant pTime = now.minus(prune_threshold);
			System.out.println(pTime.toString());
			DateTime pruneTime = pTime.toDateTime();
			
			try{
				System.out.println("About to execute db query");
				result = db.queryAll();
				System.out.println("Executed db query");
			}catch(DatabaseException e){
				System.out.println("Caught exception!");
				continue;
			}
			if(result.size()>0){
				if(archive){
					
				}else{
					for (int i=0; i<result.size(); i++){
						Map m = result.get(i).getMap();
						DateTimeFormatter fmt =  ISODateTimeFormat.dateTime();
						DateTime dt = fmt.parseDateTime((String)m.get(ReservedKeywords.RECORD_EXPIRES));
						DateTimeComparator dtc =  DateTimeComparator.getInstance();
						if(dtc.compare(dt,pruneTime)<0){
							String uri = (String)m.get(ReservedKeywords.RECORD_URI);
							try{
							 db.deleteService(uri);
							}catch(Exception e){
								System.out.println("Error pruning DB!!");
								continue;
							}
						}
					}					
				}

		     }
			
	        try{
	        	Thread.sleep(maintenanceInterval);
	        }catch(InterruptedException e){
	        	continue;
	        }
		}   
        
    }
}