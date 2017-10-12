/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adsk.jira.actionreminders.plugin.schedule;

import com.adsk.jira.actionreminders.plugin.api.AdskActionRemindersUtilImpl;
import java.util.Date;
import com.adsk.jira.actionreminders.plugin.api.ActionRemindersAOMgr;

/**
 *
 * @author prasadve
 */
public interface AdskActionRemindersScheduler {
    
    public static final String SYNC_INTERVAL = "com.adsk.jira.actionreminders.plugin.syncInterval";
    
    public static final String KEY = AdskActionRemindersScheduler.class.getName() + ":instance";
    
    public static final String JOB_NAME = AdskActionRemindersScheduler.class.getName() + ":job";
    
    public static final long DEFAULT_INTERVAL = 5L;
    
    public ActionRemindersAOMgr getActionRemindersAOMgr();
    
    public AdskActionRemindersUtilImpl getActionRemindersUtil();
    
    public long getInterval();    
    public void reschedule();
    public void setLastRun(Date lastRun);
    public Date getLastRun();
    public Date getNextRun();
    
}
