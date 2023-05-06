package com.capdevon.engine;

import com.jme3.app.state.AppState;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author capdevon
 */
public class Scene {

//    String fileName;
//    List<String> filters; //.j3f
    final String name;
    final List<Class<? extends AppState>> systemPrefabs;

    public Scene(String name) {
        this.name = name;
        systemPrefabs = new ArrayList<>();
    }

    public void addSystemPrefab(Class<? extends AppState> clazz) {
        systemPrefabs.add(clazz);
    }

    public String getName() {
        return name;
    }

}
