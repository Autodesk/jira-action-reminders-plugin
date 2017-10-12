/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adsk.jira.actionreminders.plugin.schedule;

import com.adsk.jira.actionreminders.plugin.api.AdskActionRemindersUtilImpl;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.api.scheduling.PluginScheduler;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;
import com.adsk.jira.actionreminders.plugin.api.ActionRemindersAOMgr;

/**
 *
 * @author prasadve
 */
public class AdskActionRemindersSchedulerImpl implements AdskActionRemindersScheduler, LifecycleAware {

    private static final Logger logger = Logger.getLogger(AdskActionRemindersSchedulerImpl.class);
    
    private Date lastRun = null;
    
    private final ApplicationProperties applicationProperties;
    private final PluginScheduler pluginScheduler;  // provided by SAL    
    private final ActionRemindersAOMgr actionRemindersAOMgr;
    private final AdskActionRemindersUtilImpl actionRemindersUtil;
    
    public AdskActionRemindersSchedulerImpl(ApplicationProperties applicationProperties, 
            PluginScheduler pluginScheduler, ActionRemindersAOMgr actionRemindersAOMgr, 
            AdskActionRemindersUtilImpl actionRemindersUtil) {
        this.applicationProperties = applicationProperties;
        this.pluginScheduler = pluginScheduler;
        this.actionRemindersAOMgr = actionRemindersAOMgr;
        this.actionRemindersUtil = actionRemindersUtil;
    }
    
    public long getInterval() {
        long interval = 1L;
        try {
            long sync_interval = Long.parseLong(applicationProperties
                    .getString(AdskActionRemindersScheduler.SYNC_INTERVAL));
            if(sync_interval > 0) {
                interval = sync_interval;
            }else{
                interval = AdskActionRemindersScheduler.DEFAULT_INTERVAL;
            }
        } catch (NumberFormatException e) {
            logger.debug("Action Reminders interval property is null so using default: "+ 
                    AdskActionRemindersScheduler.DEFAULT_INTERVAL);
            
            interval = AdskActionRemindersScheduler.DEFAULT_INTERVAL;
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

    public void onStart() {
        /**
         * Important place to Change minutes or hours
         * conversion here.
         */
        long time_interval = TimeUnit.MINUTES.toMillis(getInterval());
                
        pluginScheduler.scheduleJob(JOB_NAME,                   // unique name of the job
                AdskActionRemindersService.class,            // class of the job
                new HashMap<String,Object>() {{
                    put(AdskActionRemindersScheduler.KEY, AdskActionRemindersSchedulerImpl.this);
                }},                    // data that needs to be passed to the job
                getNextRun(),          // the time the job is to start
                time_interval);             // interval between repeats, in milliseconds
        logger.debug(String.format("Action Reminders task scheduled to run every %dhrs", getInterval()));
    }

    public void onStop() {
        this.pluginScheduler.unscheduleJob(JOB_NAME);
    }
    
    public void reschedule() {
        onStop();
        onStart();
    }

    public void setLastRun(Date lastRun) {
        this.lastRun = lastRun;
    }
    
    public AdskActionRemindersUtilImpl getActionRemindersUtil() {
        return actionRemindersUtil;
    }
    
    public ActionRemindersAOMgr getActionRemindersAOMgr() {
        return actionRemindersAOMgr;
    }    
    
}
