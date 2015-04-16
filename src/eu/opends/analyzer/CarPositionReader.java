package eu.opends.analyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import com.jme3.math.Vector3f;

import eu.opends.drivingTask.DrivingTask;

public class CarPositionReader {
	private File inFile;
	private BufferedReader inputReader;
	private String nameOfDrivingTaskFile;
	private String nameOfDriver;
	private Date fileDate;
	
	private LinkedList<Vector3f> carPositionList = new LinkedList<Vector3f>();
	private LinkedList<DataUnit> dataUnitList = new LinkedList<DataUnit>();
	
	
	public boolean initReader(String filePath, boolean verbose) 
	{
		String inputLine;
		String[] splittedLineArray;

		inFile = new File(filePath);
		if (!inFile.isFile()) {
			System.err.println("File " + inFile.toString()
					+ " could not be found.");
		}
		try {
			inputReader = new BufferedReader(new FileReader(inFile));

			// Read in the name of the driving task
			inputLine = inputReader.readLine();
			splittedLineArray = inputLine.split(": ");

			nameOfDrivingTaskFile = splittedLineArray[1];
			if(verbose)
				System.out.println("Driving Task: " + splittedLineArray[1]);


			
			// Read in the date and time, at which the data-file has been
			// created.
			inputLine = inputReader.readLine();
			splittedLineArray = inputLine.split(": ");
			try {
				// Save the date
				fileDate = new SimpleDateFormat("yyyy_mm_dd-hh_mm_ss")
						.parse(splittedLineArray[1]);
				if(verbose)
					System.out.println("Creation Time: " + fileDate);

			} catch (ParseException e) {
				System.err.println("The date could not be read: " + inputLine
						+ " is no valid date.");
				fileDate = null;
			}

			// Read in name of the driver
			inputLine = inputReader.readLine();
			splittedLineArray = inputLine.split(": ");
			nameOfDriver = splittedLineArray[1];
			if(verbose)
				System.out.println("Driver: " + nameOfDriver);

			// Read in the used format, so it can be skipped.
			inputLine = inputReader.readLine();
			
		} catch (IOException e) {
			//e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	
	public boolean loadDriveData() 
	{		
		try {
			// get drive data
			String inputLine = inputReader.readLine();
			
			Vector3f previousPos = null;

			while (inputLine != null) 
			{
				Vector3f carPosition = parseCarPosition(inputLine);
				carPositionList.add(carPosition);
								
				Long timeStamp = parseTimeStamp(inputLine);
				
				if(previousPos == null)
					previousPos = carPosition;

				previousPos = carPosition;

				DataUnit dataUnit = new DataUnit(new Date(timeStamp), carPosition);
				dataUnitList.add(dataUnit);
				
				inputLine = inputReader.readLine();
			}


		} catch (IOException e) {
			return false;
		}
		
		return true;
	}
	
	
	public String getNameOfDriver() 
	{
		return nameOfDriver;
	}


	public Date getFileDate() 
	{
		return fileDate;
	}
	

	public String getNameOfDrivingTaskFile() 
	{
		return nameOfDrivingTaskFile;
	}

	
	public LinkedList<Vector3f> getCarPositionList()
	{
		return carPositionList;
	}
	
	
	public LinkedList<DataUnit> getDataUnitList()
	{
		return dataUnitList;
	}
	
	
	public boolean isValidAnalyzerFile(File analyzerFile) 
	{
		String analyzerFilePath = analyzerFile.getPath();
		
		try {
			
			boolean errorOccured = !initReader(analyzerFilePath, false);
			if(errorOccured)
			{
				System.err.println("File is not a valid analyzer file: " + analyzerFilePath);
				return false;
			}
			
		} catch (Exception e) {
			
			System.err.println("File is not a valid analyzer file: " + analyzerFilePath);
			return false;
		}
		
		try {
			
			// check whether specified driving task is valid
			String drivingTaskFileName = getNameOfDrivingTaskFile();
			File drivingTaskFile = new File(drivingTaskFileName);				
			if(!DrivingTask.isValidDrivingTask(drivingTaskFile))
			{
				System.err.println("File '" + analyzerFilePath + 
						"'\npoints to an invalid driving task file : " + drivingTaskFileName);
				return false;
			}
			
		} catch (Exception e) {
			
			System.err.println("File '" + analyzerFilePath + "'\npoints to an invalid driving task file");
			return false;
		}
		
		return true;
	}
	
	
	private Long parseTimeStamp(String inputLine) 
	{
		String[] splittedLineArray = inputLine.split(":");
		return Long.parseLong(splittedLineArray[0]);
	}
	

	private Vector3f parseCarPosition(String inputLine) 
	{
		String[] splittedLineArray = inputLine.split(":");

		return new Vector3f(Float.parseFloat(splittedLineArray[1]), Float
				.parseFloat(splittedLineArray[2]), Float
				.parseFloat(splittedLineArray[3]));
	}
}
