package eu.opends.trigger;

import java.util.TreeMap;
import java.util.Map.Entry;

import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.ui.Picture;

import eu.opends.basics.SimulationBasics;
import eu.opends.tools.PanelCenter;


/**
 * This class represents a ManipulatePicture trigger action. Whenever a collision
 * with a related trigger was detected, the given picture will be manipulated in 
 * the specified way.
 * 
 * @author Rafael Math
 */
public class ShowStimuliTriggerAction extends TriggerAction 
{
	private SimulationBasics sim;
	
	

	public ShowStimuliTriggerAction(SimulationBasics sim, float delay, int maxRepeat) 
	{
		super(delay, maxRepeat);
		this.sim = sim;
	}

	
	@Override
	protected void execute() 
	{
		if(!isExceeded())
		{
			System.out.println("Yeyeyeyey");
		}
	}

}
