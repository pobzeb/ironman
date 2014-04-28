package com.sudosoftware.ironman.util;

public enum ActivityMode {
	PICTURE_MODE(0, "Picture"),
	VIDEO_MODE(1, "Video"),
	CALENDAR_MODE(2, "Calendar"),
	VOLUME_MODE(3, "Volume"),
	;

	public int mode;
	public String name;

	private ActivityMode(int mode, String name) {
		this.mode = mode;
		this.name = name;
	}

	public static ActivityMode findActivityMode(int mode) {
		for (ActivityMode activityMode : values()) {
			if (activityMode.mode == mode) {
				return activityMode;
			}
		}

		return null;
	}
}
