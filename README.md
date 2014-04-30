Ironman
=======

Real-life Ironman HUD for Android and Google Glass or JetRecon display.

<pre>
Google Glass: <a href="http://www.google.com/glass/start/" target="_blank">More Info Here</a><br />
JetRecon: <a href="http://www.reconinstruments.com/products/jet/" target="_blank">More Info Here</a>
</pre>

The idea behind this project is to build a heads up display system similar to the one in the Ironman movies.

Feel free to join in on this project. Let's see if we can't make it a reality.

Currently using OpenGL 1.0 (because that's what I know at the moment) for all graphics.

Current Features:
 - Tap the mode in the bottom left corner of the screen to toggle through
   available modes. Tap anywhere else to select the mode or press the volume
   down key to select the mode.
 - Press the volume down key to take action on the current mode. For example,
   snap a photo in Picture mode.
 - Displays current time and date.
 - Displays a horizon indicator to show pitch and roll.
 - Displays current altitude.
 - Displays horizontal scrolling compass.
 - Displays current latitude and longitude.
 - Displays fixed satellites in a visual representation.
 - Can enable/disable camera preview.
 - Preferences are saved:
   - Current camera enabled/disabled mode.
   - Current activity mode.

See ToDoList.md for upcoming features.

----------
 Updates:
----------

04-30-2014:
 - Added ability to disable/enable the camera.
 - Added alternate method for taking pictures and starting/stopping video recording.
 - Added ability to HUD elements to process touch events.
 - Fixed satellite display to better match up coordinates with satellite points.
 - Added preference saving for camera mode and current activity mode.

04-29-2014:
 - Updates to add scaling to all HUD elements text.
 - Added new Satellites HUD element to show fixed satellites.
 - Added Speedometer HUD element.
 - Updated GPSTracker to pick up new locations at a quicker rate.
 - Added new Satellites activity mode.
 - Updated circle drawing method to avoid center point with LINE_LOOPS.

04-28-2014:
 - Updates to correct issues with camera crashing application on some devices.
 - Added ActivityMode to allow switching current activity. Modes so far are:
   - Picture (Allows taking pictures with the volume down key)
   - Video (Not implemented)
   - Calendar (Not implemented)
   - Volume (Allows changing the volume on the device with the volume keys)
 - Updated picture mode to show where picture was stored.

04-27-2014:
 - Added new Compass HUD element.
 - Added new Location HUD element.
 - Other minor tweaks to improve efficiency.
 - Updated Clock HUD element to reduce size a little.

04-24-2014:
 - Major work on Circle and Arc drawing methods.
 - Added new Horizon HUD element that shows pitch and roll positions.
 - Added partial transparency to clock hands.
 - Updates HUDElement base class to include pause, resume and destroy methods.
 - Updated HUD elements to use new methods.
 - Added SensorManagerFactory to manage the SensorManager for HUD elements.
 - Moved Point3D into shapes package.
 - Started work on Altimeter HUD element.

04-23-2014:
 - Added Camera preview layer under OpenGL HUD layer.
 - Finished implementation of simple clock HUD element with date and time.
 - Cleaned up code for readability.
 - Added GL Text writer for displaying text in TTF fonts.
 - Added a few fonts to assets location.
 - Created a ColorPicker for quick color management.
 - Set application to use landscape mode and fullscreen (noTitle).
 - Added image capture capability with volume down key press.

