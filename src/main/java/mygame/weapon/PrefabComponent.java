package mygame.weapon;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public abstract class PrefabComponent {
	
	private int objectId = 0;
	
	SimpleApplication app;
	
	public PrefabComponent(Application app) {
    	this.app = (SimpleApplication) app;
    }
	
    public int nextSeqId() {
    	return ++objectId;
    }
    
    public abstract Spatial getAssetModel();
    
    public abstract Spatial instantiate(Vector3f position, Quaternion rotation, Node parent);
    
    /**
     * 
     * @param position
     * @param rotation
     * @return
     */
    public Spatial instantiate(Vector3f position, Quaternion rotation) {
    	return instantiate(position, rotation, app.getRootNode());
    }

}
