/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adsk.jira.actionreminders.plugin.dao;

import com.adsk.jira.actionreminders.plugin.model.ActionRemindersBean;
import com.atlassian.activeobjects.external.ActiveObjects;
import java.util.List;

/**
 *
 * @author prasadve
 */
public interface ActionRemindersAOMgr {
    public ActiveObjects getActiveObjects();
    public List<ActionRemindersBean> getActiveRemindActions();
    public List<ActionRemindersBean> getAllRemindActions();
    public void addRemindActionMap(ActionRemindersBean configBean);
    public void updateRemindActionMap(ActionRemindersBean configBean);
    public boolean findRemindActionMap(ActionRemindersBean configBean);
    public boolean findRemindActionMap2(ActionRemindersBean configBean);
    public void removeRemindActionMap(long mapId);
}
