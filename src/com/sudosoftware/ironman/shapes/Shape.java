package com.sudosoftware.ironman.shapes;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public abstract class Shape {
	protected static void draw(GL10 gl, Point3D[] points, int vertexCount, int drawMode) {
		// Convert the points to float vertices.
		float[] vertices = new float[points.length * 3];
		int idx = 0;
		Point3D point;
		for (int i = 0; i < points.length; i++, idx += 3) {
			point = points[i];
			if (point == null) continue;
			vertices[idx + 0] = point.x;
			vertices[idx + 1] = point.y;
			vertices[idx + 2] = point.z;
		}

		// Now draw it.
		draw(gl, vertices, vertexCount, drawMode);
	}

	protected static void draw(GL10 gl, float[] vertices, int vertexCount, int drawMode) {
		ByteBuffer bBuff = ByteBuffer.allocateDirect(vertices.length * 4);
		bBuff.order(ByteOrder.nativeOrder());
		FloatBuffer vertBuff = bBuff.asFloatBuffer();
		vertBuff.put(vertices);
		vertBuff.position(0);

		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertBuff);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDrawArrays(drawMode, 0, vertexCount);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	}
}