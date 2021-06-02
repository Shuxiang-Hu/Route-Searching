package com.aim.project.sdsstp.heuristics;

import java.util.Arrays;
import java.util.Random;

import com.aim.project.sdsstp.SDSSTPMath;
import com.aim.project.sdsstp.SDSSTPObjectiveFunction;
import com.aim.project.sdsstp.interfaces.ObjectiveFunctionInterface;
import com.aim.project.sdsstp.interfaces.SDSSTPSolutionInterface;
import com.aim.project.sdsstp.interfaces.SolutionRepresentationInterface;
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
public class OX implements XOHeuristicInterface {
	
	private final Random random;
	
	private ObjectiveFunctionInterface f;

	public OX(Random random) {
		
		this.random = random;
	}

	@Override
	public double apply(SDSSTPSolutionInterface solution, double depthOfSearch, double intensityOfMutation) {

		return -1;
	}

	@Override
	public double apply(SDSSTPSolutionInterface p1, SDSSTPSolutionInterface p2,
			SDSSTPSolutionInterface c, double depthOfSearch, double intensityOfMutation) {

		//if two parents are the same in order, then they will always produce children that are the same as themselves
		if(SDSSTPMath.isSame(p1.getSolutionRepresentation().getSolutionRepresentation(),
				p2.getSolutionRepresentation().getSolutionRepresentation())){
			return Integer.MAX_VALUE;
		}

		SDSSTPSolutionInterface parent1 = p1.clone();
		SDSSTPSolutionInterface parent2 = p2.clone();

		int timesOfMuatation = (int)Math.floor(intensityOfMutation/0.2)+1;
		int numOfLandmarks = p2.getNumberOfLandmarks();

		for(int i=0;i<timesOfMuatation;i++){
			//begin of mutation

			//initialize two children
			SDSSTPSolutionInterface child1 = parent1.clone();
			SDSSTPSolutionInterface child2 = parent2.clone();

			//get representations
			//generate two cut points
			int start = random.nextInt(numOfLandmarks);
			int end = random.nextInt(numOfLandmarks);

			//the number of elements between two cut points should be between 1 and (n-2) inclusively
			while((end-start+1)>=numOfLandmarks-1 || end < start){
				start = random.nextInt(numOfLandmarks);
				end = random.nextInt(numOfLandmarks);
			}


			//keep track of elements in both children
			//ifInChild[0][4] refers to landmark with ID 4 is in the first child
			boolean[][] ifInChild = new boolean[2][numOfLandmarks];
			Arrays.fill(ifInChild[0],false);
			Arrays.fill(ifInChild[1],false);

			int [] representation1 = child1.getSolutionRepresentation().getSolutionRepresentation();
			int [] representation2 = child2.getSolutionRepresentation().getSolutionRepresentation();

			//record landmarks between two cut points
			for(int j=start;j<=end;j++){
				ifInChild[0][representation1[j]] = true;
				ifInChild[1][representation2[j]] = true;
			}

			int currentIndex1 = (end+1)%numOfLandmarks;
			int currentIndex2 = (end+1)%numOfLandmarks;

			int [] parentRep1 = parent1.getSolutionRepresentation().getSolutionRepresentation();
			int [] parentRep2 = parent2.getSolutionRepresentation().getSolutionRepresentation();
			for(int j=1;j<=numOfLandmarks;j++){
				//copy landmark for other parent if it is not within the children yet
				int lm1 = parentRep1[(end+j)%numOfLandmarks];
				int lm2 = parentRep2[(end+j)%numOfLandmarks];

				//if current landmark in parent 1 is not in child 2
				//then add it
				if(!ifInChild[1][lm1]){
					ifInChild[1][lm1] = true;
					representation2[currentIndex2]=lm1;
					currentIndex2 = (currentIndex2+1)%numOfLandmarks;
				}

				if(!ifInChild[0][lm2]){
					ifInChild[0][lm2] = true;
					representation1[currentIndex1]=lm2;
					currentIndex1 = (currentIndex1+1)%numOfLandmarks;

				}

			}

			child1.setObjectiveFunctionValue(((SDSSTPObjectiveFunction)f).deltaEvalOX(parent1,child1,start,end));
			child2.setObjectiveFunctionValue(((SDSSTPObjectiveFunction)f).deltaEvalOX(parent2,child2,start,end));


			parent1 = child1.clone();
			parent2 = child2.clone();

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
