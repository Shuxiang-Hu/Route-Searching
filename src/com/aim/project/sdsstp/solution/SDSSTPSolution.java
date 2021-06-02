package com.aim.project.sdsstp.solution;

import com.aim.project.sdsstp.interfaces.SDSSTPSolutionInterface;
import com.aim.project.sdsstp.interfaces.SolutionRepresentationInterface;

public class SDSSTPSolution implements SDSSTPSolutionInterface {

	private SolutionRepresentationInterface representation;
	
	private double objectiveFunctionValue;
	
	private int numberOfVariables;
	
	public SDSSTPSolution(SolutionRepresentationInterface representation, double objectiveFunctionValue, int numberOfVariables) {
		
		this.representation = representation;
		this.objectiveFunctionValue = objectiveFunctionValue;
		this.numberOfVariables = numberOfVariables;
	}

	@Override
	public double getObjectiveFunctionValue() {

		return objectiveFunctionValue;
	}

	@Override
	public void setObjectiveFunctionValue(double objectiveFunctionValue) {
		
		this.objectiveFunctionValue = objectiveFunctionValue;

	}

	@Override
	public SolutionRepresentationInterface getSolutionRepresentation() {

		return representation;
	}
	
	@Override
	public SDSSTPSolutionInterface clone() {
		SolutionRepresentationInterface newRepresentation = representation.clone();
		SDSSTPSolutionInterface newSolution = new SDSSTPSolution(newRepresentation,objectiveFunctionValue,numberOfVariables);
		return newSolution;
	}

	@Override
	public int getNumberOfLandmarks() {
		return numberOfVariables;
	}

	public int getIndex(int landmarkID){
		return ((SolutionRepresentation)representation).getIndex(landmarkID);
	}
}
