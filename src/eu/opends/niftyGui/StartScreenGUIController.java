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

public class StartScreenGUIController implements ScreenController {
	
	private Nifty nifty;
	private Simulator sim;
	private Screen screen;
	private InputManager inputManager;
	private String speed = "low";
	private String gender = "man";
	
	public StartScreenGUIController(Simulator sim, Nifty nifty)
	{
		this.sim = sim;
		this.nifty = nifty;
		this.inputManager = sim.getInputManager();
		
		AssetManager assetManager = sim.getAssetManager();
		assetManager.registerLocator("assets", FileLocator.class);
		
	}

	@Override
	public void bind(Nifty arg0, Screen arg1) 
	{
	     this.screen = arg1;		
	}
	
	@Override
	public void onEndScreen() {}

	@Override
	public void onStartScreen() {}
	

	public void startTest(){
		setDrivingTaskSettings();
		sim.getInstructionsGUI().setScreen(1);
	}	
	
	public void gotoInstructions3(){
		setDrivingTaskSettings();
		sim.getInstructionsGUI().setScreen(3);	
	}
	
	public void gotoInstructions5(){
		setDrivingTaskSettings();
		sim.getInstructionsGUI().setScreen(5);	
	}
	
	public void gotoInstructions7(){
		setDrivingTaskSettings();
		sim.getInstructionsGUI().setScreen(9);	
	}
	
	@NiftyEventSubscriber(id="RadioGroup-speed")
	public void onRadioGroupSpeedChanged(final String id, final RadioButtonGroupStateChangedEvent event) 
	{
		speed = event.getSelectedId();
	}
  
	@NiftyEventSubscriber(id="RadioGroup-gender")
	public void onRadioGroupGenderChanged(final String id, final RadioButtonGroupStateChangedEvent event) 
	{
		gender = event.getSelectedId();
	}
	  
	private void setDrivingTaskSettings()
	{
		sim.setIdNr(getTextFromTextfield("input_id"));
		sim.setAge(getTextFromTextfield("input_age"));
		sim.setDiagnosisNr(getTextFromTextfield("input_diagnosis_nr"));
		sim.setGender(gender);
		sim.setSpeed(speed);
	}

	
//    private Element getElementByName(String element)
//    {
//    	return nifty.getCurrentScreen().findElementByName(element);
//    }
    
    
    private String getTextFromTextfield(String element)
    {
    	return nifty.getCurrentScreen().findNiftyControl(element, TextField.class).getRealText();
    }
}
