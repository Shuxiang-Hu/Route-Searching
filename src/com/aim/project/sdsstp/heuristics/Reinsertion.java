package com.aim.project.sdsstp.heuristics;

import java.util.Random;

import com.aim.project.sdsstp.interfaces.HeuristicInterface;
import com.aim.project.sdsstp.interfaces.SDSSTPSolutionInterface;

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
public class Reinsertion extends HeuristicOperators implements HeuristicInterface {

	private final Random random;
	
	public Reinsertion(Random random) {

		super();
		this.random = random;
	}

	@Override
	public double apply(SDSSTPSolutionInterface solution, double depthOfSearch, double intensityOfMutation) {


		int timesOfMuatation = (int)Math.floor(intensityOfMutation/0.2)+1;
		int numOfLandmarks = solution.getNumberOfLandmarks();


		for(int i=0;i<timesOfMuatation;i++){
			SDSSTPSolutionInterface temp = solution.clone();
			//select the landmark to be reinserted
			int originIndex = random.nextInt(numOfLandmarks);
			int newIndex = random.nextInt(numOfLandmarks);
			//select a different index
			while (newIndex == originIndex){
				newIndex = random.nextInt(numOfLandmarks);
			}


			//for landmarks between the new index  and the origin index ,
			//move the landmark in origin index one by one towards the new index
			int ifOrginSmallerThanNew = originIndex < newIndex ? 1:-1;

			//if origin index is smaller than new index
			//for(j = originIndex + 1;j<=newIndex;j++)
			//	swap representation[j-1] and representation[j]
			//if origin index is greater than new index
			//for(j = originIndex - 1;j>=newIndex;j--)
			//	swap representation[j+1] and representation[j]
			int [] tempRepresentation = temp.getSolutionRepresentation().getSolutionRepresentation();
			for(int j = originIndex + ifOrginSmallerThanNew;j * ifOrginSmallerThanNew<=newIndex*ifOrginSmallerThanNew;j+=ifOrginSmallerThanNew){
				int tempInt = tempRepresentation[j];
				tempRepresentation[j] = tempRepresentation[j-ifOrginSmallerThanNew];
				tempRepresentation[j-ifOrginSmallerThanNew] = tempInt;
			}


			temp.setObjectiveFunctionValue(deltaEvalRIM(solution,originIndex,newIndex));

			solution.setObjectiveFunctionValue(temp.getObjectiveFunctionValue());
			solution.getSolutionRepresentation().setSolutionRepresentation(temp.getSolutionRepresentation().getSolutionRepresentation());
		}

		return solution.getObjectiveFunctionValue();
	}

	@Override
	public boolean isCrossover() {

		return false;
	}

	@Override
	public boolean usesIntensityOfMutation() {

		return true;
	}

	@Override
	public boolean usesDepthOfSearch() {

		return false;
	}

}
