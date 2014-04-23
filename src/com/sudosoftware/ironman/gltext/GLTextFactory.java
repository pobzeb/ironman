package com.sudosoftware.ironman.gltext;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

public class GLTextFactory {
	private GL10 gl;
	private Context context;

	private static GLTextFactory instance = null;
	private static Object lock = new Object();

	private GLTextFactory(GL10 gl, Context context) {
		this.gl = gl;
		this.context = context;
	}

	public static GLTextFactory getInstance(GL10 gl, Context context) {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new GLTextFactory(gl, context);
				}
			}
		}

		return instance;
	}

	public static GLTextFactory getInstance() {
		return instance;
	}

	public static float getStringWidth(GLText glText, String string) {
		float stringWidth = 0;
		for (int i = 0; string != null && i < string.length(); i++) {
			stringWidth += glText.getCharWidth(string.charAt(i));
		}
		return stringWidth;
	}

	public GLText createGLText() {
		return new GLText(gl, context.getAssets());
	}
}