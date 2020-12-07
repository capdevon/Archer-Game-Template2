/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capdevon.control;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;

/**
 *
 */
public class AdapterControl extends AbstractControl {
    
    /**
     * 
     * @param name
     * @param location
     * @return 
     */
    public Node addEmptyNode(String name, Vector3f location) {
        Node node = new Node(name);
        ((Node) spatial).attachChild(node);
        node.setLocalTranslation(location);
        return node;
    }
    
    /**
     * 
     * @param name
     * @return 
     */
    public Spatial getChild(String name) {
        Spatial child = ((Node) spatial).getChild(name);
        if (child == null) {
            String err = "The component %s could not be found";
            throw new RuntimeException(String.format(err, name));
        }
        return child;
    }
    
    /**
     * 
     * @param <T>
     * @param key
     * @return 
     */
    public <T> T getUserData(String key) {
        T objValue = spatial.getUserData(key);
        if (objValue == null) {
            String err = "The component %s could not be found";
            throw new RuntimeException(String.format(err, key));
        }
        return objValue;
    }
    
    /**
     * 
     * @param <T>
     * @param clazz
     * @return 
     */
    public <T> T getUserData(Class<T> clazz) {
        T objValue = null;
        for (String key : spatial.getUserDataKeys()) {
            T data = spatial.getUserData(key);
            if (clazz.isAssignableFrom(data.getClass())) {
                objValue = data;
                break;
            }
        }
        if (objValue == null) {
            String err = "The component %s could not be found";
            throw new RuntimeException(String.format(err, clazz.getName()));
        }
        return objValue;
    }
    
    /**
     * @param <T>
     * @param clazz
     * @return 
     */
    protected <T extends Control> T getComponent(Class<T> clazz) {
        T control = spatial.getControl(clazz);
        if (control == null) {
            String err = "The component %s could not be found";
            throw new RuntimeException(String.format(err, clazz.getName()));
        }
        return control;
    }

    /**
     * @param <T>
     * @param clazz
     * @return 
     */
    protected <T extends Control> T getComponentInChild(final Class<T> clazz) {
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

    @Override
    protected void controlUpdate(float tpf) {
        //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //To change body of generated methods, choose Tools | Templates.
    }
    
}
