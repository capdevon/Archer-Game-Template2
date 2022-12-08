package mygame;

import com.capdevon.anim.Animation3;

/**
 * 
 * @author capdevon
 */
public interface AnimDefs {
	
    public interface Archer {
    	
        final String ASSET_PATH = "Models/Erika/Erika.j3o";

        final Animation3 Idle               = new Animation3("Idle", true);
        final Animation3 Running            = new Animation3("Running", true);
        final Animation3 Sprinting          = new Animation3("Sprinting", true);
        final Animation3 AimIdle            = new Animation3("StandingAimIdle", false);
        final Animation3 AimOverdraw        = new Animation3("StandingAimOverdraw", false);
        final Animation3 AimRecoil          = new Animation3("StandingAimRecoil", false);
        final Animation3 DrawArrow          = new Animation3("StandingDrawArrow", false);
        final Animation3 MeleeKick          = new Animation3("StandingMeleeKick", false);
        final Animation3 CrouchIdle         = new Animation3("CrouchIdle", true);
        final Animation3 StandingToCrouch   = new Animation3("StandingToCrouch", false);
        final Animation3 CrouchToStanding   = new Animation3("CrouchToStanding", false);
        final Animation3 CrouchWalkForward  = new Animation3("CrouchWalkForward", true);
        final Animation3 ReactBack          = new Animation3("StandingReactBack", false);
        final Animation3 ReactFront         = new Animation3("StandingReactFront", false);
        final Animation3 Death              = new Animation3("StandingDeathForward_1", false);
        final Animation3 Death2             = new Animation3("StandingDeathForward_2", false);

    }
    
    public interface Monster {
    	
        final String ASSET_PATH = "Models/Drake/Drake.j3o";

        final Animation3 Idle           = new Animation3("Idle", true);
        final Animation3 OrcIdle        = new Animation3("OrcIdle", true);
        final Animation3 OrcIdle2       = new Animation3("OrcIdle2", true);
        final Animation3 Running        = new Animation3("Running", true);
        final Animation3 Running2       = new Animation3("Running2", true);
        final Animation3 Walk           = new Animation3("Walk", true);
        final Animation3 Scream         = new Animation3("Scream", true);
        final Animation3 Death          = new Animation3("Death", false);
        final Animation3 Dying          = new Animation3("Dying", false);
        final Animation3 ReactionHit    = new Animation3("ReactionHit", false);
        final Animation3 ReactionHit3   = new Animation3("ReactionHit3", false);
        final Animation3 ReactionHit2   = new Animation3("ReactionHit2", false);
        final Animation3 Attack         = new Animation3("Attack", false);
        final Animation3 Attack2        = new Animation3("Attack2", false);
        final Animation3 Punching       = new Animation3("Punching", false);
    }
    
}
