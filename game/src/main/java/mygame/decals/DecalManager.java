package mygame.decals;

import java.util.Deque;
import java.util.LinkedList;
import java.util.logging.Logger;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import java.util.logging.Level;

/**
 * Manage decal geometries, "aging them out" on a first-in, first-out (FIFO)
 * basis.
 */
public class DecalManager extends BaseAppState {

    private static final Logger logger = Logger.getLogger(DecalManager.class.getName());

    // Assign unique names to inactive decals.
    private int nextId = 0;
    // Maximum number of triangles to retain.
    private int maxTriangles = 20_000;
    // Total triangles across all retained decals.
    private int totalTriangles = 0;
    // Parent the decals.
    private final Node decalNode = new Node("Decal Node");
    // Queue of retained decals, from oldest to newest.
    private final Deque<Geometry> fifo = new LinkedList<>();

    /**
     * Add the specified decal to the queue.
     *
     * @param decal the decal to add (not null, not empty, alias created)
     */
    public void addDecal(Geometry decal) {
        int triangleCount = decal.getTriangleCount();
        assert triangleCount > 0 : triangleCount;

        decal.setName("DecalGeom." + nextId);
        ++nextId;

        fifo.addLast(decal);
        decalNode.attachChild(decal);
        totalTriangles += triangleCount;
        logger.log(Level.INFO, "{0} triangleCount: {1}", new Object[]{decal, triangleCount});

        // Remove enough old decals to stay at or below the limit.
        while (totalTriangles > maxTriangles) {
            Geometry oldest = fifo.removeFirst();
            oldest.removeFromParent();
            int count = oldest.getTriangleCount();
            totalTriangles -= count;
        }
    }

    public void removeAll() {
        decalNode.detachAllChildren();
        totalTriangles = 0;
    }

    public int getMaxTriangles() {
        return maxTriangles;
    }

    public void setMaxTriangles(int maxTriangles) {
        this.maxTriangles = maxTriangles;
    }

    @Override
    protected void initialize(Application app) {
    }

    @Override
    protected void cleanup(Application app) {
    }

    @Override
    protected void onEnable() {
        getRootNode().attachChild(decalNode);
    }

    @Override
    protected void onDisable() {
        getRootNode().detachChild(decalNode);
    }

    private Node getRootNode() {
        return ((SimpleApplication) getApplication()).getRootNode();
    }

}
