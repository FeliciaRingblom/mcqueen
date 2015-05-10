package eu.opends.niftyGui;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import eu.opends.main.Simulator;

public class InstructionsGUIController implements ScreenController 
{
	
	private Simulator sim;
	
	
	public InstructionsGUIController(Simulator sim) 
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
	
	public void clickChangeSound() {
		//System.out.println("clicked sound");
		//screen.findElementByName("imgSound").getRenderer().setImage();
	}
	
	public void gotoResult(){
		sim.getNifty().gotoScreen("result"); 
	}
	
	public void gotoInstructions2(){
		System.out.println("next next next");
		sim.getNifty().exit();
		sim.getNifty().gotoScreen("instruction_2"); 
	}
	
	public void gotoInstructions3(){
		sim.getNifty().exit();
		sim.getNifty().gotoScreen("instruction_3"); 
	}
		
	public void gotoInstructions4(){
		sim.getNifty().exit();
		sim.getNifty().gotoScreen("instruction_4"); 
	}
	
	public void gotoInstructions5(){
		sim.getNifty().exit();
		sim.getNifty().gotoScreen("instruction_5"); 
	}
	
	public void gotoInstructions6(){
		sim.getNifty().exit();
		sim.getNifty().gotoScreen("instruction_6"); 
	}
	
	public void gotoEndScreen(){
		sim.getNifty().exit();
		sim.getNifty().gotoScreen("end"); 
	}
	
	public void startIntroStraight(){		
		sim.getNifty().exit();
		String drivingTask = "assets/DrivingTasks/Projects/IntroStraight/introStraight.xml";
		sim.simpleInitDrivingTask(drivingTask);	
	}
	
	public void startIntroStimuli(){		
		sim.getNifty().exit();
		String drivingTask = "assets/DrivingTasks/Projects/IntroStimuli/introStimuli.xml";
		sim.simpleInitDrivingTask(drivingTask);
	}
	
	public void startMainTest(){					
		sim.getNifty().exit();
		String drivingTask = "assets/DrivingTasks/Projects/Countryside/countryside.xml";	
		sim.simpleInitDrivingTask(drivingTask);
	}
}
