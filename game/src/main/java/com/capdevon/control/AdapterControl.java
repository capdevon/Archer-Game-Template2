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
 * 
 * @author capdevon
 */
public abstract class AdapterControl extends AbstractControl {

    /**
     * Returns the first child found with exactly the given name.
     *
     * @param name
     * @return
     */
    public Spatial getChild(String name) {
        Spatial child = ((Node) spatial).getChild(name);
        String error = "The child %s could not be found: " + spatial;
        return Objects.requireNonNull(child, String.format(error, name));
    }

    public <T> T getUserData(String key) {
        T objValue = spatial.getUserData(key);
        String error = "The UserData %s could not be found: " + spatial;
        return Objects.requireNonNull(objValue, String.format(error, key));
    }

    /**
     * Returns the component of Type type if the game object has one attached,
     * null if it doesn't.
     *
     * @param <T>
     * @param type
     * @return
     */
    public <T extends Control> T getComponent(Class<T> type) {
        T control = GameObject.getComponent(spatial, type);
        return Objects.requireNonNull(control, type + " not found: " + spatial);
    }

    /**
     * Returns all components of Type type in the GameObject or any of its
     * children children using depth first search. Works recursively.
     *
     * @param <T>
     * @param sp
     * @param type
     * @return
     */
    public <T> List<T> getComponentsInChildren(Spatial sp, Class<? extends Control> type) {
        return GameObject.getComponentsInChildren(sp, type);
    }

    /**
     * Returns the component of Type type in the GameObject or any of its
     * children using depth first search.
     *
     * @param <T>
     * @param type
     * @return
     */
    public <T extends Control> T getComponentInChildren(Class<T> type) {
        T control = GameObject.getComponentInChildren(spatial, type);
        return Objects.requireNonNull(control, type + " not found: " + spatial);
    }

    /**
     * Retrieves the component of Type type in the GameObject or any of its
     * parents.
     *
     * @param <T>
     * @param type
     * @return
     */
    public <T extends Control> T getComponentInParent(Class<T> type) {
        T control = GameObject.getComponentInParent(spatial, type);
        return Objects.requireNonNull(control, type + " not found: " + spatial);
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
