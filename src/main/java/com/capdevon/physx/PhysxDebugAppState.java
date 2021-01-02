package com.capdevon.physx;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;

/**
 * @author capdevon
 */
public class PhysxDebugAppState extends BaseAppState implements ActionListener {

    private final String TOGGLE_PHYSICS_DEBUG = "TOGGLE_PHYSICS_DEBUG";

    private InputManager inputManager;
    private BulletAppState bulletAppState;

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals(TOGGLE_PHYSICS_DEBUG) && isPressed) {
            boolean debug = bulletAppState.isDebugEnabled();
            bulletAppState.setDebugEnabled(!debug);
        }
    }

    @Override
    protected void initialize(Application app) {
        this.inputManager = app.getInputManager();
        this.bulletAppState = getState(BulletAppState.class);

        inputManager.addListener(this, TOGGLE_PHYSICS_DEBUG);
    }

    @Override
    protected void cleanup(Application app) {
        inputManager.removeListener(this);
    }

    @Override
    protected void onEnable() {
        inputManager.addMapping(TOGGLE_PHYSICS_DEBUG, new KeyTrigger(KeyInput.KEY_0));
    }

    @Override
    protected void onDisable() {
        inputManager.deleteMapping(TOGGLE_PHYSICS_DEBUG);
    }

}
