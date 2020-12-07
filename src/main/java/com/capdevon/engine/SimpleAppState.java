/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capdevon.engine;

import java.util.ArrayList;
import java.util.List;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.font.BitmapFont;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;

/**
 *
 */
public abstract class SimpleAppState extends AbstractAppState {
    
    // variables
    public SimpleApplication app;
    public BulletAppState    physics;
    public AppSettings       settings;
    public AppStateManager   stateManager;
    public AssetManager      assetManager;
    public InputManager      inputManager;
    public RenderManager     renderManager;
    public ViewPort          viewPort;
    public Camera            camera;
    public FlyByCamera       flyCam;
    public Node              rootNode;
    public Node              guiNode;
    public BitmapFont        guiFont;
    
//    public Node rootLocal = new Node("RootLocal");
//    public Node guiLocal = new Node("GuiLocal");
    
    public SimpleAppState() {
    }
    
    public SimpleAppState(boolean enabled) {
        super.setEnabled(enabled);
    }
    
    @Override
    public void initialize(AppStateManager asm, Application appl) {
        if (!(appl instanceof SimpleApplication)) {
            throw new IllegalArgumentException("application should be a SimpleApplication");
        }
        
        super.initialize(asm, appl);
        this.app     = (SimpleApplication) appl;
        this.physics = asm.getState(BulletAppState.class);
        
        refreshCacheFields();
        simpleInit();
        registerInput();
    }
    
    protected void refreshCacheFields() {
        this.settings       = app.getContext().getSettings();
        this.stateManager   = app.getStateManager();
        this.assetManager   = app.getAssetManager();
        this.inputManager   = app.getInputManager();
        this.renderManager  = app.getRenderManager();
        this.viewPort       = app.getViewPort();
        this.camera         = app.getCamera();
        this.flyCam         = app.getFlyByCamera();
        this.rootNode       = app.getRootNode();
        this.guiNode        = app.getGuiNode();
        this.guiFont        = assetManager.loadFont("Interface/Fonts/Default.fnt");
    }
    
    protected void simpleInit() {}
    
    protected void registerInput() {}

	/**
	 * @param childName
	 * @return
	 */
	public Spatial find(final String childName) {
		Spatial child = rootNode.getChild(childName);
		if (child == null) {
			String err = String.format("The spatial %s could not be found", childName);
			throw new RuntimeException(err);
		}
		return child;
	}

    /**
     * @param tagName
     * @return 
     */
    public List<Spatial> findGameObjectsWithTag(final String tagName) {
        final List<Spatial> lst = new ArrayList<>();
        rootNode.breadthFirstTraversal(new SceneGraphVisitor() {
            @Override
            public void visit(Spatial node) {
                if (tagName.equals(node.getUserData("TagName"))) {
                    lst.add(node);
                }
            }
        });
        return lst;
    }

    /**
     * @param tagName
     * @return 
     */
    public Spatial findWithTag(final String tagName) {
        List<Spatial> lst = findGameObjectsWithTag(tagName);
        if (lst.isEmpty()) {
            String err = String.format("The spatial %s could not be found", tagName);
            throw new RuntimeException(err);
        }
        return lst.get(0);
    }
    
    /**
     * By default the parent of the new object is null
     * @param model
     * @param position
     * @param rotation
     * @return
     */
    public Spatial instantiate(Spatial model, Vector3f position, Quaternion rotation) {
        Spatial sp = model.clone();
        sp.setLocalTranslation(position);
        sp.setLocalRotation(rotation);
        return sp;
    }
    
    public Spatial instantiate(Spatial model, Vector3f position, Quaternion rotation, Node parent) {
    	Spatial sp = instantiate(model, position, rotation);
        parent.attachChild(sp);
        return sp;
    }
    
    /**
     * By default the parent of the new object is null
     * @param assetName
     * @param position
     * @param rotation
     * @return
     */
    public Spatial instantiate(String assetName, Vector3f position, Quaternion rotation) {
        Spatial sp = assetManager.loadModel(assetName);
        sp.setLocalTranslation(position);
        sp.setLocalRotation(rotation);
        return sp;
    }
    
    public Spatial instantiate(String assetName, Vector3f position, Quaternion rotation, Node parent) {
    	Spatial sp = instantiate(assetName, position, rotation);
    	parent.attachChild(sp);
    	return sp;
    }
    
}
