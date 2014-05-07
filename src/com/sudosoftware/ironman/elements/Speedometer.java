package com.sudosoftware.ironman.elements;

import java.text.NumberFormat;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.sudosoftware.ironman.gltext.GLText;
import com.sudosoftware.ironman.gltext.GLTextFactory;
import com.sudosoftware.ironman.shapes.Circle;
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

	public Speedometer(Context context, int x, int y) {
		super(context, x, y);
	}

	public Speedometer(Context context, int x, int y, float scale) {
		super(context, x, y, scale);
	}

	@Override
	public void init() {
		// Set the formatter.
		speedFormat = NumberFormat.getIntegerInstance();
		speedFormat.setGroupingUsed(false);

		// Load the font.
		glSpeedText = GLTextFactory.getInstance().createGLText();
		glSpeedText.load("Roboto-Regular.ttf", 65, 2, 2);
	}

	@Override
	public void update() {
		// Get the speed and convert it from m/s to mph.
		speed = ((float)SensorManagerFactory.getInstance().getSpeed() * 2.23694f) / 1.0f;
	}

	@Override
	public void render(GL10 gl) {
		// Draw a speedometer display. This would fit nicely along the left side
		// of the horizon HUD element.
		gl.glLineWidth(10.0f);
		ColorPicker.setGLColor(gl, ColorPicker.BLACK, 0.25f);
		Quad.drawQuad(gl, -740.0f, -50.0f, 740.0f - 340.0f, 100.0f, GL10.GL_TRIANGLE_FAN);
		ColorPicker.setGLColor(gl, ColorPicker.NEONBLUE, 0.75f);
		Quad.drawRect(gl, -740.0f, -50.0f, 740.0f - 340.0f, 100.0f, GL10.GL_LINE_STRIP);
		ColorPicker.setGLColor(gl, ColorPicker.BLACK, 0.0f);
		Circle.drawCircle(gl, 340.0f, 500, GL10.GL_TRIANGLE_FAN);
		ColorPicker.setGLColor(gl, ColorPicker.NEONBLUE, 0.75f);
		gl.glPushMatrix();
		gl.glRotatef(-189.0f, 0.0f, 0.0f, 1.0f);
		Circle.drawArc(gl, 343.0f, 0.0f, 18.0f, 500, GL10.GL_LINE_STRIP, true);
		gl.glPopMatrix();
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
		glSpeedText.draw(speedDisplay, -340.0f - GLTextFactory.getStringWidth(glSpeedText, speedDisplay) - 15.0f, -(glSpeedText.getCharHeight() / 2.0f) + 5.0f);
		glSpeedText.end();
		gl.glDisable(GL10.GL_BLEND);
		gl.glDisable(GL10.GL_TEXTURE_2D);
	}
}
