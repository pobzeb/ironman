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
import com.sudosoftware.ironman.util.SensorManagerFactory;

public class SatellitesLocked extends HUDElement {
	// Hold the list of locked satellites.
	private List<GpsSatellite> satellites = new ArrayList<GpsSatellite>();

	// GL Text for display.
	private GLText glSatText;
	private GLText glDirText;

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
		// Load the font.
		glSatText = GLTextFactory.getInstance().createGLText();
		glSatText.load("Roboto-Regular.ttf", 30, 2, 2);
		glDirText = GLTextFactory.getInstance().createGLText();
		glDirText.load("Roboto-Regular.ttf", 45, 2, 2);
	}

	@Override
	public void update() {
		// Get the list of locked satellites.
		satellites = new ArrayList<GpsSatellite>(SensorManagerFactory.getInstance().getSatellites());
	}

	@Override
	public void render(GL10 gl) {
		// Get the current compass bearing.
		float bearing = SensorManagerFactory.getInstance().getCompassBearing();

		// Rotate everything so that the satellites represented here are
		// in their truest location.
		gl.glRotatef(-bearing, 0.0f, 0.0f, 1.0f);

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

		// Draw a North indicator to show orientation for satellite positions.
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		glDirText.setScale(1.0f);
		ColorPicker.setGLTextColor(glDirText, ColorPicker.NEONBLUE, 1.0f);
		glDirText.draw("N", -(GLTextFactory.getStringWidth(glDirText, "N") / 2.0f), 280.0f - (glDirText.getCharHeight() + 5.0f));
		glDirText.end();
		gl.glDisable(GL10.GL_BLEND);
		gl.glDisable(GL10.GL_TEXTURE_2D);

		// Loop through the satellite list and draw a circle for each one.
		if (satellites != null && satellites.size() > 0) {
			for (GpsSatellite sat : satellites) {
				gl.glPushMatrix();

				// Get the angel and radius for this satellite.
				float angle = (float)Math.toRadians(sat.getAzimuth());
				float r = 290.0f - ((sat.getElevation() * 290.0f) / 90.0f);

				// Rotate and flip our reference so that 0 degrees is up.
				gl.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);
				gl.glScalef(1.0f, -1.0f, 1.0f);

				// Move to the point where this satellite is represented.
				gl.glTranslatef(r * (float)Math.cos(angle), r * (float)Math.sin(angle), 0.0f);

				// Draw the satellite color coded based on signal strength.
				gl.glLineWidth(5.0f);
				float rad = 5.0f;
				if (sat.getSnr() <= 0.0f) {
					ColorPicker.setGLColor(gl, ColorPicker.GRAY15, 0.25f);
				}
				else if (sat.getSnr() > 0.0f && sat.getSnr() < 5.0f) {
					ColorPicker.setGLColor(gl, ColorPicker.FIREBRICK, 0.25f);
					rad+=3.0f;
				}
				else if (sat.getSnr() >= 5.0f && sat.getSnr() < 10.0f) {
					ColorPicker.setGLColor(gl, ColorPicker.COPPER, 0.25f);
					rad+=3.0f;
				}
				else if (sat.getSnr() >= 10.0f && sat.getSnr() < 15.0f) {
					ColorPicker.setGLColor(gl, ColorPicker.DARKGREEN, 0.25f);
					rad+=3.0f;
				}
				else if (sat.getSnr() >= 15.0f && sat.getSnr() < 20.0f) {
					ColorPicker.setGLColor(gl, ColorPicker.YELLOWGREEN, 0.25f);
					rad+=3.0f;
				}
				else if (sat.getSnr() >= 20.0f && sat.getSnr() < 25.0f) {
					ColorPicker.setGLColor(gl, ColorPicker.SEAGREEN, 0.25f);
					rad+=3.0f;
				}
				else if (sat.getSnr() >= 25.0f) {
					ColorPicker.setGLColor(gl, ColorPicker.LIMEGREEN, 0.25f);
					rad+=3.0f;
				}
				Circle.drawCircle(gl, rad, 200, GL10.GL_LINE_LOOP);
				gl.glLineWidth(1.0f);

				// Flip and rotate back by the bearing amount so our text will be under our point.
				gl.glScalef(1.0f, -1.0f, 1.0f);
				gl.glRotatef(-90.0f + bearing, 0.0f, 0.0f, 1.0f);

				// Draw the satellite id.
				gl.glEnable(GL10.GL_TEXTURE_2D);
				gl.glEnable(GL10.GL_BLEND);
				gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
				glSatText.setScale(1.0f);
				ColorPicker.setGLTextColor(glSatText, ColorPicker.CORAL, 1.0f);
				String satDisplay = String.valueOf(sat.getPrn());
//				String satDisplay = String.valueOf(sat.getSnr());
//				String satDisplay = "(" + sat.getAzimuth() + ", " + sat.getElevation() + ")";
				glSatText.draw(satDisplay, -(GLTextFactory.getStringWidth(glSatText, satDisplay) / 2.0f), -(glSatText.getCharHeight() + 15.0f));
				glSatText.end();
				gl.glDisable(GL10.GL_BLEND);
				gl.glDisable(GL10.GL_TEXTURE_2D);

				gl.glPopMatrix();
			}
		}
	}
}
