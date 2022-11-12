package mygame.controls;

import java.util.Objects;

import com.capdevon.control.AdapterControl;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 *
 * @author capdevon
 */
public class RespawnPlayer extends AdapterControl {

    private Vector3f spawnPoint = new Vector3f(0, 0, 0);
    private float height = -20f;
    private BetterCharacterControl bcc;

    @Override
    public void setSpatial(Spatial sp) {
        super.setSpatial(sp);
        if (spatial != null) {
            this.bcc = spatial.getControl(BetterCharacterControl.class);
            Objects.requireNonNull(bcc, "BetterCharacterControl not found: " + spatial);
        }
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (spatial.getWorldTranslation().y < height) {
            bcc.warp(spawnPoint);
        }
    }

    public Vector3f getSpawnPoint() {
        return spawnPoint;
    }

    public void setSpawnPoint(Vector3f spawnPoint) {
        this.spawnPoint = spawnPoint;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

}
