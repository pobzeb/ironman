package com.sudosoftware.ironman.shapes;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public abstract class Shape {
	protected static void draw(GL10 gl, float[] vertices, int vertexCount, int drawMode) {
		ByteBuffer bBuff = ByteBuffer.allocateDirect(vertices.length * 4);
		bBuff.order(ByteOrder.nativeOrder());
		FloatBuffer vertBuff = bBuff.asFloatBuffer();
		vertBuff.put(vertices);
		vertBuff.position(0);

		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertBuff);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDrawArrays(drawMode, 0, vertexCount / 2);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	}
}