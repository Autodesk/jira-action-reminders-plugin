/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adsk.jira.actionreminders.plugin.schedule;

import com.adsk.jira.actionreminders.plugin.api.AdskActionRemindersUtil;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;

/**
 *
 * @author prasadve
 */
public class AdskActionRemindersJobRunnerImpl implements AdskActionRemindersJobRunner {
    private static final Logger logger = Logger.getLogger(AdskActionRemindersSchedulerImpl.class);
    
    private Date lastRun = null;
    private final ApplicationProperties applicationProperties;
    private final AdskActionRemindersUtil actionRemindersUtil;
    
    public AdskActionRemindersJobRunnerImpl(ApplicationProperties applicationProperties, 
            final AdskActionRemindersUtil actionRemindersUtil) {
        this.applicationProperties = applicationProperties;
        this.actionRemindersUtil = actionRemindersUtil;
    }
    
    public long getInterval() {
        long interval = 1L;
        try {
            long sync_interval = Long.parseLong(applicationProperties
                    .getString(SYNC_INTERVAL));
            if(sync_interval > 0) {
                interval = sync_interval;
            }else{
                interval = DEFAULT_INTERVAL;
            }
        } catch (NumberFormatException e) {
            logger.error(e);
            logger.debug("Action Reminders interval property is null so using default: "+ 
                    DEFAULT_INTERVAL);
            
            interval = DEFAULT_INTERVAL;
        }
        return interval;
    }
    
    public Date getLastRun() {
        return this.lastRun;
    }
    
    public Date getNextRun() {
        Calendar cal = Calendar.getInstance();
        if(lastRun != null) {
            cal.setTime(lastRun);
        }
        cal.add(Calendar.MILLISECOND, (int) TimeUnit.MINUTES.toMillis(getInterval()));
        return cal.getTime();
    }
    
    public void setLastRun(Date lastRun) {
        this.lastRun = lastRun;
    }
    
    public JobRunnerResponse runJob(JobRunnerRequest request) {
        Date last_run_date_time = getLastRun();        
        setLastRun(new Date());
        
        actionRemindersUtil.run(last_run_date_time, request.getStartTime());
        
        return JobRunnerResponse.success();
    }
    
}
