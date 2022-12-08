package com.capdevon.util;

import java.util.List;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Spline;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.shape.Curve;

/**
 * 
 * @author capdevon
 */
public class LineRenderer extends AbstractControl {

    private int nbSubSegments = 1;
    private Spline spline;
    private Geometry geom;
    private Material mat;
    private boolean refresh;

    /**
     * Constructor
     */
    public LineRenderer(Application app) {
        Node rootNode = ((SimpleApplication) app).getRootNode();
        AssetManager assetManager = app.getAssetManager();
        init(rootNode, assetManager);
    }

    /**
     * @param rootNode
     * @param assetManager
     */
    private void init(Node rootNode, AssetManager assetManager) {

        mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.White);
        mat.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);

        spline = new Spline();
        spline.addControlPoint(new Vector3f(0, -1000f, 0));
        geom = new Geometry("LineRenderer", new Curve(spline, nbSubSegments));
        geom.setMaterial(mat);
        geom.setShadowMode(ShadowMode.Off);
        rootNode.attachChild(geom);
    }

    public void clearPoints() {
        spline.clearControlPoints();
    }

    public void addPoint(Vector3f point) {
        spline.addControlPoint(point);
    }

    public void setPoints(List<Vector3f> points) {
        spline.clearControlPoints();
        for (Vector3f point: points) {
            spline.addControlPoint(point);
        }
    }

    public void updateGeometry() {
        geom.setMesh(new Curve(spline, 1));
        geom.updateModelBound();
    }

    @Override
    public boolean isEnabled() {
        return (geom.getCullHint() == CullHint.Never || geom.getCullHint() == CullHint.Dynamic);
    }

    @Override
    public void setEnabled(boolean enable) {
        geom.setCullHint(enable ? CullHint.Never : CullHint.Always);
    }

    public void setColor(ColorRGBA color) {
        if (color == null) {
            throw new IllegalArgumentException("color is null");
        }
        mat.setColor("Color", color.clone());
    }

    public void setLineWidth(float lineWidth) {
        if (lineWidth <= 0) {
            throw new IllegalArgumentException("lineWidth must be greater than 0");
        }
        mat.getAdditionalRenderState().setLineWidth(lineWidth);
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (refresh) {
            updateGeometry();
            refresh = false;
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        // TODO Auto-generated method stub
    }

}
