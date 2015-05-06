package eu.opends.niftyGui;

import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.input.InputManager;
import com.jme3.niftygui.NiftyJmeDisplay;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.RadioButtonGroupStateChangedEvent;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.elements.Element;
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
	private String speed = "low";
	
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
	}
	

	@Override
	public void onEndScreen() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStartScreen() {
		System.out.println("i onstartscreen");
		String analyzerFile = "analyzerData/test/carData.txt";
		sim.calculateCarData(analyzerFile);	
		// TODO Auto-generated method stub
		
	}
	
	public void clickChangeSound() {
		//System.out.println("clicked sound");
		//screen.findElementByName("imgSound").getRenderer().setImage();
	}

	public void startTest(){
		String driverName = getTextFromTextfield("driversNameTextfield");
		nifty.exit();
		nifty.gotoScreen("instruction_1"); 
	}
	
	public void gotoInstructions2(){
		nifty.exit();
		nifty.gotoScreen("instruction_2"); 
	}
	
	public void gotoInstructions3(){
		nifty.exit();
		nifty.gotoScreen("instruction_3"); 
	}
		
	public void gotoInstructions4(){
		nifty.exit();
		nifty.gotoScreen("instruction_4"); 
	}
	
	public void gotoInstructions5(){
		nifty.exit();
		nifty.gotoScreen("instruction_5"); 
	}

	public void gotoInstructions6(){
		nifty.exit();
		nifty.gotoScreen("instruction_6"); 
	}
	
	public void gotoEndScreen(){
		nifty.exit();
		nifty.gotoScreen("end"); 
	}	
	
	public void startIntroStraight(){
		String driverName = getTextFromTextfield("driversNameTextfield");
		nifty.exit();
		
		String drivingTask = "assets/DrivingTasks/Projects/IntroStraight/introStraight.xml";
		//sim.closeDrivingTaskSelectionGUI();
		sim.simpleInitDrivingTask(drivingTask,driverName, speed);	
	}
	
	public void startIntroStimuli(){	
		String driverName = getTextFromTextfield("input_id");
		nifty.exit();	
		
		String drivingTask = "assets/DrivingTasks/Projects/IntroStimuli/introStimuli.xml";
		sim.closeDrivingTaskSelectionGUI();
		sim.simpleInitDrivingTask(drivingTask,driverName, speed);
	}
	
	public void startMainTest(){
		nifty.exit();		
		String input_id = getTextFromTextfield("input_id");
		String drivingTask = "assets/DrivingTasks/Projects/Countryside/countryside.xml";
		sim.simpleInitDrivingTask(drivingTask,input_id, speed);
	}
	
	public void gotoResult(){
		nifty.gotoScreen("result"); 
	}
	
	  @NiftyEventSubscriber(id="RadioGroup-speed")
	  public void onRadioGroupSpeedChanged(final String id, final RadioButtonGroupStateChangedEvent event) {
	    speed = event.getSelectedId();
		// System.out.println("RadioButton [" + event.getSelectedId() + "] is now selected. The old selection was [" + event.getPreviousSelectedId() + "]");
	  }

	
	public void setScreen(int screenNumber) {
//		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
//		Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);
//		System.out.println("thread size: " + threadSet.size());
//		for(int i = 0; i<threadArray.length; i++) {
//			System.out.println("ThreadArray " + threadArray[i]);
//		}
		//sim.removeAllDrivingTaskElements();
		sim.getGuiNode().detachAllChildren();
		String next = "instruction_" + screenNumber;
		nifty.gotoScreen(next);
	}
	
    public Element getElementByName(String element)
    {
    	return nifty.getCurrentScreen().findElementByName(element);
    }
    
    
    public String getTextFromTextfield(String element)
    {
    	return nifty.getCurrentScreen().findNiftyControl(element, TextField.class).getRealText();
    }
}
