package mygame;

import com.capdevon.anim.Animation3;

/**
 * 
 * @author capdevon
 */
public interface AnimDefs {
	
    public interface Archer {

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
    
//    static {
//        try {
//            String json = "Models/Archer/archer-anim-config.json";
//            URL resource = getClass().getClassLoader().getResource(json);
//            File file = new File(resource.toURI());
//            System.out.println("Reading json from file " + file.getAbsolutePath());
//
//            ObjectMapper mapper = new ObjectMapper();
//            Animation3[] array = mapper.readValue(file, Animation3[].class);
//
//            for (int i = 0; i < array.length; i++) {
//                System.out.println(array[i]);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    
}
