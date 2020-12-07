/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capdevon.trigger;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.debug.WireSphere;

/**
 *
 */
public class SphereTrigger extends EnterableTrigger {

    public SphereTrigger(AssetManager assetManager, float radius, boolean debug) {
        debugEnabled = debug;
        _geo = new Geometry("SphereTrigger", new WireSphere(radius));
        Material m = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        m.setColor("Color", ColorRGBA.Green);
        _geo.setMaterial(m);
    }

}
