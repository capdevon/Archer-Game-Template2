package mygame;

import com.capdevon.engine.Scene;
import com.capdevon.engine.SceneManager;
import com.capdevon.engine.SimpleAppState;
import com.capdevon.input.GInputAppState;
import com.capdevon.input.KeyMapping;
import com.jme3.app.Application;
import com.jme3.input.controls.ActionListener;

/**
 *
 * @author capdevon
 */
public class GameManager extends SimpleAppState implements ActionListener {

    public enum GameState {
        PREGAME, LOADING, RUNNING, PAUSED
    }

    private GameState gameState = GameState.PREGAME;
    private SceneManager sceneManager;
    private Scene currScene;

    @Override
    protected void simpleInit() {
        sceneManager = getState(SceneManager.class);
        currScene = Boot.Scene1.get();
        startGame();
    }

    @Override
    protected void cleanup(Application app) {
    }

    @Override
    protected void onEnable() {
        //getState(GInputAppState.class).addActionListener(this);
    }

    @Override
    protected void onDisable() {
        //getState(GInputAppState.class).removeActionListener(this);
    }

    @Override
    public void update(float tpf) {
    }

    private void loadScene(Scene scene) {
        System.out.println("loading scene: " + scene.getName());
        boolean sceneLoaded = sceneManager.loadScene(scene);
        if (!sceneLoaded) {
            System.out.println("An error occurred while loading scene: " + scene.getName());
        }
        // flyCam.setDragToRotate(false);
        updateState(GameState.RUNNING);
        System.out.println("loadLevel completed");
    }

    private void unloadScene(Scene scene) {
        updateState(GameState.PREGAME);
        boolean sceneUnloaded = sceneManager.unloadScene(scene);
        if (!sceneUnloaded) {
            System.out.println("An error occurred while unloading scene: " + scene.getName());
        }

        getPhysicsSpace().removeAll(rootNode);
        rootNode.detachAllChildren();
        rootNode.getLocalLightList().clear();
        System.out.println("unloadLevel completed");
    }

    @Override
    public void onAction(String action, boolean isPressed, float tpf) {
        if (gameState == GameState.RUNNING) {
            if (action.equals(KeyMapping.TOGGLE_PAUSE) && isPressed) {
                togglePause();
            }
        }
    }

    private void updateState(GameState newState) {
        gameState = newState;
    }

    public void togglePause() {
        app.togglePause();
        boolean isRunning = (gameState == GameState.RUNNING);
        updateState(isRunning ? GameState.PAUSED : GameState.RUNNING);
    }

    public void startGame() {
        updateState(GameState.LOADING);
        loadScene(currScene);
    }

    public void quitGame() {
        app.stop();
    }

}
