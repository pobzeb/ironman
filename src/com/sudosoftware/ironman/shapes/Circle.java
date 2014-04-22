package com.sudosoftware.ironman.shapes;

import javax.microedition.khronos.opengles.GL10;

import com.sudosoftware.ironman.Point3D;

public class Circle extends Shape {
	public static void drawCircle(GL10 gl, float cx, float cy, float r, int segments, int drawMode) {
		drawArc(gl, cx, cy, r, 0.0f, 360.0f, segments, drawMode);
	}

	public static void drawArc(GL10 gl, float cx, float cy, float r, float startAngle, float endAngle, int segments, int drawMode) {
		// Convert to radians.
		startAngle = (float)Math.toRadians(startAngle);
		endAngle = (float)Math.toRadians(endAngle);

		float resolution = Math.abs(endAngle - startAngle) / segments;
		if (endAngle < startAngle) {
			float tmp = startAngle;
			startAngle = endAngle;
			endAngle = tmp;
		}
		if (drawMode == GL10.GL_TRIANGLE_FAN) {
			segments += 2;
		}
		Point3D[] points = new Point3D[segments];
		int idx = 0;
		Point3D point;
		for (float angle = startAngle; angle <= endAngle && idx < points.length; angle += resolution, idx++) {
			point = new Point3D();
			point.x = r * (float)Math.cos(angle);
			point.y = r * (float)Math.sin(angle);
			points[idx] = point;
		}

		// Draw it.
		draw(gl, points, points.length, drawMode);
	}
}