package mygame;

import com.capdevon.engine.Scene;

import mygame.player.PlayerManager;
import mygame.states.CubeAppState;
import mygame.states.MonsterAppState;
import mygame.states.SceneAppState;

/**
 *
 * @author capdevon
 */
public enum Boot {

    Scene1 {
        @Override
        public Scene get() {
            Scene scene = new Scene("Scene 1");
            scene.addSystemPrefab(SceneAppState.class);
            scene.addSystemPrefab(PlayerManager.class);
            scene.addSystemPrefab(CubeAppState.class);
            scene.addSystemPrefab(MonsterAppState.class);
            return scene;
        }
    };

    public abstract Scene get();

}
