/*
*  This file is part of OpenDS (Open Source Driving Simulator).
*  Copyright (C) 2014 Rafael Math
*
*  OpenDS is free software: you can redistribute it and/or modify
*  it under the terms of the GNU General Public License as published by
*  the Free Software Foundation, either version 3 of the License, or
*  (at your option) any later version.
*
*  OpenDS is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU General Public License for more details.
*
*  You should have received a copy of the GNU General Public License
*  along with OpenDS. If not, see <http://www.gnu.org/licenses/>.
*/


package eu.opends.main;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.app.state.VideoRecorderAppState;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.input.Joystick;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.util.SkyFactory;

import de.lessvoid.nifty.Nifty;
import eu.opends.analyzer.CarPositionReader;
import eu.opends.analyzer.CarPositionWriter;
import eu.opends.analyzer.DataUnit;
import eu.opends.analyzer.DeviationComputer;
import eu.opends.analyzer.DrivingTaskLogger;
import eu.opends.audio.AudioCenter;
import eu.opends.basics.InternalMapProcessing;
import eu.opends.basics.LightFactory;
import eu.opends.main.Simulator;
import eu.opends.camera.CameraFactory;
import eu.opends.camera.SimulatorCam;
import eu.opends.cameraFlight.CameraFlight;
import eu.opends.cameraFlight.NotEnoughWaypointsException;
import eu.opends.car.ResetPosition;
import eu.opends.car.SteeringCar;
import eu.opends.drivingTask.DrivingTask;
import eu.opends.drivingTask.settings.SettingsLoader;
import eu.opends.drivingTask.settings.SettingsLoader.Setting;
import eu.opends.environment.TrafficLightCenter;
import eu.opends.input.KeyBindingCenter;
import eu.opends.knowledgeBase.KnowledgeBase;
import eu.opends.niftyGui.InstructionsGUI;
import eu.opends.niftyGui.StartScreenGUIController;
import eu.opends.niftyGui.ShutDownGUI;
import eu.opends.reactionCenter.ReactionCenter;
import eu.opends.settingsController.SettingsControllerServer;
import eu.opends.taskDescription.contreTask.SteeringTask;
import eu.opends.taskDescription.tvpTask.ThreeVehiclePlatoonTask;
import eu.opends.tools.CollisionListener;
import eu.opends.tools.ObjectManipulationCenter;
import eu.opends.tools.PanelCenter;
import eu.opends.tools.PropertiesLoader;
import eu.opends.tools.Util;
import eu.opends.tools.XMLLoader;
import eu.opends.traffic.PhysicalTraffic;
import eu.opends.trigger.TriggerAction;
import eu.opends.trigger.TriggerCenter;
import eu.opends.visualization.LightningClient;

/**
 * 
 * @author Rafael Math
 */
public class Simulator extends SimpleApplication
{
	
	private static DrivingTask drivingTask;
	private static DrivingTaskLogger drivingTaskLogger;
	
	private static Map<String,List<TriggerAction>> triggerActionListMap = new HashMap<String,List<TriggerAction>>();
	private static List<ResetPosition> resetPositionList = new LinkedList<ResetPosition>();

	private final static Logger logger = Logger.getLogger(Simulator.class);
	
	private BulletAppState bulletAppState;
	private LightFactory lightFactory;
	private CameraFactory cameraFactory;
	private Node sceneNode;
	private Node triggerNode;
	private Nifty nifty;
	private ShutDownGUI shutDownGUI;
	private InstructionsGUI instructionsGUI;
    private StartScreenGUIController startScreen;
	private KeyBindingCenter keyBindingCenter;
	private TrafficLightCenter trafficLightCenter;
    private PhysicalTraffic physicalTraffic;
	private SteeringCar car;
	private CarPositionWriter carPositionWriter;
	private LightningClient lightningClient;
	private CameraFlight cameraFlight;
	private SteeringTask steeringTask;
	private ThreeVehiclePlatoonTask threeVehiclePlatoonTask;
	private ReactionCenter reactionCenter;
	private ObjectManipulationCenter objectManipulationCenter;
	private SettingsControllerServer settingsControllerServer;
	private DataUnit currentDataUnit;
	private LinkedList<Vector3f> carPositionList = new LinkedList<Vector3f>();
	private LinkedList<DataUnit> dataUnitList = new LinkedList<DataUnit>();
	
