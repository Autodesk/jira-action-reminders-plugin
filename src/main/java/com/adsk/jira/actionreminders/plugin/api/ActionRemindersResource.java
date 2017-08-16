/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adsk.jira.actionreminders.plugin.api;

import com.adsk.jira.actionreminders.plugin.model.ActionRemindersBean;
import com.adsk.jira.actionreminders.plugin.model.ActionRemindersRun;
import com.adsk.jira.actionreminders.plugin.model.MessageBean;
import com.atlassian.jira.permission.ProjectPermissions;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.PermissionManager;
import com.atlassian.jira.user.ApplicationUser;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.log4j.Logger;

/**
 *
 * @author prasadve
 */

@Path("/run")
public class ActionRemindersResource {
    private static final Logger LOGGER = Logger.getLogger(ActionRemindersResource.class);    
    private final JiraAuthenticationContext jiraAuthenticationContext;
    private final PermissionManager permissionManager;
    private final ProjectManager projectManager;
    private final ActionRemindersAOMgr aremindersao;
    private final ActionRemindersUtil actionRemindersUtil;
    
    public ActionRemindersResource(JiraAuthenticationContext jiraAuthenticationContext, PermissionManager permissionManager, 
            ProjectManager projectManager, ActionRemindersAOMgr aremindersao, 
            ActionRemindersUtil actionRemindersUtil) {
        this.jiraAuthenticationContext = jiraAuthenticationContext;
        this.permissionManager = permissionManager;
        this.projectManager = projectManager;
        this.aremindersao = aremindersao;
        this.actionRemindersUtil = actionRemindersUtil;
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/config")
    public Response runActionReminder(ActionRemindersRun actionRemindersRun) {
        LOGGER.debug("running action reminder - "+actionRemindersRun.getId());
        
        //message object
        MessageBean messageBean = new MessageBean();
        
        //check required paramaters
        if( actionRemindersRun.getId() == 0 ) {
            messageBean.setMessage("[Error] Required mapId number field is missing.");
            return Response.status(Response.Status.BAD_REQUEST).entity(messageBean).build();
        }
        if( actionRemindersRun.isActions() == false && actionRemindersRun.isReminders() == false ) {
            messageBean.setMessage("[Error] Required either options actions or reminders.");
            return Response.status(Response.Status.BAD_REQUEST).entity(messageBean).build();
        }
        
        ActionRemindersBean actionReminder = aremindersao.getActionReminderById(actionRemindersRun.getId());
        
        //check project permissions 
        ApplicationUser loggedInAppUser = jiraAuthenticationContext.getLoggedInUser();
        Project project = projectManager.getProjectObjByKey(actionReminder.getProjectKey());
        if( project != null ) {
            if( permissionManager.hasPermission(ProjectPermissions.ADMINISTER_PROJECTS, project, loggedInAppUser) == false ) {
                messageBean.setMessage("[Error] Permission denied. Project access is required.");
                return Response.status(Response.Status.FORBIDDEN).entity(messageBean).build();
            }
        } else {
            messageBean.setMessage("[Error] Permission denied. Project is invalid.");
            return Response.status(Response.Status.FORBIDDEN).entity(messageBean).build();
        }
        
        actionRemindersUtil.process(actionReminder, 
                actionRemindersRun.isReminders(), actionRemindersRun.isActions());
        
        messageBean.setMessage("successful.");
        messageBean.setStatus(1);
        
        return Response.ok(messageBean).build();
    }
}
