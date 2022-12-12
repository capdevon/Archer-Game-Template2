package com.capdevon.input;

import com.jme3.input.Joystick;
import com.jme3.input.JoystickAxis;
import com.jme3.input.JoystickButton;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import java.util.logging.Logger;

/**
 *
 * @author capdevon
 */
public class GInputAppState extends AbstractInputAppState {

    /**
     * message logger for this class
     */
    final public static Logger logger
            = Logger.getLogger(GInputAppState.class.getName());

    @Override
    public void registerInput() {

        addMapping(KeyMapping.MOVE_FORWARD,     new KeyTrigger(KeyInput.KEY_W));
        addMapping(KeyMapping.MOVE_BACKWARD,    new KeyTrigger(KeyInput.KEY_S));
        addMapping(KeyMapping.MOVE_LEFT,        new KeyTrigger(KeyInput.KEY_A));
        addMapping(KeyMapping.MOVE_RIGHT,       new KeyTrigger(KeyInput.KEY_D));
        addMapping(KeyMapping.AIMING,           new KeyTrigger(KeyInput.KEY_E));
        addMapping(KeyMapping.SWITCH_AMMO,      new KeyTrigger(KeyInput.KEY_R));
        addMapping(KeyMapping.TOGGLE_CROUCH,    new KeyTrigger(KeyInput.KEY_Z));
        addMapping(KeyMapping.RUNNING,          new KeyTrigger(KeyInput.KEY_LSHIFT));
        addMapping(KeyMapping.FIRE,             new MouseButtonTrigger(MouseInput.BUTTON_LEFT));

        // additional key mappings for development and testing:
        addMapping(KeyMapping.DUMP_PHYSICS,     new KeyTrigger(KeyInput.KEY_O));
        addMapping(KeyMapping.DUMP_RENDER,      new KeyTrigger(KeyInput.KEY_P));
        addMapping(KeyMapping.TAKE_SCREENSHOT,  new KeyTrigger(KeyInput.KEY_F12));
        addMapping(KeyMapping.TOGGLE_VIDEO,     new KeyTrigger(KeyInput.KEY_F11));
    }

    @Override
    public void mapJoystick(Joystick joypad) {

    	// FIXME: joystick mapping doesn't work with jme3-lwjgl3 (to be investigated)
        JoystickAxis zAxis = joypad.getAxis(JoystickAxis.Z_AXIS);
        JoystickAxis zRotation = joypad.getAxis(JoystickAxis.Z_ROTATION);
        if (zRotation != null && zAxis != null) {
            logger.info("The joystick has Z axes.");

            // Let the dpad be up and down
            assignAxis(joypad.getPovYAxis(), KeyMapping.SWITCH_AMMO, KeyMapping.SWITCH_AMMO);

        } else {
            logger.info("The joystick does not have Z axes.");
        }

        assignButton(joypad, JoystickButton.BUTTON_0, KeyMapping.EMPTY);
        assignButton(joypad, JoystickButton.BUTTON_1, KeyMapping.TOGGLE_CROUCH);
        assignButton(joypad, JoystickButton.BUTTON_2, KeyMapping.EMPTY);
        assignButton(joypad, JoystickButton.BUTTON_4, KeyMapping.RUNNING);
        assignButton(joypad, JoystickButton.BUTTON_5, KeyMapping.EMPTY);
        assignButton(joypad, JoystickButton.BUTTON_6, KeyMapping.AIMING);
        assignButton(joypad, JoystickButton.BUTTON_7, KeyMapping.FIRE);
        assignButton(joypad, JoystickButton.BUTTON_8, KeyMapping.EMPTY);
        assignButton(joypad, JoystickButton.BUTTON_9, KeyMapping.EMPTY);

        // Use the (left) stick to move.
        JoystickAxis xAxis = joypad.getAxis(JoystickAxis.X_AXIS);
        JoystickAxis yAxis = joypad.getAxis(JoystickAxis.Y_AXIS);
        assignAxis(xAxis, KeyMapping.MOVE_RIGHT, KeyMapping.MOVE_LEFT);
        assignAxis(yAxis, KeyMapping.MOVE_BACKWARD, KeyMapping.MOVE_FORWARD);
    }

}
