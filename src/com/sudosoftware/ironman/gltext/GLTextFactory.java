package com.sudosoftware.ironman.gltext;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.sudosoftware.ironman.shapes.Quad;
import com.sudosoftware.ironman.util.ColorPicker;

public class GLTextFactory {
	private GL10 gl;
	private Context context;

	// Debug text.
	private GLText glDebugText;

	private static GLTextFactory instance = null;
	private static Object lock = new Object();

	private GLTextFactory(GL10 gl, Context context) {
		this.gl = gl;
		this.context = context;

		// Load the debug font.
		glDebugText = createGLText();
		glDebugText.load("Roboto-Regular.ttf", 30, 2, 2);
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

	public float getDebugStringWidth(String string) {
		float stringWidth = 0;
		for (int i = 0; string != null && i < string.length(); i++) {
			stringWidth += glDebugText.getCharWidth(string.charAt(i));
		}
		return stringWidth;
	}

	public GLText createGLText() {
		return new GLText(gl, context.getAssets());
	}

	public void debugTextBlock(GL10 gl, String[] text, float screenW, float screenH, ColorPicker color, float alpha, float scale) {
		gl.glPushMatrix();
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		// Draw a background so we can see the debug info.
		ColorPicker.setGLColor(gl, ColorPicker.BLACK, 0.50f);
		Quad.drawQuad(gl, 0.0f, 0.0f, screenW, screenH, GL10.GL_TRIANGLES);
		gl.glPopMatrix();

		// Draw the debug text.
		gl.glEnable(GL10.GL_TEXTURE_2D);
		glDebugText.setScale(scale);
		ColorPicker.setGLTextColor(glDebugText, color, alpha);
		float y = screenH - (50.0f * scale);
		for (String line : text) {
			glDebugText.draw(line, 20.0f * scale, y);
			y -= (glDebugText.getCharHeight() + (5.0f * scale));
		}
		glDebugText.end();
		gl.glDisable(GL10.GL_BLEND);
		gl.glDisable(GL10.GL_TEXTURE_2D);
	}
}
