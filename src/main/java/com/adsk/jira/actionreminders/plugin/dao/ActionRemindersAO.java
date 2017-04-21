/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adsk.jira.actionreminders.plugin.dao;


import java.util.Date;
import net.java.ao.Entity;
import net.java.ao.Preload;
import net.java.ao.schema.StringLength;

/**
 *
 * @author prasadve
 */
@Preload
public interface ActionRemindersAO extends Entity {
    public long getProject();
    public void setProject(long project);
    public String getIssueAction();
    public void setIssueAction(String issueAction);
    @StringLength(StringLength.UNLIMITED)
    public String getQuery();
    @StringLength(StringLength.UNLIMITED)
    public void setQuery(String query);
    public String getRunAuthor();
    public void setRunAuthor(String runAuthor);
    public Date getLastRun();
    public void setLastRun(Date lastRun);
    public long getExecCount();
    public void setExecCount(long execCount);
    public boolean getNotifyAssignee();
    public void setNotifyAssignee(boolean notifyAssignee);
    public boolean getNotifyReporter();
    public void setNotifyReporter(boolean notifyReporter);
    public boolean getNotifyWatchers();
    public void setNotifyWatchers(boolean notifyWatchers);
    public String getNotifyProjectrole();
    public void setNotifyProjectrole(String notifyProjectrole);
    public String getNotifyGroup();
    public void setNotifyGroup(String notifyGroup);
    @StringLength(StringLength.UNLIMITED)
    public String getMessage();
    @StringLength(StringLength.UNLIMITED)
    public void setMessage(String message);
    public boolean getActive();
    public void setActive(boolean active);
}
