package com.capdevon.engine;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.SceneProcessor;
import com.jme3.renderer.ViewPort;

/**
 *
 * @author capdevon
 */
public class WireAppState extends BaseAppState implements ActionListener {

    private static final String TOGGLE_WIREFRAME = "TOGGLE_WIREFRAME";

    private InputManager inputManager;
    private ViewPort viewPort;
    private WireProcessor wireProcessor;
    private boolean toggleWireframe;

    @Override
    protected void initialize(Application app) {
        this.inputManager = app.getInputManager();
        this.viewPort = app.getViewPort();
        this.wireProcessor = new WireProcessor(app.getAssetManager());
    }

    @Override
    protected void cleanup(Application app) {
    }

    @Override
    protected void onEnable() {
        inputManager.addMapping(TOGGLE_WIREFRAME, new KeyTrigger(KeyInput.KEY_K));
        inputManager.addListener(this, TOGGLE_WIREFRAME);
    }

    @Override
    protected void onDisable() {
        inputManager.deleteMapping(TOGGLE_WIREFRAME);
        inputManager.removeListener(this);
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals(TOGGLE_WIREFRAME) && isPressed) {

            toggleWireframe = !toggleWireframe;
            if (toggleWireframe) {
                viewPort.addProcessor(wireProcessor);
            } else {
                viewPort.removeProcessor(wireProcessor);
            }

            for (SceneProcessor processor : viewPort.getProcessors()) {
                if (processor instanceof FilterPostProcessor) {
                    ((FilterPostProcessor) processor).getFilterList().forEach(f -> f.setEnabled(!toggleWireframe));
                }
            }
        }
    }

}
