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

public class GraphColoring {

    static int N = 10;
    static int[][] graph = {
            {0,1}, {1,2}, {2,3}, {3,4}, {4,0},            
            {0,5}, {1,6}, {2,7}, {3,8}, {4,9},
            {5,7}, {5,8}, {6,8}, {6,9}, {7,9}
    };
    
    static class GraphColoringChromosome extends Chromosome {

        public GraphColoringChromosome(int[] encoded) {
            super(encoded);
        }
        
        public GraphColoringChromosome(Random rand) {
            super(rand);
        }

        @Override
        protected double calcFitness() {
            int nIllegalEdge = 0;
            for(int[] edge : graph) {
                if(encoded[edge[0]] == encoded[edge[1]]) {
                    nIllegalEdge += 1;
                }
            }
            Set<Integer> colorSet = new HashSet<>();
            for(int color : encoded) colorSet.add(color);
            int nColor = colorSet.size();
            return 1.0 / (nIllegalEdge + nColor/(1.0 + nColor));
        }

        @Override
        protected void randomInit(Random rand) {
            encoded = new int[N];
            
            for(int i = 0; i < N; i++) {
                encoded[i] = (int)(rand.nextDouble() * N);
            }            
        }

        @Override
        protected Chromosome fromEncoded(int[] encoded) {
            return new GraphColoringChromosome(encoded);
        }
        
    }
    
    public static void main(String[] args) {
        Map<String, Object> params = new HashMap<>();
        params.put("bitMutationRate", 0.2);
        params.put("tournamentThresh", 0.85);
        
        List<Chromosome> initialPopulation = new ArrayList<>();
        Random rand = new Random();
        for(int i = 0; i < 100; i++) {
            initialPopulation.add(new GraphColoringChromosome(rand));
        }
        
        GAOptimizer gaOptimizer = new GAOptimizer(initialPopulation, 
                20, 100, 0.1, 
                SelectionType.ROULETTE,
                CrossOverType.UNIFORM, 
                MutationType.MUTATE_POINT, 
                false, params);
        
        gaOptimizer.run(500);
    }
}
