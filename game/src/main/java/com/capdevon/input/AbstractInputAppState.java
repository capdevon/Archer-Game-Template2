package com.capdevon.input;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.input.Joystick;
import com.jme3.input.JoystickAxis;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.Trigger;
import com.jme3.util.SafeArrayList;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author capdevon
 */
public abstract class AbstractInputAppState extends AbstractAppState implements AnalogListener, ActionListener {

    private InputManager inputManager;
    private List<ActionListener> actionListeners = new SafeArrayList<>(ActionListener.class);
    private List<AnalogListener> analogListeners = new SafeArrayList<>(AnalogListener.class);
    private List<String> mappingNames = new ArrayList<>();

    @Override
    public void initialize(AppStateManager asm, Application app) {
        super.initialize(asm, app);
        
        inputManager = app.getInputManager();
        registerInput();
        
        Joystick[] joysticks = inputManager.getJoysticks();
        if (joysticks != null && joysticks.length > 0) {
            for (Joystick j : joysticks) {
                mapJoystick(j);
            }
        }
    }
    
    /**
     * Create a new mapping to the given triggers.
     *
     * The given mapping will be assigned to the given triggers, when any of the
     * triggers given raise an event, the listeners registered to the mappings
     * will receive appropriate events
     *
     * @param bindingName
     * @param triggers
     */
    public void addMapping(String bindingName, Trigger... triggers) {
        mappingNames.add(bindingName);
        inputManager.addMapping(bindingName, triggers);
        inputManager.addListener(this, bindingName);
    }
    
    /**
     * Assign the mapping name to receive events from the given button index on the joystick
     * @param joystick
     * @param logicalId
     * @param mappingName 
     */
    public void assignButton(Joystick joystick, String logicalId, String mappingName) {
        joystick.getButton(logicalId).assignButton(mappingName);
        inputManager.addListener(this, mappingName);
    }
    
    /**
     * Assign the mappings to receive events from the given joystick axis
     * @param axis
     * @param positiveMapping
     * @param negativeMapping 
     */
    public void assignAxis(JoystickAxis axis, String positiveMapping, String negativeMapping) {
        axis.assignAxis(positiveMapping, negativeMapping);
        inputManager.addListener(this, positiveMapping, negativeMapping);
    }

    @Override
    public void cleanup() {
        super.cleanup();
        
        for (String input : mappingNames) {
            if (inputManager.hasMapping(input)) {
                inputManager.deleteMapping(input);
            }
        }
        inputManager.removeListener(this);
    }
    
    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (isEnabled()) {
            for (ActionListener listener : actionListeners) {
                listener.onAction(name, isPressed, tpf);
            }
        }
    }

    @Override
    public void onAnalog(String name, float value, float tpf) {
        if (isEnabled()) {
            for (AnalogListener listener : analogListeners) {
                listener.onAnalog(name, value, tpf);
            }
        }
    }

    public void addActionListener(ActionListener listener) {
        actionListeners.add(listener);
    }

    public void addAnalogListener(AnalogListener listener) {
        analogListeners.add(listener);
    }

    public void removeActionListener(ActionListener listener) {
        actionListeners.remove(listener);
    }

    public void removeAnalogListener(AnalogListener listener) {
        analogListeners.remove(listener);
    }

    /** 
     * Custom Keybinding: Mapping a named action to a key input.
     */
    protected abstract void registerInput();
    
    /**
     * 
     * @param joypad 
     */
    protected abstract void mapJoystick(Joystick joypad);
    
}

