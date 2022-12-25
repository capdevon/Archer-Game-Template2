package mygame.controls;

import com.capdevon.engine.PrefabComponent;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author capdevon
 */
public class Spawner extends AbstractControl {

    public PrefabComponent prefab;
    public Vector3f center = new Vector3f(0, 0, 0);
    public float height    = 0.2f;
    public int radius      = 5;
    public int maxObjects  = 1;
    public float spawnTime = 2f;
    
    float timer = 0;

    @Override
    protected void controlUpdate(float tpf) {
        if (prefab == null) {
            return;
        }
        
        timer += tpf;
        if (timer > spawnTime) {
            timer = 0;
            
            int qty = ((Node) spatial).getQuantity();
            if (qty < maxObjects) {
                Vector3f spawnPoint = getRandomSpawnPoint().addLocal(center);
                float yAngle = FastMath.TWO_PI * FastMath.nextRandomFloat();
                Quaternion spawnRotation
                        = new Quaternion().fromAngles(0f, yAngle, 0f);
                prefab.instantiate(spawnPoint, spawnRotation, (Node) spatial);
            }
        }
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
    
    private Vector3f getRandomSpawnPoint() {
        float x = FastMath.nextRandomInt(-radius, radius);
        float z = FastMath.nextRandomInt(-radius, radius);
        return new Vector3f(x, height, z);
    }

}
