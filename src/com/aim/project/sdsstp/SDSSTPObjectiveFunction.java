package com.aim.project.sdsstp;

import com.aim.project.sdsstp.interfaces.ObjectiveFunctionInterface;
import com.aim.project.sdsstp.interfaces.SDSSTPSolutionInterface;
import com.aim.project.sdsstp.interfaces.SolutionRepresentationInterface;
import com.aim.project.sdsstp.solution.SDSSTPSolution;

import java.util.List;
import java.util.Random;

public class SDSSTPObjectiveFunction implements ObjectiveFunctionInterface {

	private final int[][] aiTimeDistanceMatrix;

	private final int[] aiTimeDistancesFromTourOffice;

	private final int[] aiTimeDistancesToTourOffice;

	private final int[] aiVisitingDurations;

	public SDSSTPObjectiveFunction(int[][] aiTimeDistanceMatrix, int[] aiTimeDistancesFromTourOffice,
			int[] aiTimeDistancesToTourOffice, int[] aiVisitingDurations) {

		this.aiTimeDistanceMatrix = aiTimeDistanceMatrix;
		this.aiTimeDistancesFromTourOffice = aiTimeDistancesFromTourOffice;
		this.aiTimeDistancesToTourOffice = aiTimeDistancesToTourOffice;
		this.aiVisitingDurations = aiVisitingDurations;
	}

	@Override
	public int getObjectiveFunctionValue(SolutionRepresentationInterface solution) {
		int [] representation = solution.getSolutionRepresentation();
		int returnVal = aiTimeDistancesFromTourOffice[representation[0]];

		for(int i=0;i< solution.getNumberOfLandmarks()-1;i++){
			returnVal += aiTimeDistanceMatrix[representation[i]][representation[i+1]];
		}

		for(int i=0;i< solution.getNumberOfLandmarks();i++){
			returnVal += aiVisitingDurations[representation[i]];
		}

		returnVal += aiTimeDistancesToTourOffice[representation[solution.getNumberOfLandmarks()-1]];
		return returnVal;
	}

	/**
	 * Get the objective function value using delta evaluation
	 * @param solution original solution
	 * @param index swap index
	 * @return new objective function
	 */
	public int deltaEvalAS(SDSSTPSolutionInterface solution, int index) {
		int value = (int)solution.getObjectiveFunctionValue();
		int numberOfLandmarks = solution.getNumberOfLandmarks();
		int landmark1 = solution.getSolutionRepresentation().getSolutionRepresentation()[index];
		int landmark2 = solution.getSolutionRepresentation().getSolutionRepresentation()[(index+1)%numberOfLandmarks];
		//if the last element was swapped with the first one


		if(index == numberOfLandmarks-1){
			value -= getTravelTimeFromTourOfficeToLandmark(landmark2);

			value -= getTravelTime(landmark2,solution.getSolutionRepresentation().getSolutionRepresentation()[1]);


			value -= getTravelTime(solution.getSolutionRepresentation().getSolutionRepresentation()[numberOfLandmarks-2],landmark1);

			value -= getTravelTimeFromLandmarkToTourOffice(landmark1);


			value += getTravelTimeFromTourOfficeToLandmark(landmark1);

			value += getTravelTime(landmark1,solution.getSolutionRepresentation().getSolutionRepresentation()[1]);

			value += getTravelTime(solution.getSolutionRepresentation().getSolutionRepresentation()[numberOfLandmarks-2],landmark2);

			value += getTravelTimeFromLandmarkToTourOffice(landmark2);


		}
		else{

			//change the travel cost between two landmarks
			value -= getTravelTime(landmark1,landmark2);
			value += getTravelTime(landmark2,landmark1);


			//change the travel cost before and after the two landmarks
			if(index == numberOfLandmarks-2){ //if the last two landmarks were swapped
				int before = solution.getSolutionRepresentation().getSolutionRepresentation()[numberOfLandmarks-3];

				value -= getTravelTime(before,landmark1) ;
				value -= getTravelTimeFromLandmarkToTourOffice(landmark2);

				value += getTravelTime(before,landmark2) ;
				value += getTravelTimeFromLandmarkToTourOffice(landmark1);

			}
			else if(index ==0){//if the first two elements were swapped
				int after = solution.getSolutionRepresentation().getSolutionRepresentation()[2];
				value -= getTravelTime(landmark2,after);

				value -= getTravelTimeFromTourOfficeToLandmark(landmark1);

				value += getTravelTime(landmark1,after);

				value+= getTravelTimeFromTourOfficeToLandmark(landmark2);

			}
			else{
				int before = solution.getSolutionRepresentation().getSolutionRepresentation()[index-1];
				int after = solution.getSolutionRepresentation().getSolutionRepresentation()[index+2];

				value -= getTravelTime(before,landmark1);

				value-= getTravelTime(landmark2,after);

				value += getTravelTime(before,landmark2);

				value+= getTravelTime(landmark1,after);

			}
		}

		return  value;
	}

