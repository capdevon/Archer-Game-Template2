package mygame.prefabs;

import com.capdevon.anim.Animator;
import com.capdevon.engine.PrefabComponent;
import com.jme3.app.Application;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.Rectangle;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.BillboardControl;

import mygame.AnimDefs;
import mygame.ai.AIControl;
import mygame.controls.Damageable;

/**
 * 
 * @author capdevon
 */
public class MonsterPrefab extends PrefabComponent {
	
    public boolean usePhysicsCharacter = true;
    public float radius = 0.4f;
    public float height = 1.8f;

    private PhysicsSpace phySpace;

    public MonsterPrefab(Application app) {
        super(app);
        this.phySpace = getPhysicsSpace();
    }

    @Override
    public Spatial instantiate(Vector3f position, Quaternion rotation, Node parent) {

        Node enemy = (Node) assetManager.loadModel(AnimDefs.Monster.ASSET_PATH);
        enemy.setName("Monster-" + nextSeqId());
        enemy.setLocalTranslation(position);
        enemy.setLocalRotation(rotation);
        enemy.setLocalScale(1.1f);
        
        enemy.addControl(new Animator());

        if (usePhysicsCharacter) {
            BetterCharacterControl bcc = new BetterCharacterControl(radius, height, 10f);
            enemy.addControl(bcc);
            phySpace.add(bcc);
            bcc.setPhysicsDamping(1f);

        } else {
            RigidBodyControl rbc = createRigidBody(radius, height);
            enemy.addControl(rbc);
            phySpace.add(rbc);
        }

        BitmapText hud = createBitmapText(ColorRGBA.White, "label-placeholder", .1f);
        hud.setName("Healthbar");
        hud.setLocalTranslation(0, 2f, 0);
        enemy.attachChild(hud);

        AIControl aiControl = new AIControl();
        aiControl.hud = hud;
        aiControl.player = getRootNode().getChild("Player");
        enemy.addControl(aiControl);

        Damageable m_Damageable = new Damageable();
        enemy.addControl(m_Damageable);

        app.enqueue(() -> parent.attachChild(enemy));

        return enemy;
    }

    private BitmapText createBitmapText(ColorRGBA color, String text, float size) {
        BitmapFont font = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText bmp = new BitmapText(font);
        bmp.setColor(color);
        bmp.setText(text);
        bmp.setBox(new Rectangle((-bmp.getLineWidth() / 2) * bmp.getSize(), 0f, bmp.getLineWidth() * bmp.getSize(), bmp.getLineHeight()));
        bmp.setAlignment(BitmapFont.Align.Center);
        bmp.setShadowMode(ShadowMode.Off);
        bmp.setQueueBucket(RenderQueue.Bucket.Transparent);
        bmp.setSize(size);
        bmp.addControl(new BillboardControl());
        return bmp;
    }

    private RigidBodyControl createRigidBody(float radius, float height) {

        CapsuleCollisionShape capsule = new CapsuleCollisionShape(radius, (height - (2 * radius)));
        CompoundCollisionShape collShape = new CompoundCollisionShape();
        Vector3f position = new Vector3f(0, (height / 2f), 0);
        collShape.addChildShape(capsule, position);

        // Setup root motion physics control
        RigidBodyControl rbc = new RigidBodyControl(collShape);
        // Kinematic mode must be enabled so character is not influenced by physics
        rbc.setKinematic(true);
        // Apply spatial transform to the collision shape
        rbc.setKinematicSpatial(true);

        return rbc;
    }

}
