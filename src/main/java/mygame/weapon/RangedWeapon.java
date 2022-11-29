package mygame.weapon;

import java.util.ArrayList;
import java.util.List;

import com.capdevon.engine.FRotator;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 * 
 * @author capdevon
 */
public class RangedWeapon extends Weapon {

    // The bullet used
    private List<RangedBullet> bullets = new ArrayList<>();
    private int currIndex;

    // The force given to the shell if the fire button is not held.
    public float m_MinLaunchForce = 30f;
    // The force given to the shell if the fire button is held for the max charge time.
    public float m_MaxLaunchForce = 45f;
    // How long the shell can charge for before it is fired at max force.
    public float m_MaxChargeTime = 0.75f;
    // How fast the launch force increases, based on the max charge time.
    public float m_ChargeSpeed;

    public RangedWeapon() {
        // The rate that the launch force charges up is the range of possible forces by the max charge time.
        this.m_ChargeSpeed = (m_MaxLaunchForce - m_MinLaunchForce) / m_MaxChargeTime;
    }

    /**
     * @param name
     * @param model
     */
//    public RangedWeapon(String name, Node model) {
//        super(name, model);
//    }

    /**
     * @return {@link RangedBullet} The bullet this weapon fires
     */
    public RangedBullet getBullet() {
        return bullets.get(currIndex);
    }

    public void setBullets(List<RangedBullet> bullets) {
        this.bullets = bullets;
    }

    @Override
    public void switchBullet() {
        currIndex = (currIndex + 1) % bullets.size();
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " AmmoType: " + getBullet().name;
    }

    public void handleShoot(Vector3f origin, Vector3f direction, float force) {
        Spatial spBullet = getBullet().instantiate(Vector3f.ZERO, Quaternion.IDENTITY);
        RigidBodyControl rgb = spBullet.getControl(RigidBodyControl.class);
        rgb.setPhysicsLocation(origin);
        rgb.setPhysicsRotation(FRotator.lookRotation(direction));
        rgb.setLinearVelocity(direction.mult(force));
        //rgb.applyTorque(FVector.right(spBullet).mult(2));
    }

}
