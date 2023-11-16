package com.capdevon.physx;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.renderer.RenderManager;

import jme3utilities.minie.DumpFlags;
import jme3utilities.minie.PhysicsDumper;

/**
 * @author capdevon
 */
public class TogglePhysicsDebugState extends BaseAppState implements ActionListener {

    private static final String TOGGLE_PHYSICS_DEBUG = "TOGGLE_PHYSICS_DEBUG";
    private static final String DUMP_PHYSICS = "DUMP_PHYSICS";
    private static final String DUMP_RENDER = "DUMP_RENDER";

    private InputManager inputManager;
    private BulletAppState bulletAppState;
    private PhysicsDumper dumper = new PhysicsDumper();

    @Override
    protected void initialize(Application app) {
        this.inputManager = app.getInputManager();
        this.bulletAppState = getState(BulletAppState.class, true);
        dumper.setEnabled(DumpFlags.ChildShapes, true);
    }

    @Override
    protected void cleanup(Application app) {
    }

    @Override
    protected void onEnable() {
        addMapping(TOGGLE_PHYSICS_DEBUG, new KeyTrigger(KeyInput.KEY_0));
        addMapping(DUMP_PHYSICS, new KeyTrigger(KeyInput.KEY_O));
        addMapping(DUMP_RENDER, new KeyTrigger(KeyInput.KEY_P));
    }
    
    private void addMapping(String mappingName, Trigger... triggers) {
        inputManager.addMapping(mappingName, triggers);
        inputManager.addListener(this, mappingName);
    }

    @Override
    protected void onDisable() {
        inputManager.deleteMapping(TOGGLE_PHYSICS_DEBUG);
        inputManager.deleteMapping(DUMP_PHYSICS);
        inputManager.deleteMapping(DUMP_RENDER);
        inputManager.removeListener(this);
    }
    
    @Override
    public void onAction(String action, boolean keyPressed, float tpf) {
        if (keyPressed) {
            if (action.equals(TOGGLE_PHYSICS_DEBUG)) {
                boolean debug = bulletAppState.isDebugEnabled();
                bulletAppState.setDebugEnabled(!debug);
                
            } else if (action.equals(DUMP_PHYSICS)) {
                dumper.dump(bulletAppState);

            } else if (action.equals(DUMP_RENDER)) {
                RenderManager renderManager = getApplication().getRenderManager();
                dumper.dump(renderManager);
            }
        }
    }

}
