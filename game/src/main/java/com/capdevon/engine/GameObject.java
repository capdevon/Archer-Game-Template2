package com.capdevon.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.SceneGraphVisitorAdapter;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import jme3utilities.MySpatial;

/**
 * https://docs.unity3d.com/ScriptReference/GameObject.html
 *
 * @author capdevon
 */
public class GameObject {

    /**
     * A private constructor to inhibit instantiation of this class.
     */
    private GameObject() {}
    
    public static final String TAG_NAME = "TagName";

    /**
     * Is Spatial tagged with tag ?
     */
    public static boolean compareTag(Spatial sp, String tag) {
        return Objects.equals(sp.getUserData(TAG_NAME), tag);
    }

    /**
     * Returns one active GameObject tagged tag. Returns null if no GameObject
     * was found.
     */
    public static Spatial findWithTag(Spatial sp, String tag) {
        List<Spatial> lst = findGameObjectsWithTag(sp, tag);
        return lst.isEmpty() ? null : lst.get(0);
    }

    /**
     * Returns an array of active GameObjects tagged tag. Returns empty array if
     * no GameObject was found.
     */
    public static List<Spatial> findGameObjectsWithTag(Spatial sp, String tag) {
        List<Spatial> lst = new ArrayList<>();
        sp.breadthFirstTraversal(new SceneGraphVisitor() {
            @Override
            public void visit(Spatial node) {
                if (tag.equals(node.getUserData(TAG_NAME))) {
                    lst.add(node);
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
        List<T> list = MySpatial.listControls(subtree, clazz, null);
        assert list.size() == 1 : list.size();
        T result = list.get(0);

        return result;
    }

    /**
     * Returns all components of Type type in the GameObject.
     *
     * @param spatial
     * @param clazz
     * @return
     */
    public static List<Node> getComponents(Spatial spatial, Class<? extends Control> clazz) {
        final List<Node> lst = new ArrayList<>(10);
        spatial.breadthFirstTraversal(new SceneGraphVisitorAdapter() {
            @Override
            public void visit(Node node) {
                if (node.getControl(clazz) != null) {
                    lst.add(node);
                }
            }
        });
        return lst;
    }

    /**
     * Returns all components of Type type in the GameObject or any of its
     * children children using depth first search. Works recursively.
     *
     * @param <T>
     * @param sp
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> getComponentsInChildren(Spatial sp, Class<? extends Control> clazz) {
        List<T> lst = new ArrayList<>(5);
        sp.breadthFirstTraversal(new SceneGraphVisitorAdapter() {
            @Override
            public void visit(Node node) {
                T control = (T) node.getControl(clazz);
                if (control != null) {
                    lst.add(control);
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
     * @param clazz
     * @return
     */
    public static <T extends Control> T getComponentInChildren(Spatial sp, final Class<T> clazz) {
        T control = sp.getControl(clazz);
        if (control != null) {
            return control;
        }

        if (sp instanceof Node) {
            for (Spatial child : ((Node) sp).getChildren()) {
                control = getComponentInChildren(child, clazz);
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
     * @param clazz
     * @return
     */
    public static <T extends Control> T getComponentInParent(Spatial sp, Class<T> clazz) {
        Node parent = sp.getParent();
        while (parent != null) {
            T control = parent.getControl(clazz);
            if (control != null) {
                return control;
            }
            parent = parent.getParent();
        }
        return null;
    }
}