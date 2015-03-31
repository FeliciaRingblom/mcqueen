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
		this.sim = sim;
		this.nifty = nifty;
		this.inputManager = sim.getInputManager();
		
		AssetManager assetManager = sim.getAssetManager();
		assetManager.registerLocator("assets", FileLocator.class);
		
	}

	@Override
	public void bind(Nifty arg0, Screen arg1) {
	     this.screen= arg1;
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
	
	public void clickNextButton(){
		
		 nifty.gotoScreen("next1"); 

	}
	
	public void clickNext2Button(){	
		//hide all elements on screen and start simulation of introductionStraight
		screen.findElementByName("txt1").hide();
		screen.findElementByName("panel1").getRenderer(PanelRenderer.class).setBackgroundColor(null);
		screen.findElementByName("img1").hide();
		
		String driverName = "Lightning McQueen";
		String drivingTask = "assets/DrivingTasks/Projects/IntroStraight/introStraight.xml";
		sim.simpleInitDrivingTask(drivingTask,driverName);
		
	}
	
	public void clickNext3Button(){
		nifty.gotoScreen("next3");
	}

	
	public static void setScreen(int screenNumber) {		
		Spatial spatial = sim.getGuiNode().getChild("hood");
		
		if(spatial instanceof Picture)
		{
			spatial.setCullHint(CullHint.Always);
			Picture picture = (Picture) spatial;
				picture.setCullHint(CullHint.Always);
		}
		String next = "next" + screenNumber;
		nifty.gotoScreen(next);
		inputManager.setCursorVisible(true);
	
	}
}
