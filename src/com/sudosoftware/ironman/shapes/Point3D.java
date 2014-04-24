package com.sudosoftware.ironman.shapes;

public class Point3D {
	public float x, y, z;

	public Point3D() {
		this.x = 0.0f;
		this.y = 0.0f;
		this.z = 0.0f;
	}

	public Point3D(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public String toString() {
		return this.x + ", " + this.y + ", " + this.z;
	}
}
