package com.adsk.jira.actionreminders.plugin.web;

import com.adsk.jira.actionreminders.plugin.api.ActionRemindersAO;
import com.adsk.jira.actionreminders.plugin.model.ActionRemindersBean;
import com.atlassian.jira.permission.ProjectPermissions;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.opensymphony.util.TextUtils;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.velocity.util.StringUtils;
import com.adsk.jira.actionreminders.plugin.api.ActionRemindersAOMgr;
import com.adsk.jira.actionreminders.plugin.api.AdskActionRemindersUtil;
import static com.adsk.jira.actionreminders.plugin.api.AdskActionRemindersUtil.QUERY_LIMIT;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author scmenthusiast@gmail.com
 */
public class AdskActionRemindersProjectAction extends JiraWebActionSupport {
    
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(AdskActionRemindersProjectAction.class);
    private final Collection<String> defaultGroups = new ArrayList<String>();
    private final Collection<String> defaultRoles = new ArrayList<String>();
    private final ActionRemindersBean configBean = new ActionRemindersBean();
    public static final StringUtils stringUtils = new StringUtils();
    public static final TextUtils textUtils = new TextUtils();
    private String submitted;
    private String status;
    private int limit;
    private final AdskActionRemindersUtil actionRemindersUtil;
    private final ActionRemindersAOMgr remindActionsMgr;
    private final JiraAuthenticationContext jiraAuthenticationContext;
    private final ProjectRoleManager projectRoleManager;
    private final GroupManager groupManager;
    private final ApplicationProperties properties;
    public AdskActionRemindersProjectAction(AdskActionRemindersUtil actionRemindersUtil, ActionRemindersAOMgr remindActionsMgr, 
            JiraAuthenticationContext jiraAuthenticationContext, ProjectRoleManager projectRoleManager, 
            GroupManager groupManager, ApplicationProperties properties) {
        this.actionRemindersUtil = actionRemindersUtil;
        this.jiraAuthenticationContext = jiraAuthenticationContext;
        this.remindActionsMgr = remindActionsMgr;
        this.projectRoleManager = projectRoleManager;
        this.groupManager = groupManager;
        this.properties = properties;
        //add default groups
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
        Project project = getProjectManager().getProjectObjByKey(configBean.getProjectKey());
        if(project == null) {
            return "error";
        }
        configBean.setProjectName(project.getName());
        
        if ( !hasProjectPermission(ProjectPermissions.ADMINISTER_PROJECTS, project) ) {
            return "error";
        }
        
        if (this.submitted != null && "ADD".equals(this.submitted)) {            
            logger.debug("Adding map -> "+ configBean.getQuery() +":"+configBean.getIssueAction()+":"+ configBean.isActive());
            
            if(configBean.getProjectKey() == null || "".equals(configBean.getProjectKey()) 
                || configBean.getConfigType() == null || "".equals(configBean.getConfigType())
                || configBean.getQuery() == null || "".equals(configBean.getQuery())
                || configBean.getRunAuthor() == null || "".equals(configBean.getRunAuthor())
                || configBean.getCronSchedule() == null || "".equals(configBean.getCronSchedule())
                || configBean.getMessage() == null || "".equals(configBean.getMessage())) {
                status = "Error: Required fields are missing!";
            }else{            
                remindActionsMgr.addActionReminders(configBean);                    
                status = "Added.";
            }
        } else if (this.submitted != null && "RUN".equals(this.submitted)) {
            logger.debug("Running map -> "+ configBean.getConfigId() +":"+ configBean.getQuery()+":"+ configBean.isActive());
            if(configBean.getConfigId() > 0) {
                logger.info("**** Processing On-Demand Action Reminder Config Id #"+ configBean.getConfigId());
                ActionRemindersAO remindAction = remindActionsMgr.getActionReminderById(configBean.getConfigId()); 
                actionRemindersUtil.process(remindAction, 
                        actionRemindersUtil.getRemindersStatus(), actionRemindersUtil.getActionsStatus(), getLimit());
                status = "Triggered! Config #"+ configBean.getConfigId();
            }
        } else if (this.submitted != null && "DELETE".equals(this.submitted)) {
            logger.debug("Deleting map Id -> "+ configBean.getConfigId());
            if(configBean.getConfigId() != 0) {
                remindActionsMgr.removeActionReminders(configBean.getConfigId());
                status = "Deleted.";
            }
        }
        
        return "success";
    }
    
    public int getLimit() {
        try {
            String queryLimit = properties.getString(QUERY_LIMIT);
            if(queryLimit != null) {
                limit = Integer.parseInt(queryLimit);
            }else{
                limit = 25;
                properties.setString(QUERY_LIMIT, ""+limit);
            }
        }catch(ClassCastException e) {
            logger.error(e);       
        }
        return limit;
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
        return jiraAuthenticationContext.getLoggedInUser().getUsername();
        //return configBean.getRunAuthor();
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
    
    public TextUtils getTextUtils() {
        return textUtils;
    }

    public static StringUtils getStringUtils() {
        return stringUtils;
    }        
    
    public List<ActionRemindersBean> getConfigList() {
        return remindActionsMgr.getAllActionRemindersByProjectKey(getProjectKey());
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
    
    public void setSubmitted(String submitted) {        
        this.submitted = submitted;
    }
    
    public String getStatus() {
        return status;
    }        
}
