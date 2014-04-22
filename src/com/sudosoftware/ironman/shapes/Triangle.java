package com.sudosoftware.ironman.shapes;

import javax.microedition.khronos.opengles.GL10;

public class Triangle extends Shape {
	public static void drawTriangle(GL10 gl, float cx, float cy, float w, float h, int drawMode) {
		// In counterclockwise order:
		float[] vertices = {
			cx,					cy - (h / 2.0f), 0.0f, // top
			cx - (w / 2.0f),	cy + (h / 2.0f), 0.0f, // bottom left
			cx + (w / 2.0f),	cy + (h / 2.0f), 0.0f  // bottom right
		};

		draw(gl, vertices, 3, drawMode);
	}
}
