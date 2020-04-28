package app;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
					s.add(encoded[9*j + i]);
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
							s.add(encoded[9*(3*y+i) + (3*x+j)]);
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
				int[] tmp = CombinationUtil.genPermation(9, fixedIndexes[i], rand);
				System.arraycopy(tmp, 0, encoded, 9*i, 9);
			}
			
		}

		@Override
		protected Chromosome fromEncoded(int[] encoded) {
			return new SudokuChromosome(encoded);
		}
	}
	
	static class SudokuGAOptimizer extends GAOptimizer {
				
		private double bitMutationRate = 0.3;
		
		public SudokuGAOptimizer(int populationSize, int crossOverPoolSize, double mutationRate,
				SelectionType selectionType, CrossOverType crossOverType, MutationType mutationType) {
			super(populationSize, crossOverPoolSize, mutationRate, selectionType, crossOverType, mutationType);
		}
		
		@Override
		protected Chromosome newRandomChromosome(Random rand) {
			return new SudokuChromosome(rand);
		}		
		
		@Override
		protected Chromosome crossOver(Chromosome[] parents) {
			SudokuChromosome parent1 = (SudokuChromosome) parents[0];
			SudokuChromosome parent2 = (SudokuChromosome) parents[1];
			
			int[] encoded = new int[9*9];
			
			for(int i = 0; i < 9; i++) {
				int[] indexes1 = new int[9];
				System.arraycopy(parent1.encoded, 9*i , indexes1, 0, 9);
				
				List<Integer> indexes2 = new ArrayList<>();
				
				for(int j = 0; j < 9; j++) {
					if(fixedIndexes[i][j] < 0) {
						indexes2.add(parent2.encoded[9*i+j]);
					}
				}
				
				for(int j = 0; j < 9; j++) {
					if(fixedIndexes[i][j] >= 0) continue;
					
					if(rand.nextBoolean()) {
						indexes2.remove((Integer) indexes1[j]);
					}else {
						indexes1[j] = -1;
					}
				}
				
				for(int j = 0; j < 9; j++) {
					if(indexes1[j] < 0) {
						indexes1[j] = indexes2.remove(0);
					}
				}
				
				System.arraycopy(indexes1, 0, encoded, 9*i, 9);
			}
			
			return new SudokuChromosome(encoded);			
		}
		
		@Override
		protected void mutateChromosome(Chromosome c) {
			for(int i = 0; i < 9; i++) {
				for(int j = 0;  j < 9; j++) {
					if(fixedIndexes[i][j] >= 0) continue;
					
					if(rand.nextDouble() < bitMutationRate) {
						int k;
						do {
							k = (int) (rand.nextDouble() * 9);
						}while(fixedIndexes[i][k] >= 0 || k == j);
						
						int tmp = c.encoded[9*i + j];
						c.encoded[9*i+j] = c.encoded[9*i+k];
						c.encoded[9*i+k] = tmp;
						c.clearFitness();
					}
				}
			}
		}	
	}	
	
	public static void main(String[] args) {
		GAOptimizer gaOptimizer = new SudokuGAOptimizer(100, 200, 0.5, 
										SelectionType.ROULETTE,
										CrossOverType.CUSTOM, 
										MutationType.CUSTOM);
		gaOptimizer.run(5000);
	}	
}
