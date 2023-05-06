package com.capdevon.input;

/**
 *
 * @author capdevon
 */
public interface KeyMapping {

    final String MOVE_FORWARD   = "MOVE_FORWARD";
    final String MOVE_BACKWARD  = "MOVE_BACKWARD";
    final String MOVE_LEFT      = "MOVE_LEFT";
    final String MOVE_RIGHT     = "MOVE_RIGHT";
    final String RUNNING        = "RUNNING";
    final String AIMING         = "AIMING";
    final String FIRE           = "FIRE";
    final String SWITCH_AMMO    = "SWITCH_WEAPON";
    final String TOGGLE_CROUCH  = "TOGGLE_CROUCH";
    final String TOGGLE_PAUSE   = "TOGGLE_PAUSE";
    final String EMPTY          = "EMPTY";

    // additional key mappings for development and testing:
    final String DUMP_PHYSICS    = "DUMP_PHYSICS";
    final String DUMP_RENDER     = "DUMP_RENDER";
    final String TAKE_SCREENSHOT = "TAKE_SCREENSHOT";
    final String TOGGLE_NIGHT    = "TOGGLE_NIGHT";
    final String TOGGLE_VIDEO    = "TOGGLE_VIDEO";

}
