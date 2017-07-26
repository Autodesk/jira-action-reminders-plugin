package com.adsk.jira.actionreminders.plugin.api;

import com.atlassian.jira.permission.ProjectPermissions;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.opensymphony.util.TextUtils;
import org.apache.log4j.Logger;

/**
 * @author scmenthusiast@gmail.com
 */
public class ActionRemindersTestAction extends JiraWebActionSupport {
    
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ActionRemindersTestAction.class); 
    public static final TextUtils textUtils = new TextUtils();
    private long projectId;
    private String projectKey;
    private String status;
        
    @Override
    public String doExecute() throws Exception {
        Project project = getProjectManager().getProjectObj(projectId);
        if(project == null) {
            return "error";
        }        
        
        if ( !hasProjectPermission(ProjectPermissions.ADMINISTER_PROJECTS, project) ) {
            return "error";
        }
        
        return "success";
    }
    
    public long getProjectId() {        
        return projectId;
    }

    public void setProjectId(long projectId) {        
        this.projectId = projectId;
    }
    
    public String getProjectKey() {        
        return projectKey;
    }

    public void setProjectKey(String projectKey) {        
        this.projectKey = projectKey;
    }
    
    public String getStatus() {
        return status;
    }        
}
