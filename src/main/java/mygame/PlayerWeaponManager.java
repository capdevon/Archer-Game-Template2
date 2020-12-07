package mygame;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.capdevon.anim.Animator;
import com.capdevon.anim.HumanBodyBones;
import com.capdevon.control.AdapterControl;
import com.capdevon.physx.Physics;
import com.capdevon.physx.RaycastHit;
import com.capdevon.util.LineRenderer;
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import mygame.weapon.FireWeapon;
import mygame.weapon.RangedWeapon;
import mygame.weapon.Weapon;

public class PlayerWeaponManager extends AdapterControl implements AnimEventListener {

	private final Logger logger = Logger.getLogger(PlayerWeaponManager.class.getName());
	
	Camera camera;
	Animator animator;
	WeaponUIManager weaponUI;
	AudioNode shoot;
    AudioNode reload;

    float fov = 0;
    float aimingSpeed = 5f;
    float aimZoomRatio = 0.7f;
    float defaultFOV = 60;
	boolean isAiming, canShooting;

    private Node aimNode;
    private float m_CurrentLaunchForce;
    
    // weapon hook
    protected Node r_wh; // right hand
    protected Node l_wh; // left hand
    protected Node s_wh; // spine
    // runtime weapon
    protected Weapon currentWeapon;
    // current weapon index
    protected int index = -1;
    // weapons list
    protected List<Weapon> lstWeapons = new ArrayList<>();
    
    
    @Override
    public void setSpatial(Spatial sp) {
        super.setSpatial(sp);
        if (spatial != null) {
        	awake();
            logger.log(Level.INFO, "Initialized");
        }
    }
    
    private void awake() {
    	this.aimNode    = addEmptyNode("aim-node", new Vector3f(0, 2, 0));
        this.weaponUI	= getComponent(WeaponUIManager.class);
        this.animator   = getComponent(Animator.class);
        r_wh = createHook(HumanBodyBones.RightHand);
        l_wh = createHook(HumanBodyBones.LeftHand);
        s_wh = createHook(HumanBodyBones.Spine2);
        
        animator.addAnimListener(this);
        setFOV(defaultFOV);
        
        switchWeapon();
    }
    
    private Node createHook(String boneName) {
        Node wh = new Node();
        wh.setName("Ref-" + boneName);
        animator.getAttachments("Armature_mixamorig:" + boneName).attachChild(wh);
        System.out.println("--Setup Hook: " + wh);
        return wh;
    }
    
	@Override
    protected void controlUpdate(float tpf) {
        // TODO Auto-generated method stub

        updateWeaponAiming(tpf);
        updateWeaponCharge(tpf);
	}
	
	private void updateWeaponCharge(float tpf) {
		if (isAiming && currentWeapon instanceof RangedWeapon) {
			RangedWeapon rWeapon = (RangedWeapon) currentWeapon;
			m_CurrentLaunchForce = Math.min(m_CurrentLaunchForce + rWeapon.m_ChargeSpeed * tpf, rWeapon.m_MaxLaunchForce);
			
			predictPOI(aimNode.getWorldTranslation(), camera.getDirection().mult(m_CurrentLaunchForce));
		}
	}
	
	private void resetWeaponCharge() {
		if (currentWeapon instanceof RangedWeapon) {
			RangedWeapon rWeapon = (RangedWeapon) currentWeapon;
			m_CurrentLaunchForce = rWeapon.m_MinLaunchForce;
		}
	}

    private void updateWeaponAiming(float tpf) {
        if ((fov == 0 && !isAiming) || (fov == 1 && isAiming)) {
            return;
        }

        float m = aimingSpeed * tpf;
        fov = (isAiming) ? (fov + m) : (fov - m);
        fov = FastMath.clamp(fov, 0, 1);
        setFOV(FastMath.interpolateLinear(fov, defaultFOV, aimZoomRatio * defaultFOV));
    }

    private void setFOV(float fov) {
        float aspect = (float) camera.getWidth() / (float) camera.getHeight();
        camera.setFrustumPerspective(fov, aspect, .2f, 100f);
    }
    
    public void switchWeaponBullet() {
    	currentWeapon.switchBullet();
        onChangeWeapon();
    }
    
    public void switchWeapon() {
    	if (lstWeapons.isEmpty()) {
            return;
        }
        
        int newIndex = (index + 1) % lstWeapons.size();
        if (newIndex == index) {
            return;
        }
        
        index = newIndex;
        currentWeapon = lstWeapons.get(index);
        Node spWeapon;
        
        switch (currentWeapon.weaponType) {
            case Normal:
//                spWeapon = (Node) assetManager.loadModel(activeWeapon.fileModel);
                bindWeapon(r_wh, currentWeapon.ik[0], null);
                bindWeapon(l_wh, Transform.IDENTITY, null);
                break;

            case Bow:
//				spWeapon = (Node) assetManager.loadModel(currentWeapon.fileModel);
//				Spatial arrow 	= spWeapon.getChild("10490_arrow_v1");
//				Spatial bow 	= spWeapon.getChild("10490_bow_v1");
//				Spatial quiver 	= spWeapon.getChild("10490_quiver_v1");
                
				spWeapon = currentWeapon.model;
				Spatial arrow 	= spWeapon.getChild("Arrow.GeoMesh");
				Spatial bow 	= spWeapon.getChild("Bow.GeoMesh");
				Spatial quiver 	= spWeapon.getChild("Quiver.GeoMesh");
				bindWeapon(r_wh, currentWeapon.ik[0], arrow);
				bindWeapon(l_wh, currentWeapon.ik[1], bow);
				bindWeapon(s_wh, currentWeapon.ik[2], quiver);
                break;

            default:
            	logger.log(Level.SEVERE, "WeaponType unknow");
                break;
        }
        
    	onChangeWeapon();
    }
    
