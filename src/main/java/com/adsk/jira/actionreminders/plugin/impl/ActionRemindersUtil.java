/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adsk.jira.actionreminders.plugin.impl;

import com.adsk.jira.actionreminders.plugin.model.ActionRemindersBean;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.jira.workflow.IssueWorkflowManager;
import com.opensymphony.workflow.loader.ActionDescriptor;
import java.util.Collection;
import java.util.List;
import org.apache.log4j.Logger;
import com.adsk.jira.actionreminders.plugin.dao.ActionRemindersAOMgr;
import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.issue.IssueInputParameters;
import com.atlassian.jira.issue.resolution.Resolution;
import com.atlassian.jira.issue.watchers.WatcherManager;
import com.atlassian.jira.mail.Email;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.ProjectRoleActors;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.workflow.WorkflowManager;
import com.atlassian.mail.MailException;
import com.atlassian.mail.server.SMTPMailServer;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


/**
 *
 * @author prasadve
 */
public final class ActionRemindersUtil {
    private static final Logger LOGGER = Logger.getLogger(ActionRemindersUtil.class);
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");    
    private static ActionRemindersUtil remindUtils = null;
    private static String defaultResolution = "1";
    private final ProjectManager projectManager = ComponentAccessor.getProjectManager();
    private final IssueService issueService = ComponentAccessor.getIssueService();
    private final ConstantsManager constantsManager = ComponentAccessor.getConstantsManager();
    private final ProjectRoleManager projectRoleManager = ComponentAccessor.getComponentOfType(ProjectRoleManager.class);
    private final WorkflowManager workflowManager = ComponentAccessor.getWorkflowManager();
    private final IssueWorkflowManager issueWorkflowManager = ComponentAccessor.getComponentOfType(IssueWorkflowManager.class);    
    private final SMTPMailServer mailServer = ComponentAccessor.getMailServerManager().getDefaultSMTPMailServer();
    private final ApplicationProperties properties = ComponentAccessor.getApplicationProperties();
    private final UserManager userManager = ComponentAccessor.getUserManager();
    private final GroupManager groupManager = ComponentAccessor.getGroupManager();
    private final WatcherManager watcherManager = ComponentAccessor.getWatcherManager();
    private final String BaseUrl = properties.getString(APKeys.JIRA_BASEURL); //"jira.baseurl"   
    private final SearchService searchService = ComponentAccessor.getComponent(SearchService.class);
    
    private final ActionRemindersAOMgr remindActionsDAO;
    public ActionRemindersUtil() {
        remindActionsDAO = ComponentAccessor.getOSGiComponentInstanceOfType(ActionRemindersAOMgr.class);
        defaultResolution = getResolutionId();
    }
    
    public static ActionRemindersUtil getInstance() {
        if( remindUtils == null ) {
            remindUtils = new ActionRemindersUtil();
        }
        return remindUtils;
    }
    
    public static String getDateString(Date datetime) {
        return DATE_FORMAT.format(datetime); // example: 2011-05-26
    }
    
    public List<Project> getProjects() {        
       return projectManager.getProjects();
    }
    
    public void run() {
        long startTime = System.currentTimeMillis();
        LOGGER.debug("Running service now....");
        
        List<ActionRemindersBean> remindActionList = remindActionsDAO.getActiveActionReminders();        
        for(ActionRemindersBean map : remindActionList) {
            LOGGER.debug("Processing original query -> "+ map.getQuery());
            process(map);
        }
        
        long totalTime = System.currentTimeMillis() - startTime;
        LOGGER.info("Service Finished. Took "+ totalTime/ 1000d +" Seconds");
    }
    
    public void run(long mapId) {
        long startTime = System.currentTimeMillis();
        LOGGER.debug("Running map Id: "+ mapId);
        
        ActionRemindersBean remindAction = remindActionsDAO.getActiveActionReminderById(mapId);        
        if(remindAction != null) {
            LOGGER.debug("Processing original query -> "+ remindAction.getQuery());
            process(remindAction);
        }
        
        long totalTime = System.currentTimeMillis() - startTime;
        LOGGER.info("Service Finished. Took "+ totalTime/ 1000d +" Seconds");
    }
    
