/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capdevon.engine;

import com.jme3.asset.AssetManager;
import com.jme3.environment.util.BoundingSphereDebug;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.debug.Grid;
import com.jme3.scene.debug.WireBox;
import com.jme3.scene.debug.WireFrustum;
import com.jme3.scene.debug.WireSphere;
import com.jme3.shadow.ShadowUtil;

/**
 * @author capdevon
 */
public class DebugShape {

    protected final AssetManager assetManager;

    // Node for attaching debug geometries.
    public Node debugNode = new Node("Debug Node");

    public float lineWidth = 4f;

    public DebugShape(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    /**
     * Use a wireframe grid (com.jme3.scene.debug.Grid) as a ruler or simple
     * floor.
     *
     * @param pos
     * @param size
     * @param color
     * @return
     */
    public Geometry attachGrid(Vector3f pos, int size, ColorRGBA color) {
        Geometry g = new Geometry("wireframe grid", new Grid(size, size, 0.2f));
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.setColor("Color", color);
        g.setMaterial(mat);
        g.center().move(pos);
        debugNode.attachChild(g);
        return g;
    }

    /**
     * The coordinate axes (com.jme3.scene.debug.Arrow) help you see the
     * cardinal directions (X,Y,Z) from their center point. Scale the arrows to
     * use them as a “ruler” for a certain length.
     *
     * @param pos
     */
    public void attachCoordinateAxes(Vector3f pos) {
        putShape(new Arrow(Vector3f.UNIT_X), ColorRGBA.Red).setLocalTranslation(pos);
        putShape(new Arrow(Vector3f.UNIT_Y), ColorRGBA.Green).setLocalTranslation(pos);
        putShape(new Arrow(Vector3f.UNIT_Z), ColorRGBA.Blue).setLocalTranslation(pos);
    }

    public Geometry putShape(Mesh shape, ColorRGBA color) {
        Geometry g = new Geometry("Mesh.Geo", shape);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.getAdditionalRenderState().setLineWidth(lineWidth);
        mat.setColor("Color", color);
        g.setMaterial(mat);
        debugNode.attachChild(g);
        return g;
    }

    /**
     * Use a wireframe cube (com.jme3.scene.debug.WireBox) as a stand-in object
     * to see whether your code scales, positions, or orients, loaded models
     * right.
     *
     * @param pos
     * @param size
     * @param color
     * @return
     */
    public Geometry attachWireBox(Vector3f pos, float size, ColorRGBA color) {
        Geometry g = new Geometry("WireBox.Geo", new WireBox(size, size, size));
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.setColor("Color", color);
        g.setMaterial(mat);
        g.setLocalTranslation(pos);
        debugNode.attachChild(g);
        return g;
    }

    /**
     * Use a wireframe sphere (com.jme3.scene.debug.WireSphere) as a stand-in
     * object to see whether your code scales, positions, or orients, loaded
     * models right.
     *
     * @param pos
     * @param size
     * @param color
     * @return
     */
    public Geometry attachWireSphere(Vector3f pos, float size, ColorRGBA color) {
        Geometry g = new Geometry("WireSphere.Geo", new WireSphere(size));
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.setColor("Color", color);
        g.setMaterial(mat);
        g.setLocalTranslation(pos);
        debugNode.attachChild(g);
        return g;
    }
    
    public Geometry createCameraFrustum(Camera cam) {

        Vector3f[] points = new Vector3f[8];
        for (int i = 0; i < 8; i++) {
            points[i] = new Vector3f();
        }

        Camera frustumCam = cam.clone();
        frustumCam.setLocation(new Vector3f(0, 0, 0));
        frustumCam.lookAt(Vector3f.UNIT_Z, Vector3f.UNIT_Y);
        ShadowUtil.updateFrustumPoints2(frustumCam, points);
        Mesh mesh = new WireFrustum(points);

        Geometry frustumGeo = new Geometry("Viewing.Frustum", mesh);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.White);
        frustumGeo.setMaterial(mat);
        frustumGeo.setCullHint(Spatial.CullHint.Never);
        frustumGeo.setShadowMode(RenderQueue.ShadowMode.Off);

        debugNode.attachChild(frustumGeo);
        return frustumGeo;
    }
    
    public Geometry createDebugSphere(float scale) {
        Geometry geo = BoundingSphereDebug.createDebugSphere(assetManager);
        geo.setShadowMode(RenderQueue.ShadowMode.Off);
        geo.setLocalScale(scale);
        debugNode.attachChild(geo);
        return geo;
    }

    /**
     * Render all the debug geometries to the specified view port.
     *
     * @param rm the render manager (not null)
     * @param vp the view port (not null)
     */
    public void show(RenderManager rm, ViewPort vp) {
        debugNode.updateLogicalState(0);
        debugNode.updateGeometricState();
        rm.renderScene(debugNode, vp);
    }
}
