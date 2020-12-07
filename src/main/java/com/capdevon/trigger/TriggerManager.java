/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capdevon.trigger;

import com.jme3.util.SafeArrayList;
import java.util.List;

/**
 *
 */
public class TriggerManager {
    
    private final List<TriggerListener> listeners = new SafeArrayList<>(TriggerListener.class);

    // private constructor.
    private TriggerManager() {
    }

    public static TriggerManager getInstance() {
        return TriggerManagerHolder.INSTANCE;
    }

    private static class TriggerManagerHolder {
        private static final TriggerManager INSTANCE = new TriggerManager();
    }
    
    public void clear() {
        listeners.clear();
    }

    /**
     * @param trigger 
     */
    public void addListener(TriggerListener trigger) {
        if (listeners.contains(trigger)) {
            throw new IllegalArgumentException("The given listener is already registed at this Trigger");
        }
        listeners.add(trigger);
    }

    /**
     * @param trigger 
     */
    public void removeListener(TriggerListener trigger) {
        if (!listeners.remove(trigger)) {
            throw new IllegalArgumentException("The given listener is not registed at this Trigger");
        }
    }
    
    protected void notifyTriggerEnter(EnterableTrigger trigger) {
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).onTriggerEnter(trigger);
        }
    }

    protected void notifyTriggerExit(EnterableTrigger trigger) {
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).onTriggerExit(trigger);
        }
    }
}
