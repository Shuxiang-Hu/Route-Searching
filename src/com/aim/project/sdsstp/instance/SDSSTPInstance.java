package com.aim.project.sdsstp.instance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import com.aim.project.sdsstp.SDSSTPMath;
import com.aim.project.sdsstp.interfaces.ObjectiveFunctionInterface;
import com.aim.project.sdsstp.interfaces.SDSSTPInstanceInterface;
import com.aim.project.sdsstp.interfaces.SDSSTPSolutionInterface;
import com.aim.project.sdsstp.interfaces.SolutionRepresentationInterface;
import com.aim.project.sdsstp.solution.SDSSTPSolution;
import com.aim.project.sdsstp.solution.SolutionRepresentation;

/**
 * 
 * @author Warren G. Jackson
 * @since 26/03/2021
 * 
 * Methods needing to be implemented:
 * - public SDSSTPSolution createSolution(InitialisationMode mode)
 */
public class SDSSTPInstance implements SDSSTPInstanceInterface {

	private final String strInstanceName;

	private final int iNumberOfLandmarks;

	private final SDSSTPLocation oTourOffice;

	private final SDSSTPLocation[] aoLandmarks;

	private final Random oRandom;

	private final ObjectiveFunctionInterface oObjectiveFunction;

	public SDSSTPInstance(String strInstanceName, int iNumberOfLandmarks,
			com.aim.project.sdsstp.instance.SDSSTPLocation oTourOffice,
			com.aim.project.sdsstp.instance.SDSSTPLocation[] aoLandmarks, Random oRandom,
			ObjectiveFunctionInterface f) {

		this.strInstanceName = strInstanceName;
		this.iNumberOfLandmarks = iNumberOfLandmarks;
		this.oTourOffice = oTourOffice;
		this.aoLandmarks = aoLandmarks;
		this.oRandom = oRandom;
		this.oObjectiveFunction = f;
	}

	@Override
	public SDSSTPSolution createSolution(InitialisationMode mode) {
		int[] representation = new int[iNumberOfLandmarks];
		if(mode == InitialisationMode.RANDOM){
			//initialize array
			Arrays.setAll(representation,i->i);

			//shuffle the array randomly with Fisher and Yates' algorithm
			SDSSTPMath.shuffle(representation,oRandom);
		}
		else if(mode == InitialisationMode.CONSTRUCTIVE){

			//this array stores whether the landmark specified by the index has been added to the initial solution
			boolean [] ifSelected = new boolean[iNumberOfLandmarks];
			Arrays.fill(ifSelected,false);

			//find the closest site from the office
			representation[0] = getSDSSTPObjectiveFunction().getClosestFromOffice(oRandom,ifSelected);
			ifSelected[representation[0]] = true;

			if(getNumberOfLandmarks()>1){
				for(int i=1;i<getNumberOfLandmarks();i++){
					representation[i] = getSDSSTPObjectiveFunction().getClosestFrom(representation[i-1],oRandom,ifSelected);
					ifSelected[representation[i]] = true;
					//System.out.print(representation[i]);
				}
			}

		}

		SolutionRepresentation initialRepresentation = new SolutionRepresentation(representation);
		//System.out.println(initialRepresentation.toString());
		int objValue = oObjectiveFunction.getObjectiveFunctionValue(initialRepresentation);
		return new SDSSTPSolution(initialRepresentation,objValue,iNumberOfLandmarks);
	}

	@Override
	public ObjectiveFunctionInterface getSDSSTPObjectiveFunction() {

		return oObjectiveFunction;
	}

	@Override
	public int getNumberOfLandmarks() {

		return iNumberOfLandmarks;
	}

	@Override
	public SDSSTPLocation getLocationForLandmark(int deliveryId) {

		return aoLandmarks[deliveryId];
	}

	@Override
	public SDSSTPLocation getTourOffice() {

		return this.oTourOffice;
	}

	@Override
	public ArrayList<SDSSTPLocation> getSolutionAsListOfLocations(SDSSTPSolutionInterface oSolution) {

		ArrayList<SDSSTPLocation> locs = new ArrayList<>();
		locs.add(oTourOffice);
		int[] aiDeliveries = oSolution.getSolutionRepresentation().getSolutionRepresentation();
		for (int i = 0; i < aiDeliveries.length; i++) {
			locs.add(getLocationForLandmark(aiDeliveries[i]));
		}
		locs.add(oTourOffice);
		return locs;
	}

	@Override
	public String getInstanceName() {
		
		return strInstanceName;
	}

}