	private CarPositionReader carPositionReader = new CarPositionReader();
	private Long initialTimeStamp = 0l;

	public enum VisualizationMode 
	{
		POINT, LINE, CONE;
	}
	
    private boolean initializationFinished = false;
	private boolean debugEnabled = false;
	private boolean carPositionWriterQuittable = false;
	private boolean showStats = false;	

	
	private int numberOfScreens;
	
	private float area = 0;
	private float lengthOfIdealLine = 1;
	private float roadWidth = 50.0f; //what is a good value here????
    private static Float gravityConstant;

    private String age, gender, diagnosisNr, idNr, speed;
	private static String driverName;
	private static String outputFolder;
	
	private DeviationComputer devComp = new DeviationComputer(roadWidth);
	private TriggerCenter triggerCenter = new TriggerCenter(this);
	

	
    public StartScreenGUIController getStartScreen() 
    {
		return startScreen;
	}
    
	public static Float getGravityConstant()
	{
		return gravityConstant;
	}
	
    public SteeringCar getCar()
    {
    	return car;
    }
    
    public PhysicalTraffic getPhysicalTraffic()
    {
    	return physicalTraffic;
    }
	
	public static DrivingTaskLogger getDrivingTaskLogger()
	{
		return drivingTaskLogger;
	}	

	public CarPositionWriter getMyCarPositionWriter() 
	{
		return carPositionWriter;
	}
	
	public Nifty getNifty()
	{
		return nifty;
	}
	
	public LightningClient getLightningClient() 
	{
		return lightningClient;
	}
	
	public TriggerCenter getTriggerCenter()
	{
		return triggerCenter;
	}

	public static List<ResetPosition> getResetPositionList() 
	{
		return resetPositionList;
	}

	
	public CameraFlight getCameraFlight()
	{
		return cameraFlight;
	}
	
	public SteeringTask getSteeringTask()
	{
		return steeringTask;
	}
	
	public ThreeVehiclePlatoonTask getThreeVehiclePlatoonTask()
	{
		return threeVehiclePlatoonTask;
	}
	
	public ReactionCenter getReactionCenter()
	{
		return reactionCenter;
	}
	
	public ObjectManipulationCenter getObjectManipulationCenter()
	{
		return objectManipulationCenter;
	}
	
	public SettingsControllerServer getSettingsControllerServer()
	{
		return settingsControllerServer;
	}		
	
	public static String getOutputFolder()
	{
		return outputFolder;
	}
	
	public KeyBindingCenter getKeyBindingCenter()
	{
		return keyBindingCenter;
	}
	
	public TrafficLightCenter getTrafficLightCenter() 
	{
		return trafficLightCenter;
	}
	
	public Node getSceneNode()
	{
		return sceneNode;
	}
	
	public Node getTriggerNode()
	{
		return triggerNode;
	}
	
    public BulletAppState getBulletAppState() 
    {
        return bulletAppState;
    }
    
    public PhysicsSpace getPhysicsSpace() 
    {
        return bulletAppState.getPhysicsSpace();
    }
    
    public static DrivingTask getDrivingTask()
	{
		return drivingTask;
	}
	
	public static SettingsLoader getSettingsLoader()
	{
		return drivingTask.getSettingsLoader();
	}

	
	public static Map<String,List<TriggerAction>> getTriggerActionListMap() 
	{
		return triggerActionListMap;
	}

	
	public AppSettings getSettings() 
	{
		return settings;
	}
	
	
	public ShutDownGUI getShutDownGUI() 
	{
		return shutDownGUI;
	}
	
	public InstructionsGUI getInstructionsGUI() 
	{
		return instructionsGUI;
	}
	

	public CameraFactory getCameraFactory() 
	{
		return cameraFactory;
	}
	
	
	public int getNumberOfScreens()
	{
		return numberOfScreens;
	}
    
    public void showStats(boolean show)
	{
		showStats = show;
		setDisplayFps(show);
    	setDisplayStatView(show);
	}
	
	public void toggleStats()
	{
		showStats = !showStats;
		showStats(showStats);
	}
    
    public float getPhysicsSpeed() 
    {
        return bulletAppState.getSpeed();
    }
   		
	public DeviationComputer getDeviationComputer() 
	{
		return devComp;
	}

