package eu.opends.niftyGui;

import com.jme3.input.FlyByCamera;

import de.lessvoid.nifty.Nifty;
import eu.opends.main.Simulator;

/**
 * 
 * @author Jessica Larsson, Felicia Ringblom
 */
public class InstructionsGUI {
	
	private Nifty nifty;
	private Simulator sim;
	private FlyByCamera flyCam;

	
	public InstructionsGUI(Simulator sim) 
	{
		this.sim = sim;
		this.flyCam = sim.getFlyByCamera();
		this.nifty = sim.getNifty();
	}
	
	
	public void setScreen(int screenNumber) {
		sim.closeDrivngTask();
        if(sim.getSceneNode() != null){
        	sim.getViewPort().detachScene(sim.getSceneNode());
        	sim.getGuiNode().detachAllChildren();
        	sim.getRootNode().attachChild(sim.getSceneNode());
        }
		String screenID = "instruction_" + screenNumber;
		String xmlPath = "Interface/InstructionsGUI.xml";
		nifty.fromXml(xmlPath, screenID, new InstructionsGUIController(sim));
		flyCam.setEnabled(false);
	}
}
