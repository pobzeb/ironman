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
	public static final float COMPASS_DIST_BETWEEN_TICKS = 22.5f;
	public static final float SCREEN_DIST_BETWEEN_TICKS = 160.0f;
	public static final float HUD_ELEMENT_LEFT_EDGE = -390.0f;
	public static final float HUD_ELEMENT_RIGHT_EDGE = 380.0f;

	// Hold our compass tick points.
	private float[] degrees;

	// Set the bearing number formatter.
	private NumberFormat bearingFormat;

	// GL Text for display.
	private GLText glBearingText;
	private GLText glDegreesText;

	public Compass(Context context) {
		super(context);
	}

	public Compass(Context context, int x, int y) {
		super(context, x, y);
	}

	public Compass(Context context, int x, int y, float scale) {
		super(context, x, y, scale);
	}

	@Override
	public void init() {
		// Initialize the points in our degrees array.
		int idx = 0;
		degrees = new float[16];
		for (float deg = 0.0f; idx < degrees.length; deg+=COMPASS_DIST_BETWEEN_TICKS, idx++) {
			degrees[idx] = deg;
		}

		// Set the formatter.
		bearingFormat = NumberFormat.getInstance();
		bearingFormat.setMaximumFractionDigits(1);
		bearingFormat.setMinimumFractionDigits(1);
		bearingFormat.setGroupingUsed(false);

		// Load the font.
		glBearingText = GLTextFactory.getInstance().createGLText();
		glBearingText.load("Roboto-Regular.ttf", 95, 2, 2);
		glDegreesText = GLTextFactory.getInstance().createGLText();
		glDegreesText.load("Roboto-Regular.ttf", 35, 2, 2);
	}

	@Override
	public void update() {}

	@Override
	public void render(GL10 gl) {
		// Draw the compass horizontally. This would fit nicely along the top
		// of the horizon HUD element.

		// Get our current bearing.
		float bearing = SensorManagerFactory.getInstance().getCompassBearing();

		// Calculate the positions and tick marks that need to be shown.
		// -------------------------------------------------------------
		// Closest tick to the left of our current bearing.
		float tickDist = bearing % COMPASS_DIST_BETWEEN_TICKS;
		float tick = bearing - tickDist;
		if (tick < 0.0f) tick = (tick + 360.0f) % 360.0f;

		// Screen coordinate of tick (will be left of the HUD element center so * -1.0).
		float tickPosition = -1.0f * ((tickDist * SCREEN_DIST_BETWEEN_TICKS) / COMPASS_DIST_BETWEEN_TICKS);

		// Check to see if we can show another tick to the left of this one.
		while (tickPosition - SCREEN_DIST_BETWEEN_TICKS >= HUD_ELEMENT_LEFT_EDGE) {
			tick -= COMPASS_DIST_BETWEEN_TICKS;
			if (tick < 0.0f) tick = (tick + 360.0f) % 360.0f;
			tickPosition -= SCREEN_DIST_BETWEEN_TICKS;
		}

		// Draw a horizontal line across the bottom.
		gl.glLineWidth(10.0f);
		ColorPicker.setGLColor(gl, ColorPicker.NEONBLUE, 0.75f);
		BezierCurve.draw2PointCurve(gl,
			new Point3D(-400.0f, 350.0f, 0.0f),
			new Point3D(400.0f, 350.0f, 0.0f), GL10.GL_LINE_STRIP);
		gl.glLineWidth(1.0f);

		// Display the visible tick marks.
		for (float xLine = tickPosition; xLine <= HUD_ELEMENT_RIGHT_EDGE; xLine+=SCREEN_DIST_BETWEEN_TICKS, tick+=COMPASS_DIST_BETWEEN_TICKS) {
			// Check to see if we crossed over 360.0.
			if (tick == 360.0f) tick = 0.0f;

			// Draw a tick mark for this position.
			gl.glLineWidth(10.0f);
			ColorPicker.setGLColor(gl, ColorPicker.NEONBLUE, 0.75f);
			BezierCurve.draw2PointCurve(gl,
				new Point3D(xLine, (tick % 90.0f == 0 ? 445.0f : (tick % 45.0f == 0 ? 415.0f : 390.0f)), 0.0f),
				new Point3D(xLine, 350.0f, 0.0f), GL10.GL_LINE_STRIP);
			gl.glLineWidth(1.0f);

			// Draw the bearing notations.
			gl.glEnable(GL10.GL_TEXTURE_2D);
			gl.glEnable(GL10.GL_BLEND);
			gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
			glDegreesText.setScale(1.0f);
			ColorPicker.setGLTextColor(glDegreesText, ColorPicker.CORAL, 1.0f);
			String bearingDisplay = String.valueOf(tick);
			try {
				bearingDisplay = bearingFormat.format(tick);
			}
			catch (Exception e) {}
			glDegreesText.drawC(bearingDisplay, xLine, 475.0f);
			glDegreesText.end();

			// Draw N, S, W or E.
			if (tick % 90.0f == 0) {
				String bearingNotation = "";
				switch ((int)tick) {
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
				glBearingText.draw(bearingNotation, xLine + 10.0f, 340.0f);
				glBearingText.end();
			}
			gl.glDisable(GL10.GL_BLEND);
			gl.glDisable(GL10.GL_TEXTURE_2D);
		}
	}
}
