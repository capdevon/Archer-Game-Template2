package mygame.decals;

import java.util.ArrayList;
import java.util.List;

import com.capdevon.engine.GameObject;
import com.jme3.material.Material;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.SceneGraphVisitorAdapter;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import jme3utilities.DecalManager;

/**
 * https://docs.unity3d.com/Packages/com.unity.render-pipelines.universal@16.0/manual/renderer-feature-decal.html
 *
 * @author capdevon
 */
public class DecalControl extends AbstractControl {

    public DecalManager decalManager;

    // Geometries with this tag will be ignored.
    private String ignoreTag = "";
    // The decal material.
    private Material material;
    // The width of the projector bounding box. 
    // The projector scales the decal to match this value along the local X axis.
    private float width = 1f;
    // The height of the projector bounding box. 
    // The projector scales the decal to match this value along the local Y axis.
    private float height = 1f;
    // The depth of the projector bounding box. 
    // The projector projects decals along the local Z axis.
    private float projectionDepth = 1f;

    public void project(Spatial subtree) {
        Vector3f position = spatial.getWorldTranslation().clone();
        Vector3f projectionDir = Vector3f.UNIT_Y.negate();
        Quaternion rotation = new Quaternion().lookAt(projectionDir, Vector3f.UNIT_Y);

        List<Geometry> geometries = new ArrayList<>();
        subtree.depthFirstTraversal(new SceneGraphVisitorAdapter() {
            @Override
            public void visit(Geometry geom) {
                if (!GameObject.compareTag(geom, ignoreTag)) {
                    geometries.add(geom);
                }
            }
        });

        Vector3f projectionBox = new Vector3f(width, height, projectionDepth);
        DecalProjector projector = new DecalProjector(geometries, position, rotation, projectionBox);
        Geometry decal = projector.project();
        decal.setMaterial(material);
        decal.setQueueBucket(Bucket.Transparent);
        decal.setShadowMode(ShadowMode.Off);

        decalManager.addDecal(decal);
    }

    @Override
    protected void controlUpdate(float tpf) {
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    public String getIgnoreTag() {
        return ignoreTag;
    }

    public void setIgnoreTag(String ignoreTag) {
        this.ignoreTag = ignoreTag;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getProjectionDepth() {
        return projectionDepth;
    }

    public void setProjectionDepth(float projectionDepth) {
        this.projectionDepth = projectionDepth;
    }

}
