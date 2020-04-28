package util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CombinationUtil {

	public static int[] genPermation(int N) {
		return genPermation(N, null, null);
	}
	
	public static int[] genPermation(int N, Random rand) {
		
		return genPermation(N, null, rand);
	}
	
	public static int[] genPermation(int N, int[] fixedIndexes) {
		return genPermation(N, fixedIndexes, null);		
	}
	
	public static int[] genPermation(int N, int[] fixedIndexes, Random rand) {
		return genCombination(N, N, fixedIndexes, rand);
	}
	
	public static int[] genCombination(int N, int k) {
		return genCombination(N, k, null, null);
	}
	
	public static int[] genCombination(int N, int k, Random rand) {
		
		return genCombination(N, k, null, rand);
	}
	
	public static int[] genCombination(int N, int k, int[] fixedIndexes) {
		return genCombination(N, k, fixedIndexes, null);		
	}
	
	public static int[] genCombination(int N, int k, int[] fixedIndexes, Random rand) {
		
		if(fixedIndexes == null) {
			fixedIndexes = new int[k];
			for(int i = 0; i < k; i++) {
				fixedIndexes[i] = -1;
			}
		}
		
		List<Integer> indexes = new ArrayList<>();
		for(int i = 0; i < N; i++) {
			indexes.add(i);
		}
		
		for(int index : fixedIndexes) {
			if(index >= 0) {
				indexes.remove((Integer) index);
			}
		}
		
		if(rand == null) rand = new Random();
		
		int[] result = new int[k];
		
		for(int i = 0; i < k; i++) {
			if(fixedIndexes[i] >= 0) {
				result[i] = fixedIndexes[i];
			}else {
				int r = (int) (rand.nextDouble() * indexes.size());
				result[i] = indexes.get(r);
				indexes.remove(r);
			}
		}
		
		return result;
	}
	
	public static void main(String[] args) {
		int[] fixedIndexes =  {-1,-1, 2, 5, -1, 0, 6, -1, -1};
		
		for(int k = 0; k < 10; k++) {
			int[] indexes = genPermation(9, fixedIndexes);
			for(int index : indexes) System.out.print(index + " ");
			System.out.println();
		}
	}
}
