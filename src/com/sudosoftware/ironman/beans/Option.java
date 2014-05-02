package com.sudosoftware.ironman.beans;

import javax.microedition.khronos.opengles.GL10;

import com.sudosoftware.ironman.gltext.GLText;
import com.sudosoftware.ironman.gltext.GLTextFactory;
import com.sudosoftware.ironman.shapes.Circle;
import com.sudosoftware.ironman.util.ColorPicker;

public class Option {
	private static final float DISC_WIDTH = 120.0f;
	private static final float SPACING = 20.0f;

	// Option title and key/value.
	public String title;
	public String key;
	public boolean value;

	// Option position.
	public float x, y;
	public float w, h;

	// GL Text for display.
	private GLText glOptionText;

	public Option(String title, String key, boolean value) {
		this.title = title;
		this.key = key;
		this.value = value;

		// Load the font.
		glOptionText = GLTextFactory.getInstance().createGLText();
		glOptionText.load("Roboto-Regular.ttf", 55, 2, 2);

		// Save the width and height.
		this.w = DISC_WIDTH + SPACING + GLTextFactory.getStringWidth(glOptionText, this.title);
		this.h = DISC_WIDTH;
	}

	public void draw(GL10 gl) {
		gl.glPushMatrix();

		// Draw a disc that can be on or off.
		gl.glTranslatef((DISC_WIDTH/2), -(DISC_WIDTH/2), 0.0f);
		ColorPicker.setGLColor(gl, ColorPicker.SLATEBLUE, 0.125f);
		Circle.drawCircle(gl, (DISC_WIDTH/2), 300, GL10.GL_TRIANGLE_FAN);
		ColorPicker.setGLColor(gl, ColorPicker.BLACK, 0.25f);
		Circle.drawCircle(gl, (DISC_WIDTH/2) - 5.0f, 300, GL10.GL_TRIANGLE_FAN);
		if (this.value) {
			ColorPicker.setGLColor(gl, ColorPicker.SLATEBLUE, 0.125f);
			Circle.drawCircle(gl, (DISC_WIDTH/2) - 10.0f, 300, GL10.GL_TRIANGLE_FAN);
		}

		// Draw the bearing notations.
		gl.glTranslatef((DISC_WIDTH/2) + SPACING, 0.0f, 0.0f);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		glOptionText.setScale(1.0f);
		ColorPicker.setGLTextColor(glOptionText, ColorPicker.SLATEBLUE, 1.0f);
		glOptionText.draw(this.title, 0.0f, -(glOptionText.getCharHeight() / 2));
		glOptionText.end();
		gl.glDisable(GL10.GL_BLEND);
		gl.glDisable(GL10.GL_TEXTURE_2D);

		gl.glPopMatrix();
	}

	@Override
	public String toString() {
		return this.key + ": " + this.value;
	}

	public void toggle() {
		this.value = !this.value;
	}
}
