/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capdevon.trigger;

import com.jme3.bounding.BoundingVolume;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 *
 */
public abstract class EnterableTrigger extends AbstractControl {

    private Spatial target;
    private BoundingVolume volume;
    protected Geometry _geo;
    protected boolean inside;
    protected boolean debugEnabled;
    protected String tagName = "NONE";
    

    @Override
    public void setSpatial(Spatial sp) {
        super.setSpatial(sp);
        if (spatial != null) {
            ((Node) spatial).attachChild(_geo);
            _geo.setCullHint(debugEnabled ? Spatial.CullHint.Never : Spatial.CullHint.Always);
            volume = _geo.getWorldBound();
//            volume.setCenter(sp.getWorldTranslation());
        }
    }

    @Override
    protected void controlUpdate(float tpf) {
        //To change body of generated methods, choose Tools | Templates.
        if (target != null) {
            boolean contains = volume.contains(target.getWorldTranslation());
            if (!inside && contains) {
            	TriggerManager.getInstance().notifyTriggerEnter(this);

            } else if (inside && !contains) {
            	TriggerManager.getInstance().notifyTriggerExit(this);
            }
            inside = contains;
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //To change body of generated methods, choose Tools | Templates.
    }

    public Spatial getTarget() {
        return target;
    }

    public void setTarget(Spatial target) {
        this.target = target;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

}
