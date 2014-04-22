package com.sudosoftware.ironman.shapes;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Circle {
	public static void drawCircle(GL10 gl, float cx, float cy, float r, int segments) {
		float theta = 2.0f * (float)Math.PI / (float)segments;
		float c = (float)Math.cos(theta);
		float s = (float)Math.sin(theta);
		float t;

		float x = r;
		float y = 0;

		float[] vertices = new float[segments * 3];
		for (int i = 0; i < segments; i++) {
			vertices[i + 0] = x + cx;
			vertices[i + 1] = y + cy;
			vertices[i + 2] = 0;
			t = x;
			x = c * x - s * y;
			y = s * t + c * y;
		}

		// Draw it.
		draw(gl, vertices, segments);
	}

	public static void drawArc(GL10 gl, float cx, float cy, float r, float startAngle, float arcAngle, int segments) {
		float theta = arcAngle / (float)(segments - 1);
		float c = (float)Math.cos(theta);
		float s = (float)Math.sin(theta);
		float t;

		float x = r * (float)Math.cos(startAngle);
		float y = r * (float)Math.sin(startAngle);

		float[] vertices = new float[segments * 3];
		for (int i = 0; i < segments; i++) {
			vertices[i + 0] = x + cx;
			vertices[i + 1] = y + cy;
			vertices[i + 2] = 0;
			t = x;
			x = c * x - s * y;
			y = s * t + c * y;
		}

		// Draw it.
		draw(gl, vertices, segments);
	}

	private static void draw(GL10 gl, float[] vertices, int vertexCount) {
		ByteBuffer bBuff = ByteBuffer.allocateDirect(vertices.length * 4);
		bBuff.order(ByteOrder.nativeOrder());
		FloatBuffer vertBuff = bBuff.asFloatBuffer();
		vertBuff.put(vertices);
		vertBuff.position(0);

		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertBuff);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, vertexCount / 2);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	}
}