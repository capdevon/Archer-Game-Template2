package com.capdevon.engine;

import com.jme3.app.Application;
import com.jme3.environment.EnvironmentCamera;
import com.jme3.environment.LightProbeFactory;
import com.jme3.environment.generation.JobProgressAdapter;
import com.jme3.light.LightProbe;
import com.jme3.material.Material;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

public class LightUtils {
	
    /**
     * A private constructor to inhibit instantiation of this class.
     */
    private LightUtils() {}
    
    /**
     * Usage:
     * 		LightProbe probe = LightUtils.generateUniform(app, 0.5f);
     * 		probe.getArea().setCenter(Vector3f.ZERO.clone());
     * 		probe.getArea().setRadius(1000);
     * 		sceneNode.addLight(probe);
     * 
     * @param app
     * @param intensity
     * @param completionListener
     * @return 
     */
    public static LightProbe generateUniform(Application app, float intensity, Runnable completionListener) {
        // clamp intensity between 0 and 1
        intensity = FastMath.clamp(intensity, 0, 1);

        Geometry skyBox = new Geometry("sky", new Box(1, 1, 1));
        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.White.mult(intensity));
        // Need to disable face culling since we're interested in the inside of the mesh, not the outside
        mat.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
        skyBox.setMaterial(mat);
        skyBox.setQueueBucket(Bucket.Sky);

        Node scene = new Node("Snapshot");
        scene.attachChild(skyBox);
        scene.updateGeometricState();

        EnvironmentCamera envCam = new EnvironmentCamera(4);
        envCam.initialize(app.getStateManager(), app);
        app.getStateManager().attach(envCam);

        return LightProbeFactory.makeProbe(envCam, scene, new JobProgressAdapter<LightProbe>() {
            @Override
            public void done(LightProbe result) {
                if (completionListener != null) {
                    completionListener.run();
                }
                app.enqueue(() -> app.getStateManager().detach(envCam));
            }
        });
    }

}