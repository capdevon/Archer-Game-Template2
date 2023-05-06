package mygame;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme3.app.SimpleApplication;
import com.jme3.system.AppSettings;

import jme3utilities.Validate;

public abstract class GameApplication extends SimpleApplication {

    private static final Logger logger = Logger.getLogger(GameApplication.class.getName());
    private File appSettings = new File("graphics.properties");

    /**
     * animation/physics speed when paused
     */
    public static final float pausedSpeed = 1e-12f;

    /**
     * Toggle the animation and physics simulation: paused/running.
     */
    public void togglePause() {
        float newSpeed = isPaused() ? 1f : pausedSpeed;
        setSpeed(newSpeed);
    }

    /**
     * Test whether animation and physics simulation are paused.
     *
     * @return true if paused, otherwise false
     */
    public boolean isPaused() {
        if (speed <= pausedSpeed) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Alter the effective speeds of physics and all animations.
     *
     * @param newSpeed animation speed (&gt;0, standard speed &rarr; 1)
     */
    @Override
    public void setSpeed(float newSpeed) {
        Validate.positive(newSpeed, "speed");
        speed = newSpeed;
    }

    public void setPaused(boolean pause) {
        this.paused = pause;
    }

    public boolean getPaused() {
        return paused;
    }

    /**
     * Access the live display settings.
     *
     * @return the pre-existing instance (not null)
     */
    public AppSettings getSettings() {
        return settings;
    }

    public void saveSettings() {
        try {
            settings.save(new FileOutputStream(appSettings));
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Failed to save settings", ex);
        }
    }

    public void loadSettings() {
        try {
            if (appSettings.exists()) {
                settings.load(new FileInputStream(appSettings));
            } else {
                settings.save(new FileOutputStream(appSettings));
            }
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Failed to save settings", ex);
        }
    }

    @Override
    public <V> Future<V> enqueue(Callable<V> callable) {
        final Throwable tracer = new Throwable("Enqueue Tracer");
        Callable<V> wrapper = () -> {
            try {
                return callable.call();
            } catch (Exception ex) {
                ex.addSuppressed(tracer);
                throw new RuntimeException("Jme Enqueued Task Exception", ex);
            }
        };
        return super.enqueue(wrapper);
    }

    @Override
    public void enqueue(Runnable runnable) {
        final Throwable tracer = new Throwable("Enqueue Tracer");
        Runnable wrapper = () -> {
            try {
                runnable.run();
            } catch (Exception ex) {
                ex.addSuppressed(tracer);
                throw new RuntimeException("Jme Enqueued Task Exception", ex);
            }
        };
        super.enqueue(wrapper);
    }
}
