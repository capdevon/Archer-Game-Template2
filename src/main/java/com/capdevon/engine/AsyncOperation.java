/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capdevon.engine;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 *
 * @author capdevon
 */
public class AsyncOperation {
    
    private CompletableFuture<Boolean> future;
    private AtomicInteger progress;
    private AtomicBoolean allowSceneActivation;
    
    public AsyncOperation(CompletableFuture<Boolean> operation) {
        this.future = operation;
        this.progress = new AtomicInteger(0);
        this.allowSceneActivation = new AtomicBoolean(true);
    }
    
    /**
     * Has the operation finished?
     * @return 
     */
    public boolean isDone() {
        return future.isDone();
    }
    
    public int getProgress() {
        return progress.get();
    }
    
    public void setProgress(int value) {
        progress.set(value);
    }
    
    public boolean isAllowSceneActivation() {
        return allowSceneActivation.get();
    }
    
    public void setAllowSceneActivation(boolean value) {
        allowSceneActivation.set(value);
    }
    
    /**
     * Event that is invoked upon operation completion
     * @param action 
     */
    public void onCompleted(Consumer action) {
        future.thenAccept(action);
    }
    
}
