package com.aim.project.sdsstp.instance.reader;


import java.io.*;
import java.nio.file.Path;
import java.util.Random;

import com.aim.project.sdsstp.SDSSTPObjectiveFunction;
import com.aim.project.sdsstp.instance.SDSSTPInstance;
import com.aim.project.sdsstp.instance.SDSSTPLocation;
import com.aim.project.sdsstp.interfaces.ObjectiveFunctionInterface;
import com.aim.project.sdsstp.interfaces.SDSSTPInstanceInterface;
import com.aim.project.sdsstp.interfaces.SDSSTPInstanceReaderInterface;

/**
 * 
 * @author Warren G. Jackson
 * @since 26/03/2021
 * 
 * Methods needing to be implemented:
 * - public SDSSTPInstanceInterface readSDSSTPInstance(Path path, Random random)
 */
public class SDSSTPInstanceReader implements SDSSTPInstanceReaderInterface {

	private static SDSSTPInstanceReader oInstance;
	
	private SDSSTPInstanceReader() {
		
	}
	
	public static SDSSTPInstanceReader getInstance() {
		
		if(oInstance == null) {
			
			oInstance = new SDSSTPInstanceReader();
		}
		
		return oInstance;
	}
	
	
	@Override
	public SDSSTPInstanceInterface readSDSSTPInstance(Path path, Random random) {

		String name = null;

		int numOfLandmarks = 0;

		int [][] timeMatrix = new int[0][];

		int [] timeFromOffice = new int[0];
		int [] timeToOffice = new int[0];
		int [] visitDuration = new int[0];
		SDSSTPLocation oTourOffice = null;

		SDSSTPLocation[] aoLandmarks = new SDSSTPLocation[0];

		ObjectiveFunctionInterface oObjectiveFunction;

		//TODO read in time
		try {
			BufferedReader in = new BufferedReader(new FileReader(path.toFile()));
			String line;
			while(true){
				line = in.readLine();

				//if eof is reached
				if(line == null){
					break;
				}

				//read in name
				if(line.contains("NAME:")){
					name = in.readLine();
					continue;
				}

				//read in number of landmarks
				if(line.contains("LANDMARKS:")){
					numOfLandmarks = Integer.parseInt(in.readLine());
					timeMatrix = new int[numOfLandmarks][numOfLandmarks];
					continue;
				}

				//read in time matrix
				if(line.contains("TIME_MATRIX:")){
					//read line by line
					for(int i=0;i<numOfLandmarks;i++){
						line = in.readLine();
						String [] tempTimes = line.split(" ");

						for(int j=0;j<numOfLandmarks;j++){
							timeMatrix[i][j] = Integer.parseInt(tempTimes[j]);
						}
					}
					continue;
				}

				//read in times from the office
				if(line.contains("TIME_FROM_OFFICE:")){
					timeFromOffice = new int[numOfLandmarks];
					line = in.readLine();
					String [] tempTimes = line.split(" ");
					for(int i=0;i<numOfLandmarks;i++){
						timeFromOffice[i] = Integer.parseInt(tempTimes[i]);
					}
					continue;
				}

				//read in times to the office
				if(line.contains("TIME_TO_OFFICE:")){
					timeToOffice = new int[numOfLandmarks];
					line = in.readLine();
					String [] tempTimes = line.split(" ");
					for(int i=0;i<numOfLandmarks;i++){
						timeToOffice[i] = Integer.parseInt(tempTimes[i].trim());
					}
					continue;
				}

				//read in visit durations of each landmark
				if(line.contains("VISIT_DURATION:")){
					visitDuration = new int[numOfLandmarks];
					line = in.readLine();
					String [] tempDuration = line.split(" ");
					for(int i=0;i<numOfLandmarks;i++){
						visitDuration[i] = Integer.parseInt(tempDuration[i].trim());
					}
					continue;
				}

				//read in office location
				if(line.contains("OFFICE_LOCATION:")){
					line = in.readLine();
					String[] officeCoords = line.split(" ");
					oTourOffice = new SDSSTPLocation(Double.parseDouble(officeCoords[0]),Double.parseDouble(officeCoords[1]));
					continue;
				}

				//read in landmark locations
				if(line.contains("LANDMARK_LOCATIONS:")){
					aoLandmarks = new SDSSTPLocation[numOfLandmarks];
					for(int i=0;i<numOfLandmarks;i++) {
						line = in.readLine();
						String[] landmarkCoords = line.split(" ");
						aoLandmarks[i] = new SDSSTPLocation(Double.parseDouble(landmarkCoords[0]), Double.parseDouble(landmarkCoords[1]));
					}
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}


		return new SDSSTPInstance(name,numOfLandmarks,oTourOffice,aoLandmarks,random,new SDSSTPObjectiveFunction(timeMatrix,timeFromOffice,timeToOffice,visitDuration));
	}
}
