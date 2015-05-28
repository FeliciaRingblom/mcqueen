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

package eu.opends.trigger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.jme3.collision.CollisionResults;
//import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

//import eu.opends.audio.AudioCenter;
//import eu.opends.basics.MapObject;
import eu.opends.main.Simulator;import eu.opends.car.Car;
import eu.opends.main.Simulator;
import eu.opends.tools.Util;


/**
 * This class is responsible for anything related to triggers on the streets.
 * 
 * @author Saied Tehrani, Rafael Math
 */
public class TriggerCenter 
{
	private CollisionResults resultCollision;

	private Simulator sim;

	private String triggerName;

	private static Map<String,Spatial> trafficLightTriggerList, trafficLightPhaseTriggerList;

	private LinkedList<Spatial> roadObjectsTriggerList;
	public static ArrayList<String> triggerReportList = new ArrayList<String>(5);

	
	public static void addToTrafficLightTriggerList(String trafficLightName, Spatial trafficLightTriggerObject)
	{
		trafficLightTriggerList.put(trafficLightName, trafficLightTriggerObject);
	}
	
	
	public static void addToTrafficLightPhaseTriggerList(String trafficLightName, Spatial trafficLightTriggerObject)
	{
		trafficLightPhaseTriggerList.put(trafficLightName, trafficLightTriggerObject);
	}
	
	
	public TriggerCenter(Simulator sim) 
	{
		this.sim = sim;

		trafficLightTriggerList = new HashMap<String,Spatial>();
		trafficLightPhaseTriggerList = new HashMap<String,Spatial>();
		roadObjectsTriggerList = new LinkedList<Spatial>();
	}

	
	public void setup() 
	{
		resultCollision = new CollisionResults();

		Spatial tempSpatial;

		String tempSpatialName;

		List<Spatial> tempList = sim.getTriggerNode().getChildren();
		
		for (Iterator<Spatial> it = tempList.iterator(); it.hasNext();) 
		{
			tempSpatial = it.next();

			tempSpatialName = tempSpatial.getName();
			
			if (Simulator.getTriggerActionListMap().containsKey(tempSpatialName))
			{
				roadObjectsTriggerList.add(tempSpatial);
			}

		}
	}

	
	public void doTriggerChecks() 
	{
		handleRoadObjectsCollision(roadObjectsTriggerList);
	}

	
	/**
	 * This method handles a collision of the car with a road object trigger. This can be:
	 * SpeedLimitTrigger, CautionSignTrigger or BlindTrigger. A collision will be forwarded 
	 * to the HMI center.
	 * 
	 * @param triggerList
	 * 			list of all road object triggers in order to monitor approximation 
	 * 			to such an object
	 */
	private void handleRoadObjectsCollision(LinkedList<Spatial> triggerList)
	{
		Car car = sim.getCar();
		
		for (Spatial trigger : triggerList) 
		{	
			// TODO: caution! trigger center may be farther away than 10 meters when hitting
			if(trigger.getWorldTranslation().distance(car.getCarNode().getWorldTranslation()) < 10)
			{
				resultCollision.clear();
				String triggerName = trigger.getName();
					
				// calculate collision of the car with a road object trigger

				Spatial triggerObject = sim.getTriggerNode().getChild(triggerName);
				car.getCarNode().collideWith(triggerObject.getWorldBound(), resultCollision);
				
				// if car has collided with a trigger --> report trigger to HMI Center
				if(resultCollision.size() > 0)
				{
					if(Simulator.getTriggerActionListMap().containsKey(triggerName))
						TriggerCenter.performTriggerAction(triggerName, car);
				}
			}
		}
	}


	Map<String,Integer> collisionMap = new HashMap<String,Integer>();
	float suspensionForce[] = {0,0,0,0};
	

	/**
	 * Reports the collision of the car with a free hand placed trigger 
	 * and performs the specified action (e.g. send a text to the screen)
	 * 
	 * @param triggerID
	 * 			name of the trigger (needed to look up action)
	 * 
	 * @param car
	 * 			user-controlled car of simulator 
	 */
	public static void performTriggerAction(String triggerID, Car car) 
	{
		if(!triggerReportList.contains(triggerID))
		{
			System.err.println("Trigger hit: " + triggerID);
			// add trigger to report list
			triggerReportList.add(triggerID);
			
			// remove trigger from report list after 2 seconds
			int seconds = 2;
		
			List<TriggerAction> triggerActionList = Simulator.getTriggerActionListMap().get(triggerID);
			for(TriggerAction triggerAction : triggerActionList)
			{
				triggerAction.performAction();
				
				// extend two seconds by time of pause duration, if pause trigger was contained in list
				if(triggerAction instanceof PauseTriggerAction)
					seconds += ((PauseTriggerAction)triggerAction).getDuration();
			}
			
			RemoveFromReportListThread removeThread = new RemoveFromReportListThread(triggerID, seconds);
			removeThread.start();
		}
	}
	
	
	/**
	 * Every time a trigger is reported it will be added to trigger report 
	 * list in order to avoid multiple instances of the event (e.g. if car 
	 * still hits the trigger after 10 milliseconds). This method removes 
	 * the given trigger from this list again.
	 *  
	 * @param objectID
	 * 			ID of the trigger to be removed from the report list
	 */
	public static void removeTriggerReport(String objectID)
	{
		if(!triggerReportList.remove(objectID))
			System.err.println("Could not remove '" + objectID + "' from trigger report list!");
	}

}
