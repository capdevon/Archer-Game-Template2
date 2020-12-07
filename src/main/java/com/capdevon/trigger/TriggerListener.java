/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capdevon.trigger;

/**
 *
 */
public interface TriggerListener {

    public void onTriggerEnter(EnterableTrigger trigger);

    public void onTriggerExit(EnterableTrigger trigger);

}
