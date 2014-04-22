package com.sudosoftware.ironman.shapes;

import javax.microedition.khronos.opengles.GL10;

import com.sudosoftware.ironman.Point3D;

public class BezierCurve extends Shape {
	public static void draw2PointCurve(GL10 gl, Point3D start, Point3D end, int segments, int drawMode) {
		float[] vertices = new float[segments * 3];
		for (int i = 0; i < segments * 3; i+=3) {
			float t = (float)i / (float)segments;
			vertices[i + 0] = bezier2Points(t, start.x, end.x);
			vertices[i + 1] = bezier2Points(t, start.y, end.y);
			vertices[i + 2] = bezier2Points(t, start.z, end.z);
		}

		// Draw it.
		draw(gl, vertices, segments, drawMode);
	}

	public static void draw3PointCurve(GL10 gl, Point3D start, Point3D tan, Point3D end, int segments, int drawMode) {
		float[] vertices = new float[segments * 3];
		for (int i = 0; i < segments * 3; i+=3) {
			float t = (float)i / (float)segments;
			vertices[i + 0] = bezier3Points(t, start.x, tan.x, end.x);
			vertices[i + 1] = bezier3Points(t, start.y, tan.y, end.y);
			vertices[i + 2] = bezier3Points(t, start.z, tan.z, end.z);
		}

		// Draw it.
		draw(gl, vertices, segments, drawMode);
	}

	public static void draw4PointCurve(GL10 gl, Point3D start, Point3D tan1, Point3D tan2, Point3D end, int segments, int drawMode) {
		float[] vertices = new float[segments * 3];
		for (int i = 0; i < segments * 3; i+=3) {
			float t = (float)i / (float)segments;
			vertices[i + 0] = bezier4Points(t, start.x, tan1.x, tan2.x, end.x);
			vertices[i + 1] = bezier4Points(t, start.y, tan1.y, tan2.y, end.y);
			vertices[i + 2] = bezier4Points(t, start.z, tan1.z, tan2.z, end.z);
		}

		// Draw it.
		draw(gl, vertices, segments, drawMode);
	}

	private static float bezier2Points(float t, float P0, float P1) {
		float point = (float)(((1 - t) * P0) + (t * P1));
		return point;
	}

	private static float bezier3Points(float t, float P0, float P1, float P2) {
		float point = (float)((Math.pow(1 - t, 2) * P0) +
			(2 * (1 - t) * t * P1) +
			(t * t * P2));
		return point;
	}

	private static float bezier4Points(float t, float P0, float P1, float P2, float P3) {
		float point = (float)((Math.pow(1 - t, 3) * P0) +
			(3 * Math.pow(1 - t, 2) * t * P1) +
			(3 * (1 - t) * t * t * P2) +
			(Math.pow(t, 3.0f) * P3));
		return point;
	}
}
