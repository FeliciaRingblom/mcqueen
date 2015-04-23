package eu.opends.niftyGui;

import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.input.InputManager;
import com.jme3.niftygui.NiftyJmeDisplay;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.render.PanelRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.SizeValue;
import eu.opends.main.Simulator;

/**
 * @author Jessica Larsson, Felicia Ringblom, 2015
 */

public class MyInstructionsGUIController implements ScreenController {
	
	private Nifty nifty;
	private Simulator sim;
	private Screen screen;
	private InputManager inputManager;
	
	public MyInstructionsGUIController(Simulator sim, Nifty nifty)
	{
		this.sim = sim;
		this.nifty = nifty;
		this.inputManager = sim.getInputManager();
		
		AssetManager assetManager = sim.getAssetManager();
		assetManager.registerLocator("assets", FileLocator.class);
		
	}

	@Override
	public void bind(Nifty arg0, Screen arg1) {
	     this.screen = arg1;
//			if(sim.getSettings().getWidth() >= 2400)
//			{
//				SizeValue sv = new SizeValue("20%");
//				nifty.getCurrentScreen().findElementByName("menuPanel").setConstraintWidth(sv);
//			}
	
		
	}
	

	@Override
	public void onEndScreen() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStartScreen() {
		// TODO Auto-generated method stub
		
	}
	
	public void clickChangeSound() {
		//System.out.println("clicked sound");
		//screen.findElementByName("imgSound").getRenderer().setImage();
	}

	public void startTest(){
		nifty.gotoScreen("instruction_1"); 
	}
	
	public void gotoInstructions2(){
		nifty.gotoScreen("instruction_2"); 
	}
	
	public void gotoInstructions3(){
		nifty.gotoScreen("instruction_3"); 
	}
		
	public void gotoInstructions4(){
		nifty.gotoScreen("instruction_4"); 
	}
	
	public void gotoInstructions5(){
		nifty.gotoScreen("instruction_5"); 
	}

	public void gotoInstructions6(){
		nifty.gotoScreen("instruction_6"); 
	}
	
	public void gotoEndScreen(){
		nifty.gotoScreen("end"); 
	}	
	
	public void startIntroStraight(){
		//hide all elements on screen and start simulation of introductionStraight		
		nifty.exit();
		
		String driverName = "Lightning McQueen";
		String drivingTask = "assets/DrivingTasks/Projects/IntroStraight/introStraight.xml";
		sim.closeDrivingTaskSelectionGUI();
		sim.simpleInitDrivingTask(drivingTask,driverName);	
	}
	
	public void startIntroStimuli(){
		//String analyzerFile = "analyzerData/2015_04_16-09_39_07/carData.txt";
		//sim.calculateCarData(analyzerFile);
		
		//hide all elements on screen and start simulation of introductionStraight		
		nifty.exit();	
		
		String driverName = "Lightning McQueen";
		String drivingTask = "assets/DrivingTasks/Projects/IntroStimuli/introStimuli.xml";
		sim.closeDrivingTaskSelectionGUI();
		sim.simpleInitDrivingTask(drivingTask,driverName);
	}
	
	public void startMainTest(){
		//hide all elements on screen and start simulation of introductionStraight
		nifty.exit();
		
		String driverName = "Lightning McQueen";
		String drivingTask = "assets/DrivingTasks/Projects/Countryside/countryside.xml";
		sim.simpleInitDrivingTask(drivingTask,driverName);
	}
	
	public void gotoResult(){
		nifty.gotoScreen("result"); 
	}

	
	public void setScreen(int screenNumber) {
//		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
//		Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);
//		System.out.println("thread size: " + threadSet.size());
//		for(int i = 0; i<threadArray.length; i++) {
//			System.out.println("ThreadArray " + threadArray[i]);
//		}
		sim.removeAllDrivingTaskElements();
		String next = "instruction_" + screenNumber;
		nifty.gotoScreen(next);
		//inputManager.setCursorVisible(true);
	}
}
