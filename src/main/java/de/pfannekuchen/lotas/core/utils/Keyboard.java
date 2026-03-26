package de.pfannekuchen.lotas.core.utils;

import org.lwjgl.glfw.GLFW;

import de.pfannekuchen.lotas.core.MCVer;

/**
 * A LWJGL style keyboard method
 * @author ScribbleLP
 */
public class Keyboard {

	public static boolean isKeyDown(int keyCode) {
		return GLFW.glfwGetKey(MCVer.getGLWindow().getWindow(), keyCode) == GLFW.GLFW_PRESS;
	}
}
