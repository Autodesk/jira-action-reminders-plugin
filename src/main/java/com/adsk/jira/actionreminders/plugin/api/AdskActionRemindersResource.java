/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adsk.jira.actionreminders.plugin.api;

import static com.adsk.jira.actionreminders.plugin.api.AdskActionRemindersUtil.QUERY_LIMIT;
import com.adsk.jira.actionreminders.plugin.model.MessageBean;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.permission.ProjectPermissions;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.PermissionManager;
import com.atlassian.jira.user.ApplicationUser;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.log4j.Logger;

/**
 *
 * @author prasadve
 */

@Path("/run")
public class AdskActionRemindersResource {
    private static final Logger logger = Logger.getLogger(AdskActionRemindersResource.class);    
    private final JiraAuthenticationContext jiraAuthenticationContext;
    private final PermissionManager permissionManager;
    private final ProjectManager projectManager;
    private final ActionRemindersAOMgr aremindersao;
    private final AdskActionRemindersUtil actionRemindersUtil;
    private final ApplicationProperties properties;
    public AdskActionRemindersResource(JiraAuthenticationContext jiraAuthenticationContext, PermissionManager permissionManager, 
            ProjectManager projectManager, ActionRemindersAOMgr aremindersao, ApplicationProperties properties, 
            AdskActionRemindersUtil actionRemindersUtil) {
        this.jiraAuthenticationContext = jiraAuthenticationContext;
        this.permissionManager = permissionManager;
        this.projectManager = projectManager;
        this.aremindersao = aremindersao;
        this.properties = properties;
        this.actionRemindersUtil = actionRemindersUtil;
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/config/id/{confId}")
    public Response runActionReminder(@PathParam("confId") String confId) {
        logger.debug("running action reminder config - "+confId);
        
        //message object
        MessageBean messageBean = new MessageBean();

        //check required paramaters
        if( confId == null || "".equals(confId) ) {
            messageBean.setMessage("[Error] Required config id is missing.");
            return Response.status(Response.Status.BAD_REQUEST).entity(messageBean).build();
        }
        
        long configId;
        try {
            configId = Long.parseLong(confId);
        }catch(ClassCastException e) {
            messageBean.setMessage("[Error] Required config id is NOT number.");
            return Response.status(Response.Status.BAD_REQUEST).entity(messageBean).build();
        }
        
        ActionRemindersAO actionReminder = aremindersao.getActionReminderById(configId);
        if(actionReminder != null) {            
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
            
            int limit = 10;
            try {
                String queryLimit = properties.getString(QUERY_LIMIT);
                if(queryLimit != null) {
                    limit = Integer.parseInt(queryLimit);
                }else{
                    limit = 10;
                    properties.setString(QUERY_LIMIT, ""+limit);
                }
            }catch(ClassCastException e) {
                logger.error(e);       
            }
            actionRemindersUtil.process(actionReminder, 
                    actionRemindersUtil.getRemindersStatus(), actionRemindersUtil.getActionsStatus(), limit);

            messageBean.setMessage("successful.");
            messageBean.setStatus(1);
        } else {
            messageBean.setMessage("[Error] Invalid config id.");
            return Response.status(Response.Status.BAD_REQUEST).entity(messageBean).build();
        }
        
        return Response.ok(messageBean).build();
    }
}
