package util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
*Util for generate combination
*/
public class CombinationUtil {


	/**
	* Generate permuation of 1..N
	*/
    public static int[] genPermation(int N) {
        return genPermation(N, null);
    }
    
	/**
	* Generate permuation of 1..N, using provided random generator
	*/
    public static int[] genPermation(int N, Random rand) {
        return genCombination(N, N, rand);
    }
    
	/**
	* Generate combination of k element from 1..N
	*/
    public static int[] genCombination(int N, int k) {
        return genCombination(N, k, null);
    }
      
    /**
	* Generate combination of k element from 1..N, using provided random generator
	*/	  
    public static int[] genCombination(int N, int k, Random rand) {
        
        List<Integer> indexes = new ArrayList<>();
        for(int i = 0; i < N; i++) {
            indexes.add(i);
        }
        
        if(rand == null) rand = new Random();
        
        int[] result = new int[k];
        
        for(int i = 0; i < k; i++) {
            int r = (int) (rand.nextDouble() * indexes.size());
            result[i] = indexes.get(r);
            indexes.remove(r);
        }
        
        return result;
    }
}
