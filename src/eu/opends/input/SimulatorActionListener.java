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

import com.jme3.input.controls.ActionListener;

import eu.opends.car.Car;
import eu.opends.main.Simulator;
import eu.opends.trigger.ManipulatePictureTriggerAction;

/**
 * 
 * @author Rafael Math
 */
public class SimulatorActionListener implements ActionListener
{
	private float steeringValue = 0;
	private float accelerationValue = 0;
	private Simulator sim;
	private Car car;
	private boolean isWireFrame = false;
	
	
    public SimulatorActionListener(Simulator sim) 
    {
    	this.sim = sim;
    	this.car = sim.getCar();
	}


	public void onAction(String binding, boolean value, float tpf) 
	{
		if (binding.equals(KeyMapping.STEER_LEFT.getID())) 
		{
			if (value) {
				steeringValue += .3f;
			} else {
				steeringValue += -.3f;
			}
			
			sim.getSteeringTask().setSteeringIntensity(-3*steeringValue);
			car.steer(steeringValue);
		} 
		
		else if (binding.equals(KeyMapping.STEER_RIGHT.getID())) 
		{
			if (value) {
				steeringValue += -.3f;
			} else {
				steeringValue += .3f;
			}
			
			
			sim.getSteeringTask().setSteeringIntensity(-3*steeringValue);
			car.steer(steeringValue);
		}
		
		else if (binding.equals(KeyMapping.REACT_SQUARE.getID())) 
		{
			if (value) 
			{
				ManipulatePictureTriggerAction manipulatePicture =  new ManipulatePictureTriggerAction(sim, 0, 4, "leftArrow_toRight", false);
				manipulatePicture.execute();
				ManipulatePictureTriggerAction manipulatePicture2 =  new ManipulatePictureTriggerAction(sim, 0, 4, "leftArrow_toLeft", false);
				manipulatePicture2.execute();

			}
			else
			{
				
			}
		}
		
		else if (binding.equals(KeyMapping.REACT_CIRCLE.getID())) 
		{
			if (value) 
			{
				ManipulatePictureTriggerAction manipulatePicture =  new ManipulatePictureTriggerAction(sim, 0, 4, "rightArrow_toRight", false);
				manipulatePicture.execute();
				ManipulatePictureTriggerAction manipulatePicture2 =  new ManipulatePictureTriggerAction(sim, 0, 4, "rightArrow_toLeft", false);
				manipulatePicture2.execute();
			}
			else
			{
				
			}
		}
		
		else if (binding.equals(KeyMapping.TOGGLE_PAUSE.getID())) 
		{
			if (value)
				sim.setPause(!sim.isPause());
		}
		
		else if (binding.equals(KeyMapping.RESET_CAR.getID())) 
		{
			if (value)
				car.setToNextResetPosition();
		}
		
		else if (binding.equals(KeyMapping.PRINT_POS.getID())) 
		{
			if (value)
			{
				
				System.out.println(car.getPosition());		
			}
		}
		
		else if (binding.equals(KeyMapping.SHUTDOWN.getID())) 
		{
			if (value)
				sim.getShutDownGUI().toggleDialog();
		}
	}

}
