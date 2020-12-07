/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capdevon.engine;

import com.jme3.app.Application;
import com.jme3.app.state.AppState;
import com.jme3.app.state.BaseAppState;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * capdevon
 */
public class SceneManager extends BaseAppState {
    
    private Scene currScene;
    private AsyncOperation asyncOperation;
    private ScheduledThreadPoolExecutor executor;
    
    @Override
    protected void initialize(Application app) {
        this.executor = new ScheduledThreadPoolExecutor(2);
        System.out.println("SceneManager initialize");
    }

    @Override
    protected void cleanup(Application app) {
        executor.shutdown();
        System.out.println("SceneManager cleanup");
    }
    
    /**
     * Unloads the Scene asynchronously in the background
     * @param newScene
     * @return 
     */
    public AsyncOperation unloadSceneAsync(Scene newScene) {
        currScene = newScene;
        // Run a task specified by a Supplier object asynchronously
        CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> unloadScene(), executor);
        asyncOperation = new AsyncOperation(future);
        return asyncOperation;
    }
    
    public void unloadScene(Scene newScene) {
        currScene = newScene;
        asyncOperation = null;
        unloadScene();
    }
    
    /**
     * Loads the Scene asynchronously in the background
     * @param newScene
     * @return 
     */
    public AsyncOperation loadSceneAsync(Scene newScene) {
        currScene = newScene;
        // Run a task specified by a Supplier object asynchronously
        CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> loadScene(), executor);
        asyncOperation = new AsyncOperation(future);
        return asyncOperation;
    }
    
    public void loadScene(Scene newScene) {
        currScene = newScene;
        asyncOperation = null;
        loadScene();
    }
    
    private boolean loadScene() {
        // attach all state managers
        float i = 1;
        for (Class<? extends AppState> clazz : currScene.systemPrefabs) {
            try {
                AppState appState = clazz.newInstance();
                getStateManager().attach(appState);
                
                System.out.println("attaching ... AppState: " + clazz.getCanonicalName());
                while (!appState.isInitialized()) {
                    Thread.sleep(500);
                }
                System.out.println("AppState Attached: " + clazz.getCanonicalName());
                
                float progress = (i / currScene.systemPrefabs.size()) * 100;
                updateProgress(Math.round(progress));
                i++;
                
            } catch (InstantiationException | IllegalAccessException | InterruptedException ex) {
                System.err.println(ex);
                return false;
            }
        }
        
        return true;
    }
    
    private boolean unloadScene() {
        float i = 1;
        for (Class<? extends AppState> clazz : currScene.systemPrefabs) {
            try {
                AppState appState = getState(clazz);
                if (appState != null) {
                    getStateManager().detach(appState);

                    System.out.println("detaching ... AppState: " + clazz.getCanonicalName());
                    while (appState.isInitialized()) {
                        Thread.sleep(500);
                    }
                    System.out.println("AppState Detached: " + clazz.getCanonicalName());

                    float progress = (i / currScene.systemPrefabs.size()) * 100;
                    updateProgress(Math.round(progress));
                    i++;
                }
            } catch (InterruptedException ex) {
                System.err.println(ex);
                return false;
            }
        }

        return true;
    }
    
    private void updateProgress(int value) {
        if (asyncOperation != null) {
            System.out.println("progress: " + value + "%");
            asyncOperation.setProgress(value);
            
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                System.err.println(ex);
            }
        }
    }

    @Override
    protected void onEnable() {
        //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void onDisable() {
        //To change body of generated methods, choose Tools | Templates.
    }
    
}
