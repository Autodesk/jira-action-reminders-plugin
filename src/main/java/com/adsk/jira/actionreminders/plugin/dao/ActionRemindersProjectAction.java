package com.adsk.jira.actionreminders.plugin.dao;

import com.adsk.jira.actionreminders.plugin.impl.ActionRemindersUtil;
import com.adsk.jira.actionreminders.plugin.model.ActionRemindersBean;
import com.atlassian.jira.permission.ProjectPermissions;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.opensymphony.util.TextUtils;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * @author scmenthusiast@gmail.com
 */
public class ActionRemindersProjectAction extends JiraWebActionSupport {
    
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ActionRemindersConfigAction.class);    
    private final ActionRemindersAOMgr remindActionsMgr;
    private final JiraAuthenticationContext jiraAuthenticationContext;
    private final ActionRemindersBean configBean = new ActionRemindersBean();  
    public static final TextUtils textUtils = new TextUtils();
    private String submitted;
    private String status;
    
    public ActionRemindersProjectAction(ActionRemindersAOMgr remindActionsMgr, JiraAuthenticationContext jiraAuthenticationContext) {
        this.jiraAuthenticationContext = jiraAuthenticationContext;
        this.remindActionsMgr = remindActionsMgr;
    }
        
    @Override
    public String doExecute() throws Exception {
        Project projectObj = getProjectManager().getProjectObj(configBean.getProject());
        if(projectObj == null) {
            return "error";
        }
        configBean.setProjectName(projectObj.getName());
        
        /*HttpServletRequest request = ExecutingHttpRequest.get();
        request.setAttribute((new StringBuilder())
            .append("com.atlassian.jira.projectconfig.util.ServletRequestProjectConfigRequestCache")
            .append(":project").toString(), projectObj);*/
        
        if ( !hasProjectPermission(ProjectPermissions.ADMINISTER_PROJECTS, projectObj) ) {
            return "error";
        }
                
        if (this.submitted != null && "RUN".equals(this.submitted)) {
            LOGGER.debug("Running map -> "+ configBean.getMapId() +":"+ configBean.getQuery()+":"+ configBean.isActive());
            if(configBean.getMapId() != 0) {
                ActionRemindersUtil.getInstance().run(configBean.getMapId(), 
                        configBean.isReminders(), configBean.isActions());
            }
        }        
        else if (this.submitted != null && "ADD".equals(this.submitted)) {            
            LOGGER.debug("Adding map -> "+ configBean.getQuery() +":"+configBean.getIssueAction()+":"+ configBean.isActive());
            if(remindActionsMgr.findActionReminders(configBean) == false) {
                if(configBean.getProject() != 0 && configBean.getQuery() !=null && !"".equals(configBean.getQuery())) {
                    remindActionsMgr.addActionReminders(configBean);                    
                    status = "Added.";
                }else{
                    status = "Remind action fields missing!";
                }
            }else{
                status = MessageFormat.format("{0} && {1} alredy exists in mapping!",
                        configBean.getQuery(), configBean.getIssueAction());
            }
        }
        else if (this.submitted != null && "SAVE".equals(this.submitted)) {            
            LOGGER.debug("Saving map -> "+ configBean.getMapId() +":"+ configBean.getQuery()+":"+ configBean.isActive());
            if(remindActionsMgr.findActionReminders2(configBean) == false) {
                if(configBean.getMapId() != 0 && configBean.getProject() != 0 && configBean.getQuery()!= null && !"".equals(configBean.getQuery())) {
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
        else if (this.submitted != null && "DELETE".equals(this.submitted)) {
            LOGGER.debug("Deleting map Id -> "+ configBean.getMapId());
            if(configBean.getMapId() != 0) {
                remindActionsMgr.removeActionReminders(configBean.getMapId());
                status = "Deleted.";
            }
        }
        return "success";
    }
    
    public long getMapId() {
        return configBean.getMapId();
    }

    public void setMapId(long mapId) {
        configBean.setMapId(mapId);
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
    
    public long getProject() {        
        return configBean.getProject();
    }

    public void setProject(long project) {        
        configBean.setProject(project);
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
        return ActionRemindersUtil.getInstance().getProjects();
    }
    
    public TextUtils getTextUtils() {
        return textUtils;
    }
    
    public List<ActionRemindersBean> getMapsList() {
        return remindActionsMgr.getAllActionRemindersByProject(getProject());
    }
    
    public void setSubmitted(String submitted) {
        this.submitted = submitted;
    }
    
    public String getStatus() {
        return status;
    }        
}
