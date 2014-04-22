package com.sudosoftware.ironman.shapes;

import javax.microedition.khronos.opengles.GL10;

public class Circle extends Shape {
	public static void drawCircle(GL10 gl, float cx, float cy, float r, int segments, int drawMode) {
		float theta = (2.0f * (float)Math.PI) / (float)segments;

		float[] vertices = new float[segments * 3];
		int idx = 0;
		for (float angle = 0.0f; angle <= (2.0f * (float)Math.PI); angle += theta, idx++) {
			vertices[idx + 0] = cx + r * (float)Math.sin(angle);
			vertices[idx + 1] = cy + r * (float)Math.cos(angle);
			vertices[idx + 2] = 0;
		}

		// Draw it.
		draw(gl, vertices, segments, drawMode);
	}

	public static void drawArc(GL10 gl, float cx, float cy, float r, float startAngle, float arcAngle, int segments, int drawMode) {
		float theta = arcAngle / (float)(segments - 1);
		float c = (float)Math.cos(theta);
		float s = (float)Math.sin(theta);

		float x = r * (float)Math.cos(startAngle);
		float y = r * (float)Math.sin(startAngle);

		float[] vertices = new float[segments * 3];
		for (int i = 0; i < segments; i++) {
			vertices[i + 0] = cx + x * s;
			vertices[i + 1] = cy + y * c;
			vertices[i + 2] = 0;
		}

		// Draw it.
		draw(gl, vertices, segments, drawMode);
	}
}