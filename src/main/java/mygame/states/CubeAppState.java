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

        Node cubeSpawner = new Node("CubeSpwaner");
        rootNode.attachChild(cubeSpawner);

        Spawner spawner = new Spawner();
        spawner.maxObjects = 10;
        spawner.radius = 20;
        spawner.height = 5f;
        spawner.spawnTime = 3f;
        spawner.prefab = new MyCubePrefab(app);
        cubeSpawner.addControl(spawner);
    }

}
