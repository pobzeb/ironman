package com.sudosoftware.ironman.shapes;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Triangle {
	private FloatBuffer vertexBuffer;

	// number of coordinates per vertex in this array
	static final int COORDS_PER_VERTEX = 3;
	static float triangleCoords[] = {   // in counterclockwise order:
		 0.0f,  0.622008459f, 0.0f, // top
		-0.5f, -0.311004243f, 0.0f, // bottom left
		 0.5f, -0.311004243f, 0.0f  // bottom right
	};

	// Set color with red, green, blue and alpha (opacity) values
	float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };

	public Triangle() {
		// initialize vertex byte buffer for shape coordinates
        // (number of coordinate values * 4 bytes per float)
		ByteBuffer bb = ByteBuffer.allocateDirect(triangleCoords.length * 4);

		// use the device hardware's native byte order
		bb.order(ByteOrder.nativeOrder());

		// create a floating point buffer from the ByteBuffer
		vertexBuffer = bb.asFloatBuffer();

		// add the coordinates to the FloatBuffer
		vertexBuffer.put(triangleCoords);

		// set the buffer to read the first coordinate
		vertexBuffer.position(0);
	}

	public void draw(GL10 gl) {
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

		// Set color for drawing the triangle
		gl.glColor4f(color[0], color[1], color[2], color[3]);
		gl.glVertexPointer(COORDS_PER_VERTEX, GL10.GL_FLOAT, 0, vertexBuffer);
	    gl.glDrawArrays(GL10.GL_TRIANGLES, 0, triangleCoords.length / COORDS_PER_VERTEX);

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	}
}
