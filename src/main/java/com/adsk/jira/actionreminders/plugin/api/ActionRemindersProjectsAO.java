/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adsk.jira.actionreminders.plugin.api;

import net.java.ao.Entity;
import net.java.ao.Preload;
import net.java.ao.schema.Indexed;

/**
 *
 * @author prasadve
 */
@Preload
public class ActionRemindersProjectsAO extends Entity {
    @Indexed
    public String getProjectKey();
    public void setProjectKey(String projectKey);    
}
