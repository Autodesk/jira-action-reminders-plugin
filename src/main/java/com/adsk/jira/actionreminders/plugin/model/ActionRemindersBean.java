package com.adsk.jira.actionreminders.plugin.model;

import java.util.Date;

public class ActionRemindersBean {
    private long configId;
    private String projectKey;
    private String projectName;
    private String query;
    private String issueAction;
    private String runAuthor;
    private Date lastRun;
    private String cronSchedule;
    private boolean notifyAssignee;
    private boolean notifyReporter;
    private boolean notifyWatchers;
    private String notifyProjectrole;
    private String notifyGroup;
    private String message;
    private boolean active;
    private boolean reminders;
    private boolean actions;

    public long getConfigId() {
        return configId;
    }

    public void setConfigId(long configId) {
        this.configId = configId;
    }        

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }        

    public String getIssueAction() {
        return issueAction;
    }

    public void setIssueAction(String issueAction) {
        this.issueAction = issueAction;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }    

    public String getRunAuthor() {
        return runAuthor;
    }

    public void setRunAuthor(String runAuthor) {
        this.runAuthor = runAuthor;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Date getLastRun() {
        return lastRun;
    }

    public void setLastRun(Date lastRun) {
        this.lastRun = lastRun;
    }

    public String getCronSchedule() {
        return cronSchedule;
    }

    public void setCronSchedule(String cronSchedule) {
        this.cronSchedule = cronSchedule;
    }

    public boolean isNotifyAssignee() {
        return notifyAssignee;
    }

    public void setNotifyAssignee(boolean notifyAssignee) {
        this.notifyAssignee = notifyAssignee;
    }

    public boolean isNotifyReporter() {
        return notifyReporter;
    }

    public void setNotifyReporter(boolean notifyReporter) {
        this.notifyReporter = notifyReporter;
    }

    public boolean isNotifyWatchers() {
        return notifyWatchers;
    }

    public void setNotifyWatchers(boolean notifyWatchers) {
        this.notifyWatchers = notifyWatchers;
    }

    public String getNotifyProjectrole() {
        return notifyProjectrole;
    }

    public void setNotifyProjectrole(String notifyProjectrole) {
        this.notifyProjectrole = notifyProjectrole;
    }

    public String getNotifyGroup() {
        return notifyGroup;
    }

    public void setNotifyGroup(String notifyGroup) {
        this.notifyGroup = notifyGroup;
    }               

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isReminders() {
        return reminders;
    }

    public void setReminders(boolean reminders) {
        this.reminders = reminders;
    }

    public boolean isActions() {
        return actions;
    }

    public void setActions(boolean actions) {
        this.actions = actions;
    }
}
