package eu.opends.niftyGui;



import com.jme3.app.state.AbstractAppState;
import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.input.InputManager;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.ui.Picture;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.PanelRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.Color;
import de.lessvoid.nifty.tools.SizeValue;
import eu.opends.main.Simulator;

/**
 * @author Jessica Larsson, Felicia Ringblom, 2015
 */

public class MyInstructionsGUIController implements ScreenController {
	
	private static Nifty nifty;
	private static Simulator sim;
	private static Screen screen;
	private static InputManager inputManager;
	
	public MyInstructionsGUIController(Simulator sim, Nifty nifty)
	{
		MyInstructionsGUIController.sim = sim;
		MyInstructionsGUIController.nifty = nifty;
		MyInstructionsGUIController.inputManager = sim.getInputManager();
		
		AssetManager assetManager = sim.getAssetManager();
		assetManager.registerLocator("assets", FileLocator.class);
		
	}

	@Override
	public void bind(Nifty arg0, Screen arg1) {
	     MyInstructionsGUIController.screen= arg1;
			if(sim.getSettings().getWidth() >= 2400)
			{
				SizeValue sv = new SizeValue("20%");
				nifty.getCurrentScreen().findElementByName("menuPanel").setConstraintWidth(sv);
			}
	
		
	}
	

	@Override
	public void onEndScreen() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStartScreen() {
		// TODO Auto-generated method stub
		
	}
	
	
	public void quickStartMainTest(){
		//hide all elements on screen and start simulation of introductionStraight
		screen.findElementByName("txt0").hide();
		screen.findElementByName("panel0").getRenderer(PanelRenderer.class).setBackgroundColor(null);
		screen.findElementByName("img0").hide();
		screen.findElementByName("quickButton").hide();
		screen.findElementByName("txt01").hide();
		String driverName = "Lightning McQueen";
		String drivingTask = "assets/DrivingTasks/Projects/Countryside/countryside.xml";
		sim.simpleInitDrivingTask(drivingTask,driverName);
		
	}
	
	public void startMainTest(){
		//hide all elements on screen and start simulation of introductionStraight
		System.out.println("hehehhe");
		screen.findElementByName("txt4").hide();
		screen.findElementByName("panel3").getRenderer(PanelRenderer.class).setBackgroundColor(null);
		screen.findElementByName("img4").hide();
		screen.findElementByName("img5").hide();
		screen.findElementByName("txt5").hide();
		String driverName = "Lightning McQueen";
		String drivingTask = "assets/DrivingTasks/Projects/Countryside/countryside.xml";
		sim.simpleInitDrivingTask(drivingTask,driverName);
		
	}
	
	public void clickNextButton(){
		 nifty.gotoScreen("next1"); 

	}
	
	public void startOver() {
		 nifty.gotoScreen("start"); 
	}
	
	public void clickNext2Button(){	
		//hide all elements on screen and start simulation of introductionStraight
		screen.findElementByName("txt1").hide();
		screen.findElementByName("panel1").getRenderer(PanelRenderer.class).setBackgroundColor(null);
		screen.findElementByName("img1").hide();
		screen.findElementByName("img2").hide();
		screen.findElementByName("txt2").hide();
		
		String driverName = "Lightning McQueen";
		String drivingTask = "assets/DrivingTasks/Projects/IntroStraight/introStraight.xml";
		
		sim.simpleInitDrivingTask(drivingTask,driverName);
		
	}
	
	public void clickNext3Button(){
		screen.findElementByName("txt3").hide();
		screen.findElementByName("panel2").getRenderer(PanelRenderer.class).setBackgroundColor(null);
		screen.findElementByName("img3").hide();

		String driverName = "Lightning McQueen";
		String drivingTask = "assets/DrivingTasks/Projects/IntroStimuli/introStimuli.xml";
		sim.simpleInitDrivingTask(drivingTask,driverName);
	}

	
	public static void setScreen(int screenNumber) {
		
		sim.removeAllDrivingTaskElements();
		String next = "next" + screenNumber;
		nifty.gotoScreen(next);
		inputManager.setCursorVisible(true);
	
	}
}
