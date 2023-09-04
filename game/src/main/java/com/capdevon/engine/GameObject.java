package com.capdevon.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.jme3.bullet.animation.DacLinks;
import com.jme3.bullet.animation.PhysicsLink;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.control.AbstractPhysicsControl;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;

/**
 * https://docs.unity3d.com/ScriptReference/GameObject.html
 *
 * @author capdevon
 */
public class GameObject {

    /**
     * A private constructor to inhibit instantiation of this class.
     */
    private GameObject() {
    }

    public static final String TAG_NAME = "TagName";

    /**
     * Is Spatial tagged with tag ?
     *
     * @param sp
     * @param tag
     * @return
     */
    public static boolean compareTag(Spatial sp, String tag) {
        return Objects.equals(sp.getUserData(TAG_NAME), tag);
    }

    /**
     * Returns one active GameObject tagged tag. Returns null if no GameObject
     * was found.
     *
     * @param sp
     * @param tag
     * @return
     */
    public static Spatial findWithTag(Spatial sp, String tag) {
        List<Spatial> lst = findGameObjectsWithTag(sp, tag);
        return lst.isEmpty() ? null : lst.get(0);
    }

    /**
     * Returns an array of active GameObjects tagged tag. Returns empty array if
     * no GameObject was found.
     *
     * @param subtree
     * @param tag
     * @return
     */
    public static List<Spatial> findGameObjectsWithTag(Spatial subtree, String tag) {
        List<Spatial> lst = new ArrayList<>();
        subtree.breadthFirstTraversal(new SceneGraphVisitor() {
            @Override
            public void visit(Spatial sp) {
                if (tag.equals(sp.getUserData(TAG_NAME))) {
                    lst.add(sp);
                }
            }
        });
        return lst;
    }

    /**
     * Returns the scene-graph control of the specified type.
     *
     * @param <T> subclass of {@code Control}
     * @param subtree the subtree to search (not null, alias created)
     * @param clazz the subclass of {@code Control} to find
     * @return an instance of the specified subclass (not null)
     */
    public static <T extends Control> T getComponent(Spatial subtree, Class<T> clazz) {
        return subtree.getControl(clazz);
    }

    /**
     * Returns all components of Type type in the GameObject or any of its
     * children using depth first search. Works recursively.
     * 
     * @param <T>
     * @param subtree
     * @param type
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T extends Control> List<T> getComponentsInChildren(Spatial subtree, Class<T> type) {
        List<T> lst = new ArrayList<>(3);
        subtree.depthFirstTraversal(new SceneGraphVisitor() {
            @Override
            public void visit(Spatial sp) {
                for (int i = 0; i < sp.getNumControls(); i++) {
                    T control = (T) sp.getControl(i);
                    if (type.isAssignableFrom(control.getClass())) {
                        lst.add(control);
                    }
                }
            }
        });
        return lst;
    }

    /**
     * Returns the component of Type type in the GameObject or any of its
     * children using depth first search.
     *
     * @param <T>
     * @param subtree
     * @param type
     * @return
     */
    public static <T extends Control> T getComponentInChildren(Spatial subtree, final Class<T> type) {
        T control = subtree.getControl(type);
        if (control != null) {
            return control;
        }

        if (subtree instanceof Node) {
            for (Spatial child : ((Node) subtree).getChildren()) {
                control = getComponentInChildren(child, type);
                if (control != null) {
                    return control;
                }
            }
        }

        return null;
    }

    /**
     * Retrieves the component of Type type in the GameObject or any of its
     * parents.
     *
     * @param <T>
     * @param subtree
     * @param type
     * @return
     */
    public static <T extends Control> T getComponentInParent(Spatial subtree, Class<T> type) {
        Node parent = subtree.getParent();
        while (parent != null) {
            T control = parent.getControl(type);
            if (control != null) {
                return control;
            }
            parent = parent.getParent();
        }
        return null;
    }

    /**
     * Utility method to find the game object associated with the specified
     * collision object.
     *
     * @param collisionObj (not null)
     * @return the pre-existing game object, or null if not found
     */
    public static Spatial findGameObject(PhysicsCollisionObject collisionObj) {
        Spatial result = null;

        Object user = collisionObj.getUserObject(); // TODO use getApplicationData()
        if (user instanceof Spatial) {
            result = (Spatial) user;

        } else if (user instanceof AbstractPhysicsControl) {
            AbstractPhysicsControl control = (AbstractPhysicsControl) user;
            // TODO assuming the control is added to the game object
            result = control.getSpatial();

        } else if (user instanceof PhysicsLink) {
            PhysicsLink link = (PhysicsLink) user;
            DacLinks control = link.getControl();
            Spatial animSpatial = control.getSpatial();
            // TODO assuming the control is added to a child of the game object
            result = animSpatial.getParent();
        }
        return result;
    }
}
