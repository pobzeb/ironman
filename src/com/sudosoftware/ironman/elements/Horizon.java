package com.sudosoftware.ironman.elements;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.sudosoftware.ironman.shapes.BezierCurve;
import com.sudosoftware.ironman.shapes.Circle;
import com.sudosoftware.ironman.shapes.Point3D;
import com.sudosoftware.ironman.util.ColorPicker;
import com.sudosoftware.ironman.util.SensorManagerFactory;

public class Horizon extends HUDElement {
	// Monitor roll and pitch of the device.
	private float roll, pitch;

	public Horizon(Context context) {
		super(context);
	}

	public Horizon(Context context, int x, int y, int w, int h) {
		super(context, x, y, w, h);
	}

	public Horizon(Context context, int x, int y, int w, int h, float scale) {
		super(context, x, y, w, h, scale);
	}

	@Override
	public void update() {
		// Get the roll and pitch.
		roll = SensorManagerFactory.getInstance().getRoll();
		pitch = SensorManagerFactory.getInstance().getPitch();

		// We need to adjust the roll to keep it in the 0 to 360 range.
		if (roll < 0.0f) roll = (roll + 360.0f) % 360.0f;
	}

	@Override
	public void render(GL10 gl) {
		// Draw a disc to hold our horizon.
		ColorPicker.setGLColor(gl, ColorPicker.NEONBLUE, 0.75f);
		Circle.drawCircle(gl, 300.0f, 500, GL10.GL_TRIANGLE_FAN);
		ColorPicker.setGLColor(gl, ColorPicker.BLACK, 0.125f);
		Circle.drawCircle(gl, 290.0f, 500, GL10.GL_TRIANGLE_FAN);

		// Rotate the disc to represent our current roll (flipped horizontally).
		// We've placed angle zero pointing up to help with our
		// pitch calculation below.
		gl.glRotatef(-roll + 90.0f, 0.0f, 0.0f, 1.0f);
		gl.glScalef(1.0f, -1.0f, 1.0f);

		// We need to calculate the start and end angle for our
		// horizon arc based on pitch (zero is looking down, 180 is looking up).
		float pitchAngle = Math.abs(pitch);		

		// Draw the horizon and indicator tick marks.
		gl.glLineWidth(5.0f);
		ColorPicker.setGLColor(gl, ColorPicker.NEONBLUE, 0.25f);
		Circle.drawArc(gl, 280.0f, pitchAngle, 360.0f - pitchAngle, 500, GL10.GL_TRIANGLE_FAN, false);
		ColorPicker.setGLColor(gl, ColorPicker.NEONBLUE, 0.75f);
		Circle.drawArc(gl, 280.0f, pitchAngle, 360.0f - pitchAngle, 500, GL10.GL_LINE_LOOP, false);
		gl.glLineWidth(10.0f);
		BezierCurve.draw2PointCurve(gl,
			new Point3D(0.0f, -330.0f, 0.0f),
			new Point3D(0.0f, -310.0f, 0.0f), GL10.GL_LINE_STRIP);
		BezierCurve.draw2PointCurve(gl,
			new Point3D(0.0f, 330.0f, 0.0f),
			new Point3D(0.0f, 310.0f, 0.0f), GL10.GL_LINE_STRIP);		
		BezierCurve.draw2PointCurve(gl,
			new Point3D(-330.0f, 0.0f, 0.0f),
			new Point3D(-310.0f, 0.0f, 0.0f), GL10.GL_LINE_STRIP);
		BezierCurve.draw2PointCurve(gl,
			new Point3D(330.0f, 0.0f, 0.0f),
			new Point3D(310.0f, 0.0f, 0.0f), GL10.GL_LINE_STRIP);
		gl.glLineWidth(1.0f);
	}
}