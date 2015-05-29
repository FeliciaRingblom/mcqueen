/*
*  This file is part of OpenDS (Open Source Driving Simulator).
*  Copyright (C) 2014 Rafael Math
*
*  OpenDS is free software: you can redistribute it and/or modify
*  it under the terms of the GNU General Public License as published by
*  the Free Software Foundation, either version 3 of the License, or
*  (at your option) any later version.
*
*  OpenDS is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU General Public License for more details.
*
*  You should have received a copy of the GNU General Public License
*  along with OpenDS. If not, see <http://www.gnu.org/licenses/>.
*/

package eu.opends.main;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

/**
 * 
 * @author Rafael Math, Felicia Ringblom
 */
public class SimulationDefaults 
{
	public static String driverName = "inget namn";
	public static String drivingTaskFileName = "assets/DrivingTasks/Projects/IntroStraight/introStraight.xml";
	
	public static boolean SettingsControllerServer_startServer = false;
	public static int SettingsControllerServer_port = 1000;
	
	public static boolean Analyzer_suppressPDFPopup = false;
	
	public static float gravity = 9.81f;
	
	public static Boolean engine_engineOn = true;
	public static Float engine_minSpeed = 0f;
	public static Float engine_maxSpeed = 130f;
	public static Float engine_acceleration = 3.3f;
	public static Float brake_decelerationBrake = 8.7f;
	public static Float brake_decelerationFreeWheel = 0.4f;
	public static Float wheel_frictionSlip = 50.0f;
	public static Float suspension_stiffness = 120.0f;
	public static Float suspension_compression = 0.2f;
	public static Float suspension_damping = 0.3f;
	public static Float light_intensity = 0.0f;
	public static Boolean transmission_automatic = true;
	public static Float transmission_reverseGear = 3.182f;
	public static Float[] transmission_forwardGears = new Float[]{3.615f, 1.955f, 1.281f, 0.973f, 0.778f, 0.646f};
	public static Float engine_minRPM = 750f;
	public static Float engine_maxRPM = 7500f;
	public static Vector3f initialCarPosition = new Vector3f(-36,0,0);
	public static Quaternion initialCarRotation = new Quaternion(0, 0, 0, 1);
	public static String skyTexture = "Textures/Sky/Bright/mountain.dds";

}
