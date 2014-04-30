package com.sudosoftware.ironman.elements;

import java.text.NumberFormat;

import javax.microedition.khronos.opengles.GL10;

import com.sudosoftware.ironman.gltext.GLText;
import com.sudosoftware.ironman.gltext.GLTextFactory;
import com.sudosoftware.ironman.shapes.BezierCurve;
import com.sudosoftware.ironman.shapes.Point3D;
import com.sudosoftware.ironman.util.ColorPicker;
import com.sudosoftware.ironman.util.GPSTracker;
import com.sudosoftware.ironman.util.SensorManagerFactory;

public class Speedometer extends HUDElement {
    // Monitor current speed.
	private float speed;

	// Hold the location info.
	private GPSTracker locationTracker;

	// Set the speed number formatter.
	private NumberFormat speedFormat;

	// GL Text for display.
	private GLText glSpeedText;

	public Speedometer() {
		super();
	}

	public Speedometer(int x, int y) {
		super(x, y);
	}

	public Speedometer(int x, int y, float scale) {
		super(x, y, scale);
	}

	@Override
	public void init() {
		// Get the location tracker.
		locationTracker = SensorManagerFactory.getInstance().getLocationTracker();

		// Set the formatter.
		speedFormat = NumberFormat.getIntegerInstance();
		speedFormat.setGroupingUsed(false);

		// Load the font.
		glSpeedText = GLTextFactory.getInstance().createGLText();
		glSpeedText.load("Roboto-Regular.ttf", 65, 2, 2);
	}

	@Override
	public void update() {
		if (locationTracker.canGetLocation()) {
			// Get the speed and convert it from m/s to mph.
			speed = ((float)locationTracker.getSpeed() * 2.23694f) / 1.0f;
		}
	}

	@Override
	public void render(GL10 gl) {
		gl.glPushMatrix();

		// Move to the element's location.
		gl.glTranslatef(this.x, this.y, 0.0f);

		// Scale the element.
		gl.glScalef(scale, scale, 1.0f);

		// Draw a speedometer display. This would fit nicely along the left side
		// of the horizon HUD element.
		gl.glLineWidth(10.0f);
		ColorPicker.setGLColor(gl, ColorPicker.NEONBLUE, 0.75f);
		BezierCurve.draw2PointCurve(gl,
			new Point3D(-340.0f, -50.0f, 0.0f),
			new Point3D(-340.0f,  50.0f, 0.0f), GL10.GL_LINE_STRIP);
		BezierCurve.draw2PointCurve(gl,
			new Point3D(-740.0f, -50.0f, 0.0f),
			new Point3D(-740.0f,  50.0f, 0.0f), GL10.GL_LINE_STRIP);
		BezierCurve.draw2PointCurve(gl,
			new Point3D(-340.0f,  50.0f, 0.0f),
			new Point3D(-740.0f,  50.0f, 0.0f), GL10.GL_LINE_STRIP);
		BezierCurve.draw2PointCurve(gl,
			new Point3D(-340.0f, -50.0f, 0.0f),
			new Point3D(-740.0f, -50.0f, 0.0f), GL10.GL_LINE_STRIP);
		gl.glLineWidth(1.0f);

		// Draw the current altitude.
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		glSpeedText.setScale(scale);
		ColorPicker.setGLTextColor(glSpeedText, ColorPicker.CORAL, 1.0f);
		String speedDisplay = "--.--";
		try {
			speedDisplay = speedFormat.format(speed);
		}
		catch (Exception e) {}
		glSpeedText.draw(speedDisplay + " mph", -340.0f - GLTextFactory.getStringWidth(glSpeedText, speedDisplay + " mph") - 15.0f, -(glSpeedText.getCharHeight() / 2.0f) + 5.0f);
		glSpeedText.end();
		gl.glDisable(GL10.GL_BLEND);
		gl.glDisable(GL10.GL_TEXTURE_2D);

		gl.glPopMatrix();
	}

	@Override
	public void onPause() {
		locationTracker.onPause();
	}

	@Override
	public void onResume() {}

	@Override
	public void onDestroy() {
		locationTracker.onPause();
	}
}