    private void onChangeWeapon() {
    	weaponUI.changeWeapon(currentWeapon);
    }

    public void setAiming(boolean isAiming) {
        this.isAiming = isAiming;
        // collCamera.setZooming(isAiming);
        currentWeapon.crosshair.setEnabled(isAiming);
        animator.setAnimation(AnimDefs.Draw_Arrow);
    }

    public void shooting() {
        if (isAiming && canShooting) {
        	
        	// Aim the ray from character location in camera direction.
        	Vector3f origin = aimNode.getWorldTranslation();
        	Vector3f dir = camera.getDirection();
            
            if (currentWeapon instanceof FireWeapon) {
            	FireWeapon fWeapon = (FireWeapon) currentWeapon;
            	fWeapon.handleShoot(origin, dir);
            	
            } else if (currentWeapon instanceof RangedWeapon) {
            	RangedWeapon rWeapon = (RangedWeapon) currentWeapon;
            	rWeapon.handleShoot(origin, dir, m_CurrentLaunchForce);
            	logger.log(Level.INFO, "m_CurrentLaunchForce: " + m_CurrentLaunchForce);
            }
            
            shoot.playInstance();
            animator.setAnimation(AnimDefs.Aim_Recoil);
        }
    }

    @Override
    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
        //To change body of generated methods, choose Tools | Templates.
        if (animName.equals(AnimDefs.Aim_Recoil.name)) {
        	animator.setAnimation(AnimDefs.Draw_Arrow);

        } else if (animName.equals(AnimDefs.Draw_Arrow.name)) {
        	animator.setAnimation(AnimDefs.Aim_Overdraw);
        }
    }

    @Override
    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
        // To change body of generated methods, choose Tools | Templates.
    	showRightHandItem(false);
    	
        if (animName.equals(AnimDefs.Aim_Recoil.name)) {
            setWeaponCharging();

        } else if (animName.equals(AnimDefs.Draw_Arrow.name)) {
        	setWeaponCharging();
        	showRightHandItem(true);
        	
        } else if (animName.equals(AnimDefs.Aim_Overdraw.name)) {
            setWeaponReady();
            showRightHandItem(true);
        }
    }

    private void setWeaponReady() {
        canShooting = true;
        currentWeapon.crosshair.setColor(ColorRGBA.White);
        reload.play();
    }

    private void setWeaponCharging() {
        canShooting = false;
        currentWeapon.crosshair.setColor(ColorRGBA.Red);
        reload.stop();
        resetWeaponCharge();
    }
    
    private void bindWeapon(Node wh, Transform ik, Spatial model) {
        wh.detachAllChildren();
        wh.setLocalTransform(ik);
        
        if (model != null)
            wh.attachChild(model);

        String msg =  String.format("\n\nModel: %s \nParent: %s \nPos: %s \nRot: %s \nScale: %s", 
        		model, wh.getName(),
                wh.getLocalTranslation().toString(), 
                wh.getLocalRotation().toString(), 
                wh.getLocalScale().toString());
        
        logger.log(Level.INFO, msg);
    }

    public boolean addWeapon(Weapon w) {
        return lstWeapons.add(w);
    }

    public boolean removeWeapon(Weapon w) {
        return lstWeapons.remove(w);
    }
    
    public Weapon getActiveWeapon() {
        return currentWeapon;
    }
    
    private void showRightHandItem(boolean show) {
        r_wh.setCullHint( show ? Spatial.CullHint.Never : Spatial.CullHint.Always );
    }
    
    private void showLeftHandItem(boolean show) {
        l_wh.setCullHint( show ? Spatial.CullHint.Never : Spatial.CullHint.Always );
    }
    
    
    RaycastHit hitInfo = new RaycastHit();
    LineRenderer lr;
    List<Vector3f> points = new LinkedList<>();
    boolean drawPoints = true;
    
    private void predictPOI(Vector3f launchLocation, Vector3f launchVelocity) {
    	Vector3f gravity = PhysicsSpace.getPhysicsSpace().getGravity(null);
    	Vector3f velocity = launchVelocity.clone();
    	Vector3f location = launchLocation.clone();
    	Vector3f previousLocation = new Vector3f();
        
        float timeStep = 0.02f; // seconds per step
//        logger.log(Level.INFO, "Start simulation. Position=" + location + ", Velocity=" + launchVelocity + ", timeStep=" + timeStep); 

        points.clear();
        
        // Predict impact for next 2 seconds.
        for (int stepIndex = 0; stepIndex < 100; ++stepIndex) {
        	
        	previousLocation.set(location);
        	accumulateScaled(location, velocity, timeStep);
        	accumulateScaled(velocity, gravity, timeStep);
        	
        	points.add(location.clone());
        	
        	if (Physics.doLinecast(previousLocation, location, hitInfo)) {
//        		logger.log(Level.INFO, hitInfo.toString());
//        		logger.log(Level.INFO, "Stop simulation. stepIndex=" + stepIndex);
        		break;
        	}
        }
        
        if (drawPoints) {
			lr.setPoints(points);
        	lr.updateGeometry();
		}
        
//        logger.log(Level.INFO, "End simulation.");
    }
    
    /**
     * Accumulate a linear combination of vectors.
     *
     * @param total sum of the scaled inputs so far (not null, modified)
     * @param input the vector to scale and add (not null, unaffected)
     * @param scale scale factor to apply to the input
     */
    private void accumulateScaled(Vector3f total, Vector3f input, float scale) {
        total.x += input.x * scale;
        total.y += input.y * scale;
        total.z += input.z * scale;
    }

}
