package com.capdevon.engine;

import com.jme3.app.Application;
import com.jme3.app.state.AppState;
import com.jme3.app.state.BaseAppState;

/**
 *
 * @author capdevon
 */
public class SceneManager extends BaseAppState {

    private Scene scene;

    /**
     * Gets the currently active Scene.
     *
     * @return
     */
    public Scene getActiveScene() {
        return scene;
    }

    /**
     * Attach all systemPrefabs associated with the given Scene.
     *
     * @param scene Scene to load.
     * @return Returns true if the Scene is loaded.
     */
    public boolean loadScene(Scene scene) {
        this.scene = scene;

        // attach all systemPrefabs
        for (Class<? extends AppState> clazz : scene.systemPrefabs) {
            try {
                AppState appState = clazz.getDeclaredConstructor().newInstance();
                getStateManager().attach(appState);
                System.out.println("attaching ... AppState: " + clazz.getCanonicalName());
            } catch (ReflectiveOperationException ex) {
                System.err.println(ex);
                return false;
            }
        }

        return true;
    }

    /**
     * Detach all systemPrefabs associated with the given Scene.
     *
     * @param scene Scene to unload.
     * @return Returns true if the Scene is unloaded.
     */
    public boolean unloadScene(Scene scene) {
        for (Class<? extends AppState> clazz : scene.systemPrefabs) {
            AppState appState = getState(clazz);
            if (appState != null) {
                getStateManager().detach(appState);
                System.out.println("detaching ... AppState: " + clazz.getCanonicalName());
            }
        }

        this.scene = null;
        return true;
    }

    @Override
    protected void initialize(Application app) {
    }

    @Override
    protected void cleanup(Application app) {
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }

}
