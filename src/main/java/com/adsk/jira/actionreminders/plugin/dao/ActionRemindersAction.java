package com.adsk.jira.actionreminders.plugin.dao;

import com.adsk.jira.actionreminders.plugin.impl.ActionRemindersUtil;
import com.adsk.jira.actionreminders.plugin.model.ActionRemindersBean;
import com.atlassian.jira.permission.GlobalPermissionKey;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.opensymphony.util.TextUtils;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * @author scmenthusiast@gmail.com
 */
public class ActionRemindersAction extends JiraWebActionSupport {
    
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ActionRemindersAction.class);
    private final ActionRemindersAOMgr remindActionsMgr;    
    private final ActionRemindersBean configBean = new ActionRemindersBean();  
    public static final TextUtils textUtils = new TextUtils();
    private String submitted;
    private String status;
    
    public ActionRemindersAction(ActionRemindersAOMgr remindActionsMgr) {
        this.remindActionsMgr = remindActionsMgr;
    }
        
    @Override
    public String doExecute() throws Exception {
        if ( !hasGlobalPermission(GlobalPermissionKey.ADMINISTER) ) {
            return "error";
        }
        
        //testing
        if (this.submitted != null && "RUN".equals(this.submitted)) {
            ActionRemindersUtil.getInstance().run();    
        }
        
        if (this.submitted != null && "ADD".equals(this.submitted)) {            
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
        if (this.submitted != null && "SAVE".equals(this.submitted)) {            
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
        if (this.submitted != null && "DEL".equals(this.submitted)) {
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

    public String getProjectName() {
        return configBean.getProjectName();
    }        
    
    public Date getLastRun() {
        return configBean.getLastRun();
    }

    public void setLastRun(Date lastRun) {
        configBean.setLastRun(lastRun);
    }

    public long getExecCount() {
        return configBean.getExecCount();
    }

    public void setExecCount(long execCount) {
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
    
    
    public List<Project> getProjects() {
        return ActionRemindersUtil.getInstance().getProjects();
    }
    
    public TextUtils getTextUtils() {
        return textUtils;
    }
    
    public List<ActionRemindersBean> getMapsList() {
        return remindActionsMgr.getAllActionReminders();
    }
    
    public void setSubmitted(String submitted) {
        this.submitted = submitted;
    }
    
    public String getStatus() {
        return status;
    }    
}
