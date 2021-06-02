package com.aim.project.sdsstp.heuristics;

import java.util.Random;

import com.aim.project.sdsstp.SDSSTPObjectiveFunction;
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
public class AdjacentSwap extends HeuristicOperators implements HeuristicInterface {

	private final Random random;

	public AdjacentSwap(Random random) {

		this.random = random;
	}

	@Override
	public double apply(SDSSTPSolutionInterface solution, double depthOfSearch, double intensityOfMutation) {

		int timesOfSwap = (int)Math.pow(2,Math.floor(intensityOfMutation/0.2));

		for(int i=0;i<timesOfSwap;i++){

			SDSSTPSolutionInterface temp = solution.clone();
			int index = random.nextInt(solution.getNumberOfLandmarks());
			this.performAdjacentSwap(temp, index);

			temp.setObjectiveFunctionValue(deltaEvalAS(solution,index));

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

