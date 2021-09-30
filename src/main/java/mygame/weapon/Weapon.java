package mygame.weapon;

import java.util.Objects;
import java.util.logging.Logger;

import com.capdevon.engine.FVector;
import com.jme3.audio.AudioNode;
import com.jme3.math.FastMath;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 * 
 * @author capevon
 */
public abstract class Weapon {

    private final Logger logger = Logger.getLogger(Weapon.class.getName());

    public enum WeaponType {
        Bow,
        Normal,
        Melee
    }
    public enum FireMode {
        Single,
        Automatic,
        Charge
    }

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
    float m_CurrentAmmo, m_MaxAmmo;
    float m_ShotsInBurst, m_MaxBurst;
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
        if (m_ShotsInBurst > 0) {
            nextShotTime = System.currentTimeMillis() + fireRate;
            m_ShotsInBurst--;
            playSound(shootSFX);
            return true;
        }
        return false;
    }

    public boolean isEmpty() {
        if (m_ShotsInBurst == 0 && m_CurrentAmmo == 0) {
            playSound(emptySFX);
            return true;
        }
        return false;
    }

    public void reload() {
        if (m_ShotsInBurst != m_MaxBurst && m_CurrentAmmo != 0) {
            nextShotTime = System.currentTimeMillis() + reloadRate;
            playSound(reloadSFX);

            float totAmmo = m_ShotsInBurst + m_CurrentAmmo;
            if (totAmmo <= m_MaxBurst) {
                m_ShotsInBurst = totAmmo;
                m_CurrentAmmo = 0;

            } else {
                float shotsFired = m_MaxBurst - m_ShotsInBurst;
                m_CurrentAmmo -= shotsFired;
                m_ShotsInBurst = m_MaxBurst;
            }
        }
    }

    public boolean isFull() {
        return m_CurrentAmmo == m_MaxAmmo;
    }

    public void setAmmo(float amount) {
        m_CurrentAmmo = FastMath.clamp(m_CurrentAmmo + amount, 0, m_MaxAmmo);
    }

    public Vector3f getShotDirectionWithinSpread(Vector3f shotDirection) {
        float spreadAngleRatio = bulletSpreadAngle / 180f;
        Vector3f spreadWorldDirection = FVector.slerp(shotDirection, FVector.insideUnitSphere(), spreadAngleRatio);
        return spreadWorldDirection;
    }

    public float getDamage() {
        return damage * (float)(Math.random() * (1 / accuracy));
    }

    private void playSound(AudioNode audio) {
        if (audio != null) {
            audio.stop();
            audio.play();
        }
    }

    public String getDescription() {
        return "Weapon[" +
            " name: " + name +
            " damage: " + damage +
            " ammo: " + m_ShotsInBurst + "/" + m_CurrentAmmo +
            " range: " + range +
            " ]";
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
        if (obj instanceof Weapon) {
            Weapon other = (Weapon) obj;
            return Objects.equals(this.name, other.name) && Objects.equals(this.weaponType, other.weaponType);
        }
        return false;
    }
    
}
