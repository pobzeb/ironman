package com.sudosoftware.ironman.util;

import com.sudosoftware.ironman.mode.CalendarMode;
import com.sudosoftware.ironman.mode.FlightMode;
import com.sudosoftware.ironman.mode.HUDMode;
import com.sudosoftware.ironman.mode.OptionsMode;
import com.sudosoftware.ironman.mode.PlaylistMode;
import com.sudosoftware.ironman.mode.SatelliteMode;
import com.sudosoftware.ironman.mode.SystemInfoMode;
import com.sudosoftware.ironman.mode.WeatherMode;

public enum ActivityMode {
	FLIGHT_MODE("Flight", new FlightMode(), Boolean.TRUE),
	WEATHER_MODE("Weather", new WeatherMode(), Boolean.FALSE),
	SATELLITE_MODE("Satellites", new SatelliteMode(), Boolean.TRUE),
	CALENDAR_MODE("Calendar", new CalendarMode(), Boolean.FALSE),
	PLAYLIST_MODE("Playlist", new PlaylistMode(), Boolean.FALSE),
	SYSTEM_INFO_MODE("System Info", new SystemInfoMode(), Boolean.FALSE),
	OPTIONS_MODE("Options", new OptionsMode(), Boolean.TRUE),
	;

	private static int modeIdx = 0;
	public int id;
	public String name;
	public HUDMode mode;
	public boolean enabled;

	private ActivityMode(String name, HUDMode mode, boolean enabled) {
		this.id = nextIdx();
		this.name = name;
		this.mode = mode;
		this.enabled = enabled;
	}

	private int nextIdx() {
		return modeIdx++;
	}

	public static ActivityMode findActivityMode(int id) {
		for (ActivityMode activityMode : values()) {
			if (activityMode.id == id) {
				return activityMode;
			}
		}

		return null;
	}
}
