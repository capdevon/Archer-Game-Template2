/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capdevon.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitorAdapter;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;

/**
 * @author capdevon
 */
public class AdapterControl extends AbstractControl {
    
    /**
     * @param <T>
     * @param key
     * @return 
     */
    public <T> T getUserData(String key) {
        T objValue = spatial.getUserData(key);
        String message = "The component data %s could not be found";
        return Objects.requireNonNull(objValue, String.format(message, key));
    }
    
    /**
     * Returns all components of Type type in the GameObject.
     * 
     * @param <T>
     * @param clazz
     * @return 
     */
    public <T extends Control> T[] getComponents(Class<T> clazz) {
        final List<Node> lst = new ArrayList<>(10);
        spatial.breadthFirstTraversal(new SceneGraphVisitorAdapter() {
            @Override
            public void visit(Node node) {
                if (node.getControl(clazz) != null) {
                    lst.add(node);
                }
            }
        });
        return (T[]) lst.toArray();
    }
    
    /**
     * Returns the component of Type type if the game object has one attached,
     * null if it doesn't.
     *
     * @param <T>
     * @param clazz
     * @return
     */
    public <T extends Control> T getComponent(Class<T> clazz) {
        T control = spatial.getControl(clazz);
        return control;
    }
    
    /**
     * Returns the component of Type type in the GameObject or any of its
     * children using depth first search.
     *
     * @param <T>
     * @param clazz
     * @return
     */
    public <T extends Control> T getComponentInChild(final Class<T> clazz) {
        return getComponentInChild(spatial, clazz);
    }
    
    private <T extends Control> T getComponentInChild(Spatial spatial, final Class<T> clazz) {
        T control = spatial.getControl(clazz);
        if (control != null) {
            return control;
        }

        if (spatial instanceof Node) {
            for (Spatial child : ((Node) spatial).getChildren()) {
                control = getComponentInChild(child, clazz);
                if (control != null) {
                    return control;
                }
            }
        }

        return null;
    }
    
    /**
     * Retrieves the component of Type type in the GameObject or any of its
     * parents.
     *
     * @param <T>
     * @param clazz
     * @return
     */
    public <T extends Control> T getComponentInParent(Class<T> clazz) {
        return getComponentInParent(spatial, clazz);
    }
    
    private <T extends Control> T getComponentInParent(Spatial spatial, Class<T> clazz) {
        Node parent = spatial.getParent();
        while (parent != null) {
            T control = parent.getControl(clazz);
            if (control != null) {
                return control;
            }
            parent = parent.getParent();
        }
        return null;
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
