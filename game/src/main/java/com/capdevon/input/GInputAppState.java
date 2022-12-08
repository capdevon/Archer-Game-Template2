package com.capdevon.input;

import com.jme3.input.Joystick;
import com.jme3.input.JoystickAxis;
import com.jme3.input.JoystickButton;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;

/**
 *
 * @author capdevon
 */
public class GInputAppState extends AbstractInputAppState {

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
        // Map it differently if there are Z axis
        if (joypad.getAxis(JoystickAxis.Z_ROTATION) != null && joypad.getAxis(JoystickAxis.Z_AXIS) != null) {

            // And let the dpad be up and down
            assignButton(joypad, JoystickButton.BUTTON_0, KeyMapping.EMPTY);
            assignButton(joypad, JoystickButton.BUTTON_1, KeyMapping.TOGGLE_CROUCH);
            assignButton(joypad, JoystickButton.BUTTON_2, KeyMapping.EMPTY);
            assignButton(joypad, JoystickButton.BUTTON_4, KeyMapping.RUNNING);
            assignButton(joypad, JoystickButton.BUTTON_5, KeyMapping.EMPTY);
            assignButton(joypad, JoystickButton.BUTTON_6, KeyMapping.AIMING);
            assignButton(joypad, JoystickButton.BUTTON_7, KeyMapping.FIRE);
            assignButton(joypad, JoystickButton.BUTTON_8, KeyMapping.EMPTY);
            assignButton(joypad, JoystickButton.BUTTON_9, KeyMapping.EMPTY);

            // Make the left stick move
            assignAxis(joypad.getXAxis(), KeyMapping.MOVE_RIGHT, KeyMapping.MOVE_LEFT);
            assignAxis(joypad.getYAxis(), KeyMapping.MOVE_BACKWARD, KeyMapping.MOVE_FORWARD);

            // And let the dpad be up and down
            assignAxis(joypad.getPovYAxis(), KeyMapping.SWITCH_AMMO, KeyMapping.SWITCH_AMMO);
        }
    }

}
