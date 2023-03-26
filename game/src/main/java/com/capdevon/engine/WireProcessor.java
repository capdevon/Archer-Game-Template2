package com.capdevon.engine;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.post.SceneProcessor;
import com.jme3.profile.AppProfiler;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.texture.FrameBuffer;

public class WireProcessor implements SceneProcessor {

    private RenderManager renderManager;
    private final Material wireMat;

    public WireProcessor(AssetManager assetManager) {
        wireMat = new Material(assetManager, "/Common/MatDefs/Misc/Unshaded.j3md");
        wireMat.setColor("Color", ColorRGBA.Green);
        wireMat.getAdditionalRenderState().setWireframe(true);
    }

    @Override
    public void initialize(RenderManager rm, ViewPort vp) {
        renderManager = rm;
    }

    @Override
    public void reshape(ViewPort vp, int w, int h) {
    }

    @Override
    public boolean isInitialized() {
        return renderManager != null;
    }

    @Override
    public void preFrame(float tpf) {
    }

    @Override
    public void postQueue(RenderQueue rq) {
        renderManager.setForcedMaterial(wireMat);
    }

    @Override
    public void postFrame(FrameBuffer out) {
        renderManager.setForcedMaterial(null);
    }

    @Override
    public void cleanup() {
        renderManager.setForcedMaterial(null);
    }

    @Override
    public void setProfiler(AppProfiler profiler) {
    }

}
