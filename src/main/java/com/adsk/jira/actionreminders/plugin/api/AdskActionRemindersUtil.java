/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adsk.jira.actionreminders.plugin.api;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.mail.server.SMTPMailServer;
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
    public void run(Date last_run_datetime, Date next_run_datetime);
    public Date getNextValidTimeAfter(String cronExp, Date currentDate);
    public boolean isValidCronExp(String cronExp);    
    public void process(ActionRemindersAO map, boolean reminders, boolean actions);    
    public void sendReminders(ActionRemindersAO map, List<Issue> issues, ApplicationUser runUser);
    public void sendActions(ActionRemindersAO map, List<Issue> issues, ApplicationUser runUser);
    public static final String ENABLE_REMINDERS = "com.adsk.jira.actionreminders.plugin.enableReminders";
    public static final String ENABLE_ACTIONS = "com.adsk.jira.actionreminders.plugin.enableActions";
    public boolean getRemindersStatus();
    public boolean getActionsStatus();
    public String getResolutionId();
    public Set<String> getGroupUsers(String group);
    public Set<String> getRoleUsers(String projectKey, String projectRole);
    public Set<String> getWatchersUsers(Issue issue);    
    public void sendMail(SMTPMailServer mailServer, String emailAddr, 
            String subject, String body, String ccfrom);
    
}
