package eu.opends.trigger;

import eu.opends.main.Simulator;
/**
 * @author Jessica Larsson, Felicia Ringblom
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
