package com.aim.project.sdsstp.hyperheuristics;

import AbstractClasses.HyperHeuristic;
import AbstractClasses.ProblemDomain;
import com.aim.project.sdsstp.AIM_SDSSTP;
import com.aim.project.sdsstp.SolutionPrinter;
import com.aim.project.sdsstp.instance.SDSSTPLocation;
import com.aim.project.sdsstp.interfaces.SDSSTPSolutionInterface;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @author Shuxiang Hu
 * @since 16/05/2021
 * Reinforcement learning based probabilistic hyper heuristic.
 * This hyper heuristic maintains the score for each low-level heuristic, which are initialized randomly.
 * It accepts equal or improving solutions.
 * A low-level heuristic gains one mark when it finds an equal or improving solution or lose one in the case of a worse one.
 * The choice of heuristics is based on the score.
 * P(h_i) = hh_score[i]/(hh_score[0]+hh_score[1]+...+hh_score[n-2]+hh_score[n-1])
 */
public class RLP_IE_HH extends HyperHeuristic {

    //scores for low-level heuristics
    private int [] hh_scores;

    final private int minScore = 1;

    final private int maxScore = 5;

    public RLP_IE_HH(long seed) {

        super(seed);
    }

    @Override
    protected void solve(ProblemDomain problem) {
        //initialize scores for hyper heuristics
        int numberOfHeuristics = problem.getNumberOfHeuristics();
        int totalScore = 0;
        hh_scores = new int[numberOfHeuristics];

        //record how many times each heuristic is called
        int [] times = new int[numberOfHeuristics];
        for (int i = 0; i < numberOfHeuristics; i++) {
            hh_scores[i] = rng.nextInt(maxScore-minScore+1) + minScore;
            totalScore += hh_scores[i];
            times[i] = 0;
        }

        problem.setMemorySize(3);


        problem.initialiseSolution(0);
        problem.initialiseSolution(1);
        double current = problem.getFunctionValue(0);

        problem.setIntensityOfMutation(0.2);
        problem.setDepthOfSearch(0.2);

        int h = 1;
        boolean accept;

        int[] xos = problem.getHeuristicsOfType(ProblemDomain.HeuristicType.CROSSOVER);
        HashSet<Integer> set = new HashSet<Integer>();
        for(int i : xos) {
            set.add(i);
        }

        System.out.println("Iteration\tf(s)\tf(s')\tAccept");

        int numberOfIteration = 0;
        int numberOfIterationWithoutImprovement = 0;
        while(!hasTimeExpired() ) {

            numberOfIteration++;

            //select heuristic based on score
            double j = rng.nextDouble();
            int sumSoFar = 0;
            for (int i = 0; i < numberOfHeuristics; i++) {
                sumSoFar += hh_scores[i];
                if(j < sumSoFar / (double) totalScore){
                    h = i;
                    break;
                }
            }
            times[h]++;


            double candidate;

            if(set.contains(h)) {

                problem.initialiseSolution(2);
                candidate = problem.applyHeuristic(h, 0, 2, 1);

            } else {

                candidate = problem.applyHeuristic(h, 0, 1);
            }


            accept = candidate <= current;

            System.out.println(numberOfIteration+"\t"+current+"\t"+candidate+"\t"+accept);
            if(accept) {

                problem.copySolution(1, 0);
                current = candidate;
                if(hh_scores[h]<maxScore){
                    hh_scores[h] ++;
                    totalScore ++;
                }

            }
            else{
                if(hh_scores[h] > minScore){
                    hh_scores[h] --;
                    totalScore --;
                }
                numberOfIterationWithoutImprovement ++;
            }

            if(numberOfIterationWithoutImprovement >=5){
                numberOfIterationWithoutImprovement = 0;
                totalScore = 0;
                for (int i = 0; i < numberOfHeuristics; i++) {
                    hh_scores[i] = rng.nextInt(maxScore-minScore+1) + minScore;
                    totalScore += hh_scores[i];
                }

            }
        }

        int[] cities = ((AIM_SDSSTP) problem).getBestSolution().getSolutionRepresentation().getSolutionRepresentation();
        List<SDSSTPLocation> routeLocations = new ArrayList<>();

        for(int i = 0; i < ((AIM_SDSSTP) problem).getBestSolution().getNumberOfLandmarks(); i++) {
            routeLocations.add(((AIM_SDSSTP) problem).instance.getLocationForLandmark(cities[i]));
        }

        SDSSTPSolutionInterface oSolution = ((AIM_SDSSTP) problem).getBestSolution();
        SolutionPrinter.printSolution(((AIM_SDSSTP) problem).instance.getSolutionAsListOfLocations(oSolution));


        for (int i = 0; i < numberOfHeuristics; i++) {
            System.out.println("Heuristic " + i +": " + times[i]);
        }
    }

    @Override
    public String toString() {

        return "RLP_IE_HH";
    }
}
