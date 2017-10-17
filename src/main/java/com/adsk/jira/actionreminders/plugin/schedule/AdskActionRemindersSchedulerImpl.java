/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adsk.jira.actionreminders.plugin.schedule;

import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.config.Schedule;

/**
 *
 * @author prasadve
 */
public class AdskActionRemindersSchedulerImpl implements AdskActionRemindersScheduler, LifecycleAware {

    private static final Logger logger = Logger.getLogger(AdskActionRemindersSchedulerImpl.class);    
    private final ApplicationProperties applicationProperties;        
    private final SchedulerService schedulerService;
    private final AdskActionRemindersJobRunner AdskActionRemindersJobRunner;
    
    public AdskActionRemindersSchedulerImpl(SchedulerService schedulerService, 
            ApplicationProperties applicationProperties, AdskActionRemindersJobRunner AdskActionRemindersJobRunner) {
        this.schedulerService = schedulerService;
        this.applicationProperties = applicationProperties;        
        this.AdskActionRemindersJobRunner = AdskActionRemindersJobRunner;
    }
    
    public long getInterval() {
        long interval = 1L;
        try {
            long sync_interval = Long.parseLong(applicationProperties
                    .getString(AdskActionRemindersJobRunner.SYNC_INTERVAL));
            if(sync_interval > 0) {
                interval = sync_interval;
            }else{
                interval = AdskActionRemindersJobRunner.DEFAULT_INTERVAL;
            }
        } catch (NumberFormatException e) {
            logger.debug("Action Reminders interval property is null so using default: "+ 
                    AdskActionRemindersJobRunner.DEFAULT_INTERVAL);
            
            interval = AdskActionRemindersJobRunner.DEFAULT_INTERVAL;
        }
        return interval;
    }
    
    private JobId getJobId() {    
      return JobId.of(AdskActionRemindersScheduler.class.getName() + ".job");
    }

    private JobRunnerKey getJobRunnerKey() {    
      return JobRunnerKey.of(AdskActionRemindersScheduler.class.getName() + ".scheduler");
    }
    
    public void onStart() {
        /**
         * Important place to Change minutes or hours
         * conversion here.
         */
        long interval = getInterval();
        long time_interval = TimeUnit.HOURS.toMillis(interval);
                
        if (!this.schedulerService.getRegisteredJobRunnerKeys().contains(getJobRunnerKey())) {        
          logger.debug("Registering JobRunner - "+ getJobRunnerKey().toString());
          this.schedulerService.registerJobRunner(getJobRunnerKey(), this.AdskActionRemindersJobRunner);
        }
        
        AdskActionRemindersJobRunner.setLastRun(new Date()); //set current date time as last execution.
        
        Date start =  new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(interval));
        Schedule schedule = Schedule.forInterval(time_interval, start);
        JobConfig jobConfig = JobConfig.forJobRunnerKey(getJobRunnerKey())
                .withRunMode(RunMode.RUN_ONCE_PER_CLUSTER).withSchedule(schedule);
        try {        
          logger.debug("Scheduling the Action Reminders Job "+ getJobRunnerKey().toString() +" with "+ 
                  jobConfig);
          
          this.schedulerService.scheduleJob(getJobId(), jobConfig);
        }
        catch (SchedulerServiceException e) {        
          logger.error("Failed to schedule the Action Reminders Job! ", e);
        }
        logger.debug(String.format("Action Reminders task scheduled to run every %dhrs", getInterval()));
    }

    public void onStop() {
        this.schedulerService.unscheduleJob(getJobId());
        this.schedulerService.unregisterJobRunner(getJobRunnerKey());
    }
    
    public void reschedule() {
        onStop();
        onStart();
    }
}
