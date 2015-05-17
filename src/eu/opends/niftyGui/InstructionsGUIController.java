package eu.opends.niftyGui;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.screen.ScreenController;
import eu.opends.main.Simulator;

public class InstructionsGUIController implements ScreenController 
{
	
	private Simulator sim;
	private Nifty nifty;
	private boolean soundIsOn = false;
	
	
	
	public InstructionsGUIController(Simulator sim) 
	{
		this.sim = sim;
		this.nifty = sim.getNifty();
	}

	
	@Override
	public void bind(Nifty arg0, Screen arg1) 
	{
		
	}

	
	@Override
	public void onEndScreen() 
	{

	}


	@Override
	public void onStartScreen() 
	{
		
	}
	
	public void toggleSound() {
		if(soundIsOn){
			stopSound();
		}else{
			playSound();
		}
	}
	
	private void stopSound(){
		Screen currentScreen = nifty.getCurrentScreen();
		NiftyImage newImage = nifty.getRenderEngine().createImage(currentScreen, "Interface/images_instruction_screen/speaker_off.png", false); // false means don't linear filter the image, true would apply linear filtering
		Element element = currentScreen.findElementByName("imgSound");
		element.getRenderer(ImageRenderer.class).setImage(newImage);
		
		soundIsOn = false;			
	}
	
	private void playSound(){
		Screen currentScreen = nifty.getCurrentScreen();
		NiftyImage newImage = nifty.getRenderEngine().createImage(currentScreen, "Interface/images_instruction_screen/speaker_on.png", false); // false means don't linear filter the image, true would apply linear filtering
		Element element = currentScreen.findElementByName("imgSound");
		element.getRenderer(ImageRenderer.class).setImage(newImage);
		
		soundIsOn = true;	
	}
	
	public void backToMenu(){
		System.out.println("Back to menu");
		sim.getGuiNode().detachAllChildren();
		sim.simpleInitApp();
	}
	
	public void gotoResult(){
		nifty.gotoScreen("result"); 
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
	
	public void gotoInstructions7(){
		sim.getNifty().exit();
		sim.getNifty().gotoScreen("instruction_7"); 
	}
	
	public void gotoInstructions8(){
		nifty.exit();
		nifty.gotoScreen("instruction_8"); 
	}
	
	public void gotoEndScreen(){
		nifty.exit();
		nifty.gotoScreen("end"); 
	}
	
	public void startIntroStraight(){		
		nifty.exit();
		String drivingTask = "assets/DrivingTasks/Projects/IntroStraight/introStraight.xml";
		sim.initDrivingTask(drivingTask);	
	}
	
	public void startTest1(){		
		nifty.exit();
		String drivingTask = "assets/DrivingTasks/Projects/Test1/test1.xml";
		sim.setTestNr(1);
		sim.initDrivingTask(drivingTask);
	}
	
	public void startTest2(){					
		nifty.exit();
		String drivingTask = "assets/DrivingTasks/Projects/Test2/test2.xml";
		sim.setTestNr(2);
		sim.initDrivingTask(drivingTask);
	}
	
	public void startTest3(){					
		nifty.exit();
		String drivingTask = "assets/DrivingTasks/Projects/Test3/test3.xml";	
		sim.setTestNr(3);
		sim.initDrivingTask(drivingTask);
	}
	
	public void playVideo1() {
		System.out.println("Play Video 1");
	}
	
	public void playVideo2() {
		System.out.println("Play Video 2");
	}
	
	public void playVideo3() {
		System.out.println("Play Video 3");
	}
	
	public void openPDF() {
		System.out.println("Open PDF");
	}
}
