package app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import api.Chromosome;
import api.GAOptimizer;
import api.GAOptimizer.CrossOverType;
import api.GAOptimizer.MutationType;
import api.GAOptimizer.SelectionType;

public class Sudoku {

    static int[] initials = {
            1,  0,  0,  0,  0,  6,  8,  3,  0,
            0,  8,  0,  5,  7,  3,  0,  9,  0,
            0,  0,  0,  0,  2,  8,  0,  0,  0,
            0,  7,  0,  3,  0,  1,  9,  8,  0,
            5,  4,  9,  0,  8,  7,  6,  1,  0,
            8,  0,  0,  4,  0,  9,  2,  0,  0,
            0,  0,  0,  0,  0,  2,  3,  0,  0,
            7,  6,  3,  0,  0,  0,  0,  0,  9,
            9,  2,  8,  7,  0,  0,  5,  0 , 0            
    };
    
    static int N = 0;
    
    static {
        for(int val : initials) {
            if(val == 0) N++;
        }
    }
    
    static class SudokuChromosome extends Chromosome {

        public SudokuChromosome(int[] encoded) {
            super(encoded);
        }
        
        public SudokuChromosome(Random rand) {
            super(rand);
        }

        @Override
        protected double calcFitness() {
            double score  = 0;
            int[] table = new int[9*9];
            int index = 0;
            
            for(int i = 0; i < 9*9; i++) {
                if(initials[i] > 0) {
                    table[i] = initials[i] - 1;
                }else {
                    table[i]= encoded[index++];
                }
            }
            
            for(int i = 0; i < 9; i++) {
                Set<Integer> s = new HashSet<>();
                for(int j = 0; j < 9; j++) {
                    s.add(table[9*j + i]);
                }
                score += s.size()/9.0;
            }
            
            for(int y = 0; y < 3; y++) {
                for(int x = 0; x < 3; x++) {
                    Set<Integer> s = new HashSet<>();
                    
                    for(int i = 0; i < 3; i++) {
                        for(int j = 0; j < 3; j++) {
                            int row = 3*y+i, col = 3*x+j;
                            s.add(table[9*row + col]);
                        }
                    }
                    
                    score += s.size()/9.0;
                }
            }
            
            return score;
        }

        @Override
        protected void randomInit(Random rand) {
            encoded = new int[N];
            
            for(int i = 0; i < N; i++) {
                encoded[i] = (int)(rand.nextDouble() * 9);
            }                      
        }

        @Override
        protected Chromosome fromEncoded(int[] encoded) {
            return new SudokuChromosome(encoded);
        }
    }
    
    public static void main(String[] args) {
        Map<String, Object> params = new HashMap<>();
        params.put("maxIndex", 9);
        params.put("bitMutationRate", 0.1);
        
        List<Chromosome> initialPopulation = new ArrayList<>();
        Random rand = new Random();
        for(int i = 0; i < 500; i++) {
            initialPopulation.add(new SudokuChromosome(rand));
        }
        
        GAOptimizer gaOptimizer = new GAOptimizer(initialPopulation,
                                        50, 500, 0.1, 
                                        SelectionType.ROULETTE,
                                        CrossOverType.UNIFORM, 
                                        MutationType.MUTATE_POINT,
                                        false, params);
        gaOptimizer.run(5000);
    }    
}
