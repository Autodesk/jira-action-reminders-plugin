/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adsk.jira.actionreminders.plugin.schedule;

/**
 *
 * @author prasadve
 */
public interface AdskActionRemindersScheduler {
    
    public long getInterval();
    
    public void reschedule();
    
}
