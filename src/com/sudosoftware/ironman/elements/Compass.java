package com.sudosoftware.ironman.elements;

import java.text.NumberFormat;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.sudosoftware.ironman.gltext.GLText;
import com.sudosoftware.ironman.gltext.GLTextFactory;
import com.sudosoftware.ironman.shapes.BezierCurve;
import com.sudosoftware.ironman.shapes.Point3D;
import com.sudosoftware.ironman.util.ColorPicker;
import com.sudosoftware.ironman.util.SensorManagerFactory;

public class Compass extends HUDElement {
	// Display values.
	public static final float TICK_DELTA = 22.5f;
	public static final float TICKS_PER_SCREEN = 4.0f;

	// Set the bearing number formatter.
	private NumberFormat bearingFormat;

	// GL Text for display.
	private GLText glBearingText;
	private GLText glDegreesText;

	public Compass(Context context) {
		super(context);
	}

	public Compass(Context context, int x, int y, int w, int h) {
		super(context, x, y, w, h);
	}

	public Compass(Context context, int x, int y, int w, int h, float scale) {
		super(context, x, y, w, h, scale);
	}

	@Override
	public void init() {
		// Set the formatter.
		bearingFormat = NumberFormat.getInstance();
		bearingFormat.setMaximumFractionDigits(1);
		bearingFormat.setMinimumFractionDigits(1);
		bearingFormat.setGroupingUsed(false);

		// Load the font.
		glBearingText = GLTextFactory.getInstance().createGLText();
		glBearingText.load("Roboto-Regular.ttf", 45, 2, 2);
		glDegreesText = GLTextFactory.getInstance().createGLText();
		glDegreesText.load("Roboto-Regular.ttf", 25, 2, 2);
	}

	@Override
	public void update() {}

	@Override
	public void render(GL10 gl) {
		// Draw the compass horizontally.

		// Get our current bearing.
		float bearing = SensorManagerFactory.getInstance().getCompassBearing();

		// Calculate the positions and tick marks that need to be shown.
		// -------------------------------------------------------------
		// Closest tick to the left of our current bearing.
		float leftTickDelta = bearing % TICK_DELTA;
		float leftTick = bearing - leftTickDelta;
		if (leftTick < 0.0f) leftTick = (leftTick + 360.0f) % 360.0f;

		// Calculate the screen tick delta.
		float tickScreenDelta = w / TICKS_PER_SCREEN;

		// Screen coordinate of tick.
		float leftTickScreenDelta = (tickScreenDelta * leftTickDelta) / TICK_DELTA;
		float leftTickScreen = (w / 2.0f) - leftTickScreenDelta;

		// Check to see if we can show another tick to the left of this one.
		float tickScreenPosition = leftTickScreen - (tickScreenDelta * ((TICKS_PER_SCREEN / 2) + 1));
		float tickPosition = leftTick - (TICK_DELTA * ((TICKS_PER_SCREEN / 2) + 1));
		if (tickPosition < 0.0f) tickPosition = (tickPosition + 360.0f) % 360.0f;

		// Draw a horizontal line across the top.
		gl.glLineWidth(5.0f);
		ColorPicker.setGLColor(gl, ColorPicker.NEONBLUE, 0.75f);
		BezierCurve.draw2PointCurve(gl,
			new Point3D(0.0f,  h, 0.0f),
			new Point3D(w, h, 0.0f), GL10.GL_LINE_STRIP);
		gl.glLineWidth(1.0f);

		// Display the visible tick marks.
		for (float xLine = tickScreenPosition; xLine <= w; xLine+=tickScreenDelta, tickPosition+=TICK_DELTA) {
			// Check to see if we crossed over 360.0.
			if (tickPosition == 360.0f) tickPosition = 0.0f;

			// Draw a tick mark for this position.
			gl.glLineWidth(5.0f);
			ColorPicker.setGLColor(gl, ColorPicker.NEONBLUE, 0.75f);
			BezierCurve.draw2PointCurve(gl,
				new Point3D(xLine, (tickPosition % 90.0f == 0 ? 20.0f : (tickPosition % 45.0f == 0 ? 30.0f : 45.0f)), 0.0f),
				new Point3D(xLine, h, 0.0f), GL10.GL_LINE_STRIP);
			gl.glLineWidth(1.0f);

			// Draw the bearing notations.
			gl.glEnable(GL10.GL_TEXTURE_2D);
			gl.glEnable(GL10.GL_BLEND);
			gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
			glDegreesText.setScale(1.0f);
			ColorPicker.setGLTextColor(glDegreesText, ColorPicker.CORAL, 1.0f);
			String bearingDisplay = String.valueOf(tickPosition);
			try {
				bearingDisplay = bearingFormat.format(tickPosition);
			}
			catch (Exception e) {}
			glDegreesText.drawC(bearingDisplay, xLine, 0.0f);
			glDegreesText.end();

			// Draw N, S, W or E.
			if (tickPosition % 90.0f == 0) {
				String bearingNotation = "";
				switch ((int)tickPosition) {
				case 0:
					bearingNotation = "N";
					break;

				case 90:
					bearingNotation = "E";
					break;

				case 180:
					bearingNotation = "S";
					break;

				case 270:
					bearingNotation = "W";
					break;
				}
				glBearingText.setScale(1.0f);
				ColorPicker.setGLTextColor(glBearingText, ColorPicker.CORAL, 1.0f);
				glBearingText.draw(bearingNotation, xLine + 10.0f, 15.0f);
				glBearingText.end();
			}
			gl.glDisable(GL10.GL_BLEND);
			gl.glDisable(GL10.GL_TEXTURE_2D);
		}
	}
}
