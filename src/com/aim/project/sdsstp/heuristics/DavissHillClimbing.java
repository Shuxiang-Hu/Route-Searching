package com.aim.project.sdsstp.heuristics;

import java.util.Arrays;
import java.util.Random;

import com.aim.project.sdsstp.SDSSTPMath;
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
public class DavissHillClimbing extends HeuristicOperators implements HeuristicInterface {
	
	private final Random random;
	
	public DavissHillClimbing(Random random) {
	
		this.random = random;
	}

	@Override
	public double apply(SDSSTPSolutionInterface solution, double depthOfSearch, double intensityOfMutation) {
		int times = (int)Math.floor(depthOfSearch/0.2)+1;
		int numberOfLandmarks = solution.getNumberOfLandmarks();


		//perform hill climbing times times
		for(int i=0;i<times;i++){
			//initialize perturbation of operations
			int[] perturbation = new int [numberOfLandmarks];
			Arrays.setAll(perturbation, j->j);
			SDSSTPMath.shuffle(perturbation,random);

			for(int j = 0;j<numberOfLandmarks;j++){
				SDSSTPSolutionInterface temp = solution.clone();
				performAdjacentSwap(temp,perturbation[j]);
				temp.setObjectiveFunctionValue(deltaEvalAS(solution,perturbation[j]));

				//update solution
				int newLoss = (int) temp.getObjectiveFunctionValue();
				int oldLoss = (int) solution.getObjectiveFunctionValue();
				if(newLoss <= oldLoss){
					solution.setObjectiveFunctionValue(newLoss);
					solution.getSolutionRepresentation().setSolutionRepresentation(temp.getSolutionRepresentation().getSolutionRepresentation());
				}
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
