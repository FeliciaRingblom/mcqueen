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

package eu.opends.jasperReport;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.JOptionPane;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JaxenXmlDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import com.jme3.math.Vector3f;

import eu.opends.analyzer.CarPositionReader;
import eu.opends.analyzer.DataUnit;
import eu.opends.analyzer.DeviationComputer;
import eu.opends.drivingTask.settings.SettingsLoader;
import eu.opends.drivingTask.settings.SettingsLoader.Setting;
import eu.opends.main.SimulationDefaults;
import eu.opends.main.Simulator;
import eu.opends.reactionCenter.ReactionTimer;
import eu.opends.tools.Util;

/**
 * 
 * @author Rafael Math
 */
public class ReactionLogger 
{
	private Simulator sim;
	private boolean isRunning = false;
	private String dataFileName = "reactionData.xml";
	private String reportFileName = "reactionReport.pdf";
	private String outputFolder;
	BufferedWriter bw;
	private float meanDeviation_part1, meanDeviation_part2, meanDeviation_part3;
	private String age, gender, diagnosisNr, idNr, speed, hands;
	private boolean hasGeneratedReport = false;
	private ReactionTimer timerList[];


	public ReactionLogger(Simulator sim) {
		this.sim = sim;
		timerList = new ReactionTimer[60];
		System.out.println("ReactionLogger.ReactionLogger()");
	}


	private void start()
	{
		try
		{		
			outputFolder = sim.getOutputFolder();
			Util.makeDirectory(outputFolder);

			bw = new BufferedWriter(new FileWriter(outputFolder + "/" + dataFileName));
			bw.write("<?xml version=\"1.0\"?>\n");    
			bw.write("<report>\n");

			isRunning = true;

		} catch (IOException e) {

			e.printStackTrace();
		}		
	}

	private int toIndex(String reactionTimerID) {
		int sep = reactionTimerID.indexOf('_');
		int testNo = Integer.parseInt(reactionTimerID.substring(0, sep));
		int reactionNo = Integer.parseInt(reactionTimerID.substring(sep + 1));
		return (testNo - 1) * 20 + reactionNo - 1;  //TODO change to constants 
	}

	public void add(ReactionTimer reactionTimer) {
		if(!isRunning) {
			start();
		}
		if(isRunning)
		{		
			timerList[toIndex(reactionTimer.getTimerID())] = reactionTimer;

		}
	}

