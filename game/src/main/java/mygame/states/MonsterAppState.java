package mygame.states;

import com.capdevon.engine.SimpleAppState;
import com.jme3.scene.Node;

import mygame.controls.Spawner;
import mygame.prefabs.MonsterPrefab;

/**
 *
 * @author capdevon
 */
public class MonsterAppState extends SimpleAppState {

    @Override
    protected void simpleInit() {

        Node monsters = new Node("MonsterSpawner");
        rootNode.attachChild(monsters);

        Spawner spawner = new Spawner();
        spawner.maxObjects = 10;
        spawner.radius = 20;
        spawner.height = 5f;
        spawner.spawnTime = 3f;
        spawner.prefab = new MonsterPrefab(app);
        monsters.addControl(spawner);
    }

}
