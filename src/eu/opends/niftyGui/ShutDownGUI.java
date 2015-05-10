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

import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.scene.Spatial.CullHint;

import de.lessvoid.nifty.Nifty;
import eu.opends.main.Simulator;

/**
 * 
 * @author Rafael Math
 */
public class ShutDownGUI 
{
	private Nifty nifty;
	private Simulator sim;
	private boolean shutDownDialogHidden = true;
	private boolean initiallyPaused = false;
	private InputManager inputManager;
	private FlyByCamera flyCam;

	
	public ShutDownGUI(Simulator sim) 
	{
		this.sim = sim;
		this.inputManager = sim.getInputManager();
		this.flyCam = sim.getFlyByCamera();
		this.nifty = sim.getNifty();
	}
	
	public void toggleDialog() 
	{
		if (shutDownDialogHidden)
			showDialog();
		else 
			hideDialog();
	}


	public void showDialog() 
	{
		if(shutDownDialogHidden)
		{
			initiallyPaused = sim.isPause();
			sim.setPause(true);
			sim.getGuiNode().setCullHint(CullHint.Always);
			init();
			shutDownDialogHidden = false;
		}
	}

	
	public void hideDialog() 
	{
		if(!shutDownDialogHidden)
		{
			
			close();
			shutDownDialogHidden = true;
			sim.getGuiNode().setCullHint(CullHint.Inherit);
			sim.setPause(initiallyPaused);
		}
	}
	
	
	private void init() 
	{
		String xmlPath = "Interface/ShutDownGUI.xml";
		nifty.fromXml(xmlPath, "start",	new ShutDownGUIController(sim));
		flyCam.setEnabled(false);
	}
	

	private void close() 
	{
		nifty.exit();
		inputManager.setCursorVisible(false);
		flyCam.setEnabled(true);
	}
}
