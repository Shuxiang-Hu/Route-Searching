package com.aim.project.sdsstp.heuristics;

import java.util.Arrays;
import java.util.Random;

import com.aim.project.sdsstp.interfaces.ObjectiveFunctionInterface;
import com.aim.project.sdsstp.interfaces.SDSSTPSolutionInterface;
import com.aim.project.sdsstp.interfaces.XOHeuristicInterface;
import com.aim.project.sdsstp.solution.SDSSTPSolution;
import com.aim.project.sdsstp.solution.SolutionRepresentation;

/**
 *
 * @author Warren G. Jackson
 * @since 26/03/2021
 *
 * Methods needing to be implemented:
 * - public double apply(SDSSTPSolutionInterface solution, double depthOfSearch, double intensityOfMutation)
 * - public double apply(SDSSTPSolutionInterface p1, SDSSTPSolutionInterface p2, SDSSTPSolutionInterface c, double depthOfSearch, double intensityOfMutation)
 * - public boolean isCrossover()
 * - public boolean usesIntensityOfMutation()
 * - public boolean usesDepthOfSearch()
 */
public class CX implements XOHeuristicInterface {

	private final Random random;

	private ObjectiveFunctionInterface f;

	public CX(Random random) {

		this.random = random;
	}

	@Override
	public double apply(SDSSTPSolutionInterface solution, double depthOfSearch, double intensityOfMutation) {

		return -1;
	}

	@Override
	public double apply(SDSSTPSolutionInterface p1, SDSSTPSolutionInterface p2,
			SDSSTPSolutionInterface c, double depthOfSearch, double intensityOfMutation) {
		if(p1.getObjectiveFunctionValue() != f.getObjectiveFunctionValue(p1.getSolutionRepresentation())){
			System.out.println("Wrong obj value recorded.");
		}
		//System.out.println("Beginning of CX");
		int timesOfMuatation = (int)Math.floor(intensityOfMutation/0.2)+1;
		int numOfLandmarks = p1.getNumberOfLandmarks();

		SDSSTPSolutionInterface parent1 = p1.clone();
		SDSSTPSolutionInterface parent2 = p2.clone();
		for(int j=0;j<timesOfMuatation;j++){

			int start = random.nextInt(numOfLandmarks);
			int curIndex = start;

			boolean [] cycle1 = new boolean[numOfLandmarks]; //record indices in the first cycle
			Arrays.fill(cycle1,false);
			while(true){
				cycle1[curIndex] = true;
				int valueInP1 = ((SolutionRepresentation)parent1.getSolutionRepresentation()).getLandmark(curIndex);
				curIndex= ((SDSSTPSolution)parent2).getIndex(valueInP1);
				if(curIndex == start){
					break;
				}
			}

			//swap indices visited in the first cycle
			for(int i=0;i<numOfLandmarks;i++){
				if(cycle1[i]){
					int temp = ((SolutionRepresentation)parent1.getSolutionRepresentation()).getLandmark(i);
					((SolutionRepresentation)parent1.getSolutionRepresentation()).setLandmark(i,((SolutionRepresentation)p2.getSolutionRepresentation()).getLandmark(i));
					((SolutionRepresentation)parent2.getSolutionRepresentation()).setLandmark(i,temp);
				}
			}

		}

		int [] rep1 = parent1.getSolutionRepresentation().getSolutionRepresentation();
		int [] rep2 = parent2.getSolutionRepresentation().getSolutionRepresentation();
		c.getSolutionRepresentation().setSolutionRepresentation(random.nextDouble()<0.5? rep1 : rep2);
		c.setObjectiveFunctionValue(f.getObjectiveFunctionValue(c.getSolutionRepresentation()));
		return c.getObjectiveFunctionValue();
	}

	@Override
	public boolean isCrossover() {

		return true;
	}

	@Override
	public boolean usesIntensityOfMutation() {
		return true;
	}

	@Override
	public boolean usesDepthOfSearch() {
		return false;
	}

	@Override
	public void setObjectiveFunction(ObjectiveFunctionInterface f) {

		this.f = f;
	}
}
