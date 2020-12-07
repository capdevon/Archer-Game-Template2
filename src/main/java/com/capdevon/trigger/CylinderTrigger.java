/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capdevon.trigger;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Cylinder;

/**
 *
 */
public class CylinderTrigger extends EnterableTrigger {

    public CylinderTrigger(AssetManager assetManager, float radius, float height, boolean debug) {
        debugEnabled = debug;
        _geo = new Geometry("CylinderTrigger", new Cylinder(2, 8, radius, height, true));
        Material m = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        m.setColor("Color", ColorRGBA.Green);
        m.getAdditionalRenderState().setWireframe(true);
        _geo.setMaterial(m);
        _geo.rotate(FastMath.HALF_PI, 0, 0);
    }

}
