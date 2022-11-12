package mygame.controls;

import java.util.ArrayList;
import java.util.List;

import com.capdevon.control.AdapterControl;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.SceneGraphVisitorAdapter;
import com.jme3.scene.Spatial;

/**
 * 
 * @author capdevon
 */
public class Damageable extends AdapterControl {

    private boolean hasAppliedDamage;

    public void applyDamage() {

        if (hasAppliedDamage)
            return;

        hasAppliedDamage = true;
        List<Geometry> lstGeometries = listGeometries(spatial);

        for (Geometry geo: lstGeometries) {
            Material mat = geo.getMaterial();

            if ("MaterializePBR".equals(mat.getName())) {

                MaterializeTimer mTimer = new MaterializeTimer(mat, false, 0.4f);
                mTimer.setMaterializerListener(new MaterializerListener() {
                    @Override
                    public void onCompleteEvent() {
                        mTimer.setMaterializerListener(null);
                        geo.removeControl(mTimer);
                        destroy();
                    }
                });
                geo.addControl(mTimer);
                System.out.println(spatial + "; MaterializeTimer added");
            }
        }
    }

    private List<Geometry> listGeometries(Spatial subtree) {
        List<Geometry> storeResult = new ArrayList<>();
        subtree.breadthFirstTraversal(new SceneGraphVisitorAdapter() {
            @Override
            public void visit(Geometry geo) {
                storeResult.add(geo);
            }
        });

        return storeResult;
    }

    private void destroy() {
        PhysicsSpace.getPhysicsSpace().removeAll(spatial);
        spatial.removeFromParent();
        System.out.println("Destroyed: " + spatial);
    }

}
