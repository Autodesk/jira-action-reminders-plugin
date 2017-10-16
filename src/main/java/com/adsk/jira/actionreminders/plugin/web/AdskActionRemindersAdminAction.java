/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adsk.jira.actionreminders.plugin.web;

import com.adsk.jira.actionreminders.plugin.api.AdskActionRemindersUtil;
import com.adsk.jira.actionreminders.plugin.schedule.AdskActionRemindersScheduler;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.permission.GlobalPermissionKey;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.opensymphony.util.TextUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author prasadve
 */
public class AdskActionRemindersAdminAction extends JiraWebActionSupport {
    
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(AdskActionRemindersAdminAction.class);
    public static final TextUtils textUtils = new TextUtils();        
    private boolean enableReminders;
    private boolean enableActions;
    private long interval;
    private String submitted;
    private String status;
    
    private final ApplicationProperties properties;
    private final AdskActionRemindersScheduler pluginSchedule;
    
    public AdskActionRemindersAdminAction(AdskActionRemindersScheduler pluginSchedule, 
            ApplicationProperties properties) {
        this.pluginSchedule = pluginSchedule;
        this.properties = properties;
    }
        
    @Override
    public String doExecute() throws Exception {
        if ( !hasGlobalPermission(GlobalPermissionKey.SYSTEM_ADMIN) ) {
            return "error";
        }
        if (this.submitted != null && "SAVE".equals(this.submitted)) {
            properties.setString(AdskActionRemindersUtil.ENABLE_REMINDERS, ""+enableReminders);
            properties.setString(AdskActionRemindersUtil.ENABLE_ACTIONS, ""+enableActions);
            status = "Updated Actions and Reminders.";
        }
        else if (this.submitted != null && "Schedule".equals(this.submitted)) {
            logger.debug("Re-Scheduling Sync with interval -> "+ interval);           
            if(interval > 0) {
                properties.setString(AdskActionRemindersScheduler.SYNC_INTERVAL, ""+interval);
                status = "Re-scheduled Sync with interval: "+ interval;
                pluginSchedule.reschedule();
            }            
        }
        return "success";
    }
    
    public long getInterval() {
        return pluginSchedule.getInterval();
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public boolean isEnableReminders() {
        boolean enableRemindersStatus = false;
        try {
            String rStatus = properties.getString(AdskActionRemindersUtil.ENABLE_REMINDERS);
            if(rStatus != null) {
                enableRemindersStatus = Boolean.parseBoolean(rStatus);
            }else{
                enableRemindersStatus = true;
                properties.setString(AdskActionRemindersUtil.ENABLE_REMINDERS, ""+enableRemindersStatus);
            }
        }catch(ClassCastException e) {
            logger.error(e);       
        }
        return enableRemindersStatus;
    }

    public void setEnableReminders(boolean enableReminders) {
        this.enableReminders = enableReminders;
    }

    public boolean isEnableActions() {
        boolean enableActionsStatus = false;
        try {
            String eStatus = properties.getString(AdskActionRemindersUtil.ENABLE_ACTIONS);
            if(eStatus != null) {
                enableActionsStatus = Boolean.parseBoolean(eStatus);
            } else {
                enableActionsStatus = true;
                properties.setString(AdskActionRemindersUtil.ENABLE_ACTIONS, ""+enableActionsStatus);
            }
        }catch(ClassCastException e) {
            logger.error(e);
        }
        return enableActionsStatus;
    }

    public void setEnableActions(boolean enableActions) {
        this.enableActions = enableActions;
    }        
    
    public TextUtils getTextUtils() {
        return textUtils;
    }    
    
    public void setSubmitted(String submitted) {
        this.submitted = submitted;
    }
    
    public String getStatus() {
        return status;
    }    
}
