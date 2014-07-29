package com.sudosoftware.ironman.elements;

import java.text.NumberFormat;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.sudosoftware.ironman.gltext.GLText;
import com.sudosoftware.ironman.gltext.GLTextFactory;
import com.sudosoftware.ironman.shapes.Quad;
import com.sudosoftware.ironman.util.ColorPicker;
import com.sudosoftware.ironman.util.SensorManagerFactory;

public class Speedometer extends HUDElement {
    // Monitor current speed.
	private float speed;

	// Set the speed number formatter.
	private NumberFormat speedFormat;

	// GL Text for display.
	private GLText glSpeedText;

	public Speedometer(Context context) {
		super(context);
	}

	public Speedometer(Context context, int x, int y, int w, int h) {
		super(context, x, y, w, h);
	}

	public Speedometer(Context context, int x, int y, int w, int h, float scale) {
		super(context, x, y, w, h, scale);
	}

	@Override
	public void init() {
		// Set the formatter.
		speedFormat = NumberFormat.getIntegerInstance();
		speedFormat.setGroupingUsed(false);

		// Load the font.
		glSpeedText = GLTextFactory.getInstance().createGLText();
		glSpeedText.load("Roboto-Regular.ttf", 45, 2, 2);
	}

	@Override
	public void update() {
		// Get the speed and convert it from m/s to mph.
		speed = ((float)SensorManagerFactory.getInstance().getSpeed() * 2.23694f) / 1.0f;
	}

	@Override
	public void render(GL10 gl) {
		// Draw a speedometer display.
		gl.glLineWidth(2.0f);
		ColorPicker.setGLColor(gl, ColorPicker.BLACK, 0.25f);
		Quad.drawQuad(gl, 0.0f, 0.0f, w, h, GL10.GL_TRIANGLE_FAN);
		ColorPicker.setGLColor(gl, ColorPicker.NEONBLUE, 0.75f);
		Quad.drawRect(gl, 0.0f, 0.0f, w, h, GL10.GL_LINE_STRIP);
		gl.glLineWidth(1.0f);

		// Draw the current altitude.
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		glSpeedText.setScale(1.0f);
		ColorPicker.setGLTextColor(glSpeedText, ColorPicker.CORAL, 1.0f);
		String speedDisplay = "--.-- mph";
		try {
			speedDisplay = speedFormat.format(speed) + " mph";
		}
		catch (Exception e) {}
		glSpeedText.draw(speedDisplay, w - GLTextFactory.getStringWidth(glSpeedText, speedDisplay) - 15.0f, (h - glSpeedText.getCharHeight()) / 2.0f);
		glSpeedText.end();
		gl.glDisable(GL10.GL_BLEND);
		gl.glDisable(GL10.GL_TEXTURE_2D);
	}
}
