package mygame.decals;

import java.util.ArrayList;
import java.util.List;

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

/**
 * https://docs.unity3d.com/Packages/com.unity.render-pipelines.universal@16.0/manual/renderer-feature-decal.html
 * @author capdevon
 */
public class DecalControl extends AbstractControl {

    public DecalManager decalManager;

    private String tagName = "";
    // The decal material.
    private Material material;
    // The properties Width, Height and Depth define the bounding volume of the decal. 
    // The decal material will be projected onto meshes within this bounding volume.
    private float width = 1f;
    private float height = 1f;
    private float depth = 1f;

    public void project(Spatial subtree) {
        Vector3f position = spatial.getWorldTranslation().clone();
        Quaternion rotation = new Quaternion().lookAt(Vector3f.UNIT_Y.negate(), Vector3f.UNIT_Y);
        Vector3f projectionBox = new Vector3f(width, height, depth);

        List<Geometry> geometries = new ArrayList<>();
        subtree.depthFirstTraversal(new SceneGraphVisitorAdapter() {
            @Override
            public void visit(Geometry geom) {
                geometries.add(geom);
            }
        });

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

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
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

    public float getDepth() {
        return depth;
    }

    public void setDepth(float depth) {
        this.depth = depth;
    }

}
