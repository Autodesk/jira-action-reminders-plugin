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
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.jira.workflow.IssueWorkflowManager;
import com.opensymphony.workflow.loader.ActionDescriptor;
import java.util.Collection;
import java.util.List;
import org.apache.log4j.Logger;
import com.adsk.jira.actionreminders.plugin.dao.ActionRemindersAOMgr;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.issue.watchers.WatcherManager;
import com.atlassian.jira.mail.Email;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.ProjectRoleActor;
import com.atlassian.jira.security.roles.ProjectRoleActors;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.security.roles.RoleActor;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.mail.MailException;
import com.atlassian.mail.server.SMTPMailServer;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang.time.DateUtils;


/**
 *
 * @author prasadve
 */
public class ActionRemindersUtil {
    private static final Logger LOGGER = Logger.getLogger(ActionRemindersUtil.class);
    private static ActionRemindersUtil remindUtils = null;        
    private final ProjectManager projectManager = ComponentAccessor.getProjectManager();
    private final ProjectRoleManager projectRoleManager = ComponentAccessor.getComponentOfType(ProjectRoleManager.class);
    private final JiraAuthenticationContext jiraAuthenticationContext = ComponentAccessor.getJiraAuthenticationContext();
    private final IssueWorkflowManager issueWorkflowManager = ComponentAccessor.getComponentOfType(IssueWorkflowManager.class);
    private final SMTPMailServer mailServer = ComponentAccessor.getMailServerManager().getDefaultSMTPMailServer();
    private final ApplicationProperties properties = ComponentAccessor.getApplicationProperties();
    private final UserManager userManager = ComponentAccessor.getUserManager();
    private final GroupManager groupManager = ComponentAccessor.getGroupManager();
    private final WatcherManager watcherManager = ComponentAccessor.getWatcherManager();
    private final String BaseUrl = properties.getString(APKeys.JIRA_BASEURL); //"jira.baseurl"
    private final ApplicationUser loggedInAppUser = jiraAuthenticationContext.getLoggedInUser();
    private final SearchService searchService = ComponentAccessor.getComponent(SearchService.class);
    private final ActionRemindersAOMgr remindActionsDAO;
    public ActionRemindersUtil() {
        remindActionsDAO = ComponentAccessor.getOSGiComponentInstanceOfType(ActionRemindersAOMgr.class);
    }
    
    public static ActionRemindersUtil getInstance() {
        if( remindUtils == null ) {
            remindUtils = new ActionRemindersUtil();
        }
        return remindUtils;
    }
    
    public List<Project> getProjects() {        
       return projectManager.getProjects();
    }
    
    public void run() {
        System.out.println("Running now....");
        List<ActionRemindersBean> remindActionList = remindActionsDAO.getActiveActionReminders();
        
        for(ActionRemindersBean map : remindActionList) {
            LOGGER.info("processing -> "+ map.getQuery());
            process(map);
        }
        
    }
    
    public void process(ActionRemindersBean map) {
        //List<Issue> issues = null;        
        ApplicationUser runAppUser = userManager.getUserByName(map.getRunAuthor());
        if(runAppUser == null){
            LOGGER.debug(map.getRunAuthor()+" - Run Author is Null/not exists!");
            return;
        }
        
        try {
            SearchService.ParseResult parseResult =  searchService.parseQuery(runAppUser, map.getQuery());
            if (parseResult.isValid()) {
                //issues = new ArrayList<Issue>();     
                SearchResults searchResults = searchService.search(runAppUser, parseResult.getQuery(), PagerFilter.newPageAlignedFilter(0, 1000));
                for(Issue i : searchResults.getIssues()) {
                    LOGGER.debug("processing issue -> "+i.getKey());
                    
                    if(map.getIssueAction() !=null && !"".equals(map.getIssueAction())) { // Transition Action
                        LOGGER.debug("processing transition action -> "+ map.getIssueAction());
                        Collection<ActionDescriptor> statuses = issueWorkflowManager.getAvailableActions(i, runAppUser);
                        boolean transition = false;
                        ActionDescriptor tActionDescriptor = null;
                        for(ActionDescriptor ad : statuses) {
                            if(ad.getName().equalsIgnoreCase(map.getIssueAction())) {
                                tActionDescriptor = ad;
                                transition = true;
                            }
                        }
                        if(transition == true) {
                            if(tActionDescriptor != null) {
                                LOGGER.info(tActionDescriptor.getId() +" *** "+ tActionDescriptor.getName());
                            }
                        }
                    
                    }else{                                            
                        processReminderEmail(map, i); // Remind or re-notify
                    }
                }
            }
        }
        catch(SearchException e) {
            LOGGER.error(e.getLocalizedMessage());
        }
        
        //return issues;
    }
    
    public void processReminderEmail(ActionRemindersBean map, Issue issue) {
        String subject = MessageFormat.format("({0}) {1}", issue.getKey(), issue.getSummary());
        String issueLink = MessageFormat.format("{0}/browse/{1}", BaseUrl, issue.getKey());
        String body = MessageFormat.format("{0}\n\n{1}", map.getMessage(), issueLink);
        ApplicationUser user = userManager.getUserByName(map.getRunAuthor());
        String ccfrom = user != null ? user.getEmailAddress() : "";
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
        LOGGER.debug("Executions count size - "+ map.getExecCount());
        
        if(map.getExecCount() == 1) {            
            if(map.getLastRun() != null) {
                LOGGER.debug("Last execution run date time - "+ map.getLastRun().toString());
                if (!DateUtils.isSameDay(map.getLastRun(), new Date())) {
                    LOGGER.debug("Last execution run date time - SAME DAY i.e. "+ (new Date()).toString());
                    for(String email : emailAddrs) {
                        sendMail(email, subject, body, ccfrom);
                    }
                }
            }else{
                LOGGER.debug("Last execution run date is null so sending now.");
                for(String email : emailAddrs) {
                    sendMail(email, subject, body, ccfrom);
                }
            }
        }else{
            LOGGER.debug("Executions count size is 0 so sending email now");
            for(String email : emailAddrs) {
                sendMail(email, subject, body, ccfrom);
            }
        }
    }
    
    public Set<String> getGroupUsers(String group) {
        Set<String> users = new HashSet<String>();
        Collection<ApplicationUser> groupUsers = groupManager.getUsersInGroup(group);
        LOGGER.debug("Group users size - "+ group +" : "+ groupUsers.size());
        for(ApplicationUser au : groupUsers){
            users.add(au.getEmailAddress());
        }
        return users;
    }
    
    public Set<String> getRoleUsers(long projectId, String projectRole) {                         
        Set<String> users = new HashSet<String>();
        if(!"ADMINISTRATORS".equalsIgnoreCase(projectRole) && !"DEVELOPERS".equalsIgnoreCase(projectRole) && !"USERS".equalsIgnoreCase(projectRole)) {
            Project projectObject = projectManager.getProjectObj(projectId);
            ProjectRole devRole = projectRoleManager.getProjectRole(projectRole);
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
