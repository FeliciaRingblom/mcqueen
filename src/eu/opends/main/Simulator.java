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
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.beans.factory.InitializingBean;

import com.jme3.app.StatsAppState;
import com.jme3.app.state.VideoRecorderAppState;
import com.jme3.font.BitmapText;
import com.jme3.input.Joystick;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.system.AppSettings;

import de.lessvoid.nifty.Nifty;
import eu.opends.analyzer.CarPositionReader;
import eu.opends.analyzer.CarPositionWriter;
import eu.opends.analyzer.DataReader;
import eu.opends.analyzer.DataUnit;
import eu.opends.analyzer.DeviationComputer;
import eu.opends.analyzer.DrivingTaskLogger;
import eu.opends.analyzer.DataWriter;
import eu.opends.audio.AudioCenter;
import eu.opends.basics.InternalMapProcessing;
import eu.opends.basics.SimulationBasics;
import eu.opends.camera.SimulatorCam;
import eu.opends.cameraFlight.CameraFlight;
import eu.opends.cameraFlight.NotEnoughWaypointsException;
import eu.opends.canbus.CANClient;
import eu.opends.car.ResetPosition;
import eu.opends.car.SteeringCar;
import eu.opends.drivingTask.DrivingTask;
import eu.opends.drivingTask.settings.SettingsLoader.Setting;
import eu.opends.input.KeyBindingCenter;
import eu.opends.knowledgeBase.KnowledgeBase;
import eu.opends.multiDriver.MultiDriverClient;
import eu.opends.niftyGui.DrivingTaskSelectionGUIController;
import eu.opends.niftyGui.MyInstructionsGUIController;
import eu.opends.reactionCenter.ReactionCenter;
import eu.opends.settingsController.SettingsControllerServer;
import eu.opends.taskDescription.contreTask.SteeringTask;
import eu.opends.taskDescription.tvpTask.ThreeVehiclePlatoonTask;
import eu.opends.tools.CollisionListener;
import eu.opends.tools.ObjectManipulationCenter;
import eu.opends.tools.PanelCenter;
import eu.opends.tools.SpeedControlCenter;
import eu.opends.tools.Util;
import eu.opends.traffic.PhysicalTraffic;
import eu.opends.trigger.TriggerCenter;
import eu.opends.visualization.LightningClient;

/**
 * 
 * @author Rafael Math
 */
public class Simulator extends SimulationBasics
{
	private final static Logger logger = Logger.getLogger(Simulator.class);

    private Nifty nifty;
    private boolean drivingTaskGiven = false;
    private boolean initializationFinished = false;
    
    private MyInstructionsGUIController myInstructions;
    public MyInstructionsGUIController getMyInstructions() {
		return myInstructions;
	}

	private static String driverName;
    
    private static Float gravityConstant;
	public static Float getGravityConstant()
	{
		return gravityConstant;
	}
	
	private SteeringCar car;
    public SteeringCar getCar()
    {
    	return car;
    }
    
    private PhysicalTraffic physicalTraffic;
    public PhysicalTraffic getPhysicalTraffic()
    {
    	return physicalTraffic;
    }
	
	private static DrivingTaskLogger drivingTaskLogger;
	public static DrivingTaskLogger getDrivingTaskLogger()
	{
		return drivingTaskLogger;
	}
	
	private boolean dataWriterQuittable = false;
	private DataWriter dataWriter;
	public DataWriter getMyDataWriter() 
	{
		return dataWriter;
	}
	
	private boolean carPositionWriterQuittable = false;
	private CarPositionWriter carPositionWriter;
	public CarPositionWriter getMyCarPositionWriter() 
	{
		return carPositionWriter;
	}
	
	private LightningClient lightningClient;
	public LightningClient getLightningClient() 
	{
		return lightningClient;
	}
	
	private TriggerCenter triggerCenter = new TriggerCenter(this);
	public TriggerCenter getTriggerCenter()
	{
		return triggerCenter;
	}

	private static List<ResetPosition> resetPositionList = new LinkedList<ResetPosition>();
	public static List<ResetPosition> getResetPositionList() 
	{
		return resetPositionList;
	}

	private boolean showStats = false;	
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
	
	private CameraFlight cameraFlight;
	public CameraFlight getCameraFlight()
	{
		return cameraFlight;
	}
	
	private SteeringTask steeringTask;
	public SteeringTask getSteeringTask()
	{
		return steeringTask;
	}
	
	private ThreeVehiclePlatoonTask threeVehiclePlatoonTask;
	public ThreeVehiclePlatoonTask getThreeVehiclePlatoonTask()
	{
		return threeVehiclePlatoonTask;
	}
	
	private ReactionCenter reactionCenter;
	public ReactionCenter getReactionCenter()
	{
		return reactionCenter;
	}
	
	private ObjectManipulationCenter objectManipulationCenter;
	public ObjectManipulationCenter getObjectManipulationCenter()
	{
		return objectManipulationCenter;
	}
	
	private String instructionScreenID = null;
	public void setInstructionScreen(String ID)
	{
		instructionScreenID = ID;
	}
	
