package mygame;

import com.capdevon.engine.Capture;
import com.capdevon.input.GInputAppState;
import com.capdevon.physx.Physics;
import com.capdevon.physx.TogglePhysicsDebugState;
import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.ScreenshotAppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.JoystickCompatibilityMappings;
import com.jme3.system.AppSettings;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.logging.Level;
import jme3utilities.Heart;
import mygame.audio.SoundManager;
import mygame.player.PlayerManager;
import mygame.states.CubeAppState;
import mygame.states.MonsterAppState;
import mygame.states.SceneAppState;

/**
 * @author capdevon
 */
public class Main extends SimpleApplication {

    /**
     * Start the jMonkeyEngine application
     * @param args
     */
    public static void main(String[] args) {
        // Mute the chatty loggers in certain packages.
        Heart.setLoggingLevels(Level.WARNING);
        
        loadMoreJoystickMappings();

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

    @Override
    public void simpleInitApp() {
        // disable the default 1st-person flyCam!
        stateManager.detach(stateManager.getState(FlyCamAppState.class));
        flyCam.setEnabled(false);

        SoundManager.init(assetManager);
        
        // preload models
        assetManager.loadModel(AnimDefs.Archer.ASSET_PATH);
        assetManager.loadModel(AnimDefs.Monster.ASSET_PATH);

        /** Initialize the physics simulation */
        BulletAppState physics = new BulletAppState();
        stateManager.attach(physics);
        physics.getPhysicsSpace().setGravity(Physics.DEFAULT_GRAVITY);

        stateManager.attach(new SceneAppState());
        stateManager.attach(new GInputAppState());
        stateManager.attach(new PlayerManager());
        stateManager.attach(new CubeAppState());
        stateManager.attach(new MonsterAppState());
        stateManager.attach(new TogglePhysicsDebugState());

        // an app state for taking screenshots:
        String workingDirectory = System.getProperty("user.dir") + File.separator;
        ScreenshotAppState screenshot = new ScreenshotAppState(workingDirectory, "screenshot");
        stateManager.attach(screenshot);
    }

    /**
     * Callback invoked when requesting the JmeContext to close.
     *
     * @param waitFor true&rarr;wait for the context to be fully destroyed,
     * true&rarr;don't wait
     */
    @Override
    public void stop(boolean waitFor) {
        Capture.cleanup(stateManager);
        super.stop(waitFor);
    }

    /**
     * Load joystick mappings that are specific to this application.
     */
    public static void loadMoreJoystickMappings() {
        ClassLoader loader = Main.class.getClassLoader();
        String resourcePath = "Interface/joystick-mapping.properties";
        try {
            Enumeration<URL> en = loader.getResources(resourcePath);
            while (en.hasMoreElements()) {
                URL u = en.nextElement();
                JoystickCompatibilityMappings.loadMappingProperties(u);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
