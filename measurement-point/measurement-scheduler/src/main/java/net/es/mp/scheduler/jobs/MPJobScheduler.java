package net.es.mp.scheduler.jobs;

import java.net.URI;

import net.es.mp.authz.AuthzConditions;
import net.es.mp.scheduler.MPSchedulerException;
import net.es.mp.scheduler.types.Schedule;

public interface MPJobScheduler {
    
    public URI submitSchedule(Schedule schedule, AuthzConditions authzConditions) throws MPSchedulerException;
    
    public void commitSchedule(Schedule schedule, AuthzConditions authzConditions) throws MPSchedulerException;
}
