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
	private String pictureName;
	private Picture stimuliPicture;
	
	

	public ShowStimuliTriggerAction(SimulationBasics sim, float delay, int maxRepeat, String pictureString) 
	{
		super(delay, maxRepeat);
		this.sim = sim;
		this.pictureName = pictureString;
		this.stimuliPicture = createStimuliPicture();
		this.sim.getGuiNode().attachChild(this.stimuliPicture);
		this.stimuliPicture.setCullHint(CullHint.Always);
	}
	
	private Picture createStimuliPicture(){
		Picture img = new Picture(this.pictureName);
        img.setImage(sim.getAssetManager(), "Textures/Misc/" + pictureName, true);
        
        img.setWidth(100);
        img.setHeight(100);
        
        img.setPosition(100, 100);
        
        return img;
	}

	
	@Override
	protected void execute() 
	{
		if(!isExceeded())
		{
			System.out.println("Yeyeyeyey: " + pictureName);
			stimuliPicture.setCullHint(CullHint.Dynamic);
		}
	}

}