    public void process(ActionRemindersBean map) {       
        ApplicationUser runAppUser = userManager.getUserByName(map.getRunAuthor());
        if(runAppUser == null){
            LOGGER.debug(map.getRunAuthor()+" - Run Author is Null/not exists!");
            return;
        }
        
        Project projectObj = projectManager.getProjectObj(map.getProject());
        if(projectObj == null){
            LOGGER.debug(map.getProject()+" - Project is Null/not exists!");
            return;
        }
        
        if(map.getExecCount() == 1) {
            if(map.getLastRun() != null) {
                if(getDateString(map.getLastRun()).equals(getDateString(new Date()))) {
                    LOGGER.debug("+++Last execution run date time is SAME DAY i.e. "+ map.getLastRun().toString());
                    return;
                }
            }
        }
        
        boolean is_issue_action = false;
        if(map.getIssueAction() != null && !"".equals(map.getIssueAction())) {
            is_issue_action = true;
        }
        
        try {
            String secureQuery = MessageFormat.format("project = {0} AND {1}", projectObj.getKey(), map.getQuery());
            
            SearchService.ParseResult parseResult =  searchService.parseQuery(runAppUser, secureQuery);
            
            if (parseResult.isValid()) {
                LOGGER.debug("Processing secure query -> "+ parseResult.getQuery());
                
                SearchResults searchResults = searchService.search(runAppUser, parseResult.getQuery(), PagerFilter.newPageAlignedFilter(0, 1000));
                
                for(Issue issue : searchResults.getIssues()) {
                    LOGGER.debug("processing issue -> "+ issue.getKey());
                                        
                    if(is_issue_action == true) { // Transition Action                        
                        LOGGER.debug("processing transition action -> "+ map.getIssueAction());                                                
                        
                        Collection<ActionDescriptor> ActionDescriptors = workflowManager.getWorkflow(issue).getActionsByName(map.getIssueAction());
                        boolean is_action_exists = false;
                        
                        for(ActionDescriptor actionDescriptor : ActionDescriptors) {                            
                            if(issueWorkflowManager.isValidAction(issue, actionDescriptor.getId(), runAppUser)) {
                                LOGGER.info("action is valid - "+ actionDescriptor.getName() +" : "+ actionDescriptor.getId()); 
                                is_action_exists = true;
                                IssueInputParameters issueInputParameters = issueService.newIssueInputParameters();
                                issueInputParameters.setRetainExistingValuesWhenParameterNotProvided(true);                                                                
                                issueInputParameters.setResolutionId(defaultResolution);
                                issueInputParameters.setComment(map.getMessage());

                                IssueService.TransitionValidationResult validation = issueService.validateTransition(runAppUser, issue.getId(), 
                                        actionDescriptor.getId(), issueInputParameters);                                                                        

                                if (validation.isValid()) {
                                    IssueService.IssueResult issueResult = issueService.transition(runAppUser, validation);
                                    if (issueResult.isValid()) {
                                        LOGGER.debug("Transition successful.");
                                        for(String e : issueResult.getErrorCollection().getErrorMessages()) {
                                            LOGGER.debug(e);
                                        }
                                    }
                                } else {
                                    LOGGER.debug("Transition validation errors: ");
                                    for(String e : validation.getErrorCollection().getErrorMessages()) {
                                        LOGGER.debug(e);
                                    }
                                }
                                break;
                            }                            
                        }
                        
                        if( is_action_exists == false ) {    
                            LOGGER.debug("Transition action is not valid - "+ map.getIssueAction());
                        }
                    
                    } else {
                        
                        LOGGER.debug("Execution count is 0 so sending now.");
                        sendReminders(map, issue, runAppUser); // Remind or re-notify
                    }
                    
                    remindActionsDAO.setActionRemindersLastRun(map.getMapId()); // set last run
                }
            }
        }
        catch(SearchException e) {
            LOGGER.error(e.getLocalizedMessage());
        }                
    }
    
    public String getResolutionId() {
        Collection<Resolution> resolutions = constantsManager.getResolutions();
        for(Resolution r : resolutions){
            return r.getId();
        }
        return "1";
    }
    
