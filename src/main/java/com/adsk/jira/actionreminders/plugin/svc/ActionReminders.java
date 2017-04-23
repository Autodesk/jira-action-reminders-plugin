/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adsk.jira.actionreminders.plugin.svc;

import com.adsk.jira.actionreminders.plugin.impl.ActionRemindersUtil;
import com.atlassian.configurable.ObjectConfiguration;
import com.atlassian.configurable.ObjectConfigurationException;
import com.atlassian.jira.service.AbstractService;
import com.opensymphony.module.propertyset.PropertySet;
import org.apache.log4j.Logger;

/**
 *
 * @author prasadve
 */
public class ActionReminders extends AbstractService {
    
    private static final Logger LOGGER = Logger.getLogger(ActionReminders.class);
    // Map strings for listener properties
    public static final String ENABLE_REMINDERS = "enable_reminders";
    public static final String ENABLE_ACTIONS = "enable_actions";
    private boolean reminders = false;
    private boolean actions = false;
    
    @Override
    public void init(PropertySet props) throws ObjectConfigurationException {
        super.init(props);
        if (hasProperty(ENABLE_REMINDERS)){
            reminders = Boolean.parseBoolean(getProperty(ENABLE_REMINDERS));
        }
        if (hasProperty(ENABLE_ACTIONS)){
            actions = Boolean.parseBoolean(getProperty(ENABLE_ACTIONS));
        }
    }
    
    @Override
    public void run() {
        ActionRemindersUtil.getInstance().run(reminders, actions);        
    }
    

    public ObjectConfiguration getObjectConfiguration() throws ObjectConfigurationException {
        return getObjectConfiguration("ActionRemindersService0001", "jira-action-reminders-service.xml", null);
    }
    
}
