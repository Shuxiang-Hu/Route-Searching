package com.aim.project.sdsstp.heuristics;


import com.aim.project.sdsstp.SDSSTPObjectiveFunction;
import com.aim.project.sdsstp.interfaces.ObjectiveFunctionInterface;
import com.aim.project.sdsstp.interfaces.SDSSTPSolutionInterface;

/**
 * 
 * @author Warren G. Jackson
 * @since 26/03/2021
 * 
 */
public class HeuristicOperators {

	/*
	 *  PLEASE NOTE THAT USE OF THIS CLASS IS OPTIONAL BUT WE
	 *  STRONGLY RECOMMEND THAT YOU IMPLEMENT ANY COMMON FUNCTIONALITY
	 *  IN HERE TO HELP YOU WITH IMPLEMENTING THE HEURISTICS.
	 *
	 *  (HINT: It might be worthwhile to have a method that performs adjacent swaps in here :)) 
	 */

	private static final boolean ENABLE_CHECKING = false;

	private ObjectiveFunctionInterface obj;

	public HeuristicOperators() {

	}

	public void setObjectiveFunction(ObjectiveFunctionInterface f) {

		this.obj = f;
	}

	protected void performAdjacentSwap(SDSSTPSolutionInterface solution, int var_a) {

		// OPTIONAL: this might be useful to implement and reuse in your heuristics!
		int[] representation = solution.getSolutionRepresentation().getSolutionRepresentation();
		int temp = representation[var_a];
		int nextIndex = (var_a+1)%representation.length;
		representation[var_a] = representation[nextIndex];
		representation[nextIndex] = temp;
	}


	public double getObjectiveFunctionValue(SDSSTPSolutionInterface solution){
		return obj.getObjectiveFunctionValue(solution.getSolutionRepresentation());

	}

	public double deltaEvalAS(SDSSTPSolutionInterface solution, int index){
		return ((SDSSTPObjectiveFunction)this.obj).deltaEvalAS(solution, index);
	}

	public double deltaEvalIM(SDSSTPSolutionInterface solution, int start, int end){
		return ((SDSSTPObjectiveFunction)this.obj).deltaEvalIM(solution, start, end);
	}

	public double deltaEvalRIM(SDSSTPSolutionInterface solution, int originIndex, int newIndex){
		return ((SDSSTPObjectiveFunction)this.obj).deltaEvalRIM(solution, originIndex, newIndex);
	}

	public double deltaEvalOX(SDSSTPSolutionInterface p, SDSSTPSolutionInterface c, int start, int end){
		return ((SDSSTPObjectiveFunction)this.obj).deltaEvalOX(p,c,start,end);
	}
}
