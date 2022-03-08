/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.capdevon.anim.Animation3;
import com.jme3.animation.LoopMode;

public interface AnimDefs {
    
    final String MODEL              = "Models/Archer/archer.j3o";
    
    final Animation3 Idle           = new Animation3("Idle", LoopMode.Loop, .2f);
    final Animation3 Running        = new Animation3("Running", LoopMode.Loop);
    final Animation3 Running_2      = new Animation3("Running_2", LoopMode.Loop);
    final Animation3 Aim_Idle       = new Animation3("Aim_Idle", LoopMode.DontLoop);
    final Animation3 Aim_Overdraw   = new Animation3("Aim_Overdraw", LoopMode.DontLoop);
    final Animation3 Aim_Recoil     = new Animation3("Aim_Recoil", LoopMode.DontLoop);
    final Animation3 Draw_Arrow     = new Animation3("Draw_Arrow", LoopMode.DontLoop);
    final Animation3 Water_Idle     = new Animation3("Water_Idle", LoopMode.Loop);
    final Animation3 Water_Moving   = new Animation3("Water_Moving", LoopMode.Loop);
    final Animation3 Swimming       = new Animation3("Swimming", LoopMode.Loop);
}
