package eu.opends.niftyGui;



import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.FileLocator;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.PanelRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.Color;
import de.lessvoid.nifty.tools.SizeValue;
import eu.opends.main.Simulator;

public class MyInstructionsGUIController implements ScreenController{
	
	private Nifty nifty;
	private Simulator sim;
	private Screen screen;
	
	public MyInstructionsGUIController(Simulator sim, Nifty nifty)
	{
		this.sim = sim;
		this.nifty = nifty;
		
		AssetManager assetManager = sim.getAssetManager();
		assetManager.registerLocator("assets", FileLocator.class);
		
	}

	@Override
	public void bind(Nifty arg0, Screen arg1) {
	
		 //this.nifty= arg0;
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
		//System.out.println("clickNextButton() clicked!");
		 nifty.gotoScreen("next1"); 

	}
	
	public void clickNext2Button(){
		//nifty.gotoScreen("next2");
		 String driverName = "Lightning McQueen";
		String drivingTask = "assets/DrivingTasks/Projects/IntroStimuli/introStimuli.xml";
		screen.findElementByName("panel1").getRenderer(PanelRenderer.class).setBackgroundColor(null);
		screen.findElementByName("img1").hide();
		sim.simpleInitDrivingTask(drivingTask,driverName);
		
		
	}
	
	public void clickNext3Button(){
		nifty.gotoScreen("next3");
	}
}
