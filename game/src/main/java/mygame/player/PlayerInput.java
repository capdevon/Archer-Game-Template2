package mygame.player;

import com.capdevon.control.AdapterControl;
import com.capdevon.engine.Capture;
import com.capdevon.input.KeyMapping;
import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.scene.Spatial;

import mygame.states.SceneAppState;

/**
 *
 * @author capdevon
 */
public class PlayerInput extends AdapterControl implements ActionListener {

    private final Application app;
    private PlayerWeaponManager m_PlayerWeaponManager;
    private PlayerControl playerControl;

    /**
     * Instantiate an adapter.
     *
     * @param app the running application (not null)
     */
    PlayerInput(Application app) {
        this.app = app;
    }

    @Override
    public void setSpatial(Spatial sp) {
        super.setSpatial(sp);
        if (spatial != null) {
            this.playerControl = getComponent(PlayerControl.class);
            this.m_PlayerWeaponManager = getComponent(PlayerWeaponManager.class);
        }
    }

    @Override
    public void onAction(String action, boolean keyPressed, float tpf) {
        if (action.equals(KeyMapping.MOVE_LEFT)) {
            playerControl._MoveLeft = keyPressed;
        } else if (action.equals(KeyMapping.MOVE_RIGHT)) {
            playerControl._MoveRight = keyPressed;
        } else if (action.equals(KeyMapping.MOVE_FORWARD)) {
            playerControl._MoveForward = keyPressed;
        } else if (action.equals(KeyMapping.MOVE_BACKWARD)) {
            playerControl._MoveBackward = keyPressed;
        } else if (action.equals(KeyMapping.RUNNING)) {
            playerControl.isRunning = keyPressed;
        } else if (action.equals(KeyMapping.AIMING)) {
            m_PlayerWeaponManager.setAiming(keyPressed);
        } else if (action.equals(KeyMapping.FIRE) && keyPressed) {
            m_PlayerWeaponManager.shooting();
        } else if (action.equals(KeyMapping.SWITCH_AMMO) && keyPressed) {
            m_PlayerWeaponManager.switchWeaponBullet();
        }

        // Additional actions for use during development and testing:
        if (keyPressed) {
            AppStateManager stateManager = app.getStateManager();
            switch (action) {
                case KeyMapping.TOGGLE_NIGHT:
                    SceneAppState sas = stateManager.getState(SceneAppState.class);
                    sas.toggleNight();
                    return;

                case KeyMapping.TAKE_SCREENSHOT:
                    Capture.takeScreenshot(app, tpf);
                    return;

                case KeyMapping.TOGGLE_VIDEO:
                    float quality = 0.5f;
                    Capture.toggleVideo(app, quality);
                    return;
            }
        }
    }
}
