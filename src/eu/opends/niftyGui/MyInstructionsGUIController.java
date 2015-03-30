package eu.opends.niftyGui;

import java.util.Locale;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.niftygui.NiftyJmeDisplay;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.SizeValue;
import eu.opends.basics.SimulationBasics;
import eu.opends.main.Simulator;

public class MyInstructionsGUIController extends AbstractAppState implements ScreenController{
	
	private Nifty nifty;
	private SimulationBasics sim;
	
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        //TODO: initialize your AppState, e.g. attach spatials to rootNode
        //this is called on the OpenGL thread after the AppState has been attached
    }

	@Override
	public void bind(Nifty arg0, Screen arg1) {
	
		 this.nifty= arg0;
	     //this.screen= arg1;
	
		
	}
	
   @Override
    public void update(float tpf) {
        //TODO: implement behavior during runtime
    }
 
    @Override
    public void cleanup() {
        super.cleanup();
        //TODO: clean up what you initialized in the initialize method,
        //e.g. remove all spatials from rootNode
        //this is called on the OpenGL thread after the AppState has been detached
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
		nifty.gotoScreen("next2");
	}
	
	public void clickNext3Button(){
		nifty.gotoScreen("next3");
	}
}
