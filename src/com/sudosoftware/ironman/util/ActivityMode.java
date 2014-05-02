package com.sudosoftware.ironman.util;

public enum ActivityMode {
	PICTURE_MODE("Picture", Boolean.TRUE),
	VIDEO_MODE("Video", Boolean.FALSE),
	WEATHER_MODE("Weather", Boolean.FALSE),
	SATELLITE_MODE("Satellites", Boolean.TRUE),
	CALENDAR_MODE("Calendar", Boolean.FALSE),
	PLAYLIST_MODE("Playlist", Boolean.FALSE),
	SYSTEM_INFO_MODE("System Info", Boolean.FALSE),
	OPTIONS_MODE("Options", Boolean.TRUE),
	;

	private static int modeIdx = 0;
	public int mode;
	public String name;
	public boolean enabled;

	private ActivityMode(String name, boolean enabled) {
		this.mode = nextIdx();
		this.name = name;
		this.enabled = enabled;
	}

	private int nextIdx() {
		return modeIdx++;
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
