package util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CombinationUtil {

    public static int[] genPermation(int N) {
        return genPermation(N, null);
    }
    
    public static int[] genPermation(int N, Random rand) {
        return genCombination(N, N, rand);
    }
    
    public static int[] genCombination(int N, int k) {
        return genCombination(N, k, null);
    }
        
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