	private void generateDataXML () {
		try {
			for (ReactionTimer reactionTimer : timerList) {
				if (reactionTimer != null) { 
					bw.write("\t<reactionMeasurement>\n");
					bw.write("\t\t<reactionID>" + toIndex(reactionTimer.getTimerID()) + "</reactionID>\n");
					bw.write("\t\t<reactionGroup>" + reactionTimer.getReactionGroupID() + "</reactionGroup>\n");	
					bw.write("\t\t<reactionResult>" + reactionTimer.getReactionResult() + "</reactionResult>\n");				
					bw.write("\t\t<reactionTime>" + reactionTimer.getReactionTime() + "</reactionTime>\n");				
					bw.write("\t\t<absoluteTime>" + reactionTimer.getReactionStartTime() + "</absoluteTime>\n");				
					bw.write("\t\t<experimentTime>" + reactionTimer.getRelativeStartTime() + "</experimentTime>\n");				
					bw.write("\t\t<comment>" + reactionTimer.getComment() + "</comment>\n");
					bw.write("\t</reactionMeasurement>\n");
				}
			}

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void close(String age, String idNr, String gender, String diagnosisNr, String speed, String hands)
	{
		this.age = age;
		this.idNr = idNr;
		this.gender = gender;
		this.diagnosisNr = diagnosisNr;
		this.speed = speed; 
		this.hands = hands;
		translateVariables();

		if(isRunning)
		{

			isRunning = false;

			try {
				generateDataXML();
				bw.write("</report>\n");        
				bw.close();
				// open PDF file
				boolean suppressPDF = Simulator.getSettingsLoader().getSetting(Setting.Analyzer_suppressPDFPopup, 
						SimulationDefaults.Analyzer_suppressPDFPopup);
				if (!hasGeneratedReport){
					generateReport();
				}
				if(!suppressPDF) {
					Util.open(outputFolder + "/" + reportFileName);
				}

			} catch (IOException e) {

				e.printStackTrace();
			}
		}
	}

	public void generateReport()
	{
		try
		{
			System.out.println("GenerateReport()");
			//calculate data of driving deviation and steadiness for all three parts
			try {
				meanDeviation_part1 = calculateCarData(outputFolder + "/carData.txt");
				System.out.println("meanDev part 1" + meanDeviation_part1);
			} catch (Exception e) {
				meanDeviation_part1 = 0.0f;
				System.out.println("meanDeviation part 1 failed");
			}
			try {
				meanDeviation_part2 = calculateCarData(outputFolder + "/positionData_2.txt");
				System.out.println("meanDev part 2" + meanDeviation_part2);
			} catch (Exception e) {
				meanDeviation_part2 = 0.0f;
				System.out.println("meanDeviation part 2 failed");
			}
			try {
				meanDeviation_part3 = calculateCarData(outputFolder + "/positionData_3.txt");
				System.out.println("meanDev part 3" + meanDeviation_part3);
			} catch (Exception e) {
				meanDeviation_part3 = 0.0f;
				System.out.println("meanDeviation part 3 failed");
			}

			// open XML data source
			//JRDataSource dataSource = new JaxenXmlDataSource(new File(outputFolder + "/" + dataFileName),
			//	"report/reactionMeasurement");

			JRDataSource dataSource = new JaxenXmlDataSource(new File(outputFolder + "/reactionData.xml"),
					"report/reactionMeasurement");
			//get report template for reaction measurement
			//InputStream reportStream = new FileInputStream("assets/JasperReports/templates/reactionMeasurement.jasper");
			//InputStream inputStream = new FileInputStream("assets/JasperReports/templates/reactionMeasurement.jrxml");
			InputStream inputStream = new FileInputStream("assets/JasperReports/templates/assessmentResults.jrxml");
			JasperDesign design = JRXmlLoader.load(inputStream);
			JasperReport report = JasperCompileManager.compileReport(design);

			// fill report with parameters and data
			Map<String, Object> parameters = getParameters();
			JasperPrint print = JasperFillManager.fillReport(report, parameters, dataSource);

			// create PDF file
			boolean succesful = false;
			while (!succesful) {
				long start = System.currentTimeMillis();
				try {
					JasperExportManager.exportReportToPdfFile(print, outputFolder + "/" + reportFileName);
					succesful = true;
					hasGeneratedReport = true;
				} catch (Exception e) {
					//custom title, error icon
					JOptionPane.showMessageDialog(null,
							"Please close the report file before proceeding: \n" + outputFolder + "/" + reportFileName +
							"\n L�pnr: " + sim.getIdNr(),
							"PDF creation error",
							JOptionPane.ERROR_MESSAGE);
				}
				System.out.println("PDF creation time : " + (System.currentTimeMillis() - start) + " ms");
				System.out.println(outputFolder + reportFileName);
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
	}



	private Map<String, Object> getParameters() 
	{
		SettingsLoader settingsLoader = Simulator.getDrivingTask().getSettingsLoader();

		Map<String, Object> parameters = new HashMap<String, Object>();

		String groupLeft = settingsLoader.getSetting(Setting.ReactionMeasurement_groupLeft, "    ");
		if(!groupLeft.isEmpty())
			parameters.put("groupLeft", groupLeft);

		String groupRight = settingsLoader.getSetting(Setting.ReactionMeasurement_groupRight, "     ");
		if(!groupRight.isEmpty())
			parameters.put("groupRight", groupRight);

		parameters.put("age", age);
		parameters.put("gender", gender);
		parameters.put("diagnosisNr", diagnosisNr);
		parameters.put("idNr", idNr);
		parameters.put("hands", hands);
		parameters.put("speed", speed);
		parameters.put("meanDeviation1", String.format("%.2f", meanDeviation_part1));
		parameters.put("meanDeviation2", String.format("%.2f", meanDeviation_part2));
		parameters.put("meanDeviation3", String.format("%.2f", meanDeviation_part3));

		return parameters;
	}

	/**
	 *  calculate the drivers steadiness on road 
	 */
	public float calculateCarData(String fileName){
		CarPositionReader carPositionReader = new CarPositionReader();
		LinkedList<Vector3f> carPositionList = new LinkedList<Vector3f>();
		float roadWidth = 10.0f; //TODO what is a good value here????
		DeviationComputer devComp = sim.getDeviationComputer();
		LinkedList<DataUnit> dataUnitList = new LinkedList<DataUnit>();
		Long initialTimeStamp = 0l;
		float area = 0;
		float lengthOfIdealLine = 1;
		float meanDeviation = 0;

		carPositionReader.initReader(fileName, true);
		carPositionReader.loadDriveData();

		carPositionList = carPositionReader.getCarPositionList();

		for(Vector3f carPos : carPositionList){
			devComp.addWayPoint(carPos);
		}

		dataUnitList = carPositionReader.getDataUnitList();

		if(dataUnitList.size() > 0)
			initialTimeStamp = dataUnitList.get(0).getDate().getTime();

		//float[] areaArray = new float[1000]; 
		try {

			area = devComp.getDeviation();
			//areaArray = devComp.getAreaPoints();
			lengthOfIdealLine = devComp.getLengthOfIdealLine();
			meanDeviation = (float)area/lengthOfIdealLine;
			//			System.out.println("Area between ideal line and driven line: " + area);
			//			System.out.println("Length of ideal line: " + lengthOfIdealLine);
			//			System.out.println("Mean deviation: " + (float)area/lengthOfIdealLine + "\n");
		} catch (Exception e) {
			System.out.println(e.getMessage() + "\n");
		}
		return meanDeviation;
	}

	/*translates the variable names from the input screen to the right value for the result presentation*/
	private void translateVariables() {

		if(speed.equals("low"))
			speed = "40 km/h";	
		else if(speed.equals("middle"))
			speed = "50 km/h";
		else speed = "60 km/h";

		if(hands.equals("both"))
			hands = "-";
		else if(hands.equals("right"))
			hands = "endas höger hand";
		else hands = "endast vänster hand";
	}



}
