package com.sudosoftware.ironman.shapes;

import javax.microedition.khronos.opengles.GL10;

public class Quad extends Shape {
	public static void drawQuad(GL10 gl, float x, float y, float w, float h, int drawMode) {
		gl.glPushMatrix();
		gl.glTranslatef(x, y, 0.0f);
		drawQuad(gl, w, h, drawMode);
		gl.glPopMatrix();
	}

	public static void drawQuad(GL10 gl, float w, float h, int drawMode) {
		// In counterclockwise order:
		float[] vertices = {
			0.0f,	0.0f,	0.0f, // top left
			0.0f,	h,		0.0f, // bottom left
			w,		h,		0.0f, // bottom right
			0.0f,	0.0f,	0.0f, // top left
			w,		h,		0.0f, // bottom right
			w,		0.0f,	0.0f, // top right
		};

		draw(gl, vertices, vertices.length / 3, drawMode);
	}

	public static void drawRect(GL10 gl, float x, float y, float w, float h, int drawMode) {
		gl.glPushMatrix();
		gl.glTranslatef(x, y, 0.0f);
		drawRect(gl, w, h, drawMode);
		gl.glPopMatrix();
	}

	public static void drawRect(GL10 gl, float w, float h, int drawMode) {
		BezierCurve.draw2PointCurve(gl, new Point3D(0, 0, 0.0f), new Point3D(w, 0, 0.0f), drawMode);
		BezierCurve.draw2PointCurve(gl, new Point3D(0, h, 0.0f), new Point3D(w, h, 0.0f), drawMode);
		BezierCurve.draw2PointCurve(gl, new Point3D(0, 0, 0.0f), new Point3D(0, h, 0.0f), drawMode);
		BezierCurve.draw2PointCurve(gl, new Point3D(w, 0, 0.0f), new Point3D(w, h, 0.0f), drawMode);
	}
}