	/**
	 * Get the objective function value using delta evaluation after applying the Inversion Mutation heuristic
	 * @param solution original solution
	 * @param start start point of reversion
	 * @param end end point of reversion
	 * @return new objective value
	 */
	public int deltaEvalIM(SDSSTPSolutionInterface solution, int start, int end){
		int numberOfLandmarks = solution.getNumberOfLandmarks();
		int value = (int)solution.getObjectiveFunctionValue();
		int [] representation = solution.getSolutionRepresentation().getSolutionRepresentation();

		//if start from the beginning
		if(start == 0){
			value -= getTravelTimeFromTourOfficeToLandmark(representation[start]);
			value += getTravelTimeFromTourOfficeToLandmark(representation[end]);
		}else{
			value -= getTravelTime(representation[start-1],representation[start]);
			value += getTravelTime(representation[start-1],representation[end]);
		}

		//if finish at the end
		if(end == numberOfLandmarks-1){
			value += getTravelTimeFromLandmarkToTourOffice(representation[start]);
			value -= getTravelTimeFromLandmarkToTourOffice(representation[end]);
		}else{
			value -= getTravelTime(representation[end],representation[end+1]);
			value += getTravelTime(representation[start],representation[end+1]);
		}

		//for each pair within the start and end
		for(int i=start;i<end;i++){
			value -= getTravelTime(representation[i],representation[i+1]);
			value += getTravelTime(representation[i+1],representation[i]);
		}

		return value;
	}

	/**
	 * Get the objective function value using delta evaluation after applying the Re-Insertion Mutation heuristic
	 * @param solution1 original solution
	 * @param originIndex original index of the landmark to be reinserted
	 * @param newIndex new Index after reinsertion
	 * @return new objective value
	 */
	public int deltaEvalRIM(SDSSTPSolutionInterface solution1, int originIndex, int newIndex){
		int numberOfLandmarks = solution1.getNumberOfLandmarks();
		int value = (int)solution1.getObjectiveFunctionValue();
		int [] representation1 = solution1.getSolutionRepresentation().getSolutionRepresentation();

		if(originIndex < newIndex){
			if(originIndex == 0){
				value -= getTravelTimeFromTourOfficeToLandmark(representation1[originIndex]);
				value += getTravelTimeFromTourOfficeToLandmark(representation1[originIndex+1]);
			}
			else{
				value -= getTravelTime(representation1[originIndex-1],representation1[originIndex]);
				value += getTravelTime(representation1[originIndex-1],representation1[originIndex+1]);
			}

			value -= getTravelTime(representation1[originIndex],representation1[originIndex+1]);

			if(newIndex == numberOfLandmarks-1){
				value -= getTravelTimeFromLandmarkToTourOffice(representation1[newIndex]);
				value += getTravelTimeFromLandmarkToTourOffice(representation1[originIndex]);
			}
			else{
				value -= getTravelTime(representation1[newIndex],representation1[newIndex+1]);
				value += getTravelTime(representation1[originIndex],representation1[newIndex+1]);
			}
			value += getTravelTime(representation1[newIndex],representation1[originIndex]);
		}
		else{
			if(originIndex == numberOfLandmarks-1){
				value -= getTravelTimeFromLandmarkToTourOffice(representation1[originIndex]);
				value += getTravelTimeFromLandmarkToTourOffice(representation1[originIndex-1]);
			}
			else{
				value -= getTravelTime(representation1[originIndex],representation1[originIndex+1]);
				value += getTravelTime(representation1[originIndex-1],representation1[originIndex+1]);
			}
			value -= getTravelTime(representation1[originIndex-1],representation1[originIndex]);



			if(newIndex == 0){
				value -= getTravelTimeFromTourOfficeToLandmark(representation1[newIndex]);
				value += getTravelTimeFromTourOfficeToLandmark(representation1[originIndex]);
			}
			else{
				value -= getTravelTime(representation1[newIndex-1],representation1[newIndex]);
				value += getTravelTime(representation1[newIndex-1],representation1[originIndex]);
			}

			value += getTravelTime(representation1[originIndex],representation1[newIndex]);

		}


		return value;
	}

