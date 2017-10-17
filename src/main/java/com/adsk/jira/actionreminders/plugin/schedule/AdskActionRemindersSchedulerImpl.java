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
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.config.Schedule;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author prasadve
 */
@Named
@ExportAsService({LifecycleAware.class})
public class AdskActionRemindersSchedulerImpl implements AdskActionRemindersScheduler, LifecycleAware {

    private static final Logger logger = Logger.getLogger(AdskActionRemindersSchedulerImpl.class);    
    private final ApplicationProperties applicationProperties;        
    private final SchedulerService schedulerService;
    private final AdskActionRemindersJobRunner AdskActionRemindersJobRunner;
    
    @Inject
    public AdskActionRemindersSchedulerImpl(@ComponentImport ApplicationProperties applicationProperties, 
            SchedulerService schedulerService, AdskActionRemindersJobRunner AdskActionRemindersJobRunner) {
        this.applicationProperties = applicationProperties;
        this.schedulerService = schedulerService;
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
      return JobId.of(AdskActionRemindersJobRunner.class.getName() + ".job");
    }

    private JobRunnerKey getJobRunnerKey() {    
      return JobRunnerKey.of(AdskActionRemindersJobRunner.class.getName() + ".scheduler");
    }
    
    public void onStart() {
        /**
         * Important place to Change minutes or hours
         * conversion here.
         */
        long time_interval = TimeUnit.MINUTES.toMillis(getInterval());
                
        if (!this.schedulerService.getRegisteredJobRunnerKeys().contains(getJobRunnerKey())) {        
          logger.debug("Registering JobRunner - "+ getJobRunnerKey().toString());
          this.schedulerService.registerJobRunner(getJobRunnerKey(), this.AdskActionRemindersJobRunner);
        }
        
        AdskActionRemindersJobRunner.setLastRun(new Date()); //set current date time as last execution.
        
        Date start =  new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1L));
        Schedule schedule = Schedule.forInterval(time_interval, start);
        JobConfig jobConfig = JobConfig.forJobRunnerKey(getJobRunnerKey())
                .withRunMode(RunMode.RUN_ONCE_PER_CLUSTER).withSchedule(schedule);
        try {        
          logger.debug("Scheduling the LDAP Groups Synchronization Job "+ getJobRunnerKey().toString() +" with "+ 
                  jobConfig);
          
          this.schedulerService.scheduleJob(getJobId(), jobConfig);
        }
        catch (SchedulerServiceException e) {        
          logger.error("Failed to schedule the Jenkins Synchronization Job. Builds can only be synchronized manually until this is fixed!", e);
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
