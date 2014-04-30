package com.sudosoftware.ironman.elements;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.location.GpsSatellite;

import com.sudosoftware.ironman.gltext.GLText;
import com.sudosoftware.ironman.gltext.GLTextFactory;
import com.sudosoftware.ironman.shapes.BezierCurve;
import com.sudosoftware.ironman.shapes.Circle;
import com.sudosoftware.ironman.shapes.Point3D;
import com.sudosoftware.ironman.util.ColorPicker;
import com.sudosoftware.ironman.util.GPSTracker;
import com.sudosoftware.ironman.util.SensorManagerFactory;

public class SatellitesLocked extends HUDElement {
	// Hold the location tracker.
	private GPSTracker locationTracker;

	// Hold the list of locked satellites.
	private List<GpsSatellite> satellites = new ArrayList<GpsSatellite>();

	// GL Text for display.
	private GLText glSatText;

	public SatellitesLocked() {
		super();
	}

	public SatellitesLocked(int x, int y) {
		super(x, y);
	}

	public SatellitesLocked(int x, int y, float scale) {
		super(x, y, scale);
	}

	@Override
	public void init() {
		locationTracker = SensorManagerFactory.getInstance().getLocationTracker();

		// Load the font.
		glSatText = GLTextFactory.getInstance().createGLText();
		glSatText.load("Roboto-Regular.ttf", 65, 2, 2);
	}

	@Override
	public void update() {
		if (locationTracker.canGetLocation()) {
			// Get the list of locked satellites.
			satellites = new ArrayList<GpsSatellite>(locationTracker.getSatellites());
		}
	}

	@Override
	public void render(GL10 gl) {
		gl.glPushMatrix();

		// Move to the element's location.
		gl.glTranslatef(this.x, this.y, 0.0f);

		// Scale the element.
		gl.glScalef(scale, scale, 1.0f);

		// Draw a disc to hold our satellite list.
		ColorPicker.setGLColor(gl, ColorPicker.NEONBLUE, 0.75f);
		Circle.drawCircle(gl, 300.0f, 500, GL10.GL_TRIANGLE_FAN);
		ColorPicker.setGLColor(gl, ColorPicker.BLACK, 0.25f);
		Circle.drawCircle(gl, 290.0f, 500, GL10.GL_TRIANGLE_FAN);

		// Draw a cross in the center to indicate our position.
		gl.glLineWidth(4.0f);
		ColorPicker.setGLColor(gl, ColorPicker.NEONBLUE, 0.75f);
		BezierCurve.draw2PointCurve(gl,
			new Point3D(-15.0f,  0.0f, 0.0f),
			new Point3D( 15.0f,  0.0f, 0.0f), GL10.GL_LINE_STRIP);
		BezierCurve.draw2PointCurve(gl,
			new Point3D( 0.0f, -15.0f, 0.0f),
			new Point3D( 0.0f,  15.0f, 0.0f), GL10.GL_LINE_STRIP);
		gl.glLineWidth(1.0f);

		// Draw a North indicator at the top of the outer disc.
		gl.glLineWidth(4.0f);
		ColorPicker.setGLColor(gl, ColorPicker.NEONBLUE, 0.75f);
		BezierCurve.draw2PointCurve(gl,
			new Point3D(  0.0f, 290.0f, 0.0f),
			new Point3D(-15.0f, 270.0f, 0.0f), GL10.GL_LINE_STRIP);
		BezierCurve.draw2PointCurve(gl,
			new Point3D(  0.0f, 290.0f, 0.0f),
			new Point3D( 15.0f, 270.0f, 0.0f), GL10.GL_LINE_STRIP);
		BezierCurve.draw2PointCurve(gl,
			new Point3D(-15.0f, 270.0f, 0.0f),
			new Point3D( 15.0f, 270.0f, 0.0f), GL10.GL_LINE_STRIP);
		gl.glLineWidth(1.0f);

		// Draw the position of the satellite.
		gl.glPushMatrix();
		gl.glTranslatef(0.0f, 280.0f, 0.0f);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		glSatText.setScale(0.75f);
		ColorPicker.setGLTextColor(glSatText, ColorPicker.NEONBLUE, 1.0f);
		glSatText.draw("N", -(GLTextFactory.getStringWidth(glSatText, "N") / 2.0f), -(glSatText.getCharHeight() + 5.0f));
		glSatText.end();
		gl.glDisable(GL10.GL_BLEND);
		gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glPopMatrix();

		// Loop through the satellite list and draw a circle for each one.
		if (satellites != null && satellites.size() > 0) {
			for (GpsSatellite sat : satellites) {
				gl.glPushMatrix();

				// Get the angel and radius for this satellite.
				float angle = (float)Math.toRadians(sat.getAzimuth());
				float r = 290.0f - ((sat.getElevation() * 290.0f) / 90.0f);

				// Rotate and flip our reference so that 0 degrees is up.
				gl.glRotatef(-90.0f, 0.0f, 0.0f, 1.0f);
				gl.glScalef(scale, -scale, 1.0f);

				// Move to the point where this satellite is represented.
				gl.glTranslatef(r * (float)Math.cos(angle), r * (float)Math.sin(angle), 0.0f);

				// Draw the satellite.
				gl.glLineWidth(5.0f);
				ColorPicker.setGLColor(gl, ColorPicker.NEONBLUE, 0.25f);
				Circle.drawCircle(gl, 10.0f, 200, GL10.GL_LINE_LOOP);
				gl.glLineWidth(1.0f);

				gl.glPopMatrix();
				gl.glPushMatrix();

				// Adjust the angle again.
				angle = angle + (float)Math.toRadians(-90.0);

				// Move to the point where this satellite is represented.
				gl.glTranslatef((r * (float)Math.cos(angle)) * -1.0f, r * (float)Math.sin(angle), 0.0f);

				// Draw the position of the satellite.
				gl.glEnable(GL10.GL_TEXTURE_2D);
				gl.glEnable(GL10.GL_BLEND);
				gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
				glSatText.setScale(0.25f);
				ColorPicker.setGLTextColor(glSatText, ColorPicker.CORAL, 1.0f);
				String satPosDisplay = "(--, --)";
				try {
					satPosDisplay = "(" + sat.getAzimuth() + ", " + sat.getElevation() + ")";
				}
				catch (Exception e) {}
				glSatText.draw(satPosDisplay, -(GLTextFactory.getStringWidth(glSatText, satPosDisplay) / 2.0f), -(glSatText.getCharHeight() + 15.0f));
				glSatText.end();
				gl.glDisable(GL10.GL_BLEND);
				gl.glDisable(GL10.GL_TEXTURE_2D);

				gl.glPopMatrix();
			}
		}

		gl.glPopMatrix();
	}
}
