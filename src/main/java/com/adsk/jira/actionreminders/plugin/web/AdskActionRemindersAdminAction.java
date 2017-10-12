/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adsk.jira.actionreminders.plugin.web;

import com.adsk.jira.actionreminders.plugin.api.ActionRemindersAOMgr;
import com.adsk.jira.actionreminders.plugin.api.AdskActionRemindersUtil;
import com.adsk.jira.actionreminders.plugin.model.ActionRemindersBean;
import com.adsk.jira.actionreminders.plugin.schedule.AdskActionRemindersScheduler;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.permission.GlobalPermissionKey;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.opensymphony.util.TextUtils;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author prasadve
 */
public class AdskActionRemindersAdminAction extends JiraWebActionSupport {
    
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(AdskActionRemindersAdminAction.class);       
    private final ActionRemindersBean configBean = new ActionRemindersBean();  
    public static final TextUtils textUtils = new TextUtils();
    private long interval;
    private String submitted;
    private String status;
    
    private final ApplicationProperties properties;
    private final AdskActionRemindersScheduler pluginSchedule;
    private final ActionRemindersAOMgr remindActionsMgr;
    private final AdskActionRemindersUtil actionRemindersUtil;
    
    public AdskActionRemindersAdminAction(AdskActionRemindersScheduler pluginSchedule, 
            ApplicationProperties properties, ActionRemindersAOMgr remindActionsMgr, 
            AdskActionRemindersUtil actionRemindersUtil) {
        this.pluginSchedule = pluginSchedule;
        this.properties = properties;
        this.remindActionsMgr = remindActionsMgr;
        this.actionRemindersUtil = actionRemindersUtil;
    }
        
    @Override
    public String doExecute() throws Exception {
        if ( !hasGlobalPermission(GlobalPermissionKey.SYSTEM_ADMIN) ) {
            return "error";
        }
        
        if (this.submitted != null && "Schedule".equals(this.submitted)) {
            LOGGER.debug("Re-Scheduling Sync with interval -> "+ interval);           
            if(interval > 0) {
                properties.setString(AdskActionRemindersScheduler.SYNC_INTERVAL, ""+interval);
                status = "Re-scheduled Sync with interval: "+ interval;
                pluginSchedule.reschedule();
            }            
        }
        else if (this.submitted != null && "DELETE".equals(this.submitted)) {
            LOGGER.debug("Deleting map Id -> "+ configBean.getConfigId());
            if(configBean.getConfigId() != 0) {
                remindActionsMgr.removeActionReminders(configBean.getConfigId());
                status = "Deleted.";
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
    
    public long getConfigId() {
        return configBean.getConfigId();
    }

    public void setConfigId(long configId) {
        configBean.setConfigId(configId);
    }

    public String getIssueAction() {
        return configBean.getIssueAction();
    }

    public void setIssueAction(String issueAction) {
        configBean.setIssueAction(issueAction);
    }

    public boolean isActive() {
        return configBean.isActive();
    }

    public void setActive(boolean active) {
        configBean.setActive(active);
    }
    
    public String getProjectKey() {        
        return configBean.getProjectKey();
    }

    public void setProjectId(String projectKey) {        
        configBean.setProjectKey(projectKey);
    }

    public String getQuery() {
        return configBean.getQuery();
    }

    public void setQuery(String query) {
        configBean.setQuery(query);
    }

    public String getRunAuthor() {        
        return configBean.getRunAuthor();
    }

    public void setRunAuthor(String runAuthor) {
        configBean.setRunAuthor(runAuthor);
    }            
    
    public Date getLastRun() {
        return configBean.getLastRun();
    }

    public void setLastRun(Date lastRun) {
        configBean.setLastRun(lastRun);
    }

    public int getExecCount() {
        return configBean.getExecCount();
    }

    public void setExecCount(int execCount) {
        configBean.setExecCount(execCount);
    }

    public boolean isNotifyAssignee() {
        return configBean.isNotifyAssignee();
    }

    public void setNotifyAssignee(boolean notifyAssignee) {
        configBean.setNotifyAssignee(notifyAssignee);
    }

    public boolean isNotifyReporter() {
        return configBean.isNotifyReporter();
    }

    public void setNotifyReporter(boolean notifyReporter) {
        configBean.setNotifyReporter(notifyReporter);
    }

    public boolean isNotifyWatchers() {
        return configBean.isNotifyWatchers();
    }

    public void setNotifyWatchers(boolean notifyWatchers) {
        configBean.setNotifyWatchers(notifyWatchers);
    }

    public String getNotifyProjectrole() {
        return configBean.getNotifyProjectrole();
    }

    public void setNotifyProjectrole(String notifyProjectrole) {
        configBean.setNotifyProjectrole(notifyProjectrole);
    }

    public String getNotifyGroup() {
        return configBean.getNotifyGroup();
    }

    public void setNotifyGroup(String notifyGroup) {
        configBean.setNotifyGroup(notifyGroup);
    }
    
    public String getMessage() {
        return configBean.getMessage();
    }

    public void setMessage(String message) {
        configBean.setMessage(message);
    }
    
    public boolean isReminders() {
        return configBean.isReminders();
    }

    public void setReminders(boolean reminders) {
        configBean.setReminders(reminders);
    }

    public boolean isActions() {
        return configBean.isActions();
    }

    public void setActions(boolean actions) {
        configBean.setActions(actions);
    }
    
    
    public List<Project> getProjects() {
        return actionRemindersUtil.getProjects();
    }
    
    public TextUtils getTextUtils() {
        return textUtils;
    }
    
    public List<ActionRemindersBean> getConfigList() {
        return remindActionsMgr.getAllActionReminders();
    }
    
    public void setSubmitted(String submitted) {
        this.submitted = submitted;
    }
    
    public String getStatus() {
        return status;
    }    
}
