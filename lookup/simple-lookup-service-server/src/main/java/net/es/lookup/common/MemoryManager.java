package net.es.lookup.common;

import net.es.lookup.api.QueryServices;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Author: sowmya
 * Date: 5/13/14
 * Time: 4:18 PM
 */
public class MemoryManager implements org.quartz.Job {

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        if(QueryServices.QUERY_ALL_FLAG){
            System.gc();
            QueryServices.QUERY_ALL_FLAG=false;
        }

    }
}
