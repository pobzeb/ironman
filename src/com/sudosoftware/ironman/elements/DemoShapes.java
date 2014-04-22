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

		// Set the line width and point size.
		gl.glLineWidth(20.0f);
		gl.glPointSize(20.0f);

		// Draw a triangle.
		gl.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
		Triangle.drawTriangle(gl, this.x, this.y, 200.0f, 300.0f, GL10.GL_TRIANGLES);

		// Draw a couple circle around the triangle.
		gl.glColor4f(0.0f, 1.0f, 1.0f, 1.0f);
		Circle.drawCircle(gl, this.x, this.y, 320.0f, 80, GL10.GL_LINE_LOOP);
		Circle.drawCircle(gl, this.x, this.y, 350.0f, 80, GL10.GL_LINE_LOOP);
		Circle.drawCircle(gl, this.x, this.y, 280.0f, 80, GL10.GL_LINE_LOOP);

		// Draw an arc over the triangle.
		gl.glColor4f(1.0f, 1.0f, 0.0f, 1.0f);
		Circle.drawArc(gl, this.x, this.y, 200.0f, 135.0f, 45.0f, 300, GL10.GL_LINE_STRIP);

		// Draw two straight lines above and below.
		gl.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
		BezierCurve.draw2PointCurve(gl,
				new Point3D(this.x - 400.0f, this.y + 450.0f, 0.0f),
				new Point3D(this.x + 400.0f, this.y + 450.0f, 0.0f),
				GL10.GL_LINES);
		BezierCurve.draw2PointCurve(gl,
				new Point3D(this.x - 400.0f, this.y - 450.0f, 0.0f),
				new Point3D(this.x + 400.0f, this.y - 450.0f, 0.0f),
				GL10.GL_LINES);

		// Draw four rounded corners to form a square.
		gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
		BezierCurve.draw3PointCurve(gl,
				new Point3D(this.x - 400.0f,	this.y,				0.0f),
				new Point3D(this.x - 400.0f,	this.y + 400.0f,	0.0f),
				new Point3D(this.x,				this.y + 400.0f,	0.0f),
				100, GL10.GL_POINTS);
		BezierCurve.draw3PointCurve(gl,
				new Point3D(this.x,				this.y + 400.0f,	0.0f),
				new Point3D(this.x + 400.0f,	this.y + 400.0f,	0.0f),
				new Point3D(this.x + 400.0f,	this.y,				0.0f),
				100, GL10.GL_POINTS);
		BezierCurve.draw3PointCurve(gl,
				new Point3D(this.x - 400.0f,	this.y,				0.0f),
				new Point3D(this.x - 400.0f,	this.y - 400.0f,	0.0f),
				new Point3D(this.x,				this.y - 400.0f,	0.0f),
				100, GL10.GL_POINTS);
		BezierCurve.draw3PointCurve(gl,
				new Point3D(this.x,				this.y - 400.0f,	0.0f),
				new Point3D(this.x + 400.0f,	this.y - 400.0f,	0.0f),
				new Point3D(this.x + 400.0f,	this.y,				0.0f),
				100, GL10.GL_POINTS);

		// Draw a tear drop.
		gl.glColor4f(0.5f, 0.5f, 0.5f, 1.0f);
		BezierCurve.draw4PointCurve(gl,
				new Point3D(this.x,				this.y + 300.0f, 0.0f),
				new Point3D(this.x + 500.0f,	this.y - 500.0f, 0.0f),
				new Point3D(this.x - 500.0f,	this.y - 500.0f, 0.0f),
				new Point3D(this.x,				this.y + 300.0f, 0.0f),
				100, GL10.GL_POINTS);

		// Draw a path.
		gl.glColor4f(0.45f, 0.20f, 0.10f, 1.0f);
		BezierCurve.drawPath(gl, new Point3D[] {
			new Point3D(this.x - 500.0f,	this.y,				0.0f),
			new Point3D(this.x - 250.0f,	this.y + 125.0f,	0.0f),
			new Point3D(this.x - 250.0f,	this.y + 375.0f,	0.0f),
			new Point3D(this.x,				this.y + 500.0f,	0.0f),
			new Point3D(this.x + 125.0f,	this.y + 250.0f,	0.0f),
			new Point3D(this.x + 375.0f,	this.y + 250.0f,	0.0f),
			new Point3D(this.x + 500.0f,	this.y,				0.0f),
			new Point3D(this.x + 250.0f,	this.y - 125.0f,	0.0f),
			new Point3D(this.x + 250.0f,	this.y - 375.0f,	0.0f),
			new Point3D(this.x,				this.y - 500.0f,	0.0f),
			new Point3D(this.x - 125.0f,	this.y - 250.0f,	0.0f),
			new Point3D(this.x - 375.0f,	this.y - 250.0f,	0.0f),
			new Point3D(this.x - 500.0f,	this.y,				0.0f),
		}, 100, GL10.GL_POINTS);

		gl.glPopMatrix();
	}
}