	/**
	 * Get the objective function value using delta evaluation after applying the ordered crossover
	 * @param p parent
	 * @param c child that inherits landmarks between two cut points from the p
	 * @return new objective values of children, child 0 inherits variables between cut points from parent2
	 */
	public int deltaEvalOX(SDSSTPSolutionInterface p, SDSSTPSolutionInterface c, int start, int end){
		int numberOfLandmarks = p.getNumberOfLandmarks();
		int [] representation1 = p.getSolutionRepresentation().getSolutionRepresentation();
		int [] representation2 = c.getSolutionRepresentation().getSolutionRepresentation();
		int value = (int)p.getObjectiveFunctionValue();
		if(start != 0){
			value -= getTravelTimeFromTourOfficeToLandmark(representation1[0]);
			value += getTravelTimeFromTourOfficeToLandmark(representation2[0]);
			for(int i=0;i<start;i++){
				value -= getTravelTime(representation1[i],representation1[i+1]);
				value += getTravelTime(representation2[i],representation2[i+1]);
			}
		}

		if (end != numberOfLandmarks-1){
			value -= getTravelTimeFromLandmarkToTourOffice(representation1[numberOfLandmarks-1]);
			value += getTravelTimeFromLandmarkToTourOffice(representation2[numberOfLandmarks-1]);
			for(int i=end;i<numberOfLandmarks-1;i++){
				value -= getTravelTime(representation1[i],representation1[i+1]);
				value += getTravelTime(representation2[i],representation2[i+1]);
			}
		}

		return value;
	}
	@Override
	public int getTravelTime(int location_a, int location_b) {


		return aiTimeDistanceMatrix[location_a][location_b];
	}

	@Override
	public int getVisitingTimeAt(int landmarkId) {

		return aiVisitingDurations[landmarkId];
	}

	@Override
	public int getTravelTimeFromTourOfficeToLandmark(int toLandmarkId) {

		return aiTimeDistancesFromTourOffice[toLandmarkId];
	}

	@Override
	public int getTravelTimeFromLandmarkToTourOffice(int fromLandmarkId) {

		return aiTimeDistancesToTourOffice[fromLandmarkId];
	}

	@Override
	public int getClosestFromOffice(Random rng, boolean[] tabu){
		return SDSSTPMath.getMinTabu(aiTimeDistancesFromTourOffice,tabu,rng);
	}

	@Override
	public int getClosestToOffice(Random rng, boolean[] tabu){
		return SDSSTPMath.getMinTabu(aiTimeDistancesToTourOffice,tabu,rng);
	}

	@Override
	public int getClosestFrom(int start,Random rng, boolean[] tabu){
		return SDSSTPMath.getMinTabu(aiTimeDistanceMatrix[start],tabu,rng);
	}
}
