package mygame.player;

import java.util.ArrayList;
import java.util.List;

import com.capdevon.anim.Animator;
import com.capdevon.engine.GameObject;
import com.capdevon.engine.SimpleAppState;
import com.capdevon.input.GInputAppState;
import com.capdevon.util.LineRenderer;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;

import mygame.AnimDefs;
import mygame.AudioLib;
import mygame.audio.SoundManager;
import mygame.camera.BPCameraCollider;
import mygame.controls.RespawnPlayer;
import mygame.prefabs.ArrowPrefab;
import mygame.prefabs.ExplosionPrefab;
import mygame.prefabs.ExplosiveArrowPrefab;
import mygame.weapon.CrosshairData;
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
    private PlayerInput playerInput;

    @Override
    protected void simpleInit() {
        setupPlayer();
        registerInput();
    }

    private void registerInput() {
        GInputAppState gInputManager = getState(GInputAppState.class);
        gInputManager.addActionListener(playerInput);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        app.getListener().setLocation(player.getWorldTranslation());
    }

    private void setupPlayer() {
        // setup character model
        player = (Node) assetManager.loadModel(AnimDefs.Archer.ASSET_PATH);
        player.setUserData(GameObject.TAG_NAME, "TagPlayer");
        player.setName("Player");
        rootNode.attachChild(player);
        
        // configure Physics Character
        BetterCharacterControl bcc = new BetterCharacterControl(.5f, 1.8f, 20f);
        player.addControl(bcc);
        getPhysicsSpace().add(bcc);

        player.addControl(new Animator());
        player.addControl(new RespawnPlayer());
        
        BPCameraCollider bpCamera = new BPCameraCollider(camera, inputManager);
        bpCamera.setXOffset(-0.4f);
        bpCamera.setYHeight(1.8f);
        bpCamera.setMinDistance(1f);
        bpCamera.setMaxDistance(3f);
        bpCamera.setMinVerticalRotation(FastMath.DEG_TO_RAD * -45);
        bpCamera.setMaxVerticalRotation(FastMath.DEG_TO_RAD * 45);
        bpCamera.setRotationSpeed(1f);
        bpCamera.setIgnoreTag("TagPlayer");
        player.addControl(bpCamera);

        WeaponUIManager weaponUI = new WeaponUIManager();
        weaponUI.weaponText = createUIText(20, settings.getHeight() - 20, ColorRGBA.Red);
        player.addControl(weaponUI);

        LineRenderer lr = new LineRenderer(app);
        lr.setLineWidth(3f);
        player.addControl(lr);

        PlayerWeaponManager weaponManager = new PlayerWeaponManager();
        weaponManager.assetManager = assetManager;
        weaponManager.camera = camera;
        weaponManager.addWeapon(createRangedWeapon());
        weaponManager.addWeapon(createFireWeapon());
        weaponManager.shootSFX = SoundManager.createAudioBuffer(AudioLib.ARROW_HIT);
        weaponManager.reloadSFX = SoundManager.createAudioBuffer(AudioLib.BOW_PULL);
        player.addControl(weaponManager);

        PlayerControl playerControl = new PlayerControl();
        playerControl.camera = camera;
        playerControl.footstepsSFX = SoundManager.createAudioBuffer(AudioLib.GRASS_FOOTSTEPS);
        player.addControl(playerControl);

        playerInput = new PlayerInput(app);
        player.addControl(playerInput);
    }

    private Weapon createFireWeapon() {
        FireWeapon fWeapon = new FireWeapon();
        fWeapon.name = "SniperRifle";
        fWeapon.weaponType = WeaponType.Normal;
        fWeapon.crosshair = new CrosshairData(guiNode, getCrossHair("+"));
        
        Spatial rifle = createFakeRifleModel();
        rifle.addControl(fWeapon);
        
        return fWeapon;
    }

    private Weapon createRangedWeapon() {
        RangedWeapon rWeapon = new RangedWeapon();
        rWeapon.name = "Bow";
        rWeapon.weaponType = WeaponType.Bow;
        rWeapon.crosshair = new CrosshairData(guiNode, getCrossHair("-.-"));
        Spatial bow = createFakeBowModel();
        bow.addControl(rWeapon);

        // 1.
        ExplosionPrefab eFlame = new ExplosionPrefab(app);
        eFlame.assetName = "Scenes/jMonkey/Flame.j3o";
        eFlame.explosionColor = ColorRGBA.Orange.clone();
        eFlame.lifeTimeVFX = 1.05f;
        
        ExplosiveArrowPrefab fArrow = new ExplosiveArrowPrefab(app);
        fArrow.name = "FlameArrow";
        fArrow.mass = 6f;
        fArrow.explosionPrefab = eFlame;

        // 2.
        ExplosionPrefab ePoison = new ExplosionPrefab(app);
        ePoison.assetName = "Scenes/jMonkey/Poison.j3o";
        ePoison.explosionColor = new ColorRGBA(0, 1.0f, 0.452f, 1f);
        ePoison.lifeTimeVFX = 8.85f;
        
        ExplosiveArrowPrefab pArrow = new ExplosiveArrowPrefab(app);
        pArrow.name = "PoisonArrow";
        pArrow.mass = 6f;
        pArrow.explosionPrefab = ePoison;
        
        // 3.
        ArrowPrefab arrow = new ArrowPrefab(app);
        arrow.mass = 6f;
        arrow.name = "Arrow";
        
        List<RangedBullet> bullets = new ArrayList<>();
        bullets.add(fArrow);
        bullets.add(pArrow);
        bullets.add(arrow);
        
        // set arrows
        rWeapon.setBullets(bullets);

        return rWeapon;
    }

    private Node createFakeRifleModel() {
        Node model = new Node("Rifle");
        Geometry geo = makeGeometry("Weapon.GeoMesh", new Sphere(8, 8, .05f), ColorRGBA.Red);
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
    
    private Geometry makeGeometry(String name, Mesh mesh, ColorRGBA color) {
        Geometry geo = new Geometry(name, mesh);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        geo.setMaterial(mat);
        return geo;
    }
    
    private BitmapText createUIText(float xPos, float yPos, ColorRGBA color) {
        BitmapFont font = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText hud = new BitmapText(font);
        hud.setSize(font.getCharSet().getRenderedSize());
        hud.setLocalTranslation(xPos, yPos, 0);
        hud.setColor(color);
        guiNode.attachChild(hud);
        return hud;
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
