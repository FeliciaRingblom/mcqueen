package eu.opends.niftyGui;



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

public class MyInstructionsGUIController implements ScreenController{
	
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
	
	public void startMainTest(){
		//hide all elements on screen and start simulation of introductionStraight
		System.out.println("i clickQuick");
		screen.findElementByName("txt0").hide();
		screen.findElementByName("panel0").getRenderer(PanelRenderer.class).setBackgroundColor(null);
		screen.findElementByName("img0").hide();
		screen.findElementByName("quickButton").hide();
		screen.findElementByName("txt01").hide();
		String driverName = "Lightning McQueen";
		String drivingTask = "assets/DrivingTasks/Projects/Countryside/countryside.xml";
		sim.simpleInitDrivingTask(drivingTask,driverName);
	}
	
	public void clickNextButton(){
		
		 nifty.gotoScreen("next1"); 

	}
	
	public void startOver() {
		System.out.println("i startOver()");
		prepareScreen();
		 nifty.gotoScreen("start"); 
	}
	
	public void clickNext2Button(){	
		System.out.println("number of Layer elements: " + screen.getLayerElements().size()); 
		//hide all elements on screen and start simulation of introductionStraight
		screen.findElementByName("txt1").hide();
		screen.findElementByName("panel1").getRenderer(PanelRenderer.class).setBackgroundColor(null);
		screen.findElementByName("img1").hide();
		screen.findElementByName("img2").hide();
		screen.findElementByName("txt2").hide();
		System.out.println("number of Layer elements: " + screen.getLayerElements().size()); 
		
		String driverName = "Lightning McQueen";
		String drivingTask = "assets/DrivingTasks/Projects/IntroStraight/introStraight.xml";
		sim.simpleInitDrivingTask(drivingTask,driverName);
		
	}
	
	public void clickNext3Button(){
		System.out.println("number of Layer elements: " + screen.getLayerElements().size()); 
		screen.findElementByName("txt3").hide();
		screen.findElementByName("panel2").getRenderer(PanelRenderer.class).setBackgroundColor(null);
		screen.findElementByName("img3").hide();
		prepareScreen();
		sim.destroyDrivingTask();
		String driverName = "Lightning McQueen";
		String drivingTask = "assets/DrivingTasks/Projects/IntroStimuli/introStimuli.xml";
		sim.simpleInitDrivingTask(drivingTask,driverName);
	}

	
	public static void setScreen(int screenNumber) {
		prepareScreen();
		String next = "next" + screenNumber;
		nifty.gotoScreen(next);
		inputManager.setCursorVisible(true);
	
	}
	
	public static void prepareScreen() {
		System.out.println("i prepareScreen");
		Spatial spatial = sim.getGuiNode().getChild("hood");	
		if(spatial instanceof Picture)
		{
			System.out.println("picture");
			spatial.setCullHint(CullHint.Always);
			Picture picture = (Picture) spatial;
				picture.setCullHint(CullHint.Always);
		}
		
		Spatial spatial2 = sim.getGuiNode().getChild("speedometer");		
		if(spatial2 instanceof Picture)
		{
			spatial2.setCullHint(CullHint.Always);
			Picture picture = (Picture) spatial2;
				picture.setCullHint(CullHint.Always);
		}
		
		Spatial s3 = sim.getGuiNode().getChild("RPMgauge");	
		if(s3 instanceof Picture)
		{
			s3.setCullHint(CullHint.Always);
			Picture picture = (Picture) s3;
				picture.setCullHint(CullHint.Always);
		}
		
		Spatial s4 = sim.getGuiNode().getChild("RPMNeedle");		
		if(s4 instanceof Picture)
		{
			s4.setCullHint(CullHint.Always);
			Picture picture = (Picture) s4;
				picture.setCullHint(CullHint.Always);
		}
		
		Spatial s5 = sim.getGuiNode().getChild("speedNeedle");		
		if(s5 instanceof Picture)
		{
			s5.setCullHint(CullHint.Always);
			Picture picture = (Picture) s5;
				picture.setCullHint(CullHint.Always);
		}
		
		//Spatial s6 = sim.getGuiNode().getChild("speedText");
		

	}
}
