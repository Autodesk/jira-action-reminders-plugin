package com.adsk.jira.actionreminders.plugin.web;

import com.adsk.jira.actionreminders.plugin.api.ActionRemindersAO;
import com.adsk.jira.actionreminders.plugin.model.ActionRemindersBean;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.opensymphony.util.TextUtils;
import java.util.Date;
import org.apache.log4j.Logger;
import org.apache.velocity.util.StringUtils;
import com.adsk.jira.actionreminders.plugin.api.ActionRemindersAOMgr;
import com.adsk.jira.actionreminders.plugin.api.AdskActionRemindersUtil;
import com.atlassian.jira.permission.ProjectPermissions;
import com.atlassian.jira.project.Project;
import java.text.MessageFormat;

/**
 * @author scmenthusiast@gmail.com
 */
public class AdskActionRemindersConfigAction extends JiraWebActionSupport {
    
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(AdskActionRemindersConfigAction.class);    
    
    private final ActionRemindersBean configBean = new ActionRemindersBean();
    public static final StringUtils stringUtils = new StringUtils();
    public static final TextUtils textUtils = new TextUtils();
    private String submitted;
    private String status;
    
    private final ActionRemindersAOMgr remindActionsMgr;
    private final AdskActionRemindersUtil actionRemindersUtil;
    public AdskActionRemindersConfigAction(ActionRemindersAOMgr remindActionsMgr, 
            AdskActionRemindersUtil actionRemindersUtil) {
        this.remindActionsMgr = remindActionsMgr;
        this.actionRemindersUtil = actionRemindersUtil;
    }
        
    @Override
    public String doExecute() throws Exception {
                        
        LOGGER.info("ConfigId: "+ configBean.getConfigId());
        if (this.submitted != null && "RUN".equals(this.submitted)) {
            Project project = getProjectManager().getProjectObjByKey(configBean.getProjectKey());
            if(project == null) {
                return "error";
            }            
            if ( !hasProjectPermission(ProjectPermissions.ADMINISTER_PROJECTS, project) ) {
                return "error";
            }
            configBean.setProjectName(project.getName());

            LOGGER.debug("Running map -> "+ configBean.getConfigId() +":"+ configBean.getQuery()+":"+ configBean.isActive());
            if(configBean.getConfigId() > 0) {                
                ActionRemindersAO remindAction = remindActionsMgr.getActionReminderById(configBean.getConfigId()); 
                actionRemindersUtil.process(remindAction, 
                        configBean.isReminders(), configBean.isActions());
            }
        }        
        else if (this.submitted != null && "SAVE".equals(this.submitted)) {
            Project project = getProjectManager().getProjectObjByKey(configBean.getProjectKey());
            if(project == null) {
                return "error";
            }
            configBean.setProjectName(project.getName());

            if ( !hasProjectPermission(ProjectPermissions.ADMINISTER_PROJECTS, project) ) {
                return "error";
            }

            LOGGER.debug("Saving map -> "+ configBean.getConfigId() +":"+ configBean.getQuery()+":"+ configBean.isActive());
            if(remindActionsMgr.findActionReminders2(configBean) == false) {
                if(configBean.getConfigId() > 0 && configBean.getProjectKey() != null && configBean.getQuery()!= null && !"".equals(configBean.getQuery())) {
                    remindActionsMgr.updateActionReminders(configBean);                    
                    status = "Saved.";
                }else{
                    status = "Remind action fields missing!";
                }
            }else{
                status = MessageFormat.format("{0} && {1} alredy exists in mapping!",
                        configBean.getQuery(), configBean.getIssueAction());
            }
        }
        else {
            if(configBean.getConfigId() > 0) {
                ActionRemindersAO map = remindActionsMgr.getActionReminderById(configBean.getConfigId());
                configBean.setProjectKey(map.getProjectKey());
                configBean.setQuery(map.getQuery());
                configBean.setIssueAction(map.getIssueAction());           
                configBean.setRunAuthor(map.getRunAuthor());
                configBean.setLastRun(map.getLastRun());
                configBean.setCronSchedule(map.getCronSchedule());
                configBean.setNotifyAssignee(map.getNotifyAssignee());
                configBean.setNotifyReporter(map.getNotifyReporter());
                configBean.setNotifyWatchers(map.getNotifyWatchers());
                configBean.setNotifyProjectrole(map.getNotifyProjectrole());
                configBean.setNotifyGroup(map.getNotifyGroup());
                configBean.setMessage(map.getMessage());
                configBean.setActive(map.getActive());
                
                Project project = getProjectManager().getProjectObjByKey(configBean.getProjectKey());
                if(project == null) {
                    return "error";
                }
                configBean.setProjectName(project.getName());

                if ( !hasProjectPermission(ProjectPermissions.ADMINISTER_PROJECTS, project) ) {
                    return "error";
                }
            }
        }
        
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
    
    public String getCronSchedule() {
        return configBean.getCronSchedule();
    }

    public void setCronSchedule(String cronSchedule) {
        configBean.setCronSchedule(cronSchedule);
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
