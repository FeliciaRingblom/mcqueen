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

package eu.opends.drivingTask.scenario;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPathConstants;

import org.w3c.dom.NodeList;

import Jama.Matrix;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

import eu.opends.basics.MapObject;
import eu.opends.cameraFlight.CameraFlightSettings;
import eu.opends.car.ResetPosition;
import eu.opends.drivingTask.DrivingTask;
import eu.opends.drivingTask.DrivingTaskDataQuery;
import eu.opends.drivingTask.DrivingTaskDataQuery.Layer;
import eu.opends.drivingTask.scene.SceneLoader;
import eu.opends.main.Simulator;


/**
 * 
 * @author Rafael Math
 */
@SuppressWarnings("unchecked")
public class ScenarioLoader 
{
	private DrivingTaskDataQuery dtData;
	private Simulator sim;
	private SceneLoader sceneLoader;
	private float driverCarMass;
	private Vector3f driverCarStartLocation;
	private Quaternion driverCarStartRotation;
	private String driverCarModelPath;
	private CameraFlightSettings cameraFlightSettings;
	private Matrix modelToGeoMatrix;
	private Matrix geoToModelMatrix;
	
	
	public enum CarProperty
	{
		tires_type,
		tires_size,
		engine_engineOn,
		engine_minSpeed,
		engine_maxSpeed,
		engine_acceleration,
		suspension_stiffness, 
		suspension_compression, 
		suspension_damping,
		brake_decelerationFreeWheel,
		brake_decelerationBrake, 
		wheel_frictionSlip, 
		engine_minRPM,
		engine_maxRPM,
		light_intensity;

		public String getXPathQuery()
		{
			String[] array = this.toString().split("_");
			if(array.length >= 2)
				return "/scenario:scenario/scenario:driver/scenario:car/scenario:"+array[0]+"/scenario:"+array[1];
			else
				return "/scenario:scenario/scenario:driver/scenario:car/scenario:"+array[0];
		}
	}
	

	public ScenarioLoader(DrivingTaskDataQuery dtData, Simulator sim, DrivingTask drivingTask) 
	{
		this.dtData = dtData;
		this.sim = sim;
		this.sceneLoader = drivingTask.getSceneLoader();
		processSceneCar();
		extractCameraFlight();
		extractConversionMatrices();
		
		if(sim instanceof Simulator)
			extractIdealLine();
	}