	private SettingsControllerServer settingsControllerServer;
	public SettingsControllerServer getSettingsControllerServer()
	{
		return settingsControllerServer;
	}		
	
	private static String outputFolder;
	public static String getOutputFolder()
	{
		return outputFolder;
	}
	
	/*Added by Felicia*/
	
	private float area = 0;
	private float lengthOfIdealLine = 1;
	
	private float roadWidth = 50.0f; //what is a good value here????
	private DeviationComputer devComp = new DeviationComputer(roadWidth);
	public DeviationComputer getDeviationComputer() 
	{
		return devComp;
	}

	private LinkedList<Vector3f> carPositionList = new LinkedList<Vector3f>();
	private LinkedList<DataUnit> dataUnitList = new LinkedList<DataUnit>();
	private LinkedList<Vector3f> idealPositionList = new LinkedList<Vector3f>();
	
	private CarPositionReader carPositionReader = new CarPositionReader();
	private CarPositionReader idealPositionReader = new CarPositionReader();
	private Long initialTimeStamp = 0l;

	public enum VisualizationMode 
	{
		POINT, LINE, CONE;
	}

	private DataUnit currentDataUnit;
	public DataUnit getCurrentDataUnit() 
	{
		return currentDataUnit;
	}
	
	public void calculateCarData(String fileName){
		carPositionReader.initReader("analyzerData/test/countryside.txt", true);
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
	
	/*end added by Felicia*/
	
	
    @Override
    public void simpleInitApp()
    {
    	showStats(false);
    	initDrivingAssessmentTest();
    }
    
    /*Author: Jessica Larsson, Felicia Ringblom, 2015 */
    private void initDrivingAssessmentTest() 
    {
		NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
    	
    	// Create a new NiftyGUI object
    	nifty = niftyDisplay.getNifty();
    	nifty.setLocale(new Locale("sv", "SE"));
    	
    	myInstructions = new MyInstructionsGUIController(this, nifty);
    		
    	String xmlPath = "Interface/MyInstructionsGUI.xml";
    	nifty.fromXml(xmlPath, "start", myInstructions);
    	
    	// attach the Nifty display to the gui view port as a processor
    	guiViewPort.addProcessor(niftyDisplay);
    	
    	// disable fly cam
    	flyCam.setEnabled(false);
    }
    
	public void closeDrivingTaskSelectionGUI() 
	{
		nifty.exit();
        //inputManager.setCursorVisible(false);
        flyCam.setEnabled(true);
	}
	
    public void simpleInitDrivingTask(String drivingTaskFileName, String driverName, String speed)
    {
    	initializationFinished = false;
    	
    	SimulationDefaults.drivingTaskFileName = drivingTaskFileName;
    	
    	Util.makeDirectory("analyzerData");
    	outputFolder = "analyzerData/" + Util.getDateTimeString();
    	
    	initDrivingTaskLayers();
    	
    	// show stats if set in driving task
    	showStats(drivingTask.getSettingsLoader().getSetting(Setting.General_showStats, false));  	
    	
    	// sets up physics, camera, light, shadows and sky
    	super.simpleInitApp();
    	
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
			driverName = settingsLoader.getSetting(Setting.General_driverName, SimulationDefaults.driverName);
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
		
		if(settingsLoader.getSetting(Setting.SettingsControllerServer_startServer, SimulationDefaults.SettingsControllerServer_startServer))
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
        
        String videoPath = settingsLoader.getSetting(Setting.General_captureVideo, "");
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
    
    
	private void initDrivingTaskLayers()
	{
		String drivingTaskFileName = SimulationDefaults.drivingTaskFileName;
		File drivingTaskFile = new File(drivingTaskFileName);
		drivingTask = new DrivingTask(this, drivingTaskFile);

		sceneLoader = drivingTask.getSceneLoader();
		scenarioLoader = drivingTask.getScenarioLoader();
		interactionLoader = drivingTask.getInteractionLoader();
		settingsLoader = drivingTask.getSettingsLoader();
	}
	
	
	/**
	 * That method is going to be executed, when the dataWriter is
	 * <code>null</code> and the S-key is pressed.
	 *
	 */
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

    
	private void updateDataWriter() 
	{
		if (carPositionWriter != null && carPositionWriter.isDataWriterEnabled()) 
		{
			if(!isPause())
				carPositionWriter.saveAnalyzerData();

			if (!dataWriterQuittable)
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

		super.destroy();
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
    		
    		System.out.println("new sim main");
	    	Simulator sim = new Simulator();

	    	if(args.length >= 1)
	    	{
	    		if(DrivingTask.isValidDrivingTask(new File(args[0])))
	    		{
	    			SimulationDefaults.drivingTaskFileName = args[0];
	    			sim.drivingTaskGiven = true;
	    		}
	    	}
	
	    	if(args.length >= 2)
	    	{
	    		SimulationDefaults.driverName = args[1];
	    	}
			
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
			sim.setShowSettings(false);
			sim.start();
    	}
    	catch(Exception e1)
    	{
    		logger.fatal("Could not run main method:", e1);
    	}
    }
}
