package com.adsk.jira.actionreminders.plugin.model;

import java.util.Date;

public class ActionRemindersBean {
    private long mapId;
    private long project;
    private String projectName;
    private String query;
    private long filterId;
    private String issueAction;
    private String runAuthor;
    private Date lastRun;
    private long execCount;
    private boolean notifyAssignee;
    private boolean notifyReporter;
    private boolean notifyWatchers;
    private String notifyProjectrole;
    private String notifyGroup;
    private String message;
    private boolean active;
    
    public long getMapId() {
        return mapId;
    }

    public void setMapId(long mapId) {
        this.mapId = mapId;
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

    public long getProject() {
        return project;
    }

    public void setProject(long project) {
        this.project = project;
    }

    public long getFilterId() {
        return filterId;
    }

    public void setFilterId(long filterId) {
        this.filterId = filterId;
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

    public long getExecCount() {
        return execCount;
    }

    public void setExecCount(long execCount) {
        this.execCount = execCount;
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
}
