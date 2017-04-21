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
import com.atlassian.jira.issue.status.Status;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.jira.workflow.IssueWorkflowManager;
import com.atlassian.jira.workflow.WorkflowManager;
import com.opensymphony.workflow.loader.ActionDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.log4j.Logger;
import com.adsk.jira.actionreminders.plugin.dao.ActionRemindersAOMgr;
import com.adsk.jira.actionreminders.plugin.dao.ActionRemindersAO;

/**
 *
 * @author prasadve
 */
public class ActionRemindersUtil {
    private static final Logger LOGGER = Logger.getLogger(ActionRemindersUtil.class);
    private static ActionRemindersUtil remindUtils = null;        
    private final ProjectManager projectManager = ComponentAccessor.getProjectManager();
    private final JiraAuthenticationContext jiraAuthenticationContext = ComponentAccessor.getJiraAuthenticationContext();
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
        List<ActionRemindersBean> remindActionList = remindActionsDAO.getActiveRemindActions();
        
        for(ActionRemindersBean map : remindActionList) {
            LOGGER.info("processing -> "+ map.getQuery());
            getIssues(map.getQuery());
        }
        
    }
    
    public void getIssues(String q) {
        //List<Issue> issues = null;        
        IssueWorkflowManager issueWorkflowManager = ComponentAccessor
				.getComponentOfType(IssueWorkflowManager.class);
        try {
            SearchService.ParseResult parseResult =  searchService.parseQuery(loggedInAppUser, q);
            if (parseResult.isValid()) {
                //issues = new ArrayList<Issue>();     
                SearchResults searchResults = searchService.search(loggedInAppUser, parseResult.getQuery(), PagerFilter.newPageAlignedFilter(0, 1000));
                for(Issue i : searchResults.getIssues()) {
                    System.out.println("processing issue -> "+i.getKey());
                    
                    Collection<ActionDescriptor> statuses = issueWorkflowManager.getAvailableActions(i, loggedInAppUser);
                    
                    for(ActionDescriptor s : statuses) {
                        LOGGER.info(s.getId() +" *** "+ s.getName());
                    }
                }
            }
        }
        catch(SearchException e) {
            LOGGER.error(e.getLocalizedMessage());
        }
        
        //return issues;
    }
}
