package com.capdevon.control;

import java.util.List;
import java.util.Objects;

import com.capdevon.engine.GameObject;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;

/**
 * @author capdevon
 */
public abstract class AdapterControl extends AbstractControl {

    /**
     * Returns the first child found with exactly the given name.
     */
    public Spatial getChild(String name) {
        Spatial child = ((Node) spatial).getChild(name);
        return Objects.requireNonNull(child, name + " not found");
    }
    
    public <T> T getUserData(String key) {
        T objValue = spatial.getUserData(key);
        String message = "The component data %s could not be found";
        return Objects.requireNonNull(objValue, String.format(message, key));
    }

    /**
     * Returns all components of Type type in the GameObject.
     */
    public List<Node> getComponents(Class<? extends Control> clazz) {
        return GameObject.getComponents(spatial, clazz);
    }

    /**
     * Returns the component of Type type if the game object has one attached,
     * null if it doesn't.
     */
    public <T extends Control> T getComponent(Class<T> clazz) {
        return GameObject.getComponent(spatial, clazz);
    }
    
    /**
     * Returns all components of Type type in the GameObject or any of its
     * children children using depth first search. Works recursively.
     */
    public <T> List<T> getComponentsInChildren(Spatial sp, Class<? extends Control> clazz) {
    	return GameObject.getComponentsInChildren(sp, clazz);
    }

    /**
     * Returns the component of Type type in the GameObject or any of its
     * children using depth first search.
     */
    public <T extends Control> T getComponentInChildren(Class<T> clazz) {
        return GameObject.getComponentInChildren(spatial, clazz);
    }

    /**
     * Retrieves the component of Type type in the GameObject or any of its
     * parents.
     */
    public <T extends Control> T getComponentInParent(Class<T> clazz) {
        return GameObject.getComponentInParent(spatial, clazz);
    }

    @Override
    protected void controlUpdate(float tpf) {
        //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //To change body of generated methods, choose Tools | Templates.
    }

}
