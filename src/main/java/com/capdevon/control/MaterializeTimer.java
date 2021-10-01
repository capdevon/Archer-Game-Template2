package com.capdevon.control;

import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 * 
 * @author capdevon
 */
public class MaterializeTimer extends AbstractControl {

    private static final String EFFECT_TIME = "EffectTime";
    private static final String MATERIALIZING = "Materializing";

    private final Material material;
    private final boolean fadeIn;
    private final float speed;
    private float time;

    private MaterializerListener listener;

    public MaterializeTimer(Material material, boolean fadeIn) {
        this(material, fadeIn, 0.3f);
    }

    /**
     * Materialize a material.
     * 
     * @param material the material to materialize.
     * @param fadeIn   if true materialize in, else materialize out
     * @param speed    speed modifier between 0 and 1. 0.1 = 10 seconds, 1 = 1 second.
     */
    public MaterializeTimer(Material material, boolean fadeIn, float speed) {
        this.material = material;
        this.material.setBoolean(MATERIALIZING, true);
        this.material.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        this.fadeIn = fadeIn;
        this.speed = speed;
        this.time = fadeIn ? 0f : 1f;
    }

    @Override
    public void controlUpdate(float tpf) {

        if (fadeIn) {
            time += tpf * speed;
        } else {
            time -= tpf * speed;
        }

        material.setFloat(EFFECT_TIME, this.time);

        if ((fadeIn && time > 1.0) | (!fadeIn && time < 0)) {
            material.setBoolean(MATERIALIZING, false);
            material.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Back);

            if (listener != null) {
                listener.onCompleteEvent();
            }
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        // TODO Auto-generated method stub
    }

    public void setMaterializerListener(MaterializerListener listener) {
        this.listener = listener;
    }

    public float getTime() {
        return time;
    }

    public Material getMaterial() {
        return material;
    }

}
