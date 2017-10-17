/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adsk.jira.actionreminders.plugin.schedule;

import com.atlassian.scheduler.JobRunner;
import java.util.Date;

/**
 *
 * @author prasadve
 */
public interface AdskActionRemindersJobRunner extends JobRunner {
    public static final String SYNC_INTERVAL = "com.adsk.jira.actionreminders.plugin.syncInterval";
    public static final long DEFAULT_INTERVAL = 1L;
    public long getInterval();
    public void setLastRun(Date lastRun);
    public Date getLastRun();
    public Date getNextRun();
}
