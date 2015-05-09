package eu.opends.niftyGui;

import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.input.InputManager;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.RadioButtonGroupStateChangedEvent;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
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
	private String gender = "man";
	private String age, idNr, diagnosisNr;
	
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
	public void onEndScreen() {}

	@Override
	public void onStartScreen() {}
	
	public void clickChangeSound() {
		//System.out.println("clicked sound");
		//screen.findElementByName("imgSound").getRenderer().setImage();
	}

	public void startTest(){
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
		idNr = getTextFromTextfield("input_id");
		age = getTextFromTextfield("input_age");
		diagnosisNr = getTextFromTextfield("input_diagnosis_nr");
		nifty.exit();
		String drivingTask = "assets/DrivingTasks/Projects/IntroStraight/introStraight.xml";
		
		//sim.closeDrivingTaskSelectionGUI();
		sim.simpleInitDrivingTask(drivingTask, speed, idNr, diagnosisNr, gender, age);	
	}
	
	public void startIntroStimuli(){		
		idNr = getTextFromTextfield("input_id");
		age = getTextFromTextfield("input_age");
		diagnosisNr = getTextFromTextfield("input_diagnosis_nr");
		nifty.exit();
		String drivingTask = "assets/DrivingTasks/Projects/IntroStimuli/introStimuli.xml";
		
		sim.closeDrivingTaskSelectionGUI();
		sim.simpleInitDrivingTask(drivingTask, speed, idNr, diagnosisNr, gender, age);
	}
	
	public void startMainTest(){			
		idNr = getTextFromTextfield("input_id");
		age = getTextFromTextfield("input_age");
		diagnosisNr = getTextFromTextfield("input_diagnosis_nr");
		nifty.exit();
		String drivingTask = "assets/DrivingTasks/Projects/Countryside/countryside.xml";
		
		sim.simpleInitDrivingTask(drivingTask, speed, idNr, diagnosisNr, gender, age);
	}
	
	public void gotoResult(){
		nifty.gotoScreen("result"); 
	}
	
	  @NiftyEventSubscriber(id="RadioGroup-speed")
	  public void onRadioGroupSpeedChanged(final String id, final RadioButtonGroupStateChangedEvent event) {
	    speed = event.getSelectedId();
	  }
	  
	  @NiftyEventSubscriber(id="RadioGroup-gender")
	  public void onRadioGroupGenderChanged(final String id, final RadioButtonGroupStateChangedEvent event) {
	    gender = event.getSelectedId();
	  }

	
	public void setScreen(int screenNumber) {
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
