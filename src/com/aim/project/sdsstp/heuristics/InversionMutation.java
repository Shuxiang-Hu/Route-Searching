package com.aim.project.sdsstp.heuristics;

import java.util.Random;

import com.aim.project.sdsstp.interfaces.HeuristicInterface;
import com.aim.project.sdsstp.interfaces.SDSSTPSolutionInterface;
import com.aim.project.sdsstp.solution.SDSSTPSolution;
import com.aim.project.sdsstp.solution.SolutionRepresentation;

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
public class InversionMutation extends HeuristicOperators implements HeuristicInterface {
	
	private final Random random;
	
	public InversionMutation(Random random) {
	
		super();
		
		this.random = random;
	}

	@Override
	public double apply(SDSSTPSolutionInterface solution, double depthOfSearch, double intensityOfMutation) {
		int timesOfMuatation = (int)Math.ceil(intensityOfMutation/0.2);
		int numOfLandmarks = solution.getNumberOfLandmarks();


		for(int i=0;i<timesOfMuatation;i++){
			SDSSTPSolutionInterface temp = solution.clone();

			//get two random numbers
			int r1 = random.nextInt(numOfLandmarks);
			int r2 = random.nextInt(numOfLandmarks);

			//repeat until get two different indices
			while(r1 == r2){
				r1 = random.nextInt(numOfLandmarks);
				r2 = random.nextInt(numOfLandmarks);
			}

			int start = Math.min(r1,r2);
			int end = Math.max(r1,r2);
			int p1 = start;
			int p2 = end;
			temp.setObjectiveFunctionValue(deltaEvalIM(solution,start, end));

			int []tempRepresentation = temp.getSolutionRepresentation().getSolutionRepresentation();
			while(p1<=p2){
				int tempInt = tempRepresentation[p1];
				tempRepresentation[p1] = tempRepresentation[p2];
				tempRepresentation[p2] = tempInt;
				p1++;
				p2--;
			}

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
