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

package eu.opends.drivingTask.interaction;

import java.util.Properties;

import eu.opends.drivingTask.DrivingTaskDataQuery.Layer;
import eu.opends.main.Simulator;
import eu.opends.trigger.ManipulatePictureTriggerAction;
import eu.opends.trigger.PauseTriggerAction;
import eu.opends.trigger.PlaySoundAction;
import eu.opends.trigger.ResetCarToResetPointAction;
import eu.opends.trigger.ReturnToInstruction;
import eu.opends.trigger.SetupKeyReactionTimerTriggerAction;
import eu.opends.trigger.SetupLaneChangeReactionTimerTriggerAction;
import eu.opends.trigger.StartReactionMeasurementTriggerAction;
import eu.opends.trigger.TriggerAction;

/**
 * 
 * @author Rafael Math, Felicia Ringblom, Jessica Larsson
 */
public class InteractionMethods 
{  	
	
	@Action(
			name = "manipulatePicture", 
			layer = Layer.INTERACTION, 
			description = "Manipulates visibility of the given picture",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="id", type="String", defaultValue="picture01", 
								description="ID of the picture to manipulate"),
					 @Parameter(name="visible", type="Boolean", defaultValue="true", 
								description="Makes the picture (in)visible")
					}
		)
	public TriggerAction manipulatePicture(Simulator sim, float delay, int repeat, Properties parameterList)
	{
		String parameter = "";
		
		try {

			// look up id of picture to manipulate --> if not available, quit
			parameter = "id";
			String id = parameterList.getProperty(parameter);
			if(id == null)
				throw new Exception();

			// set visibility, if available
			parameter = "visible";
			
			String visible = parameterList.getProperty(parameter);
			if(visible == null)
				throw new Exception();

			return new ManipulatePictureTriggerAction(sim, delay, repeat, id, Boolean.parseBoolean(visible));
			
		} catch (Exception e) {

			reportError("manipulatePicture", parameter);
			return null;
		}
	}


	private Float[] extractFloatValues(Properties parameterList, String[] keys, 
			Float[] defaultValues) throws NotAFloatException 
	{
		Float[] values = new Float[keys.length];
		boolean keyFound = false;
		
		for(int i=0; i<keys.length; i++)
		{
			try {
				
				String stringValue = parameterList.getProperty(keys[i]);
				if(stringValue != null)
				{
					values[i] = Float.parseFloat(stringValue);
					keyFound = true;
				}
				else
					values[i] = defaultValues[i];
				
			} catch(Exception e) {
				
				throw new NotAFloatException(keys[i]);
			}

		}
		
		if(keyFound)
			return values;
		
		return null;
	}
	
	
	@Action(
			name = "pauseSimulation", 
			layer = Layer.INTERACTION, 
			description = "Stops the simulation for the given amount of time",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="duration", type="Integer", defaultValue="1", 
							 	description="Amount of seconds to pause (0 = infinite)")
					}
		)
	public TriggerAction pauseSimulation(Simulator sim, float delay, int repeat, Properties parameterList)
	{
		String parameter = "duration";
		
		try {
			
			// read duration of pause
			String durationString = parameterList.getProperty(parameter);
			if(durationString == null)
				durationString = setDefault("pauseSimulation", parameter, "1");
			int duration = Integer.parseInt(durationString);
			
			// create PauseTriggerAction
			return new PauseTriggerAction(sim, delay, repeat, duration);
			
		} catch (Exception e) {
	
			reportError("pauseSimulation", parameter);
			return null;
		}
	}
	
	@Action(
			name = "returnToInstruction", 
			layer = Layer.INTERACTION, 
			description = "Returns to the instructions to given screen",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="next", type="Integer", defaultValue="1", 
							 	description="screen number to switch to")
					}
		)
	public TriggerAction returnToInstruction(Simulator sim, float delay, int repeat, Properties parameterList)
	{
		String parameter = "next";

		try {
						
			// read next screen
			String nextString = parameterList.getProperty(parameter);
			
			
			int screenNumber = Integer.parseInt(nextString);
			return new ReturnToInstruction((Simulator)sim, delay, repeat, screenNumber);
			
		} catch (Exception e) {
	
			reportError("returnToInstruction", parameter);
			return null;
			
		}
		
	}
	
	/**
	 * Creates a ResetCar trigger action by parsing the node list for the name
	 * of the reset point to place the car when the trigger was hit.
	 * 
	 * @return
	 * 			ResetCar trigger action with the name of the reset point to 
	 * 			move the car to.
	 */
	@Action(
			name = "resetCar",
			layer = Layer.SCENARIO,
			description = "Moves the driving car to the given reset point",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="resetPointID", type="String", defaultValue="resetPoint01", 
								description="ID of the reset point to move the driving car to")
					}
		)
	public TriggerAction resetCar(Simulator sim, float delay, int repeat, Properties parameterList)
	{
		String parameter = "resetPointID";
		
		try {
			
			// read ID of reset point
			String resetPointID = parameterList.getProperty(parameter);
			if(resetPointID == null)
				throw new Exception();
			
			// create ResetCarToResetPointAction
			return new ResetCarToResetPointAction(delay, repeat, resetPointID, (Simulator)sim);
			
		} catch (Exception e) {
	
			reportError("resetCar", parameter);
			return null;
		}
	}
	
	
	
	@Action(
			name = "playSound",
			layer = Layer.SCENE,
			description = "Plays a sound file specified in the scene layer",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="soundID", type="String", defaultValue="soundEffect01", 
								description="ID of sound file to play")
					}
		)
	public TriggerAction playSound(Simulator sim, float delay, int repeat, Properties parameterList)
	{	
		String parameter = "";
		
		try {
			
			// extract name of local danger warning
			parameter = "soundID";
			String soundID = parameterList.getProperty(parameter);
			if(soundID == null)
				throw new Exception();

			// create PlaySoundAction
			return new PlaySoundAction(delay, repeat, soundID);
			
		} catch (Exception e) {
			
			reportError("playSound", parameter);
			return null;
		}
	}

	
	
	@Action(
			name = "startReactionMeasurement",
			layer = Layer.INTERACTION,
			description = "Starts the reaction measurement clock",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {}
		)
	public TriggerAction startReactionMeasurement(Simulator sim, float delay, int repeat, Properties parameterList)
	{	
		// create StartReactionMeasurementTriggerAction
		return new StartReactionMeasurementTriggerAction(delay, repeat, (Simulator)sim);
	}
	
	
	@Action(
			name = "setupReactionTimer",
			layer = Layer.INTERACTION,
			description = "Sets up a reaction timer (Deprecated)",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="reactionGroup", type="String", defaultValue="timer1", 
								description="ID of the timer for identification in output file"),
					 @Parameter(name="correctReaction", type="String", defaultValue="C", 
								description="list of keys triggering the correct reaction"),
					 @Parameter(name="failureReaction", type="String", defaultValue="F", 
								description="list of keys triggering the failure reaction")
					}
		)
	public TriggerAction setupReactionTimer(Simulator sim, float delay, int repeat, Properties parameterList)
	{	
		String parameter = "";
		
		try {
			
			// extract ID of timer
			parameter = "timerID";
			String timerID = parameterList.getProperty(parameter);
			if(timerID == null)
				timerID = "timer1";
			
			// extract reaction group
			parameter = "reactionGroup";
			String reactionGroup = parameterList.getProperty(parameter);
			if(reactionGroup == null)
				throw new Exception();
			
			// extract list of keys triggering the correct reaction
			parameter = "correctReaction";
			String correctReaction = parameterList.getProperty(parameter);
			if(correctReaction == null)
				throw new Exception();
			
			// extract list of keys triggering the failure reaction
			parameter = "failureReaction";
			String failureReaction = parameterList.getProperty(parameter);
			if(failureReaction == null)
				throw new Exception();
			
			// extract optional comment
			parameter = "comment";
			String comment = parameterList.getProperty(parameter);
			if(comment == null)
				comment = "";
	
			// create SetupKeyReactionTimerTriggerAction
			return new SetupKeyReactionTimerTriggerAction(delay, repeat, timerID, reactionGroup, correctReaction, 
					failureReaction, comment, (Simulator)sim);
			
		} catch (Exception e) {
			
			reportError("setupReactionTimer", parameter);
			return null;
		}
	}
	
	
	@Action(
			name = "setupKeyReactionTimer",
			layer = Layer.INTERACTION,
			description = "Sets up a key reaction timer",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="reactionGroup", type="String", defaultValue="timer1", 
								description="ID of the timer for identification in output file"),
					 @Parameter(name="correctReaction", type="String", defaultValue="C", 
								description="list of keys triggering the correct reaction"),
					 @Parameter(name="failureReaction", type="String", defaultValue="F", 
								description="list of keys triggering the failure reaction")
					}
		)
	public TriggerAction setupKeyReactionTimer(Simulator sim, float delay, int repeat, Properties parameterList)
	{	
		String parameter = "";
		
		try {
			
			// extract ID of timer
			parameter = "timerID";
			String timerID = parameterList.getProperty(parameter);
			if(timerID == null)
				timerID = "timer1";
			
			// extract reaction group
			parameter = "reactionGroup";
			String reactionGroup = parameterList.getProperty(parameter);
			if(reactionGroup == null)
				throw new Exception();
			
			// extract list of keys triggering the correct reaction
			parameter = "correctReaction";
			String correctReaction = parameterList.getProperty(parameter);
			if(correctReaction == null)
				throw new Exception();
			
			// extract list of keys triggering the failure reaction
			parameter = "failureReaction";
			String failureReaction = parameterList.getProperty(parameter);
			if(failureReaction == null)
				throw new Exception();
			
			// extract optional comment
			parameter = "comment";
			String comment = parameterList.getProperty(parameter);
			if(comment == null)
				comment = "";
	
			// create SetupKeyReactionTimerTriggerAction
			return new SetupKeyReactionTimerTriggerAction(delay, repeat, timerID, reactionGroup, correctReaction, 
					failureReaction, comment, (Simulator)sim);
			
		} catch (Exception e) {
			
			reportError("setupKeyReactionTimer", parameter);
			return null;
		}
	}
	
	
	@Action(
			name = "setupLaneChangeReactionTimer",
			layer = Layer.INTERACTION,
			description = "Sets up a lane change reaction timer",
			defaultDelay = 0,
			defaultRepeat = 0,
			param = {@Parameter(name="timerID", type="String", defaultValue="timer1", 
								description="ID of the timer for scheduling the measurement"),
					 @Parameter(name="congruenceClass", type="String", defaultValue="groupRed", 
								description="Groups similar measurements to same color in output visualization"),
					 @Parameter(name="startLane", type="String", defaultValue="centerLane", 
								description="Lane where lane change must start from"),
					 @Parameter(name="targetLane", type="String", defaultValue="leftLane", 
								description="Lane where lane change must end"),
					 @Parameter(name="minSteeringAngle", type="Float", defaultValue="0", 
								description="Minimal steering angle that has to be overcome"),
					 @Parameter(name="taskCompletionAfterTime", type="Float", defaultValue="0", 
							 	description="Task must be completed after x milliseconds (0 = no limit)"),
					 @Parameter(name="taskCompletionAfterDistance", type="Float", defaultValue="0", 
								description="Task must be completed after x meters (0 = no limit)"),
					 @Parameter(name="allowBrake", type="Boolean", defaultValue="true", 
								description="Driver may brake while changing lanes? (If false, failure reaction will be reported)"),
					 @Parameter(name="holdLaneFor", type="Float", defaultValue="2000", 
							 	description="Number of milliseconds the target lane must be kept"),
					 @Parameter(name="failSound", type="String", defaultValue="failSound01", 
								description="Sound file that will be played after failed/missed lane change (optional)"),
					 @Parameter(name="successSound", type="String", defaultValue="successSound01", 
								description="Sound file that will be played after successful lane change (optional)"),
					 @Parameter(name="comment", type="String", defaultValue="", 
								description="optional comment")
					}
		)
	public TriggerAction setupLaneChangeReactionTimer(Simulator sim, float delay, int repeat, Properties parameterList)
	{	
		String parameter = "";
		
		try {
			
			// extract ID of timer
			parameter = "timerID";
			String timerID = parameterList.getProperty(parameter);
			if(timerID == null)
				timerID = "timer1";
			
			// extract reaction group
			parameter = "congruenceClass";
			String reactionGroupID = parameterList.getProperty(parameter);
			if(reactionGroupID == null)
				throw new Exception();
			
			// extract lane where lane change must start from
			parameter = "startLane";
			String startLane = parameterList.getProperty(parameter);
			if(startLane == null)
				throw new Exception();
			
			// extract lane where lane change must end
			parameter = "targetLane";
			String targetLane = parameterList.getProperty(parameter);
			if(targetLane == null)
				throw new Exception();
			
			// extract minimal steering angle that has to be overcome
			parameter = "minSteeringAngle";
			Float minSteeringAngle = Float.parseFloat(parameterList.getProperty(parameter));
			if(minSteeringAngle == null)
				minSteeringAngle = 0f;
			
			// task must be completed after x milliseconds (0 = no limit)
			parameter = "taskCompletionAfterTime";
			Float taskCompletionAfterTime = Float.parseFloat(parameterList.getProperty(parameter));
			if(taskCompletionAfterTime == null)
				taskCompletionAfterTime = 0f;
			
			// task must be completed after x meters (0 = no limit)
			parameter = "taskCompletionAfterDistance";
			Float taskCompletionAfterDistance = Float.parseFloat(parameterList.getProperty(parameter));
			if(taskCompletionAfterDistance == null)
				taskCompletionAfterDistance = 0f;
			
			// driver may brake while changing lanes? (if false, failure reaction will be reported) 
			parameter = "allowBrake";
			Boolean allowBrake = Boolean.parseBoolean(parameterList.getProperty(parameter));
			if(allowBrake == null)
				allowBrake = true;
			
			// number of milliseconds the target lane must be kept
			parameter = "holdLaneFor";
			Float holdLaneFor = Float.parseFloat(parameterList.getProperty(parameter));
			if(holdLaneFor == null)
				holdLaneFor = 0f;
			
			// sound file that will be played after failed/missed lane change (optional)
			parameter = "failSound";
			String failSound = parameterList.getProperty(parameter);
			
			// sound file that will be played after successful lane change (optional)
			parameter = "successSound";
			String successSound = parameterList.getProperty(parameter);
			
			// extract optional comment
			parameter = "comment";
			String comment = parameterList.getProperty(parameter);
			if(comment == null)
				comment = "";
	
			// create SetupLaneChangeReactionTimerTriggerAction
			return new SetupLaneChangeReactionTimerTriggerAction(delay, repeat, timerID, reactionGroupID, startLane, 
					targetLane, minSteeringAngle, taskCompletionAfterTime, taskCompletionAfterDistance, allowBrake, 
					holdLaneFor, failSound, successSound, comment, (Simulator)sim);
			
		} catch (Exception e) {
			
			reportError("setupLaneChangeReactionTimer", parameter);
			return null;
		}
	}

	private void reportError(String methodName, String parameter)
	{
		System.err.println("Error in action \"" + methodName + "\": check parameter \"" + parameter + "\"");
	}
	
	
	private String setDefault(String methodName, String parameter, String defaultValue)
	{
		System.err.println("Warning in action \"" + methodName + "\": default \"" + defaultValue 
				+ "\" set to parameter \"" + parameter + "\"");
		return defaultValue;
	}
	
}