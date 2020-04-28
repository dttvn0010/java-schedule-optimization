package app;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import api.Chromosome;
import api.GAOptimizer;
import api.GAOptimizer.CrossOverType;
import api.GAOptimizer.MutationType;
import api.GAOptimizer.SelectionType;
import util.CombinationUtil;

public class TSM {
	
	private static double[][] distance_matrix = {
			{0, 3,  5,  8, 5,  45, 23, 30},
			{3, 0,  6,  7, 15, 43, 10, 5},
			{5, 6,  0,  9, 12, 6,  17, 4},
			{8, 7,  9,  0, 20, 32, 12, 26},
			{5, 15, 12, 20, 0, 11, 24, 36},
			{45, 43, 6, 32, 11, 0, 48, 12},
			{23, 10, 17, 12, 24,48, 0, 41},
			{30, 5, 4, 26, 36, 12, 41, 0}
	};
	
	private final static int N = distance_matrix.length - 1;
	
	static class TSMChromosome extends Chromosome {
		
		@Override
		protected void randomInit(Random rand) {
			
			encoded = CombinationUtil.genPermation(N, rand);
			for(int i = 0; i < encoded.length; i++) {
				encoded[i] += 1;
			}			
		}
		
		public TSMChromosome(Random rand) {
						
			super(rand);
		}
		
		public TSMChromosome(int[] encoded) {
			super(encoded);
		}
				
		@Override
		protected double calcFitness() {
			double distance = 0;
			int prev_i = 0;
			
			for(int i : encoded) {
				distance += distance_matrix[prev_i][i];
				prev_i = i;
			}
			
			distance += distance_matrix[prev_i][0];
			return 1.0/distance;
		}

		@Override
		protected Chromosome fromEncoded(int[] encoded) {
			return new TSMChromosome(encoded);			
		}
		
	}
	
	static class TSMGAOptimizer extends GAOptimizer {
	
		public TSMGAOptimizer(int populationSize, int crossOverPoolSize, double mutationRate,
				SelectionType selectionType, CrossOverType crossOverType, MutationType mutationType, 
				Map<String, Object> params) {
			super(populationSize, crossOverPoolSize, mutationRate, selectionType, crossOverType, mutationType, params);
		}

		@Override
		protected Chromosome newRandomChromosome(Random rand) {
			return new TSMChromosome(rand);
		}

			
	}
	
	public static void main(String[] args) {
		Map<String, Object> params = new HashMap<>();
		params.put("bitMutationRate", 0.2);
		
		GAOptimizer gaOptimizer = new TSMGAOptimizer(40, 20, 0.05, 
										SelectionType.ROULETTE,
										CrossOverType.UNIFORM, 
										MutationType.SWITCH_POINT, 
										params);
		gaOptimizer.run(500);
	}

}
