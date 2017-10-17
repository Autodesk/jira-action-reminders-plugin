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
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author scmenthusiast@gmail.com
 */
public class AdskActionRemindersConfigAction extends JiraWebActionSupport {
    
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(AdskActionRemindersConfigAction.class);    
    private final Collection<String> defaultGroups = new ArrayList<String>();
    private final Collection<String> defaultRoles = new ArrayList<String>();
    private final ActionRemindersBean configBean = new ActionRemindersBean();
    public static final StringUtils stringUtils = new StringUtils();
    public static final TextUtils textUtils = new TextUtils();
    private String submitted;
    private String status;
    
    private final ActionRemindersAOMgr remindActionsMgr;
    private final AdskActionRemindersUtil actionRemindersUtil;
    private final JiraAuthenticationContext jiraAuthenticationContext;
    private final ProjectRoleManager projectRoleManager;
    private final GroupManager groupManager;
    
    public AdskActionRemindersConfigAction(ActionRemindersAOMgr remindActionsMgr, 
            AdskActionRemindersUtil actionRemindersUtil, JiraAuthenticationContext jiraAuthenticationContext, 
            ProjectRoleManager projectRoleManager, GroupManager groupManager) {
        this.remindActionsMgr = remindActionsMgr;
        this.actionRemindersUtil = actionRemindersUtil;
        this.jiraAuthenticationContext = jiraAuthenticationContext;
        this.projectRoleManager = projectRoleManager;
        this.groupManager = groupManager;
        //add default groups n roles
        defaultGroups.add("jira-administrators");
        defaultGroups.add("jira-software-users");
        defaultGroups.add("jira-developers");
        defaultGroups.add("jira-users");
        defaultRoles.add("administrators");
        defaultRoles.add("developers");
        defaultRoles.add("users");
    }
        
    @Override
    public String doExecute() throws Exception {
                        
        logger.info("ConfigId: "+ configBean.getConfigId());
        if (this.submitted != null && "RUN".equals(this.submitted)) {
            Project project = getProjectManager().getProjectObjByKey(configBean.getProjectKey());
            if(project == null) {
                return "error";
            }            
            if ( !hasProjectPermission(ProjectPermissions.ADMINISTER_PROJECTS, project) ) {
                return "error";
            }
            configBean.setProjectName(project.getName());

            logger.debug("Running map -> "+ configBean.getConfigId() +":"+ configBean.getQuery()+":"+ configBean.isActive());
            if(configBean.getConfigId() > 0) {                
                ActionRemindersAO remindAction = remindActionsMgr.getActionReminderById(configBean.getConfigId()); 
                actionRemindersUtil.process(remindAction, 
                        actionRemindersUtil.getRemindersStatus(), actionRemindersUtil.getActionsStatus());
                status = "Triggered!";
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

            logger.debug("Saving map -> "+ configBean.getConfigId() +":"+ configBean.getQuery()+":"+ configBean.isActive());
            
            if(configBean.getConfigId() < 0 || configBean.getProjectKey() == null || "".equals(configBean.getProjectKey()) 
                || configBean.getConfigType() == null || "".equals(configBean.getConfigType())
                || configBean.getQuery() == null || "".equals(configBean.getQuery())
                || configBean.getRunAuthor() == null || "".equals(configBean.getRunAuthor())
                || configBean.getCronSchedule() == null || "".equals(configBean.getCronSchedule())
                || configBean.getMessage() == null || "".equals(configBean.getMessage())) {
                status = "Error: Required fields are missing!";
            }else{            
                remindActionsMgr.updateActionReminders(configBean);                    
                status = "Saved.";
            }
        }
        else {
            if(configBean.getConfigId() > 0) {
                ActionRemindersAO map = remindActionsMgr.getActionReminderById(configBean.getConfigId());
                configBean.setProjectKey(map.getProjectKey());
                configBean.setQuery(map.getQuery());
                configBean.setIssueAction(map.getIssueAction());
                configBean.setConfigType(map.getConfigType());
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
    
    public String getConfigType() {
        return configBean.getConfigType();
    }

    public void setConfigType(String configType) {
        configBean.setConfigType(configType);
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
    
    public Project getProject() {
        return getProjectManager().getProjectObjByKey(configBean.getProjectKey());        
    }
    
    public Collection<String> getProjectRoles() {        
        Collection<String> roleNames = new ArrayList<String>();
        for(ProjectRole projectRole : projectRoleManager.getProjectRoles()) {
            if(!defaultRoles.contains(projectRole.getName().toLowerCase())){
                roleNames.add(projectRole.getName());
            }
        }
        return roleNames;
    }
    
    public Collection<String> getGroupNames() {
        Collection<String> groupNames = new ArrayList<String>();
        for(String group : groupManager
                .getGroupNamesForUser(jiraAuthenticationContext.getLoggedInUser())) {
            if(!defaultGroups.contains(group.toLowerCase())){
                groupNames.add(group);
            }
        }
        return groupNames;
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
