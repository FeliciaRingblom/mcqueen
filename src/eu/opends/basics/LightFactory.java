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

package eu.opends.basics;


import java.util.List;

import com.jme3.light.Light;

/**
 * 
 * @author Rafael Math
 */
public class LightFactory 
{
	private SimulationBasics sim;
	
	
	public LightFactory(SimulationBasics sim)
	{
		this.sim = sim;
	}
	
	
	public void initLight() 
	{
		List<Light> lightList = SimulationBasics.getDrivingTask().getSceneLoader().getLightList();
		
		for(Light light : lightList)
			sim.getSceneNode().addLight(light);     
	}

}