	public DataUnit getCurrentDataUnit() 
	{
		return currentDataUnit;
	}
	
    public boolean isPause() 
    {
        return !bulletAppState.isEnabled();
    }
    
    
    public void setPause(boolean pause) 
    {
    	if(this instanceof Simulator)
    	{
    		CameraFlight camFlight = ((Simulator)this).getCameraFlight();
    		if(camFlight != null && !camFlight.isTerminated())
    		{
    			camFlight.play(); 
    		
    			if(pause)				
    				camFlight.pause();
    		}
    	}
        bulletAppState.setEnabled(!pause);
    }
	
	public void toggleDebugMode()
	{
		debugEnabled = !debugEnabled;
		bulletAppState.setDebugEnabled(debugEnabled);
	}
	
	public void calculateCarData(String fileName){
		carPositionReader.initReader("analyzerData/test/carData.txt", true);
		carPositionReader.loadDriveData();
		
		carPositionList = carPositionReader.getCarPositionList();

		for(Vector3f carPos : carPositionList){
			devComp.addWayPoint(carPos);
		}
		
		dataUnitList = carPositionReader.getDataUnitList();
		
		if(dataUnitList.size() > 0)
			initialTimeStamp = dataUnitList.get(0).getDate().getTime();
		
		try {
			
			area = devComp.getDeviation();
			lengthOfIdealLine = devComp.getLengthOfIdealLine();
			System.out.println("Area between ideal line and driven line: " + area);
			System.out.println("Length of ideal line: " + lengthOfIdealLine);
			System.out.println("Mean deviation: " + (float)area/lengthOfIdealLine + "\n");
		} catch (Exception e) {
			System.out.println(e.getMessage() + "\n");
		}
	}
	
		
    @Override
    public void simpleInitApp()
    {
    	showStats(false);
    	initNifty();
        instructionsGUI = new InstructionsGUI(this);
        shutDownGUI = new ShutDownGUI(this);
    	initDrivingAssessmentTest();
    }
    
    private void initNifty(){
		NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
    	
    	// Create a new NiftyGUI object
    	nifty = niftyDisplay.getNifty();
    	nifty.setLocale(new Locale("sv", "SE"));
    	    	
    	// attach the Nifty display to the gui view port as a processor
    	guiViewPort.addProcessor(niftyDisplay);
    }
    
    private void initDrivingAssessmentTest() 
    {
    	startScreen = new StartScreenGUIController(this, nifty);    	
    	String xmlPath = "Interface/StartScreenGUI.xml";
    	nifty.fromXml(xmlPath, "start", startScreen);
    	
    	// disable fly cam
    	flyCam.setEnabled(false);
    }
    
	public void closeDrivingTaskSelectionGUI() 
	{
		nifty.exit();
        //inputManager.setCursorVisible(false);
        flyCam.setEnabled(true);
	}
	
