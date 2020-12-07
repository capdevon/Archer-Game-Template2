/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.weapon;

import java.util.Objects;
import java.util.logging.Logger;

import com.capdevon.engine.FVector;
import com.jme3.audio.AudioNode;
import com.jme3.math.FastMath;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

public abstract class Weapon {
	
	private final Logger logger = Logger.getLogger(Weapon.class.getName());
	
	public enum WeaponType { Bow, Normal, Melee }
    public enum FireMode { Single, Automatic, Charge }

    public WeaponType weaponType;
    // The type of weapon wil affect how it shoots
    public FireMode fireMode;
	
	// Default data for the crosshair
    public CrosshairData crosshair;
    public Node weaponHook;
    public Node model;
    public String name;
    public String fileModel;
    public Transform[] ik;
    public float range;
    public float damage;
    
    float accuracy;
    float _CurrentAmmo, _MaxAmmo;
    float _ShotsInBurst, _MaxBurst;
    long nextShotTime;
    long fireRate, reloadRate;
    
    // Angle for the cone in which the bullets will be shot randomly (0 means no spread at all)
    public float bulletSpreadAngle = 0f;
    // Amount of bullets per shot
    public int bulletsPerShot = 1;
    
    // Sound played when shooting
    public AudioNode shootSFX;
    // Sound played when reloading
    public AudioNode reloadSFX;
    // Sound played when ammo is empty
    public AudioNode emptySFX;
    // Sound played when changing to this weapon
    public AudioNode changeWeaponSFX;
    
    public Weapon() {
    	// default empty.
    }

    /**
     * @param name
     * @param model
     */
    public Weapon(String name, Node model) {
        this.model = model;
        this.name = name;
    }
    
    public void switchBullet() {
    	// default empty.
    }

	public boolean canShooting() {
        return System.currentTimeMillis() > nextShotTime;
    }

    public boolean tryShoot() {
        if (_ShotsInBurst > 0) {
            nextShotTime = System.currentTimeMillis() + fireRate;
            _ShotsInBurst--;
            playSound(shootSFX);
            return true;
        }
        return false;
    }

    public boolean isEmpty() {
        if (_ShotsInBurst == 0 && _CurrentAmmo == 0) {
            playSound(emptySFX);
            return true;
        }
        return false;
    }

    public void reload() {
        if (_ShotsInBurst != _MaxBurst && _CurrentAmmo != 0) {
            nextShotTime = System.currentTimeMillis() + reloadRate;
            playSound(reloadSFX);

            float totAmmo = _ShotsInBurst + _CurrentAmmo;
            if (totAmmo <= _MaxBurst) {
                _ShotsInBurst = totAmmo;
                _CurrentAmmo = 0;

            } else {
                float shotsFired = _MaxBurst - _ShotsInBurst;
                _CurrentAmmo -= shotsFired;
                _ShotsInBurst = _MaxBurst;
            }
        }
    }

    public boolean isFull() {
        return _CurrentAmmo == _MaxAmmo;
    }

    public void setAmmo(float amount) {
        _CurrentAmmo = FastMath.clamp(_CurrentAmmo + amount, 0, _MaxAmmo); 
    }
    
    public Vector3f getShotDirectionWithinSpread(Vector3f shotDirection) {
        float spreadAngleRatio = bulletSpreadAngle / 180f;
        Vector3f spreadWorldDirection = FVector.slerp(shotDirection, FVector.insideUnitSphere(), spreadAngleRatio);
        return spreadWorldDirection;
    }

    public float getDamage() {
        return damage * (float) (Math.random() * (1 / accuracy));
    }

    private void playSound(AudioNode audio) {
        if (audio != null) {
            audio.stop();
            audio.play();
        }
    }

    public String getDescription() {
        return "Weapon["
                + " name: " + name
                + " damage: " + damage
                + " ammo: " + _ShotsInBurst + "/" + _CurrentAmmo
                + " range: " + range
                + " ]";
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.weaponType);
        hash = 79 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Weapon other = (Weapon) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (this.weaponType != other.weaponType) {
            return false;
        }
        return true;
    }
}
