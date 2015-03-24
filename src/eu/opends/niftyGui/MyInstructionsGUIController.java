package eu.opends.niftyGui;

import java.util.Locale;

import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.FileLocator;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.SizeValue;
import eu.opends.main.Simulator;

public class MyInstructionsGUIController implements ScreenController{
	
	private Simulator sim;
	private Nifty nifty;
	private String currentPath = "./assets/DrivingTasks/Projects";
	private Element errorPopup;
	
	public MyInstructionsGUIController(Simulator sim, Nifty nifty)
	{
		this.sim = sim;
		this.nifty = nifty;
		
		
		AssetManager assetManager = sim.getAssetManager();
		assetManager.registerLocator("assets", FileLocator.class);
	}

	@Override
	public void bind(Nifty arg0, Screen arg1) {
	

	
		
	}

	@Override
	public void onEndScreen() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStartScreen() {
		// TODO Auto-generated method stub
		
	}

}
