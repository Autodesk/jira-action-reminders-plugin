/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adsk.jira.actionreminders.plugin.schedule;

import com.atlassian.sal.api.scheduling.PluginJob;
import java.util.Date;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author prasadve
 */
public class ActionRemindersService implements PluginJob {
    private static final Logger logger = Logger.getLogger(ActionRemindersService.class);
    
    public void execute(Map<String, Object> jobDataMap) {
        
        final ActionRemindersScheduler sch = (ActionRemindersSchedulerImpl) 
                jobDataMap.get(ActionRemindersSchedulerImpl.KEY);
        
        assert sch != null;
        
        sch.setLastRun(new Date());        
        logger.info("Okta Groups Sync Service Interval : "+ sch.getInterval());
        
        sch.getActionRemindersUtil().run(true, true);
    }
    
}
