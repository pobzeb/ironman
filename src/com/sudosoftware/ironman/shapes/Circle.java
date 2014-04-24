package com.sudosoftware.ironman.shapes;

import javax.microedition.khronos.opengles.GL10;

public class Circle extends Shape {
	public static void drawCircle(GL10 gl, float r, int segments, int drawMode) {
		drawArc(gl, r, 0.0f, 361.0f, segments, drawMode);
	}

	public static void drawArc(GL10 gl, float r, float startAngle, float endAngle, int segments, int drawMode) {
		drawArc(gl, r, startAngle, endAngle, segments, drawMode, true);
	}

	public static void drawArc(GL10 gl, float r, float startAngle, float endAngle, int segments, int drawMode, boolean forceUseCenter) {
		// Convert to radians.
		startAngle = (float)Math.toRadians(startAngle);
		endAngle = (float)Math.toRadians(endAngle);

		float resolution = Math.abs(endAngle - startAngle) / segments;
		if (endAngle < startAngle) {
			float tmp = startAngle;
			startAngle = endAngle;
			endAngle = tmp;
		}
		Point3D[] points = new Point3D[segments];
		int idx = 0;
		// When using Triangle Fan or Line Loop mode, we need the first point to start in the center.
		if (drawMode == GL10.GL_TRIANGLE_FAN || drawMode == GL10.GL_LINE_LOOP) {
			segments += 1;
			points = new Point3D[segments];
			idx = 1;
			// Drawing a full circle so set the extra point in the exact center.
			points[0] = new Point3D(0.0f, 0.0f, 0.0f);

			if (!forceUseCenter) {
				// This is an arc but we need it filled. Extra point should be between the
				// start and end points.
				if (Math.toDegrees(endAngle - startAngle) < 360.0f) {
					// Get the sector angle.
					float sectorAngle = endAngle - startAngle;
	
					// If the angle is over 180, get the inverse.
					if (Math.toDegrees(endAngle - startAngle) > 180.0f) {
						sectorAngle = (float)Math.toRadians(360.0) - sectorAngle;
					}
	
					// Length of a chord (line connecting the start and end points) is:
					// chord = 2r * sin(angle / 2)
					float chord = (2 * r * (float)Math.sin(sectorAngle / 2));
	
					// Now we need the radius length that intersects the center of our chord.
					// d = sqrt(r^2 - (chord / 2)^2)
					float d = (float)Math.sqrt((r * r) - Math.pow(chord / 2, 2.0));
	
					// If the angle is over 180, get the inverse.
					if (Math.toDegrees(endAngle - startAngle) > 180.0f) {
						sectorAngle = (float)Math.toRadians(360.0f) - sectorAngle;
						d *= -1;
					}
	
					// Now we need to set our start point where these two intersect.
					points[0].x = d * (float)Math.cos(endAngle - (sectorAngle / 2));
					points[0].y = d * (float)Math.sin(endAngle - (sectorAngle / 2));
				}
			}
		}
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