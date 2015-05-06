package eu.opends.analyzer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import eu.opends.car.Car;
import eu.opends.tools.Util;

public class CarPositionWriter {
	private Calendar startTime =new GregorianCalendar();
	
	private ArrayList<DataUnit> arrayDataList;
	private BufferedWriter out;
	private File outFile;
	private String newLine = System.getProperty("line.separator");
	private Date lastAnalyzerDataSave;
	private String outputFolder;
	private Car car;
	private File analyzerDataFile;
	private boolean dataWriterEnabled = false;
	private String driverName = "";
	private Date curDate;
	private String relativeDrivingTaskPath;
	
	public CarPositionWriter(String outputFolder, Car car, String driverName, String absoluteDrivingTaskPath) 
	{		
		this.outputFolder = outputFolder;
		this.car = car;
		this.driverName = driverName;
		this.relativeDrivingTaskPath = getRelativePath(absoluteDrivingTaskPath);
		//System.err.println(relativeDrivingTaskPath);
		
		Util.makeDirectory(outputFolder);

		analyzerDataFile = new File(outputFolder + "/carData.txt");

		initWriter();
	}
	
	
	private String getRelativePath(String absolutePath)
	{
		URI baseURI = new File("./").toURI();
		URI absoluteURI = new File(absolutePath).toURI();
		URI relativeURI = baseURI.relativize(absoluteURI);
		
		return relativeURI.getPath();
	}


	public void initWriter() 
	{

		if (analyzerDataFile.getAbsolutePath() == null) 
		{
			System.err.println("Parameter not accepted at method initWriter.");
			return;
		}
		
		outFile = new File(analyzerDataFile.getAbsolutePath());
		
		int i = 2;
		while(outFile.exists()) 
		{
			analyzerDataFile = new File(outputFolder + "/carData(" + i + ").txt");
			outFile = new File(analyzerDataFile.getAbsolutePath());
			i++;
		}
		
		
		try {
			out = new BufferedWriter(new FileWriter(outFile));
			out.write("Driving Task: " + relativeDrivingTaskPath + newLine);
			out.write("Date-Time: "
					+ new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss")
							.format(new Date()) + newLine);
			out.write("Driver: " + driverName + newLine);
			out.write("Position (x,y,z)" + newLine);

		} catch (IOException e) {
			e.printStackTrace();
		}
		arrayDataList = new ArrayList<DataUnit>();
		lastAnalyzerDataSave = new Date();
	}


	/**
	 * Save the car data at a frequency of 20Hz. That class should be called in
	 * the update-method <code>Simulator.java</code>.
	 */
	public void saveAnalyzerData() 
	{
		curDate = new Date();

		if (curDate.getTime() - lastAnalyzerDataSave.getTime() >= 50) 
		{
			write(
					curDate,
					Math.round(car.getPosition().x * 1000) / 1000.0f,
					Math.round(car.getPosition().y * 1000) / 1000.0f,
					Math.round(car.getPosition().z * 1000) / 1000.0f);

			lastAnalyzerDataSave = curDate;
		}

	}

	
	/**
	 * 
	 * see eu.opends.analyzer.IAnalyzationDataWriter#write(float,
	 *      float, float, float, java.util.Date, float, float, boolean, float)
	 */
	public void write(Date curDate, float x, float y, float z) 
	{
		DataUnit row = new DataUnit(curDate, x, y, z);
		this.write(row);

	}
	

	/**
	 * Write data to the data pool. After 50 data sets, the pool is flushed to
	 * the file.
	 */
	public void write(DataUnit row)
	{
		arrayDataList.add(row);
		if (arrayDataList.size() > 50)
			flush();
	}
	

	public void flush() 
	{
		try {
			StringBuffer sb = new StringBuffer();
			for (DataUnit r : arrayDataList) {
				sb.append(r.getDate().getTime() + ":" + r.getXpos() + ":"
						+ r.getYpos() + ":" + r.getZpos() + newLine
						);
			}
			out.write(sb.toString());
			arrayDataList.clear();
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	
	public void quit() 
	{
		dataWriterEnabled = false;
		flush();
		try {
			if (out != null)
				out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	public boolean isDataWriterEnabled() 
	{
		return dataWriterEnabled;
	}

	
	public void setDataWriterEnabled(boolean dataWriterEnabled) 
	{
		this.dataWriterEnabled = dataWriterEnabled;
	}

	
	public void setStartTime() 
	{
		this.startTime = new GregorianCalendar();
	}
	
	
	public String getElapsedTime()
	{
		Calendar now = new GregorianCalendar();
		
		long milliseconds1 = startTime.getTimeInMillis();
	    long milliseconds2 = now.getTimeInMillis();
	    
	    long elapsedMilliseconds = milliseconds2 - milliseconds1;
	    
	    return "Time elapsed: " + new SimpleDateFormat("mm:ss.SSS").format(elapsedMilliseconds);
	}

}

