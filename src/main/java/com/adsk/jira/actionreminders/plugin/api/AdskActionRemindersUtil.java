/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adsk.jira.actionreminders.plugin.api;

import com.adsk.jira.actionreminders.plugin.model.ActionRemindersBean;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.user.ApplicationUser;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 *
 * @author prasadve
 */
public interface AdskActionRemindersUtil {
    
    public String getDateString(Date datetime);
    public List<Project> getProjects();
    public void run(boolean reminders, boolean actions);
    public void run(long configId, boolean reminders, boolean actions);
    public void process(ActionRemindersBean map, boolean reminders, boolean actions);
    public String getResolutionId();
    public void sendReminders(ActionRemindersBean map, Issue issue, ApplicationUser runUser);
    public Set<String> getGroupUsers(String group);
    public ProjectRole getProjectRole(String projectRole);
    public Set<String> getRoleUsers(String projectKey, String projectRole);
    public Set<String> getWatchersUsers(Issue issue);
    public void sendMail(String emailAddr, String subject, String body, String ccfrom);
    
}
