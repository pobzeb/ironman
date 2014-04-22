package com.sudosoftware.ironman.elements;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

import com.sudosoftware.ironman.Point3D;
import com.sudosoftware.ironman.shapes.BezierCurve;
import com.sudosoftware.ironman.shapes.Circle;
import com.sudosoftware.ironman.shapes.Triangle;

public class Clock extends HUDElement {
	private Calendar datetime;
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("MM-dd-yyyy");
	private SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");

	public Clock() {
		datetime = Calendar.getInstance();
	}

	public void update() {
	}

	public void render(GL10 gl) {
		Log.i(this.getClass().getName(), "Date: " + dateFormatter.format(datetime.getTime()));
		Log.i(this.getClass().getName(), "Time: " + timeFormatter.format(datetime.getTime()));
		gl.glPushMatrix();
		gl.glTranslatef(0, 0, 0);
		gl.glColor4f(1.0f, 0.5f, 0.5f, 1.0f);
		gl.glLineWidth(10.0f);
		gl.glPointSize(10.0f);
		Triangle.drawTriangle(gl, 200.0f, 200.0f, 200.0f, 200.0f, GL10.GL_TRIANGLES);
		Circle.drawCircle(gl, 10.0f, 10.0f, 5.0f, 300, GL10.GL_LINE_LOOP);
		Circle.drawArc(gl, 20.0f, 20.0f, 5.0f, 0.0f, 45.0f, 30, GL10.GL_LINE_STRIP);
		BezierCurve.draw3PointCurve(gl,
				new Point3D( 100.0f, 300.0f, 0.0f),
				new Point3D( 100.0f, 100.0f, 0.0f),
				new Point3D( 300.0f, 100.0f, 0.0f),
				1000, GL10.GL_POINTS);
		gl.glColor4f(0.5f, 1.0f, 1.0f, 1.0f);
		BezierCurve.draw4PointCurve(gl,
				new Point3D( 500.0f,  200.0f, 0.0f),
				new Point3D( 200.0f,  100.0f, 0.0f),
				new Point3D(-100.0f,  100.0f, 0.0f),
				new Point3D( 500.0f,  200.0f, 0.0f),
				1000, GL10.GL_POINTS);
		gl.glColor4f(0.5f, 0.0f, 1.0f, 1.0f);
		BezierCurve.draw2PointCurve(gl,
				new Point3D(200.0f, 200.0f, 0.0f),
				new Point3D(200.0f, 800.0f, 0.0f),
				100, GL10.GL_LINE_STRIP);
		gl.glPopMatrix();
	}
}