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
	private int count = 0;
	private float meanDeviation_part1, meanDeviation_part2, meanDeviation_part3;
	private String age, gender, diagnosisNr, idNr, speed, hands;


	public ReactionLogger(Simulator sim) {
		this.sim = sim;
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
	
	
	public void add(String reactionGroup, int reactionResult, long reactionTime, 
			long absoluteTime, long experimentTime, String comment)
	{
		if(!isRunning)
			start();
		
		if(isRunning)
		{
			
			try {
				
	            bw.write("\t<reactionMeasurement>\n");
	            bw.write("\t\t<reactionID>" + count + "</reactionID>\n");
	            bw.write("\t\t<reactionGroup>" + reactionGroup + "</reactionGroup>\n");	
				bw.write("\t\t<reactionResult>" + reactionResult + "</reactionResult>\n");				
				bw.write("\t\t<reactionTime>" + reactionTime + "</reactionTime>\n");				
				bw.write("\t\t<absoluteTime>" + absoluteTime + "</absoluteTime>\n");				
				bw.write("\t\t<experimentTime>" + experimentTime + "</experimentTime>\n");				
				bw.write("\t\t<comment>" + comment + "</comment>\n");
	            bw.write("\t</reactionMeasurement>\n");
	            count = count+1;
	            
			} catch (IOException e) {

				e.printStackTrace();
			}
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
				
				bw.write("</report>\n");        
				bw.close();
				generateReport();
				
				
			} catch (IOException e) {
	
				e.printStackTrace();
			}
		}
	}
	
	
	private void generateReport()
	{
		try
		{
			//calculate data of driving deviation and steadiness for all three parts
			try {
				meanDeviation_part1 = calculateCarData(outputFolder + "/carData.txt");
			} catch (Exception e) {
				meanDeviation_part1 = 0.0f;
				System.out.println("meanDeviation part 1 failed");
			}
			try {
				meanDeviation_part2 = calculateCarData(outputFolder + "/positionData_2.txt");
			} catch (Exception e) {
				meanDeviation_part2 = 0.0f;
				System.out.println("meanDeviation part 2 failed");
			}
			try {
				meanDeviation_part3 = calculateCarData(outputFolder + "/positionData_3.txt");
			} catch (Exception e) {
				meanDeviation_part3 = 0.0f;
				System.out.println("meanDeviation part 3 failed");
			}
			
			// open XML data source
			//JRDataSource dataSource = new JaxenXmlDataSource(new File(outputFolder + "/" + dataFileName),
				//	"report/reactionMeasurement");
			
			JRDataSource dataSource = new JaxenXmlDataSource(new File("analyzerData/Analyzer_1fel/reactionData.xml"),
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
			long start = System.currentTimeMillis();
			System.out.println("Before crash");
			boolean succesful = false;
			while (!succesful) {
				try {
					JasperExportManager.exportReportToPdfFile(print, outputFolder + "/" + reportFileName);
					succesful = true;
				} catch (Exception e) {
					//custom title, error icon
					JOptionPane.showMessageDialog(null,
					    "Please close the report file before proceeding: \n" + outputFolder + "/" + reportFileName +
					    "\n L�pnr: " + sim.getTestNr(),
					    "PDF creation error",
					    JOptionPane.ERROR_MESSAGE);
				}
			}
			System.out.println("PDF creation time : " + (System.currentTimeMillis() - start) + " ms");
			
			// open PDF file
			//boolean suppressPDF = Simulator.getSettingsLoader().getSetting(Setting.Analyzer_suppressPDFPopup, 
			//		SimulationDefaults.Analyzer_suppressPDFPopup);
			boolean suppressPDF = true;
			if(!suppressPDF)
				Util.open(outputFolder + "/" + reportFileName);

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
	
	/* calculate the drivers steadiness on road */
	public float calculateCarData(String fileName){
		CarPositionReader carPositionReader = new CarPositionReader();
		LinkedList<Vector3f> carPositionList = new LinkedList<Vector3f>();
		float roadWidth = 10.0f; //what is a good value here????
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
