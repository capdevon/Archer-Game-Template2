/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capdevon.util;

import com.jme3.asset.AssetManager;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.shadow.BasicShadowRenderer;
import com.jme3.shadow.DirectionalLightShadowRenderer;

/**
 *
 */
public class Shadow {
    
    private AssetManager assetManager;
    private ViewPort viewPort;

    /**
     * @param assetManager
     * @param viewPort
     */
    public Shadow(AssetManager assetManager, ViewPort viewPort) {
        this.assetManager = assetManager;
        this.viewPort = viewPort;
    }
    
    public void addLowShadows(Node node) {
        for (Light light : node.getLocalLightList()) {
            if (light instanceof DirectionalLight) {
                addLowShadows((DirectionalLight) light);
                break;
            }
        }
    }

    public void addHighShadows(Node node) {
        for (Light light : node.getLocalLightList()) {
            if (light instanceof DirectionalLight) {
                addHighShadows((DirectionalLight) light);
                break;
            }
        }
    }

    public void addLowShadows(DirectionalLight sun) {
        BasicShadowRenderer shadowRenderer = new BasicShadowRenderer(assetManager, 2048);
        shadowRenderer.setDirection(sun.getDirection());
        viewPort.addProcessor(shadowRenderer);
    }

    public void addHighShadows(DirectionalLight sun) {
        DirectionalLightShadowRenderer shadowRenderer = new DirectionalLightShadowRenderer(assetManager, 2048, 3);
        shadowRenderer.setLight(sun);
        shadowRenderer.setShadowIntensity(0.2f);
        viewPort.addProcessor(shadowRenderer);
    }

}
