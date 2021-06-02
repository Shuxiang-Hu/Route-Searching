package com.aim.project.sdsstp.solution;

import com.aim.project.sdsstp.interfaces.SolutionRepresentationInterface;

/**
 * 
 * @author Warren G. Jackson
 * 
 *
 */
public class SolutionRepresentation implements SolutionRepresentationInterface {

	private int[] representation;
	
	public SolutionRepresentation(int[] representation) {
		
		this.representation = representation;
	}
	
	@Override
	public int[] getSolutionRepresentation() {

		return representation;
	}


	public int getLandmark(int index){
		return representation[index];
	}


	public void setLandmark(int index,int id){
		representation[index] = id;
	}

	@Override
	public void setSolutionRepresentation(int[] solution) {
		
		representation = solution;
	}

	@Override
	public int getNumberOfLandmarks() {


		return representation.length;
	}

	@Override
	public SolutionRepresentationInterface clone() {


		return new SolutionRepresentation(representation.clone());
	}

	@Override
	public String toString(){
		StringBuilder str = new StringBuilder();
		for(int i : representation){
			str.append(i).append(" ");
		}
		return str.toString();
	}


	public int getIndex(int landmarkID){
		for(int i=0;i<representation.length;i++){
			if(representation[i] == landmarkID){
				return i;
			}
		}

		return -1;
	}
}
