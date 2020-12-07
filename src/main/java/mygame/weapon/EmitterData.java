package mygame.weapon;

import com.jme3.audio.AudioNode;
import com.jme3.effect.ParticleEmitter;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class EmitterData {

	public Node emitter;

	public EmitterData(Node emitter) {
		this.emitter = emitter;
	}

	protected void stop() {
		for (Spatial sp : emitter.getChildren()) {
			if (sp instanceof AudioNode) {
				((AudioNode) sp).stop();

			} else if (sp instanceof ParticleEmitter) {
				((ParticleEmitter) sp).killAllParticles();
			}
		}
	}

	protected void play() {
		for (Spatial sp : emitter.getChildren()) {
			if (sp instanceof AudioNode) {
				((AudioNode) sp).play();

			} else if (sp instanceof ParticleEmitter) {
				((ParticleEmitter) sp).emitAllParticles();
			}
		}
	}

}