package com.aim.project.sdsstp;
import java.util.Random;

public class SDSSTPMath {
    /**
     * finds the index of minimum value in the array and randomly choose if there are more than one minimum.
     * Always ignore the entries noted as tabu
     * @param array input array
     * @param tabu entries that will be ignored
     * @param rng a Random object
     * @return index of minimum value
     */
    public static int getMinTabu(int [] array,boolean [] tabu, Random rng){

        if(array.length<=0){
            return -1;
        }

        int numberOfMins = 0;

        int [] minIndices = new int[array.length];

        int min = Integer.MAX_VALUE;

        for(int i=0;i<array.length;i++){
            if(tabu[i]){//if current element is a tabu, skip it
                continue;
            }

            int current = array[i];
            if(current < min){//update the min
                min = current;
                numberOfMins = 1;
                minIndices[numberOfMins-1] = i;
            }
            else if (current == min){
                numberOfMins++;
                minIndices[numberOfMins-1] = i;
            }

        }

        return minIndices[rng.nextInt(numberOfMins)];
    }

    /**
     * Return true if two arrays are the same regardless of position
     * For example {0,1,2,3,4,5} and {0,3,4,5,1,2} are the same.
     * @param a an array of integers
     * @param b an array of integers
     * @return if two arrays are the same
     */
    public static boolean isSame(int[] a, int[] b){

        if(a.length != b.length){
            return false;
        }

        int startInB = -1;
        int firstInA = a[0];
        int length = b.length;
        for(int i=0;i<length;i++){
            if(b[i] == firstInA){
                startInB = i;
            }
        }

        if(startInB == -1){ //the first variable in a does not exist in b
            return false;
        }

        for(int i=0;i<length;i++){
            if(a[i] != b[(startInB+i)%length]){
                return false;
            }
        }

        return true;
    }

    public static void shuffle(int []a,Random oRandom){
        int length = a.length;
        for (int i = length-1; i >0; i--) {
            int swapIndex = oRandom.nextInt(i + 1);
            // Simple swap
            int temp = a[swapIndex];
            a[swapIndex] =a[i];
            a[i] = temp;
        }
    }
}