	private void processSceneCar() 
	{
		String driverCarRef = dtData.getValue(Layer.SCENARIO, 
					"/scenario:scenario/scenario:driver/scenario:car/@ref", String.class);
		
		MapObject sceneCar = null;
		
		for(MapObject mapObject : sceneLoader.getMapObjects())
		{
			if(mapObject.getName().equals(driverCarRef))
			{
				sceneCar = mapObject;
				driverCarMass = sceneCar.getMass();
				driverCarStartLocation = sceneCar.getLocation();
				driverCarStartRotation = sceneCar.getRotation();
				driverCarModelPath = sceneCar.getModelPath();
			}
		}
		
		if(sceneCar != null)
			sceneLoader.getMapObjects().remove(sceneCar);
		
		extractResetPoints();
	}
	
	
	private void extractResetPoints() 
	{
		String path = "/scenario:scenario/scenario:driver/scenario:car/scenario:resetPoints/scenario:resetPoint";
		try {
			NodeList positionNodes = (NodeList) dtData.xPathQuery(Layer.SCENARIO, 
					path, XPathConstants.NODESET);

			for (int k = 1; k <= positionNodes.getLength(); k++) 
			{
				ResetPosition resetPosition = createResetPosition(path + "["+k+"]");
			
				String resetPositionRef = dtData.getValue(Layer.SCENARIO, 
						path + "["+k+"]/@ref", String.class);

				Map<String, ResetPosition> resetPositionMap = sceneLoader.getResetPositionMap();
				
				if(resetPosition != null)
				{
					Simulator.getResetPositionList().add(resetPosition);
				}
				else if((resetPositionRef != null) && (resetPositionMap.containsKey(resetPositionRef)))
				{
					ResetPosition refPosition = resetPositionMap.get(resetPositionRef);
					Simulator.getResetPositionList().add(refPosition);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private ResetPosition createResetPosition(String path) 
	{
		String id = dtData.getValue(Layer.SCENARIO, path + "/@id", String.class);
		Vector3f translation = dtData.getVector3f(Layer.SCENARIO, path + "/scenario:translation");
		Quaternion rotation = dtData.getQuaternion(Layer.SCENARIO, path + "/scenario:rotation");

		if((id != null) && (translation != null) && (rotation != null))
			return new ResetPosition(id, translation, rotation);
		
		return null;
	}


	private void extractCameraFlight() 
	{
		List<Vector3f> cameraFlightWayPointList = new ArrayList<Vector3f>();
		
		try {

			Float cameraFlightSpeed = dtData.getValue(Layer.SCENARIO, 
					"/scenario:scenario/scenario:driver/scenario:cameraFlight/scenario:speed", Float.class);
			
			Boolean automaticStart = dtData.getValue(Layer.SCENARIO, 
					"/scenario:scenario/scenario:driver/scenario:cameraFlight/scenario:automaticStart", Boolean.class);
			
			Boolean automaticStop = dtData.getValue(Layer.SCENARIO, 
					"/scenario:scenario/scenario:driver/scenario:cameraFlight/scenario:automaticStop", Boolean.class);
			
			NodeList pointNodes = (NodeList) dtData.xPathQuery(Layer.SCENARIO, 
					"/scenario:scenario/scenario:driver/scenario:cameraFlight/scenario:track/scenario:point", XPathConstants.NODESET);

			for (int k = 1; k <= pointNodes.getLength(); k++) 
			{
				Vector3f point = dtData.getVector3f(Layer.SCENARIO, 
						"/scenario:scenario/scenario:driver/scenario:cameraFlight/scenario:track/scenario:point["+k+"]/scenario:translation");
				
				String pointRef = dtData.getValue(Layer.SCENARIO, 
						"/scenario:scenario/scenario:driver/scenario:cameraFlight/scenario:track/scenario:point["+k+"]/@ref", String.class);

				Map<String, Vector3f> pointMap = sceneLoader.getPointMap();
				
				if(point != null)
				{
					cameraFlightWayPointList.add(point);
				}
				else if((pointRef != null) && (pointMap.containsKey(pointRef)))
				{
					Vector3f translation = pointMap.get(pointRef);
					cameraFlightWayPointList.add(translation);
				}
				else 
					throw new Exception("Error in camera flight way point list");
			}
			
			cameraFlightSettings = new CameraFlightSettings(cameraFlightSpeed, automaticStart, 
					automaticStop, cameraFlightWayPointList);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public CameraFlightSettings getCameraFlightSettings() 
	{
		return cameraFlightSettings;
	}
	
	
	private void extractIdealLine()
	{
		List<Vector3f> idealPoints = new ArrayList<Vector3f>();
		
		try {
			
			NodeList pointNodes = (NodeList) dtData.xPathQuery(Layer.SCENARIO, 
					"/scenario:scenario/scenario:driver/scenario:idealTrack/scenario:point", XPathConstants.NODESET);
			
			for (int k = 1; k <= pointNodes.getLength(); k++) 
			{
				Vector3f point = dtData.getVector3f(Layer.SCENARIO, 
						"/scenario:scenario/scenario:driver/scenario:idealTrack/scenario:point["+k+"]/scenario:translation");
				
				String pointRef = dtData.getValue(Layer.SCENARIO, 
						"/scenario:scenario/scenario:driver/scenario:idealTrack/scenario:point["+k+"]/@ref", String.class);

				Map<String, Vector3f> pointMap = sceneLoader.getPointMap();
				
				if(point != null)
				{
					idealPoints.add(point);
				}
				else if((pointRef != null) && (pointMap.containsKey(pointRef)))
				{
					Vector3f translation = pointMap.get(pointRef);
					idealPoints.add(translation);
				}
				else 
					throw new Exception("Error in ideal point list");
			}
			
			for(Vector3f idealPoint : idealPoints)
			{
				Vector2f idealPoint2f = new Vector2f(idealPoint.getX(), idealPoint.getZ());
				((Simulator) sim).getDeviationComputer().addIdealPoint(idealPoint2f);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private void extractConversionMatrices()
	{
		String modelToGeoPath = "/scenario:scenario/scenario:coordinateConversion/scenario:modelToGeo";
		modelToGeoMatrix = dtData.getMatrix(Layer.SCENARIO, modelToGeoPath);
		
		String geoToModelPath = "/scenario:scenario/scenario:coordinateConversion/scenario:geoToModel";
		geoToModelMatrix = dtData.getMatrix(Layer.SCENARIO, geoToModelPath);
	}
	
	
	public Matrix getModelToGeoMatrix()
	{
		return modelToGeoMatrix;
	}
	
	
	public Matrix getGeoToModelMatrix()
	{
		return geoToModelMatrix;
	}
	

	/**
	 * Looks up the node "startPosition" for the initial location 
	 * of the car at the beginning of the simulation.
	 * 
	 */
	public Vector3f getStartLocation() 
	{
		return driverCarStartLocation;
	}
	
	
	/**
	 * Looks up the node "startPosition" for the initial rotation 
	 * of the car at the beginning of the simulation.
	 * 
	 */
	public Quaternion getStartRotation() 
	{
		return driverCarStartRotation;
	}
	
	
	public float getChassisMass() 
	{
		return driverCarMass;
	}
	
	
	public String getModelPath() 
	{
		return driverCarModelPath;
	}
	
	
	
	/**
	 * Looks up the sub node (specified in parameter name) of the given element node
	 * and writes the data to the global variable with the same name. If this was 
	 * successful, the global variable "isSet_&lt;name&gt;" will be set to true.
	 */
	public <T> T getCarProperty(CarProperty carProperty, T defaultValue)
	{		
		try {
			
			Class<T> cast = (Class<T>) defaultValue.getClass();
			return (T) dtData.getValue(Layer.SCENARIO, carProperty.getXPathQuery(), cast);
			
		} catch (Exception e2) {
			dtData.reportInvalidValueError(carProperty.toString(), dtData.getScenarioPath());
		}
		
		return (T) defaultValue;
	}
	
	

	public boolean isAutomaticTransmission(boolean defaultValue) 
	{
		Boolean isAutomatic = dtData.getValue(Layer.SCENARIO, 
				"/scenario:scenario/scenario:driver/scenario:car/scenario:transmission/scenario:automatic", Boolean.class);
		
		if(isAutomatic != null)
			return isAutomatic;
		else
			return defaultValue;
	}


	public float getReverseGear(float defaultValue) 
	{
		Float transmission = dtData.getValue(Layer.SCENARIO, 
				"/scenario:scenario/scenario:driver/scenario:car/scenario:transmission/scenario:reverse", Float.class);
		
		if(transmission != null)
			return transmission;
		else
			return defaultValue;
	}
	
	
	public float getEngineSoundIntensity(float defaultValue) 
	{
		Float soundIntensity = dtData.getValue(Layer.SCENARIO, 
				"/scenario:scenario/scenario:driver/scenario:car/scenario:engine/scenario:soundIntensity", Float.class);
		
		if(soundIntensity != null)
			return soundIntensity;
		else
			return defaultValue;
	}


	public Float[] getForwardGears(Float[] defaultValue) 
	{
		List<Float> transmission = dtData.getArray(Layer.SCENARIO, 
				"/scenario:scenario/scenario:driver/scenario:car/scenario:transmission/scenario:forward", Float.class);
		
		Float[] transmissionArray = new Float[transmission.size()];
		
		for(int i=0; i<transmission.size(); i++)
			transmissionArray[i] = transmission.get(i);
		
		if((transmissionArray != null) && (transmissionArray.length >= 1))
			return transmissionArray;
		else
			return defaultValue;
	}


	
	
}
