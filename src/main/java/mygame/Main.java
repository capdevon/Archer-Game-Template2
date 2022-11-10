package mygame;

import com.capdevon.input.GInputAppState;
import com.capdevon.physx.Physics;
import com.capdevon.physx.TogglePhysicsDebugState;
import com.github.stephengold.jmepower.JmeLoadingState;
import com.jme3.app.DebugKeysAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.app.state.AppState;
import com.jme3.app.state.ConstantVerifierState;
import com.jme3.audio.AudioListenerState;
import com.jme3.bullet.BulletAppState;
import com.jme3.system.AppSettings;

import jme3utilities.Loadable;
import mygame.audio.SoundManager;
import mygame.player.PlayerManager;
import mygame.player.PlayerModel;
import mygame.prefabs.ArrowPrefab;
import mygame.prefabs.ExplosionPrefab;
import mygame.prefabs.MyCubePrefab;
import mygame.states.CubeAppState;
import mygame.states.SceneAppState;

/**
 * @author capdevon
 */
public class Main extends SimpleApplication {

    /**
     * Construct the application instance but don't attach any appstates.
     */
    private Main() {
        super((AppState[]) null);
    }

    /**
     * Start the jMonkeyEngine application
     * @param args
     */
    public static void main(String[] args) {

        Main app = new Main();

        AppSettings settings = new AppSettings(true);
        settings.setTitle("Archer-Game-Template-2");
        settings.setUseJoysticks(true);
        settings.setResolution(1280, 720);
        settings.setSamples(4);
        settings.setBitsPerPixel(32);
        //settings.setFrameRate(60);

        app.setSettings(settings);
        app.setShowSettings(true);
        app.setPauseOnLostFocus(false);
        app.start();
    }

    /**
     * Start the loading screen.
     */
    @Override
    public void simpleInitApp() {
        // Initialize the physics simulation.
        BulletAppState physics = new BulletAppState();
        physics.setThreadingType(BulletAppState.ThreadingType.SEQUENTIAL);
        stateManager.attach(physics);
        physics.getPhysicsSpace().setGravity(Physics.DEFAULT_GRAVITY);
        physics.setDebugEnabled(false);

        ExplosionPrefab eFlame = new ExplosionPrefab(this);
        eFlame.assetName = "Scenes/jMonkey/Flame.j3o";

        ExplosionPrefab ePoison = new ExplosionPrefab(this);
        ePoison.assetName = "Scenes/jMonkey/Poison.j3o";

        Loadable[] preloadArray = new Loadable[]{
            new PlayerModel(),
            //new MonsterPrefab(this),
            new ArrowPrefab(this),
            eFlame,
            ePoison,
            new MyCubePrefab(this),
            AudioLib.ARROW_HIT,
            AudioLib.BOW_PULL,
            AudioLib.GRASS_FOOTSTEPS
        };
        JmeLoadingState loading = new JmeLoadingState(preloadArray);
        stateManager.attach(loading);
    }

    /**
     * Check for completion of the loading screen.
     *
     * @param tpf ignored
     */
    @Override
    public void simpleUpdate(float tpf) {
        AppState loading = stateManager.getState(JmeLoadingState.class);
        if (loading != null && !loading.isEnabled()) {
            getStateManager().detach(loading);
            startGame();
        }
    }

    /**
     * After the loading screen has completed, initialize the game.
     */
    private void startGame() {
        /*
         * Attach the appstates that SimpleApplication attaches by default,
         * except for FlyCamAppState.
         */
        stateManager.attachAll(
                new AudioListenerState(),
                new ConstantVerifierState(),
                new DebugKeysAppState(),
                new StatsAppState()
        );

        SoundManager.init(assetManager);
        stateManager.attach(new SceneAppState());
        stateManager.attach(new CubeAppState());
        stateManager.attach(new GInputAppState());
        stateManager.attach(new PlayerManager());
        stateManager.attach(new TogglePhysicsDebugState());

        //String dirName = System.getProperty("user.dir") + "/video";
        //Capture.captureVideo(this, 0.5f, dirName);
    }

}
