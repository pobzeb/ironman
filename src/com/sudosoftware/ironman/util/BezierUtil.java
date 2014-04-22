package com.sudosoftware.ironman.util;

public class BezierUtil {
	public static float bezier(float t, float P0, float P1, float P2, float P3) {
		float point = (float)((Math.pow(1 - t, 3.0f) * P0) +
			(3 * Math.pow(1 - t, 2.0f) * t * P1) +
			(3 * (1 - t) * t * t * P2) +
			(Math.pow(t, 3.0f) * P3));
		return point;
	}
}
