/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.capdevon.anim.Animator;
import com.capdevon.engine.JMonkey3.UIEditor;
import com.capdevon.engine.SimpleAppState;
import com.capdevon.engine.SoundManager;
import com.capdevon.input.GInputAppState;
import com.capdevon.util.AudioLib;
import com.capdevon.util.LineRenderer;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;

import mygame.camera.BPCameraCollider;
import mygame.weapon.ArrowPrefab;
import mygame.weapon.CrosshairData;
import mygame.weapon.ExplosionPrefab;
import mygame.weapon.ExplosiveArrowPrefab;
import mygame.weapon.FireWeapon;
import mygame.weapon.RangedBullet;
import mygame.weapon.RangedWeapon;
import mygame.weapon.Weapon;
import mygame.weapon.Weapon.WeaponType;

/**
 * @author capdevon
 */
public class PlayerManager extends SimpleAppState {

    private Node player;
    private PlayerInput m_PlayerInput;

    @Override
    protected void simpleInit() {
        setupPlayer();
    }

    @Override
    protected void registerInput() {
        GInputAppState ginput = stateManager.getState(GInputAppState.class);
        ginput.addActionListener(m_PlayerInput);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        app.getListener().setLocation(player.getWorldTranslation());
    }

    private void setupPlayer() {
        // Create a node for the character model
        player = (Node) assetManager.loadModel(AnimDefs.MODEL);
        player.setName("Player");

        // add Physics & Animation Control
        player.addControl(new Animator());
        player.addControl(new BetterCharacterControl(.4f, 1.8f, 80f));
        physics.getPhysicsSpace().add(player);
        rootNode.attachChild(player);

        BPCameraCollider bpCamera = new BPCameraCollider(camera, inputManager);
        bpCamera.setXOffset(-0.5f);
        bpCamera.setYHeight(1.8f);
        bpCamera.setMinDistance(1f);
        bpCamera.setMaxDistance(3f);
        bpCamera.setMinVerticalRotation(-FastMath.DEG_TO_RAD * (20));
        bpCamera.setMaxVerticalRotation(FastMath.DEG_TO_RAD * (30));
        bpCamera.setRotationSpeed(1f);
        bpCamera.setIgnoreTag("TagPlayer");
        player.addControl(bpCamera);

        WeaponUIManager m_WeaponUIManager = new WeaponUIManager();
        m_WeaponUIManager.weaponText = UIEditor.getText(20, settings.getHeight() - 20);
        player.addControl(m_WeaponUIManager);

        LineRenderer lr = new LineRenderer(app);
        lr.setLineWidth(3f);
        player.addControl(lr);

        PlayerWeaponManager m_PlayerWeaponManager = new PlayerWeaponManager();
        m_PlayerWeaponManager.assetManager = assetManager;
        m_PlayerWeaponManager.camera = camera;
        m_PlayerWeaponManager.addWeapon(createRangedWeapon());
        m_PlayerWeaponManager.addWeapon(createFireWeapon());
        m_PlayerWeaponManager.shootSFX = SoundManager.getAudioClip(AudioLib.ARROW_HIT);
        m_PlayerWeaponManager.reloadSFX = SoundManager.getAudioClip(AudioLib.BOW_PULL);
        player.addControl(m_PlayerWeaponManager);

        PlayerControl m_PlayerControl = new PlayerControl();
        m_PlayerControl.camera = camera;
        m_PlayerControl.footsteps = SoundManager.getAudioClip(AudioLib.GRASS_FOOTSTEPS);
        player.addControl(m_PlayerControl);

        m_PlayerInput = new PlayerInput();
        player.addControl(m_PlayerInput);
    }

    private Weapon createFireWeapon() {
        FireWeapon fWeapon = new FireWeapon();
        fWeapon.name = "SniperRifle";
        fWeapon.model = createFakeRifleModel();
        fWeapon.weaponType = WeaponType.Normal;
        fWeapon.ik = IKPositions.RIFLE;
        fWeapon.crosshair = new CrosshairData(guiNode, getCrossHair("+"));
        return fWeapon;
    }

    private Weapon createRangedWeapon() {
        RangedWeapon rWeapon = new RangedWeapon();
        rWeapon.name = "Bow";
        rWeapon.model = createFakeBowModel();
        rWeapon.weaponType = WeaponType.Bow;
        rWeapon.ik = IKPositions.ARCHER;
        rWeapon.crosshair = new CrosshairData(guiNode, getCrossHair("-.-"));

        RangedBullet[] bullets = new RangedBullet[3];
        bullets[0] = new ArrowPrefab(app, "Arrow");

        ExplosionPrefab eFlame = new ExplosionPrefab(app, "Scenes/jMonkey/Flame.j3o", ColorRGBA.Orange.clone(), 1.05f);
        bullets[1] = new ExplosiveArrowPrefab(app, "FlameArrow", 6f, eFlame);

        ExplosionPrefab ePoison = new ExplosionPrefab(app, "Scenes/jMonkey/Poison.j3o", new ColorRGBA(0, 1.0f, 0.452f, 1f), 8.85f);
        bullets[2] = new ExplosiveArrowPrefab(app, "PoisonArrow", 6f, ePoison);
        rWeapon.setBullets(bullets);

        return rWeapon;
    }

    private Node createFakeRifleModel() {
        Node model = new Node("Rifle");
        Geometry geo = createGeometry("Weapon.GeoMesh", new Sphere(8, 8, .05f), ColorRGBA.Red);
        model.setCullHint(Spatial.CullHint.Never);
        model.attachChild(geo);

        return model;
    }

    private Node createFakeBowModel() {
        Node model = new Node("ArcherToolkit");
        //        Geometry bow = createGeometry("Bow.GeoMesh", new Sphere(8, 8, .05f), ColorRGBA.Red);
        //        Geometry arrow = createGeometry("Arrow", new Sphere(8, 8, .05f), ColorRGBA.Green);
        //        Geometry quiver = createGeometry("Quiver", new Sphere(8, 8, .05f), ColorRGBA.Green);
        //        model.setCullHint(Spatial.CullHint.Never);
        //        model.attachChild(bow);
        //        model.attachChild(arrow);
        //        model.attachChild(quiver);

        return model;
    }

    private Geometry createGeometry(String name, Mesh mesh, ColorRGBA color) {
        Geometry geo = new Geometry(name, mesh);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color.clone());
        geo.setMaterial(mat);
        return geo;
    }

    /* A centered plus sign to help the player aim. */
    private BitmapText getCrossHair(String text) {
        BitmapText ch = new BitmapText(guiFont);
        ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
        ch.setText(text);
        float width = settings.getWidth() / 2 - ch.getLineWidth() / 2;
        float height = settings.getHeight() / 2 + ch.getLineHeight() / 2;
        ch.setLocalTranslation(width, height, 0);
        return ch;
    }
}
