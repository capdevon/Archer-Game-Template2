package com.capdevon.physx;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.app.state.BaseAppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;

public class PhysxDebugAppState extends BaseAppState implements ActionListener {
	
	private final String TOGGLE_PHYSICS_DEBUG = "TOGGLE_PHYSICS_DEBUG";
	
	private AppStateManager stateManager;
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
		this.stateManager = app.getStateManager();
		this.bulletAppState = stateManager.getState(BulletAppState.class);
		
		app.getInputManager().addMapping(TOGGLE_PHYSICS_DEBUG, new KeyTrigger(KeyInput.KEY_0));
		app.getInputManager().addListener(this, TOGGLE_PHYSICS_DEBUG);
	}
	
	@Override
	protected void cleanup(Application app) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onEnable() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onDisable() {
		// TODO Auto-generated method stub
	}

}
