package eu.opends.trigger;

import eu.opends.main.Simulator;import eu.opends.main.Simulator;
import eu.opends.niftyGui.StartScreenGUIController;
/**
 * @author Jessica Larsson, Felicia Ringblom, 2015
 */
public class ReturnToInstruction extends TriggerAction {
	
	
	private Simulator sim;
	private int screenNumber;
	
	
	public ReturnToInstruction(Simulator sim, float delay, int maxRepeat, int screenNumber)
	{
		super(delay, maxRepeat);
		this.sim = sim;
		this.screenNumber = screenNumber;
	}

	@Override
	protected void execute() {
		sim.getInstructionsGUI().setScreen(this.screenNumber);
	}

}
