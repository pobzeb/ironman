package com.sudosoftware.ironman.elements;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

import com.sudosoftware.ironman.Point3D;
import com.sudosoftware.ironman.shapes.Circle;
import com.sudosoftware.ironman.util.BezierUtil;

public class Clock extends HUDElement {
	private Calendar datetime;
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("MM-dd-yyyy");
	private SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");

	private int points = 300;

	public Clock() {
		datetime = Calendar.getInstance();

//		Point3D pStart =	new Point3D(-0.5f, -0.5f, 0.0f);
//		Point3D pTan1 =		new Point3D(-0.5f,  0.5f, 0.0f);
//		Point3D pTan2 =		new Point3D(-0.5f,  0.5f, 0.0f);
//		Point3D pEnd =		new Point3D( 0.5f,  0.5f, 0.0f);
//		vertices = new float[points * 3];
//		for (int i = 0; i < points * 3; i+=3) {
//			float t = (float)i / (float)points;
//			vertices[i + 0] = BezierUtil.bezier(t, pStart.x, pTan1.x, pTan2.x, pEnd.x);
//			vertices[i + 1] = BezierUtil.bezier(t, pStart.y, pTan1.y, pTan2.y, pEnd.y);
//			vertices[i + 2] = BezierUtil.bezier(t, pStart.z, pTan1.z, pTan2.z, pEnd.z);
//		}
//		ByteBuffer bBuff = ByteBuffer.allocateDirect(vertices.length * 4);
//		bBuff.order(ByteOrder.nativeOrder());
//		vertBuff = bBuff.asFloatBuffer();
//		vertBuff.put(vertices);
//		vertBuff.position(0);
	}

	public void update() {
	}

	public void render(GL10 gl) {
		Log.i(this.getClass().getName(), "Date: " + dateFormatter.format(datetime.getTime()));
		Log.i(this.getClass().getName(), "Time: " + timeFormatter.format(datetime.getTime()));
		gl.glPushMatrix();
		gl.glTranslatef(0, 0, 0);
		gl.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
		gl.glLineWidth(10.0f);
		Circle.drawCircle(gl, 0, 0, 0.5f, points);
		Circle.drawArc(gl, 0, 0, 0.3f, 0.0f, 45.0f, points);
		gl.glPopMatrix();
	}
}