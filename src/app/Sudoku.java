package app;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import api.Chromosome;
import api.GAOptimizer;
import api.GAOptimizer.CrossOverType;
import api.GAOptimizer.MutationType;
import api.GAOptimizer.SelectionType;
import util.CombinationUtil;

public class Sudoku {

    static int[][] fixedIndexes = {
            {0,  -1,  -1, -1, -1,  5,  7,  2, -1},
            {-1,  7,  -1,  4,  6,  2, -1,  8, -1},
            {-1, -1,  -1, -1,  1,  7, -1, -1, -1},
            {-1,  6,  -1,  2, -1,  0,  8,  7, -1},
            {4,   3,   8, -1,  7,  6,  5,  0, -1},
            {7,  -1,  -1,  3, -1,  8,  1, -1, -1},
            {-1, -1,  -1, -1, -1,  1,  2, -1, -1},
            {6,   5,   2, -1, -1, -1, -1, -1,  8},
            {8,   1,   7,  6, -1, -1,  4, -1 , 1}            
    };
    
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
            
            for(int i = 0; i < 9; i++) {
                Set<Integer> s = new HashSet<>();
                for(int j = 0; j < 9; j++) {
                	int val = encoded[9*j + i];
                	if(fixedIndexes[i][j] >= 0 && val != fixedIndexes[i][j]) continue;
                    s.add(val);
                }
                int n = s.size();
                if(n == 9) {
                    score += 1;
                }else {
                    score += n/9.0;
                }
            }
            
            for(int y = 0; y < 3; y++) {
                for(int x = 0; x < 3; x++) {
                    Set<Integer> s = new HashSet<>();
                    
                    for(int i = 0; i < 3; i++) {
                        for(int j = 0; j < 3; j++) {
                        	int row = 3*y+i, col = 3*x+j;
                        	int val = encoded[9*row + col];
                        	if(fixedIndexes[row][col] >= 0 && val != fixedIndexes[row][col]) continue;                        	
                            s.add(val);
                        }
                    }
                    
                    int n = s.size();
                    if(n == 9) {
                        score += 1;
                    }else {
                        score += n/9.0;
                    }
                }
            }
            
            return score;
        }

        @Override
        protected void randomInit(Random rand) {
            encoded = new int[9*9];
            
            for(int i = 0; i < 9; i++) {
                int[] tmp = CombinationUtil.genPermation(9, rand);
                System.arraycopy(tmp, 0, encoded, 9*i, 9);
            }                      
        }

        @Override
        protected Chromosome fromEncoded(int[] encoded) {
            return new SudokuChromosome(encoded);
        }
    }
    
    static class SudokuGAOptimizer extends GAOptimizer {
                
        public SudokuGAOptimizer(int populationSize, int eliteSize, int crossOverPoolSize, double mutationRate,
                SelectionType selectionType, CrossOverType crossOverType, MutationType mutationType,
                Map<String, Object> params) {
            super(populationSize, eliteSize, crossOverPoolSize, mutationRate, selectionType, crossOverType, mutationType, false, params);
        }
        
        @Override
        protected Chromosome newRandomChromosome(Random rand) {
            return new SudokuChromosome(rand);
        }
    }
    
    public static void main(String[] args) {
    	Map<String, Object> params = new HashMap<>();
        params.put("maxIndex", 9);
        params.put("bitMutationRate", 0.2);
        
        GAOptimizer gaOptimizer = new SudokuGAOptimizer(200, 20, 100, 0.1, 
                                        SelectionType.ROULETTE,
                                        CrossOverType.ONE_POINT, 
                                        MutationType.MUTATE_POINT,
                                        params);
        gaOptimizer.run(5000);
    }    
}