    public void initDrivingTask(String drivingTaskFileName)
    {
    	initializationFinished = false;
    	   	
    	SimulationDefaults.drivingTaskFileName = drivingTaskFileName;

    	//System.out.println("start: " + this.idNr + ", " + this.diagnosisNr + ", " + this.gender + ", " + this.age);
    	
    	Util.makeDirectory("analyzerData");
    	outputFolder = "analyzerData/" + Util.getDateTimeString();
    	
    	initDrivingTaskLayers();
    	
    	// show stats if set in driving task
    	showStats(drivingTask.getSettingsLoader().getSetting(Setting.General_showStats, false));  	
    	
    	// sets up physics, camera, light, shadows and sky
    	lookupNumberOfScreens();
    	
    	// init physics
    	stateManager.detach(bulletAppState);
    	stateManager.cleanup();
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        
        // register loader for *.properties-files
        assetManager.registerLoader(PropertiesLoader.class, "properties");
        assetManager.registerLoader(XMLLoader.class, "xml");
        
        if(sceneNode != null){
        	getViewPort().detachScene(sceneNode);
        	rootNode.detachChild(sceneNode);
        }
		sceneNode = new Node("sceneNode");
		rootNode.attachChild(sceneNode);
		        
		triggerNode = new Node("triggerNode");
		sceneNode.attachChild(triggerNode);

        // setup light settings
        lightFactory = new LightFactory(this);
        lightFactory.initLight();
        
        // build sky
        createSkyBox();
    	
    	// set gravity
    	gravityConstant = drivingTask.getSceneLoader().getGravity(SimulationDefaults.gravity);
    	getPhysicsSpace().setGravity(new Vector3f(0, -gravityConstant, 0));	
    	
    	PanelCenter.init(this);
	
        Joystick[] joysticks = inputManager.getJoysticks();
        if(joysticks != null)
        	for (Joystick joy : joysticks)
        		System.out.println("Connected joystick: " + joy.toString());
        
    	//load map model
		new InternalMapProcessing(this);
		
		// create and place steering car
		car = new SteeringCar(this, speed);
		
		if(driverName == null || driverName.isEmpty())
			driverName = getSettingsLoader().getSetting(Setting.General_driverName, SimulationDefaults.driverName);
    	SimulationDefaults.driverName = driverName;
        
		// setup key binding
		keyBindingCenter = new KeyBindingCenter(this);
		
        AudioCenter.init(this);

        // setup camera settings
        cameraFactory = new SimulatorCam(this, car);

		// init trigger center
		triggerCenter.setup();
				
		drivingTaskLogger = new DrivingTaskLogger(outputFolder, driverName, drivingTask.getFileName());
			
		try {		
			// attach camera to camera flight
			cameraFlight = new CameraFlight(this);
		} catch (NotEnoughWaypointsException e) {
			// if not enough way points available, attach camera to driving car
			car.getCarNode().attachChild(cameraFactory.getMainCameraNode());
		}
		
		reactionCenter = new ReactionCenter(this);
		
		steeringTask = new SteeringTask(this, driverName);
		
		objectManipulationCenter = new ObjectManipulationCenter(this);
		
		if(getSettingsLoader().getSetting(Setting.SettingsControllerServer_startServer, SimulationDefaults.SettingsControllerServer_startServer))
		{
			settingsControllerServer = new SettingsControllerServer(this);
			settingsControllerServer.start();
		}
		
		StatsAppState statsAppState = stateManager.getState(StatsAppState.class);
    	if (statsAppState != null && statsAppState.getFpsText() != null && statsAppState.getStatsView() != null) 
    	{
    		statsAppState.getFpsText().setLocalTranslation(3, getSettings().getHeight()-145, 0);
    		statsAppState.getStatsView().setLocalTranslation(3, getSettings().getHeight()-145, 0);
    		statsAppState.setDarkenBehind(false);
        }
    	
    	// add physics collision listener
    	CollisionListener collisionListener = new CollisionListener();
        getPhysicsSpace().addCollisionListener(collisionListener);
        
        String videoPath = getSettingsLoader().getSetting(Setting.General_captureVideo, "");
        if((videoPath != null) && (!videoPath.isEmpty()) && (Util.isValidFilename(videoPath)))
        {
        	File videoFile = new File(videoPath);
        	stateManager.attach(new VideoRecorderAppState(videoFile));
        }
		
        //start car position writer
		initializeDataWriter();
		if (carPositionWriter.isDataWriterEnabled() == false) {
			System.out.println("Start storing Drive-Data");
			carPositionWriter.setDataWriterEnabled(true);
		} 
		
        
		initializationFinished = true;
		
		
		String analyzerFile = "analyzerData/test/carData.txt";
		calculateCarData(analyzerFile);	
    }
    
    public void closeDrivngTask()
    {
    	
    }
    
    
	private void initDrivingTaskLayers()
	{
		String drivingTaskFileName = SimulationDefaults.drivingTaskFileName;
		File drivingTaskFile = new File(drivingTaskFileName);
		drivingTask = new DrivingTask(this, drivingTaskFile);
	}
	
	
	public void initializeDataWriter() 
	{
		carPositionWriter = new CarPositionWriter(outputFolder, car, driverName, SimulationDefaults.drivingTaskFileName);
	}
	
    @Override
    public void simpleUpdate(float tpf) 
    {
    	
    	if(initializationFinished)
    	{
			super.simpleUpdate(tpf);
			
			// updates camera
			cameraFactory.updateCamera();
		
			if(!isPause())
				car.getTransmission().updateRPM(tpf);
		
			PanelCenter.update();
		
			triggerCenter.doTriggerChecks();
		
			updateDataWriter();
			
			// send camera data via TCP to Lightning
			if(lightningClient != null)
				lightningClient.sendCameraData(cam);
					
			if(!isPause())
				car.update(tpf);

			// update necessary even in pause
			AudioCenter.update(tpf, cam);
			
			if(!isPause())
				steeringTask.update(tpf);
			
			if(cameraFlight != null)
				cameraFlight.update();
			
			reactionCenter.update();
			
    	}
    }
    

