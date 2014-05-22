package net.es.lookup.common;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Author: sowmya
 * Date: 5/13/14
 * Time: 4:18 PM
 */
public class MemoryManager implements org.quartz.Job {

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.gc();
    }
}