    public void sendReminders(ActionRemindersBean map, Issue issue, ApplicationUser runUser) {
        String subject = MessageFormat.format("({0}) {1}", issue.getKey(), issue.getSummary());
        String issueLink = MessageFormat.format("{0}/browse/{1}", BaseUrl, issue.getKey());
        String body = MessageFormat.format("{0}\n\n{1}", map.getMessage(), issueLink);        
        String ccfrom = runUser != null ? runUser.getEmailAddress() : "";
        Set<String> emailAddrs = new HashSet<String>();
        
        if(map.isNotifyAssignee()) {
            if(issue.getAssigneeUser() != null) {
                emailAddrs.add(issue.getAssigneeUser().getEmailAddress());
            }
        }
        
        if(map.isNotifyReporter()) {
            if(issue.getReporterUser() != null) {
                emailAddrs.add(issue.getReporterUser().getEmailAddress());
            }
        }
        
        if(map.isNotifyWatchers()) {
            emailAddrs.addAll(getWatchersUsers(issue));
        }
        
        if(map.getNotifyProjectrole() != null && !"".equals(map.getNotifyProjectrole())) {
            emailAddrs.addAll(getRoleUsers(map.getProject(), map.getNotifyProjectrole()));
        }
        
        if(map.getNotifyGroup()!= null && !"".equals(map.getNotifyGroup())) {
            emailAddrs.addAll(getGroupUsers(map.getNotifyGroup()));
        }
        
        LOGGER.debug("Total email users size - "+ emailAddrs.size());
        
        for(String email : emailAddrs) {
            sendMail(email, subject, body, ccfrom);
        }
    }
    
    public Set<String> getGroupUsers(String group) {
        Set<String> users = new HashSet<String>();
        if(!"jira-administrators".equalsIgnoreCase(group) && !"jira-developers".equalsIgnoreCase(group) && !"jira-users".equalsIgnoreCase(group)) {
            Collection<ApplicationUser> groupUsers = groupManager.getUsersInGroup(group);
            LOGGER.debug("Group users size - "+ group +" : "+ groupUsers.size());
            for(ApplicationUser au : groupUsers){
                users.add(au.getEmailAddress());
            }
        }else{
            LOGGER.warn(group +" - Default jira groups are not supported!");
        }
        return users;
    }
    
    public ProjectRole getProjectRole(String projectRole) {
        Collection<ProjectRole> projectRoles = projectRoleManager.getProjectRoles();
        for(ProjectRole role : projectRoles){
            if(role.getName().equalsIgnoreCase(projectRole)){
                return role;
            }
        }
        return null;
    }
    
    public Set<String> getRoleUsers(long projectId, String projectRole) {                         
        Set<String> users = new HashSet<String>();
        if(!"ADMINISTRATORS".equalsIgnoreCase(projectRole) && !"DEVELOPERS".equalsIgnoreCase(projectRole) && !"USERS".equalsIgnoreCase(projectRole)) {
            Project projectObject = projectManager.getProjectObj(projectId);
            ProjectRole devRole = getProjectRole(projectRole);
            if(devRole != null) {
                LOGGER.debug("Project role name: "+ devRole.getName());
                ProjectRoleActors roleActors = projectRoleManager.getProjectRoleActors(devRole, projectObject);
                Set<ApplicationUser> actors = roleActors.getApplicationUsers();
                LOGGER.debug("Project role users size - "+ projectRole +" : "+ actors.size());
                for(ApplicationUser au : actors){
                    users.add(au.getEmailAddress());
                }
            }else{
                LOGGER.debug("Project role is not exists! "+ projectRole);
            }
        }else{
            LOGGER.warn(projectId +":"+ projectRole +" - Default project role does not supported!");
        }
        return users;
    }
    
    public Set<String> getWatchersUsers(Issue issue) {
        Set<String> users = new HashSet<String>();
        List<String> watchUsers = watcherManager.getCurrentWatcherUsernames(issue);
        LOGGER.debug("Issue watchers size - "+ issue.getKey() +" : "+ watchUsers.size());
        for(String user : watchUsers) {
            ApplicationUser au = userManager.getUserByName(user);
            if(au != null) {
                users.add(au.getEmailAddress());
            }
        }
        return users;
    }
    
    public void sendMail(String emailAddr, String subject, String body, String ccfrom) {
        try {            
            if (mailServer != null) {
                Email email = new Email(emailAddr);
                email.setSubject(subject);
                email.setBody(body);
                email.setFrom(ccfrom);
                email.setCc(ccfrom);                
                mailServer.send(email);
                LOGGER.debug("Mail sent To: "+ emailAddr +" Cc: "+ ccfrom);
            } else {
                LOGGER.warn("Please make sure that a valid mailServer is configured");
            }
        } catch (MailException ex) {
            LOGGER.error(ex.getLocalizedMessage());
        }
    }
    
}
