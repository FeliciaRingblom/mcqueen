package eu.opends.trigger;

import eu.opends.basics.SimulationBasics;
import eu.opends.main.Simulator;
import eu.opends.niftyGui.MyInstructionsGUIController;
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
		sim.getMyInstructions().setScreen(this.screenNumber);
	}

}
