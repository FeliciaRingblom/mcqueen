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

package eu.opends.input;

import java.util.ArrayList;

import eu.opends.main.Simulator;
/**
 * 
 * @author Rafael Math
 */
public class KeyMapping
{
	public static KeyMapping SHUTDOWN = new KeyMapping("shutdown", "exit simulator", new String[] {"KEY_ESCAPE"});
	public static KeyMapping STEER_LEFT = new KeyMapping("steer_left", "steer left", new String[] {"KEY_LEFT"});
	public static KeyMapping STEER_RIGHT = new KeyMapping("steer_right", "steer right", new String[] {"KEY_RIGHT"});	
	public static KeyMapping TOGGLE_PAUSE = new KeyMapping("toggle_pause", "pause/resume", new String[] {"KEY_P"});
	public static KeyMapping RESET_CAR = new KeyMapping("reset_car", "reset car", new String[] {"KEY_R"});
	public static KeyMapping REACT_ARROW_LEFT = new KeyMapping("react_arrow_left", "react to arrow pointing to the left", new String[] {"KEY_B"});
	public static KeyMapping REACT_ARROW_RIGHT = new KeyMapping("react_arrow_right", "react to arrow pointing to the right", new String[] {"KEY_N"});

	// help function that prints the cars current position to the console
	public static KeyMapping PRINT_POS = new KeyMapping("print_pos", "prints the car position", new String[] {"KEY_Q"});
	
	public static ArrayList<KeyMapping> getSimulatorActionKeyMappingList()
	{
		ArrayList<KeyMapping> keyMappingList = new ArrayList<KeyMapping>();
		
		// specify order of key mapping list (if available)
		keyMappingList.add(KeyMapping.SHUTDOWN);
		keyMappingList.add(KeyMapping.STEER_LEFT);
		keyMappingList.add(KeyMapping.STEER_RIGHT);
		keyMappingList.add(KeyMapping.TOGGLE_PAUSE);
		keyMappingList.add(KeyMapping.RESET_CAR);
		keyMappingList.add(KeyMapping.REACT_ARROW_LEFT);
		keyMappingList.add(KeyMapping.REACT_ARROW_RIGHT);
		keyMappingList.add(KeyMapping.PRINT_POS);

		
		Simulator.getDrivingTask().getSettingsLoader().lookUpKeyMappings(keyMappingList);
		
		return keyMappingList;
	}
	
	
	public static ArrayList<KeyMapping> getSimulatorAnalogKeyMappingList() 
	{
		ArrayList<KeyMapping> keyMappingList = new ArrayList<KeyMapping>();

		// specify order of key mapping list  (if available)
		// <no mapping yet>
		
		Simulator.getDrivingTask().getSettingsLoader().lookUpKeyMappings(keyMappingList);
		
		return keyMappingList;
	}
	
	
	public static ArrayList<KeyMapping> getDriveAnalyzerActionKeyMappingList() 
	{
		ArrayList<KeyMapping> keyMappingList = new ArrayList<KeyMapping>();

		// specify order of key mapping list  (if available)
		keyMappingList.add(KeyMapping.SHUTDOWN);
		
		Simulator.getDrivingTask().getSettingsLoader().lookUpKeyMappings(keyMappingList);
		
		return keyMappingList;
	}
	
		
	private String ID;
	private String description;
	private String[] defaultKeys;
	private String[] keys = null;
	
	public KeyMapping(String ID, String description, String[] defaultKeys)
	{
		this.ID = ID;
		this.description = description;
		this.defaultKeys = defaultKeys;
	}
		
	
	public String getID()
	{
		return ID;
	}
	
	
	public String toString()
	{
		return ID;
	}
	
	
	public String getDescription()
	{
		return description;
	}
	
	
	public String[] getDefaultKeys()
	{
		return defaultKeys;
	}
	
	
	public void setKeys(String[] keys)
	{
		this.keys = keys;
	}
	
	
	public String[] getKeys()
	{
		if(keys != null)
			return keys;
		else
			return defaultKeys;
	}

}
