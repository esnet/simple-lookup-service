package net.es.mp.scheduler.jobs;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import net.es.mp.authz.AuthzConditions;
import net.es.mp.scheduler.MPSchedulerException;
import net.es.mp.scheduler.types.Schedule;

public class JobRouter {
    Map<String,MPJobScheduler> routeTable;

    public JobRouter(HashMap<String, MPJobScheduler> jobRoutingTable){
        this.routeTable = jobRoutingTable;
        for(String key: this.routeTable.keySet()){
            System.out.println("Type: " + key);
        }
    }

    public URI submit(Schedule schedule, AuthzConditions authzConditions) throws MPSchedulerException {
        //
        System.out.println("Type=" + schedule.getType() + "!");
        System.out.println("routeTable.containsKey(schedule.getType())=" + routeTable.containsKey(schedule.getType()));
        System.out.println("routeTable.containsKey(schedule.getType()))=" +  routeTable.containsKey("blah"));
        if(routeTable.containsKey(schedule.getType()) &&
                routeTable.get(schedule.getType()) != null){
            return this.routeTable.get(schedule.getType()).submitSchedule(schedule, authzConditions);
        }
        throw new MPSchedulerException("Submit found unrecognized measurement type \"" + 
                schedule.getType() + "\"" );
    }

    public void commit(Schedule schedule, AuthzConditions authzConditions) throws MPSchedulerException {
        //
        if(routeTable.containsKey(schedule.getType()) &&
                routeTable.get(schedule.getType()) != null){
            this.routeTable.get(schedule.getType()).commitSchedule(schedule, authzConditions);
        }else{
            throw new MPSchedulerException("Commit found Unrecognized measurement type \"" + 
                    schedule.getType() + "\"" );
        }
    }

}
