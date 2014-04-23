package com.sudosoftware.ironman.elements;

import javax.microedition.khronos.opengles.GL10;

import com.sudosoftware.ironman.Point3D;
import com.sudosoftware.ironman.shapes.BezierCurve;
import com.sudosoftware.ironman.shapes.Circle;
import com.sudosoftware.ironman.shapes.Triangle;

public class DemoShapes extends HUDElement {

	public DemoShapes() {
		this(0, 0);
	}

	public DemoShapes(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public void update() {
	}

	@Override
	public void render(GL10 gl) {
		gl.glPushMatrix();

		// Move to the element's location.
		gl.glTranslatef(this.x, this.y, 0.0f);

		// Set the line width and point size.
		gl.glLineWidth(20.0f);
		gl.glPointSize(20.0f);

		// Draw a triangle.
		gl.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
		Triangle.drawTriangle(gl, 200.0f, 300.0f, GL10.GL_TRIANGLES);

		// Draw a couple circle around the triangle.
		gl.glColor4f(0.0f, 1.0f, 1.0f, 1.0f);
		Circle.drawCircle(gl, 320.0f, 80, GL10.GL_LINE_LOOP);
		Circle.drawCircle(gl, 350.0f, 80, GL10.GL_LINE_LOOP);
		Circle.drawCircle(gl, 280.0f, 80, GL10.GL_LINE_LOOP);

		// Draw an arc over the triangle.
		gl.glColor4f(1.0f, 1.0f, 0.0f, 1.0f);
		Circle.drawArc(gl, 200.0f, 135.0f, 45.0f, 300, GL10.GL_LINE_STRIP);

		// Draw two straight lines above and below.
		gl.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
		BezierCurve.draw2PointCurve(gl,
			new Point3D(-400.0f, 450.0f, 0.0f),
			new Point3D( 400.0f, 450.0f, 0.0f),
			GL10.GL_LINES);
		BezierCurve.draw2PointCurve(gl,
			new Point3D(-400.0f, -450.0f, 0.0f),
			new Point3D( 400.0f, -450.0f, 0.0f),
			GL10.GL_LINES);

		// Draw four rounded corners to form a square.
		gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
		BezierCurve.draw3PointCurve(gl,
			new Point3D(-400.0f,	   0.0f,	0.0f),
			new Point3D(-400.0f,	 400.0f,	0.0f),
			new Point3D(   0.0f,	 400.0f,	0.0f),
			100, GL10.GL_POINTS);
		BezierCurve.draw3PointCurve(gl,
			new Point3D(   0.0f,	 400.0f,	0.0f),
			new Point3D( 400.0f,	 400.0f,	0.0f),
			new Point3D( 400.0f,	   0.0f,	0.0f),
			100, GL10.GL_POINTS);
		BezierCurve.draw3PointCurve(gl,
			new Point3D(-400.0f,	   0.0f,	0.0f),
			new Point3D(-400.0f,	-400.0f,	0.0f),
			new Point3D(   0.0f,	-400.0f,	0.0f),
			100, GL10.GL_POINTS);
		BezierCurve.draw3PointCurve(gl,
			new Point3D(   0.0f,	-400.0f,	0.0f),
			new Point3D( 400.0f,	-400.0f,	0.0f),
			new Point3D( 400.0f,	   0.0f,	0.0f),
			100, GL10.GL_POINTS);

		// Draw a tear drop.
		gl.glColor4f(0.5f, 0.5f, 0.5f, 1.0f);
		BezierCurve.draw4PointCurve(gl,
			new Point3D(   0.0f,	 300.0f,	0.0f),
			new Point3D( 500.0f,	-500.0f,	0.0f),
			new Point3D(-500.0f,	-500.0f,	0.0f),
			new Point3D(   0.0f,	 300.0f,	0.0f),
			100, GL10.GL_POINTS);

		// Draw a path.
		gl.glColor4f(0.45f, 0.20f, 0.10f, 1.0f);
		BezierCurve.drawPath(gl, new Point3D[] {
			new Point3D(-500.0f,	   0.0f,	0.0f),
			new Point3D(-250.0f,	 125.0f,	0.0f),
			new Point3D(-250.0f,	 375.0f,	0.0f),
			new Point3D(   0.0f,	 500.0f,	0.0f),
			new Point3D( 125.0f,	 250.0f,	0.0f),
			new Point3D( 375.0f,	 250.0f,	0.0f),
			new Point3D( 500.0f,	   0.0f,	0.0f),
			new Point3D( 250.0f,	-125.0f,	0.0f),
			new Point3D( 250.0f,	-375.0f,	0.0f),
			new Point3D(   0.0f,	-500.0f,	0.0f),
			new Point3D(-125.0f,	-250.0f,	0.0f),
			new Point3D(-375.0f,	-250.0f,	0.0f),
			new Point3D(-500.0f,	   0.0f,	0.0f),
		}, 100, GL10.GL_POINTS);

		// Reset the line width and point size.
		gl.glLineWidth(1.0f);
		gl.glPointSize(1.0f);

		gl.glPopMatrix();
	}
}