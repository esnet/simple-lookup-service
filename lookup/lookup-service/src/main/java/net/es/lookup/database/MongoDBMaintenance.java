package net.es.lookup.database;

import net.es.lookup.common.Service;
import net.es.lookup.common.Message;
import net.es.lookup.common.ReservedKeywords;
import net.es.lookup.common.exception.internal.DatabaseException;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.DateTimeComparator;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class MongoDBMaintenance implements Runnable{
	private ServiceDAOMongoDb db;
	private static long prune_threshold = 60*1000; //in milliseconds
	private static long maintenanceInterval = 60*60*1000;//in milliseconds
	private boolean archive = false; //default is false
	private ArchiveDAOMongoDb archivedb=null;
	
	
	
	public static long getPruneThreshold(){
		return prune_threshold;
	}
	
	public MongoDBMaintenance(boolean archive){
		this.db = ServiceDAOMongoDb.getInstance();
		this.archive = archive;
		
		if(archive){
			this.archivedb = ArchiveDAOMongoDb.getInstance();
		}
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
			
			List<Message> messages = new ArrayList<Message>();
			if(result.size()>0){
					for (int i=0; i<result.size(); i++){
						Map m = result.get(i).getMap();
						DateTimeFormatter fmt =  ISODateTimeFormat.dateTime();
						DateTime dt = fmt.parseDateTime((String)m.get(ReservedKeywords.RECORD_EXPIRES));
						DateTimeComparator dtc =  DateTimeComparator.getInstance();
						if(dtc.compare(dt,pruneTime)<0){
							String uri = (String)m.get(ReservedKeywords.RECORD_URI);
							try{
							 messages.add(db.deleteService(uri));
							}catch(Exception e){
								System.out.println("Error pruning DB!!");
								continue;
							}
						}
					}
					
					if(archive){
						try{
							archivedb.insert(messages);
							}catch(DatabaseException e){
								System.out.println("Error inserting in Archive DB!!");
								continue;
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