	private void createSkyBox()
	{
		String skyModelPath = Simulator.getDrivingTask().getSceneLoader().getSkyTexture(SimulationDefaults.skyTexture);
        assetManager.registerLocator("assets", FileLocator.class);
        Spatial sky;
        try{
        	sky = SkyFactory.createSky(assetManager, skyModelPath, false);
        } catch (AssetNotFoundException e) {
        	System.err.println("Simulator: Could not find sky texture '" + skyModelPath + 
        			"'. Using default ('" + SimulationDefaults.skyTexture + "').");
        	sky = SkyFactory.createSky(assetManager, SimulationDefaults.skyTexture, false);
        }
        sky.setShadowMode(ShadowMode.Off);
        sceneNode.attachChild(sky);
	}
    
    
    private void lookupNumberOfScreens()
    {
		numberOfScreens = drivingTask.getSettingsLoader().getSetting(Setting.General_numberOfScreens, -1);
		
		if(numberOfScreens < 1)
		{
			int width = getSettings().getWidth();
	    	int height = getSettings().getHeight();
	    	
			if((width == 5040 && height == 1050) || (width == 4200 && height == 1050))
				numberOfScreens = 3;
			else
				numberOfScreens = 1;
		}
    }

    
	private void updateDataWriter() 
	{
		if (carPositionWriter != null && carPositionWriter.isDataWriterEnabled()) 
		{
			if(!isPause())
				carPositionWriter.saveAnalyzerData();

			if (!carPositionWriterQuittable)
				carPositionWriterQuittable = true;
		} 
		else 
		{
			if (carPositionWriterQuittable) 
			{
				carPositionWriter.quit();
				carPositionWriter = null;
				carPositionWriterQuittable = false;
			}
		}
	}
	
	/**
	 * Cleanup after game loop was left
	 * Will be called whenever application is closed.
	 */
	
	@Override
	public void destroy()
    {
		System.out.println("i destroy()");
		logger.info("started destroy()");

		if(initializationFinished)
		{
			if(lightningClient != null)
				lightningClient.close();
						
			steeringTask.close();
			
			reactionCenter.close();
			
			KnowledgeBase.KB.disconnect();
			
			car.close();

			if(settingsControllerServer != null)
				settingsControllerServer.close();
			
		}

		//super.destroy();
		logger.info("finished destroy()");
    }
	
	
    public static void main(String[] args) 
    {    
    	try
    	{
    		
    		// load logger configuration file
    		PropertyConfigurator.configure("assets/JasperReports/log4j/log4j.properties");
    	
    		// only show severe jme3-logs
    		java.util.logging.Logger.getLogger("").setLevel(java.util.logging.Level.SEVERE);
   
    		Simulator sim = new Simulator();

	    	AppSettings settings = new AppSettings(true);
	        settings.setUseJoysticks(true);
	        settings.setSettingsDialogImage("assets/Textures/Logo/mcQueen.jpg");
	        settings.setTitle("Testa din körförmåga. ");
	        
	        // set splash screen parameters
	        settings.setFullscreen(false);
	        settings.setResolution(1280, 720);
	        settings.setSamples(4);
	        settings.setBitsPerPixel(24);
	        settings.setVSync(false);
	        settings.setFrequency(60);
	     
	        
			sim.setSettings(settings);
			
			sim.setPauseOnLostFocus(false);
			System.out.println("ska nu anropa sim.start() från main");
			sim.setShowSettings(true);
			sim.start();
    	}
    	catch(Exception e1)
    	{
    		logger.fatal("Could not run main method:", e1);
    	}
    }

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getDiagnosisNr() {
		return diagnosisNr;
	}

	public void setDiagnosisNr(String diagnosisNr) {
		this.diagnosisNr = diagnosisNr;
	}

	public String getIdNr() {
		return idNr;
	}

	public void setIdNr(String idNr) {
		this.idNr = idNr;
	}
	
	public void setSpeed(String speed){
		this.speed = speed;
	}
}
