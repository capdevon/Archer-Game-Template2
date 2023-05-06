package com.capdevon.engine;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.font.BitmapFont;
import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;

import mygame.GameApplication;

/**
 * 
 * @author capdevon
 */
public abstract class SimpleAppState extends BaseAppState {
    
    // variables
    public GameApplication  app;
    public AppSettings      settings;
    public AppStateManager  stateManager;
    public AssetManager     assetManager;
    public InputManager     inputManager;
    public RenderManager    renderManager;
    public ViewPort         viewPort;
    public Camera           camera;
    public Node             rootNode;
    public Node             guiNode;
    public BitmapFont       guiFont;
    
    @Override
    public void initialize(Application app) {
        if (!(app instanceof GameApplication)) {
            throw new IllegalArgumentException("application should be a GameApplication");
        }
        
        refreshCacheFields(app);
        simpleInit();
    }
    
    protected void refreshCacheFields(Application appl) {
        this.app            = (GameApplication) appl;
        this.settings       = app.getContext().getSettings();
        this.stateManager   = app.getStateManager();
        this.assetManager   = app.getAssetManager();
        this.inputManager   = app.getInputManager();
        this.renderManager  = app.getRenderManager();
        this.viewPort       = app.getViewPort();
        this.camera         = app.getCamera();
        this.rootNode       = app.getRootNode();
        this.guiNode        = app.getGuiNode();
        this.guiFont        = assetManager.loadFont("Interface/Fonts/Default.fnt");
    }
    
    protected void simpleInit() {}

    public final PhysicsSpace getPhysicsSpace() {
        return getState(BulletAppState.class, true).getPhysicsSpace();
    }

    @Override
    protected void cleanup(Application app) {
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }

}
