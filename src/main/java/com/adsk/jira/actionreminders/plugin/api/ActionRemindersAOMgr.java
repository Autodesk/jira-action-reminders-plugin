/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adsk.jira.actionreminders.plugin.api;

import com.adsk.jira.actionreminders.plugin.model.ActionRemindersBean;
import com.atlassian.activeobjects.external.ActiveObjects;
import java.util.List;

/**
 *
 * @author prasadve
 */
public interface ActionRemindersAOMgr {
    public ActiveObjects getActiveObjects();
    public ActionRemindersAO[] getActiveActionReminders();
    public ActionRemindersAO[] getAllActionReminders();
    public List<ActionRemindersBean> getAllActionRemindersByProjectKey(String projectKey);
    public ActionRemindersAO getActionReminderById(long mapId);
    public void addActionReminders(ActionRemindersBean configBean);
    public void updateActionReminders(ActionRemindersBean configBean);
    public void removeActionReminders(long mapId);
    public void setActionRemindersLastRun(long mapId);
}
