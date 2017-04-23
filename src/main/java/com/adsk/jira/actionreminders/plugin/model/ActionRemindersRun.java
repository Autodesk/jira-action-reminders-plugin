/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adsk.jira.actionreminders.plugin.model;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 *
 * @author prasadve
 */

@JsonAutoDetect
public class ActionRemindersRun {
    @JsonProperty
    private long id;
    @JsonProperty
    private boolean actions;
    @JsonProperty
    private boolean reminders;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }    

    public boolean isActions() {
        return actions;
    }

    public void setActions(boolean actions) {
        this.actions = actions;
    }

    public boolean isReminders() {
        return reminders;
    }

    public void setReminders(boolean reminders) {
        this.reminders = reminders;
    }        
}
