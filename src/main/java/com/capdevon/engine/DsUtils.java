package com.capdevon.engine;

import com.jme3.system.NullContext;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

/**
 * Utility methods concerned with display settings.
 *
 * @author Stephen Gold sgold@sonic.net
 */
final class DsUtils {

    /**
     * message logger for this class
     */
    final public static Logger logger
            = Logger.getLogger(DsUtils.class.getName());

    final private static boolean hasLwjglVersion2;
    final private static boolean hasLwjglVersion3;
    final private static Field lwjglListenerField;
    final private static Field nullListenerField;

    final private static Method getBitsPerPixel;
    final private static Method getBlueBits;
    final private static Method getFrequency;
    final private static Method getGreenBits;
    final private static Method getMode;
    final private static Method getModeHeight;
    final private static Method getModeWidth;
    final private static Method getPrimaryMonitor;
    final private static Method getRedBits;

    static {
        boolean foundVersion2;
        try {
            Class.forName("org.lwjgl.opengl.Display");
            foundVersion2 = true;
        } catch (ClassNotFoundException exception) {
            foundVersion2 = false;
        }
        hasLwjglVersion2 = foundVersion2;

        boolean foundVersion3;
        try {
            Class.forName("com.jme3.system.lwjgl.LwjglWindow");
            foundVersion3 = true;
        } catch (ClassNotFoundException exception) {
            foundVersion3 = false;
        }
        hasLwjglVersion3 = foundVersion3;

        try {
            Class<?> contextClass
                    = Class.forName("com.jme3.system.lwjgl.LwjglContext");
            lwjglListenerField = contextClass.getDeclaredField("listener");
            lwjglListenerField.setAccessible(true);

            nullListenerField = NullContext.class.getDeclaredField("listener");
            nullListenerField.setAccessible(true);

            if (foundVersion2) {
                getBlueBits = null;
                getGreenBits = null;
                getPrimaryMonitor = null;
                getRedBits = null;

                Class<?> displayClass
                        = Class.forName("org.lwjgl.opengl.Display");
                getMode = displayClass.getDeclaredMethod(
                        "getDesktopDisplayMode");

                Class<?> displayModeClass = Class.forName(
                        "org.lwjgl.opengl.DisplayMode");
                getBitsPerPixel
                        = displayModeClass.getDeclaredMethod("getBitsPerPixel");
                getFrequency
                        = displayModeClass.getDeclaredMethod("getFrequency");
                getModeHeight = displayModeClass.getDeclaredMethod("getHeight");
                getModeWidth = displayModeClass.getDeclaredMethod("getWidth");

            } else if (foundVersion3) {
                getBitsPerPixel = null;

                Class<?> glfwClass = Class.forName("org.lwjgl.glfw.GLFW");
                getMode = glfwClass.getDeclaredMethod(
                        "glfwGetVideoMode", long.class);
                getPrimaryMonitor = glfwClass.getDeclaredMethod(
                        "glfwGetPrimaryMonitor");

                Class<?> vidModeClass
                        = Class.forName("org.lwjgl.glfw.GLFWVidMode");
                getBlueBits = vidModeClass.getDeclaredMethod("blueBits");
                getFrequency = vidModeClass.getDeclaredMethod("refreshRate");
                getGreenBits = vidModeClass.getDeclaredMethod("greenBits");
                getModeHeight = vidModeClass.getDeclaredMethod("height");
                getModeWidth = vidModeClass.getDeclaredMethod("width");
                getRedBits = vidModeClass.getDeclaredMethod("redBits");

                Class<?>[] vmInnerClasses = vidModeClass.getDeclaredClasses();
                assert vmInnerClasses.length == 1 : vmInnerClasses.length;

            } else { // LWJGL not found
                getBitsPerPixel = null;
                getBlueBits = null;
                getFrequency = null;
                getGreenBits = null;
                getMode = null;
                getModeHeight = null;
                getModeWidth = null;
                getPrimaryMonitor = null;
                getRedBits = null;
            }
        } catch (ClassNotFoundException | NoSuchFieldException
                | NoSuchMethodException | SecurityException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * A private constructor to inhibit instantiation of this class.
     */
    private DsUtils() {
    }

    /**
     * Return the default monitor's current display mode.
     *
     * @return a new instance (not null)
     */
    static DisplayMode displayMode() {
        DisplayMode result;

        if (hasLwjglVersion2) {
            result = displayMode2();

        } else if (hasLwjglVersion3) {
            result = displayMode3();

        } else { // use AWT
            GraphicsEnvironment environment
                    = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice device = environment.getDefaultScreenDevice();
            result = device.getDisplayMode();
        }

        return result;
    }

    /**
     * Return the default monitor's current display mode.
     *
     * @return a new instance (not null)
     */
    private static DisplayMode displayMode2() {
        assert hasLwjglVersion2;

        try {
            // DisplayMode glMode = Display.getDesktopDisplayMode();
            Object glMode = getMode.invoke(null);

            DisplayMode result = makeDisplayMode2(glMode);
            return result;

        } catch (IllegalAccessException | InvocationTargetException exception) {
            throw new IllegalStateException(exception);
        }
    }

    /**
     * Return the default monitor's current display mode.
     *
     * @return a new instance (not null)
     */
    private static DisplayMode displayMode3() {
        assert hasLwjglVersion3;

        try {
            // long monitorId = GLFW.glfwGetPrimaryMonitor();
            Object monitorId = getPrimaryMonitor.invoke(null);

            // GLFWVidMode vidMode = GLFW.glfwGetVideoMode(monitorId);
            Object vidMode = getMode.invoke(null, monitorId);

            DisplayMode result = makeDisplayMode3(vidMode);
            return result;

        } catch (IllegalAccessException | InvocationTargetException exception) {
            throw new IllegalStateException(exception);
        }
    }

    /**
     * Convert an org.lwjgl.opengl.DisplayMode to a java.awt.DisplayMode via
     * reflection of LWJGL v2.
     *
     * @param glMode the AWT display mode (not null, unaffected)
     * @return a new instance
     *
     * @throws IllegalAccessException ?
     * @throws InvocationTargetException ?
     */
    private static DisplayMode makeDisplayMode2(Object glMode)
            throws IllegalAccessException, InvocationTargetException {
        assert hasLwjglVersion2;

        // int width = glMode.getWidth();
        int width = (Integer) getModeWidth.invoke(glMode);

        // int height = glMode.getHeight();
        int height = (Integer) getModeHeight.invoke(glMode);

        // int bitDepth = glMode.getBitsPerPixel();
        int bitDepth = (Integer) getBitsPerPixel.invoke(glMode);

        // int rate = glMode.getFrequency();
        int rate = (Integer) getFrequency.invoke(glMode);

        DisplayMode result = new DisplayMode(width, height, bitDepth, rate);

        return result;
    }

    /**
     * Convert a GLFWVidMode to a DisplayMode via reflection of LWJGL v3.
     *
     * @param glfwVidMode (not null, unaffected)
     * @return a new instance
     *
     * @throws IllegalAccessException ?
     * @throws InvocationTargetException ?
     */
    private static DisplayMode makeDisplayMode3(Object glfwVidMode)
            throws IllegalAccessException, InvocationTargetException {
        assert hasLwjglVersion3;

        // int width = glfwVidMode.width();
        int width = (Integer) getModeWidth.invoke(glfwVidMode);

        // int height = glfwVidMode.height();
        int height = (Integer) getModeHeight.invoke(glfwVidMode);

        // int redBits = glfwVidMode.redBits();
        int redBits = (Integer) getRedBits.invoke(glfwVidMode);

        // int greenBits = glfwVidMode.greenBits();
        int greenBits = (Integer) getGreenBits.invoke(glfwVidMode);

        // int blueBits = glfwVidMode.blueBits();
        int blueBits = (Integer) getBlueBits.invoke(glfwVidMode);

        // int rate = glfwVidMode.refreshRate();
        int rate = (Integer) getFrequency.invoke(glfwVidMode);

        int bitDepth = redBits + greenBits + blueBits;
        DisplayMode result = new DisplayMode(width, height, bitDepth, rate);

        return result;
    }
}
