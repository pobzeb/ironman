package com.sudosoftware.ironman.shapes;

import javax.microedition.khronos.opengles.GL10;

public class BezierCurve extends Shape {
	public static void draw2PointCurve(GL10 gl, Point3D start, Point3D end, int drawMode) {
		float[] vertices = new float[2 * 3];
		vertices[0] = bezier2Points(0, start.x, end.x);
		vertices[1] = bezier2Points(0, start.y, end.y);
		vertices[2] = bezier2Points(0, start.z, end.z);
		vertices[3] = bezier2Points(1, start.x, end.x);
		vertices[4] = bezier2Points(1, start.y, end.y);
		vertices[5] = bezier2Points(1, start.z, end.z);

		// Draw it.
		draw(gl, vertices, vertices.length / 3, drawMode);
	}

	public static void draw3PointCurve(GL10 gl, Point3D start, Point3D tan, Point3D end, int segments, int drawMode) {
		float[] vertices = new float[segments * 3];
		int idx = 0;
		for (int i = 0; i < segments; i++, idx += 3) {
			float t = (float)i / (float)segments;
			vertices[idx + 0] = bezier3Points(t, start.x, tan.x, end.x);
			vertices[idx + 1] = bezier3Points(t, start.y, tan.y, end.y);
			vertices[idx + 2] = bezier3Points(t, start.z, tan.z, end.z);
		}

		// Draw it.
		draw(gl, vertices, vertices.length / 3, drawMode);
	}

	public static void draw4PointCurve(GL10 gl, Point3D start, Point3D tan1, Point3D tan2, Point3D end, int segments, int drawMode) {
		float[] vertices = new float[segments * 3];
		int idx = 0;
		for (int i = 0; i < segments; i++, idx += 3) {
			float t = (float)i / (float)segments;
			vertices[idx + 0] = bezier4Points(t, start.x, tan1.x, tan2.x, end.x);
			vertices[idx + 1] = bezier4Points(t, start.y, tan1.y, tan2.y, end.y);
			vertices[idx + 2] = bezier4Points(t, start.z, tan1.z, tan2.z, end.z);
		}

		// Draw it.
		draw(gl, vertices, vertices.length / 3, drawMode);
	}

	public static void drawPath(GL10 gl, Point3D[] points, int segments, int drawMode) {
		// Points array needs to be a factor of 3 plus a starting point.
		if (points.length == 0 || (points.length - 1) % 3 != 0) return;

		// Loop through the points.
		for (int i = 0; i < points.length - 1; i += 3) {
			// Draw the remaining segments.
			draw4PointCurve(gl,
				points[i + 0],
				points[i + 1],
				points[i + 2],
				points[i + 3],
				segments, drawMode);
		}
	}

	private static float bezier2Points(float t, float P0, float P1) {
		float point = (float)(((1 - t) * P0) + (t * P1));
		return point;
	}

	private static float bezier3Points(float t, float P0, float P1, float P2) {
		float point = (float)(
			(Math.pow(1 - t, 2) * P0) +
			(2 * (1 - t) * t * P1) +
			(t * t * P2));
		return point;
	}

	private static float bezier4Points(float t, float P0, float P1, float P2, float P3) {
		float point = (float)(
			(Math.pow(1 - t, 3) * P0) +
			(3 * Math.pow(1 - t, 2) * t * P1) +
			(3 * (1 - t) * t * t * P2) +
			(Math.pow(t, 3.0f) * P3));
		return point;
	}
}
