package eu.opends.trigger;

import eu.opends.analyzer.DataWriter;
import eu.opends.main.Simulator;
import eu.opends.tools.PanelCenter;

public class ShowStimuliTriggerAction extends TriggerAction {
	
	
private Simulator sim;
	
	public ShowStimuliTriggerAction(float delay, int maxRepeat, Simulator sim)
	{
		super(delay, maxRepeat);
		this.sim = sim;
	}
	
	@Override
	protected void execute() 
	{
		if(!isExceeded())
		{
			System.out.println("i showStimulitriggeraction");
		}
	}

}
