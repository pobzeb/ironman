package com.sudosoftware.ironman.elements;

import java.text.NumberFormat;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.sudosoftware.ironman.gltext.GLText;
import com.sudosoftware.ironman.gltext.GLTextFactory;
import com.sudosoftware.ironman.shapes.Quad;
import com.sudosoftware.ironman.util.ColorPicker;
import com.sudosoftware.ironman.util.SensorManagerFactory;

public class Altimeter extends HUDElement {
    // Monitor current altitude.
	private float altitude;

	// Set the altitude number formatter.
	private NumberFormat altitudeFormat;

	// GL Text for display.
	private GLText glAltitudeText;

	public Altimeter(Context context) {
		super(context);
	}

	public Altimeter(Context context, int x, int y, int w, int h) {
		super(context, x, y, w, h);
	}

	public Altimeter(Context context, int x, int y, int w, int h, float scale) {
		super(context, x, y, w, h, scale);
	}

	@Override
	public void init() {
		// Set the formatter.
		altitudeFormat = NumberFormat.getInstance();
		altitudeFormat.setMaximumFractionDigits(2);
		altitudeFormat.setMinimumFractionDigits(2);
		altitudeFormat.setGroupingUsed(false);

		// Load the font.
		glAltitudeText = GLTextFactory.getInstance().createGLText();
		glAltitudeText.load("Roboto-Regular.ttf", 45, 2, 2);
	}

	@Override
	public void update() {
		// Get the altitude and convert it from meters to feet.
		altitude = (float)SensorManagerFactory.getInstance().getAltitude() * 3.28084f;
	}

	@Override
	public void render(GL10 gl) {
		// Draw an altitude scale.
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
		glAltitudeText.setScale(1.0f);
		ColorPicker.setGLTextColor(glAltitudeText, ColorPicker.CORAL, 1.0f);
		String altitudeDisplay = "--.-- ft.";
		try {
			altitudeDisplay = altitudeFormat.format(altitude) + " ft.";
		}
		catch (Exception e) {}
		glAltitudeText.draw(altitudeDisplay, w - GLTextFactory.getStringWidth(glAltitudeText, altitudeDisplay) - 15.0f, (h - glAltitudeText.getCharHeight()) / 2.0f);
		glAltitudeText.end();
		gl.glDisable(GL10.GL_BLEND);
		gl.glDisable(GL10.GL_TEXTURE_2D);
	}
}
