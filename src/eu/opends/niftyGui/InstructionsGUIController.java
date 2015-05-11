package eu.opends.niftyGui;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import eu.opends.main.Simulator;

public class InstructionsGUIController implements ScreenController 
{
	
	private Simulator sim;
	private boolean soundIsOn = false;
	
	
	
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
	
	public void toggleSound() {
		if(soundIsOn){
			System.out.println("sound of");
			soundIsOn = false;		
		}else{
			System.out.println("soun on");
			soundIsOn = true;
		}
		//screen.findElementByName("imgSound").getRenderer().setImage();
	}
	
	public void gotoResult(){
		sim.getNifty().gotoScreen("result"); 
	}
	
	public void gotoInstructions2(){
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
	
	public void gotoInstructions7(){
		sim.getNifty().exit();
		sim.getNifty().gotoScreen("instruction_7"); 
	}
	
	public void gotoInstructions8(){
		sim.getNifty().exit();
		sim.getNifty().gotoScreen("instruction_8"); 
	}
	
	public void gotoEndScreen(){
		sim.getNifty().exit();
		sim.getNifty().gotoScreen("end"); 
	}
	
	public void startIntroStraight(){		
		sim.getNifty().exit();
		String drivingTask = "assets/DrivingTasks/Projects/IntroStraight/introStraight.xml";
		sim.initDrivingTask(drivingTask);	
	}
	
	public void startTest1(){		
		sim.getNifty().exit();
		String drivingTask = "assets/DrivingTasks/Projects/Test1/test1.xml";
		sim.setTestNr(1);
		sim.initDrivingTask(drivingTask);
	}
	
	public void startTest2(){					
		sim.getNifty().exit();
		String drivingTask = "assets/DrivingTasks/Projects/Test2/test2.xml";
		sim.setTestNr(2);
		sim.initDrivingTask(drivingTask);
	}
	
	public void startTest3(){					
		sim.getNifty().exit();
		String drivingTask = "assets/DrivingTasks/Projects/Test3/test3.xml";	
		sim.setTestNr(3);
		sim.initDrivingTask(drivingTask);
	}
}
