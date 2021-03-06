/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adsk.jira.actionreminders.plugin.api;

import com.adsk.jira.actionreminders.plugin.model.ActionRemindersBean;
import com.atlassian.activeobjects.external.ActiveObjects;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.java.ao.Query;
import org.apache.log4j.Logger;

/**
 *
 * @author prasadve
 */
public class ActionRemindersAOMgrImpl implements ActionRemindersAOMgr {
    private static final Logger LOGGER = Logger.getLogger(ActionRemindersAOMgrImpl.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final ActiveObjects ao;
    public ActionRemindersAOMgrImpl(final ActiveObjects ao) {
        this.ao = ao;
    }
    
    public ActiveObjects getActiveObjects() {
        return this.ao;
    }
    
    public static Date getDateFromString(String dateStr) {        
        // example: 2011-05-26 10:54:41+xx:xx (timezone is ignored because we parse utc timestamp date with utc parser)
        try {        
            return DATE_FORMAT.parse(dateStr);
        } catch (ParseException e) {
            LOGGER.error(e);
        }
        return null;
    }
    
    public static String getDateString(Date datetime) {
        return DATE_FORMAT.format(datetime); // example: 2011-05-26 10:54:41
    }
    
    public ActionRemindersAO[] getActiveActionReminders() {
        return ao.find(ActionRemindersAO.class, Query.select().where("ACTIVE = ?", true));
    }
    
    public ActionRemindersAO[] getAllActionReminders() {        
        return ao.find(ActionRemindersAO.class, Query.select());
    }
    
    @Override
    public List<ActionRemindersBean> getAllActionRemindersByProjectKey(String projectKey) {
        List<ActionRemindersBean> map_lists = new ArrayList<ActionRemindersBean>();
        for(ActionRemindersAO map : ao.find(ActionRemindersAO.class, Query.select()
                .where("PROJECT_KEY = ?", projectKey).order("ID DESC"))) {
            ActionRemindersBean bean = new ActionRemindersBean();
            bean.setConfigId(map.getID());
            bean.setProjectKey(map.getProjectKey());
            bean.setQuery(map.getQuery());
            bean.setConfigType(map.getConfigType());
            bean.setIssueAction(map.getIssueAction());           
            bean.setRunAuthor(map.getRunAuthor());
            bean.setCronSchedule(map.getCronSchedule());
            bean.setNotifyAssignee(map.getNotifyAssignee());
            bean.setNotifyReporter(map.getNotifyReporter());
            bean.setNotifyWatchers(map.getNotifyWatchers());
            bean.setNotifyProjectrole(map.getNotifyProjectrole());
            bean.setNotifyGroup(map.getNotifyGroup());
            bean.setMessage(map.getMessage());
            bean.setActive(map.getActive());
            map_lists.add(bean);
        }
        return map_lists;
    }
    
    @Override
    public ActionRemindersAO getActionReminderById(long mapId) {
        final ActionRemindersAO[] maps = ao.find(ActionRemindersAO.class, Query.select().where("ID = ?", mapId));
        if(maps.length > 0) {
            return maps[0];
        }
        return null;
    }

    @Override
    public void addActionReminders(ActionRemindersBean configBean) {
        final ActionRemindersAO map = ao.create(ActionRemindersAO.class);        
        map.setProjectKey(configBean.getProjectKey());
        map.setQuery(configBean.getQuery());
        map.setIssueAction(configBean.getIssueAction());
        map.setRunAuthor(configBean.getRunAuthor());
        map.setConfigType(configBean.getConfigType());
        map.setCronSchedule(configBean.getCronSchedule());
        map.setNotifyAssignee(configBean.isNotifyAssignee());
        map.setNotifyReporter(configBean.isNotifyReporter());
        map.setNotifyWatchers(configBean.isNotifyWatchers());
        map.setNotifyProjectrole(configBean.getNotifyProjectrole());
        map.setNotifyGroup(configBean.getNotifyGroup());
        map.setMessage(configBean.getMessage());
        map.setActive(configBean.isActive());
        map.save();
    }
    
    @Override
    public void updateActionReminders(ActionRemindersBean configBean) {
        final ActionRemindersAO[] maps = ao.find(ActionRemindersAO.class, Query.select().where("ID = ?", configBean.getConfigId()));
        if(maps.length > 0) {
            final ActionRemindersAO map = maps[0];        
            map.setProjectKey(configBean.getProjectKey());
            map.setQuery(configBean.getQuery());
            map.setIssueAction(configBean.getIssueAction());
            map.setRunAuthor(configBean.getRunAuthor());
            map.setConfigType(configBean.getConfigType());
            map.setCronSchedule(configBean.getCronSchedule());
            map.setNotifyAssignee(configBean.isNotifyAssignee());
            map.setNotifyReporter(configBean.isNotifyReporter());
            map.setNotifyWatchers(configBean.isNotifyWatchers());
            map.setNotifyProjectrole(configBean.getNotifyProjectrole());
            map.setNotifyGroup(configBean.getNotifyGroup());
            map.setMessage(configBean.getMessage());
            map.setActive(configBean.isActive());
            map.save();
        }
    }    

    @Override
    public void removeActionReminders(long mapId) {
        final ActionRemindersAO[] maps = ao.find(ActionRemindersAO.class, Query.select().where("ID = ?", mapId));
        if(maps.length > 0) {
            ao.delete(maps[0]);
        }
    }    
}
