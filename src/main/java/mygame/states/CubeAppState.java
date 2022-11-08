package mygame.states;

import com.capdevon.engine.SimpleAppState;
import com.jme3.scene.Node;

import mygame.controls.Spawner;
import mygame.prefabs.MyCubePrefab;

/**
 *
 * @author capdevon
 */
public class CubeAppState extends SimpleAppState {

    @Override
    protected void simpleInit() {

        Node cubes = new Node("CubeSpwaner");
        rootNode.attachChild(cubes);

        Spawner spawner = new Spawner();
        spawner.maxObjects = 10;
        spawner.radius = 20;
        spawner.height = 5f;
        spawner.spawnTime = 3f;
        spawner.prefab = new MyCubePrefab(app);
        cubes.addControl(spawner);
    }

}
