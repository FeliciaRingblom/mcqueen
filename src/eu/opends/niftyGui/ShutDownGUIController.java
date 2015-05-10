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

package eu.opends.niftyGui;

import de.lessvoid.nifty.Nifty;
//import de.lessvoid.nifty.controls.button.ButtonControl;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import eu.opends.main.Simulator;

/**
 * 
 * @author Rafael Math
 */
public class ShutDownGUIController implements ScreenController 
{
	private Simulator sim;
	
	
	public ShutDownGUIController(Simulator sim) 
	{
		this.sim = sim;
	}

	
	@Override
	public void bind(Nifty arg0, Screen arg1) 
	{
		
	}

	
	@Override
	public void onEndScreen() 
	{

	}


	@Override
	public void onStartScreen() 
	{
		
	}
	
	
	public void clickCancelButton()
	{
		sim.getShutDownGUI().toggleDialog();
	}
	
	public void clickBackToMenuButton(){
		sim.getShutDownGUI().hideDialog();
		sim.getGuiNode().detachChildNamed("hood");
		sim.simpleInitApp();
	}
	
	
	public void clickCloseButton()
	{
		sim.stop();
	}
}
