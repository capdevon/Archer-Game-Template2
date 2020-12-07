/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capdevon.engine;

import com.jme3.app.state.AppState;
import java.util.List;

/**
 *
 * @author capdevon
 */
public class Scene {
    
    public String name;
    public List<Class<? extends AppState>> systemPrefabs;
    
}