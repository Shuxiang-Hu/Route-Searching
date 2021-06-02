package com.aim.project.sdsstp.heuristics;


import java.util.Random;

import com.aim.project.sdsstp.interfaces.HeuristicInterface;
import com.aim.project.sdsstp.interfaces.SDSSTPSolutionInterface;
import com.aim.project.sdsstp.solution.SDSSTPSolution;


/**
 * 
 * @author Warren G. Jackson
 * @since 26/03/2021
 * 
 * Methods needing to be implemented:
 * - public double apply(SDSSTPSolutionInterface solution, double depthOfSearch, double intensityOfMutation)
 * - public boolean isCrossover()
 * - public boolean usesIntensityOfMutation()
 * - public boolean usesDepthOfSearch()
 */
public class NextDescent extends HeuristicOperators implements HeuristicInterface {
	
	private final Random random;
	
	public NextDescent(Random random) {
	
		this.random = random;
	}

	@Override
	public double apply(SDSSTPSolutionInterface solution, double depthOfSearch, double intensityOfMutation) {

		int times = (int)Math.floor(depthOfSearch/0.2)+1;
		int numberOfImprovements = 0;
		int numberOfIterationsWithoutImprovement = 0;
		int numberOfLandmarks = solution.getNumberOfLandmarks();

		int index = random.nextInt(numberOfLandmarks);


		//while both of termination criteria are not satisfied
		while(numberOfIterationsWithoutImprovement < numberOfLandmarks & numberOfImprovements < times){

			SDSSTPSolutionInterface temp = solution.clone();
			performAdjacentSwap(temp,index);
			temp.setObjectiveFunctionValue(deltaEvalAS(solution,index));

			index = (index+1)%numberOfLandmarks;

			int newLoss = (int) temp.getObjectiveFunctionValue();
			int oldLoss = (int) solution.getObjectiveFunctionValue();
			if(newLoss < oldLoss){

				numberOfImprovements ++;
				solution.setObjectiveFunctionValue(newLoss);
				solution.getSolutionRepresentation().setSolutionRepresentation(temp.getSolutionRepresentation().getSolutionRepresentation());
				numberOfIterationsWithoutImprovement = 0;
			}
			else{
				numberOfIterationsWithoutImprovement++;
			}
		}

		return solution.getObjectiveFunctionValue();
	}


	@Override
	public boolean isCrossover() {

		return false;
	}

	@Override
	public boolean usesIntensityOfMutation() {

		return false;
	}

	@Override
	public boolean usesDepthOfSearch() {


		return true;
	}
}
