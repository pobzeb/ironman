package com.sudosoftware.ironman.shapes;

import javax.microedition.khronos.opengles.GL10;

public class Triangle extends Shape {
	public static void drawTriangle(GL10 gl, float w, float h, int drawMode) {
		// In counterclockwise order:
		float[] vertices = {
			 0,				 (h / 2.0f), 0.0f, // top
			-(w / 2.0f),	-(h / 2.0f), 0.0f, // bottom left
			 (w / 2.0f),	-(h / 2.0f), 0.0f  // bottom right
		};

		draw(gl, vertices, vertices.length / 3, drawMode);
	}
}
