package com.adsk.jira.actionreminders.plugin.web;

import com.adsk.jira.actionreminders.plugin.api.ActionRemindersAOMgr;
import com.adsk.jira.actionreminders.plugin.model.ActionRemindersBean;
import com.atlassian.jira.permission.ProjectPermissions;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.opensymphony.util.TextUtils;
import java.util.Date;
import org.apache.log4j.Logger;
import org.apache.velocity.util.StringUtils;

/**
 * @author scmenthusiast@gmail.com
 */
public class ActionRemindersProjectEditAction extends JiraWebActionSupport {
    
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ActionRemindersProjectEditAction.class);    
    
    private final ActionRemindersBean configBean = new ActionRemindersBean();
    public static final StringUtils stringUtils = new StringUtils();
    public static final TextUtils textUtils = new TextUtils();
    private String submitted;
    private String status;
    
    private final ActionRemindersAOMgr remindActionsMgr;
    
    public ActionRemindersProjectEditAction(ActionRemindersAOMgr remindActionsMgr) {
        this.remindActionsMgr = remindActionsMgr;
    }
        
    @Override
    public String doExecute() throws Exception {
        Project project = getProjectManager().getProjectObjByKey(configBean.getProjectKey());
        if(project == null) {
            return "error";
        }
        configBean.setProjectName(project.getName());
        
        if ( !hasProjectPermission(ProjectPermissions.ADMINISTER_PROJECTS, project) ) {
            return "error";
        }
                
        LOGGER.info("ConfigId: "+ configBean.getConfigId());
        if(configBean.getConfigId() > 0) {
            ActionRemindersBean map = remindActionsMgr.getActionReminderById(configBean.getConfigId());
            configBean.setProjectKey(map.getProjectKey());
            configBean.setQuery(map.getQuery());
            configBean.setIssueAction(map.getIssueAction());           
            configBean.setRunAuthor(map.getRunAuthor());
            configBean.setLastRun(map.getLastRun());
            configBean.setExecCount(map.getExecCount());
            configBean.setNotifyAssignee(map.isNotifyAssignee());
            configBean.setNotifyReporter(map.isNotifyReporter());
            configBean.setNotifyWatchers(map.isNotifyWatchers());
            configBean.setNotifyProjectrole(map.getNotifyProjectrole());
            configBean.setNotifyGroup(map.getNotifyGroup());
            configBean.setMessage(map.getMessage());
            configBean.setActive(map.isActive());
        }
        
        LOGGER.info("QUERY: "+ configBean.getQuery());
        
        return "success";
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

    public void setProjectKey(String projectKey) {        
        configBean.setProjectKey(projectKey);
    }
    
    public String getProjectName() {        
        return configBean.getProjectName();
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
    
    public TextUtils getTextUtils() {
        return textUtils;
    }

    public static StringUtils getStringUtils() {
        return stringUtils;
    }
    
    public void setSubmitted(String submitted) {
        this.submitted = submitted;
    }
    
    public String getStatus() {
        return status;
    }        
}
