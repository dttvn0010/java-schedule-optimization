package api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class GAOptimizer {
    
    public static enum SelectionType {
        ROULETTE,
        TOURNAMENT,
        REFINED,
    }
    
    public static enum CrossOverType {
        UNIFORM,
        ONE_POINT,
        UNI_THREE_PARENT,
        UNI_ONE_POINT,
    }
    
    public static enum MutationType {
        MUTATE_POINT,
        SWITCH_POINT,        
    }

    protected Chromosome[] population;
    protected double chromosomeMutationRate;
    protected int eliteSize;
    protected int crossOverPoolSize;
    protected SelectionType selectionType;
    protected CrossOverType crossOverType;
    protected MutationType mutationType;
    protected Random rand = new Random();
    protected int generation = 0;
    protected boolean checkUnique;
    protected Map<String, Object> extraParams;
    
        
    public GAOptimizer(List<Chromosome> initialPopulation, int eliteSize, int crossOverPoolSize, double mutationRate,
                        SelectionType selectionType, CrossOverType crossOverType, MutationType mutationType,
                        boolean checkUnique, Map<String, Object> params) {
        
        this.population = initialPopulation.toArray(new Chromosome[0]);
        this.eliteSize = eliteSize;
        this.crossOverPoolSize = crossOverPoolSize;
        this.chromosomeMutationRate = mutationRate;
        
        this.selectionType = selectionType;
        this.crossOverType = crossOverType;
        this.mutationType = mutationType;
        this.checkUnique = checkUnique;
        this.extraParams = params;
    }
    
    public GAOptimizer(List<Chromosome> initialPopulation, int eliteSize, int crossOverPoolSize, double mutationRate, 
                        SelectionType selectionType, CrossOverType crossOverType, MutationType mutationType,
                        boolean checkUnique) {
        this(initialPopulation, eliteSize, crossOverPoolSize, mutationRate, 
                selectionType, crossOverType, mutationType, checkUnique, new HashMap<>());
    }
    
    protected Chromosome[] selectParents() {
        if(selectionType == SelectionType.ROULETTE) {
            return rouletteSelectParents(population, crossOverPoolSize);
        }
        
        if(selectionType == SelectionType.TOURNAMENT) {
            double tournamentThresh = (Double) extraParams.get("tournamentThresh");
            return tournamentSelectParents(population, crossOverPoolSize, tournamentThresh);
        }
        
        if(selectionType == SelectionType.REFINED) {
            double tournamentThresh = (Double) extraParams.get("tournamentThresh");
            return refineSelectParents(population, crossOverPoolSize, tournamentThresh);
        }
        
        throw new RuntimeException("Unsupported selection type: " + selectionType);
        
    }
        
    private Chromosome[] rouletteSelectParents(Chromosome[] pool, int nSelected) {

        double total_fitness = 0;
        for(Chromosome c : pool) total_fitness += c.getFitness();
        Chromosome[] parents = new Chromosome[nSelected];
        
        for(int i = 0; i < nSelected; i++) {
            double r = rand.nextDouble();
            int k = 0;
            double acc_fitness = 0;
            
            while(acc_fitness <= r * total_fitness) {
                acc_fitness += pool[k].getFitness();
                k += 1;
            }
            parents[i] = pool[k-1];
        }
        return parents;
    }
    
    private Chromosome[] tournamentSelectParents(Chromosome[] pool, int nSelected, double tournamentThresh) {
        
        Chromosome[] parents = new Chromosome[nSelected];
        for(int i = 0; i < nSelected; i++) {
            int index1 = (int) (rand.nextDouble() * pool.length);
            int index2 = (int) (rand.nextDouble() * pool.length - 1);
            index2 = (index1 + index2 + 1) % (pool.length);
            Chromosome c1 = pool[index1], c2 = pool[index2];
            double r = rand.nextDouble();
            
            if(r < tournamentThresh) {
                parents[i] =  c1.getFitness() < c2.getFitness()? c2 : c1;
            }else {
                parents[i] =  c1.getFitness() > c2.getFitness()? c2 : c1;
            }
        }
        return parents;
    }
    
    private Chromosome[] refineSelectParents(Chromosome[] pool, int nSelected, double tournamentThresh) {
        Chromosome[] pool2 = tournamentSelectParents(pool, pool.length, tournamentThresh);
        return rouletteSelectParents(pool2, nSelected);
    }
        
    // Uniform crossover
    private Chromosome uniformCrossOverUnchecked(Chromosome[] parents) {
        Chromosome parent1 = parents[0], parent2 =  parents[1];
        
        int N = parent1.encoded.length;
        int[] indexes = new int[N];
        
        for(int i = 0; i < N; i++) {
            if(rand.nextBoolean()) {
                indexes[i] = parent1.encoded[i];
            }else {
                indexes[i] = parent2.encoded[i];
            }
        }
   
        return parent1.fromEncoded(indexes);
    }
    
    private Chromosome uniformCrossOverChecked(Chromosome[] parents) {
        Chromosome parent1 = parents[0], parent2 =  parents[1];
        
        int N = parent1.encoded.length;
        int[] indexes1 = new int[N];
        System.arraycopy(parent1.encoded, 0 , indexes1, 0, N);
        
        List<Integer> indexes2 = new ArrayList<>();            
        for(int index : parent2.encoded) indexes2.add(index);
        
        for(int i = 0; i < N; i++) {
            if(rand.nextBoolean()) {
                indexes2.remove((Integer) indexes1[i]);
            }else {
                indexes1[i] = -1;
            }
        }
        
        for(int i = 0; i < N; i++) {
            if(indexes1[i] < 0) {
                indexes1[i] = indexes2.remove(0);
            }
        }
        return parent1.fromEncoded(indexes1);
    }
        
    // One point cross over
    private Chromosome onePointCrossOverUnchecked(Chromosome[] parents) {
        Chromosome parent1 = parents[0], parent2 =  parents[1];
        
        int N = parent1.encoded.length;
        int[] indexes = new int[N];

        int p = (int) (rand.nextDouble() * N);        // cross over point
        for(int i = 0; i < p; i++) {
            indexes[i] = parent1.encoded[i];
        }
                
        for(int i = p; i < N; i++) {
            indexes[i] = parent2.encoded[i];
        }
        
        return parent1.fromEncoded(indexes);
    }
    
    
    private Chromosome onePointCrossOverChecked(Chromosome[] parents) {
        Chromosome parent1 = parents[0], parent2 =  parents[1];
        
        int N = parent1.encoded.length;
        int[] indexes1 = new int[N];
        System.arraycopy(parent1.encoded, 0 , indexes1, 0, N);
                
        List<Integer> indexes2 = new ArrayList<>();            
        for(int index : parent2.encoded) indexes2.add(index);
        
        int p = (int) (rand.nextDouble() * N);        // cross over point
        for(int i = 0; i < p; i++) {
            indexes2.remove((Integer) indexes1[i]);
        }
                
        for(int i = p; i < N; i++) {
            indexes1[i] = indexes2.remove(0);
        }
        
        return parent1.fromEncoded(indexes1);
    }
    
    // uni-three cross over
    private Chromosome uniThreeParentCrossOverUnchecked(Chromosome[] parents) {
        Chromosome parent1 = parents[0], parent2 =  parents[1], parent3 = parents[2];
                
        int N = parent1.encoded.length;
        int[] indexes = new int[N];

        for(int i = 0; i < N; i++) {
            if(parent1.encoded[i] == parent2.encoded[i]) {
                indexes[i] = parent1.encoded[i];
            }else {
                indexes[i] = parent3.encoded[i];
            }
        }
        
        return parent1.fromEncoded(indexes);
    }
    
    private Chromosome uniThreeParentCrossOverChecked(Chromosome[] parents) {
        Chromosome parent1 = parents[0], parent2 =  parents[1], parent3 = parents[2];
                
        int N = parent1.encoded.length;
        int[] indexes1 = new int[N];
        System.arraycopy(parent1.encoded, 0 , indexes1, 0, N);
        
        List<Integer> indexes3 = new ArrayList<>();            
        for(int index : parent3.encoded) indexes3.add(index);
        
        for(int i = 0; i < N; i++) {
            if(parent1.encoded[i] == parent2.encoded[i]) {
                indexes3.remove((Integer) indexes1[i]);
            }else {
                indexes1[i] = -1;
            }
        }
        
        for(int i = 0; i < N; i++) {
            if(indexes1[i] < 0) {
                indexes1[i] = indexes3.remove(0);
            }
        }
        return parent1.fromEncoded(indexes1);
    }
    
    // uni-one-point cross over
    private Chromosome uniOnePointCrossOverUnchecked(Chromosome[] parents) {
        Chromosome parent1 = parents[0], parent2 =  parents[1], parent3 = parents[2];
        
        return onePointCrossOverUnchecked(new Chromosome[] {
            uniformCrossOverUnchecked(new Chromosome[] {parent1, parent2}),
            parent3
        });        
    }
    
    private Chromosome uniOnePointCrossOverChecked(Chromosome[] parents) {
        Chromosome parent1 = parents[0], parent2 =  parents[1], parent3 = parents[2];
        
        return onePointCrossOverChecked(new Chromosome[] {
            uniformCrossOverChecked(new Chromosome[] {parent1, parent2}),
            parent3
        });        
    }

    private int getNumberOfParents() {
        if(crossOverType == CrossOverType.UNI_THREE_PARENT || crossOverType == CrossOverType.UNI_ONE_POINT) {
            return 3;
        }
        else return 2;
    }
    
    protected Chromosome crossOver(Chromosome[] parents) {
        if(crossOverType == CrossOverType.UNIFORM) {
            return checkUnique? uniformCrossOverChecked(parents) : uniformCrossOverUnchecked(parents);
        }
        
        if(crossOverType == CrossOverType.ONE_POINT) {
            return checkUnique? onePointCrossOverChecked(parents) : onePointCrossOverUnchecked(parents);
        }
        
        if(crossOverType == CrossOverType.UNI_THREE_PARENT) {
            return checkUnique? uniThreeParentCrossOverChecked(parents) : uniThreeParentCrossOverUnchecked(parents);
        }
        
        if(crossOverType == CrossOverType.UNI_ONE_POINT) {
            return checkUnique? uniOnePointCrossOverChecked(parents) : uniOnePointCrossOverUnchecked(parents);
        }
        
        throw new RuntimeException("Unsupported crossover type: " + crossOverPoolSize);
    }
    
    // Mutate switch point   
    private boolean mutateSwitchPoint(Chromosome c) {
        double bitMutationRate = (Double) extraParams.get("bitMutationRate");
        int N = c.encoded.length;
        
        for(int i = 0; i < N; i++) {
            if(rand.nextDouble() < bitMutationRate) {
                int j = (int)((N-1) * rand.nextDouble());
                j = (i + j + 1) % N;
                int tmp = c.encoded[i];
                c.encoded[i] = c.encoded[j];
                c.encoded[j] = tmp;
                c.clearFitness();
            }
        }    
        return true;
    }
    
    // Mutate one point
    private boolean mutateOnePointUnchecked(Chromosome c) {
        double bitMutationRate = (Double) extraParams.get("bitMutationRate");
        int N = (Integer) extraParams.getOrDefault("maxIndex", c.encoded.length);
        
        for(int i = 0; i < c.encoded.length; i++) {
            if(rand.nextDouble() < bitMutationRate) {
                c.encoded[i] = (int)(rand.nextDouble() * N);                
                c.clearFitness();
            }
        }
        return true;
    }
    
    private boolean mutateOnePointChecked(Chromosome c) {
        double bitMutationRate = (Double) extraParams.get("bitMutationRate");
        int N = (Integer) extraParams.getOrDefault("maxIndex", c.encoded.length);
        int k = c.encoded.length;
        
        List<Integer> indexes = new ArrayList<>();
        
        for(int i = 0; i < N; i++) indexes.add(i);
        
        for(int i = 0; i < k; i++) indexes.remove((Integer) c.encoded[i]);
        
        for(int i = 0; i < k; i++) {
            if(rand.nextDouble() < bitMutationRate && indexes.size() > 0) {
                int r = (int)(rand.nextDouble() * indexes.size());
                int tmp = c.encoded[i];
                c.encoded[i] = indexes.remove(r);
                indexes.add(tmp);                
                c.clearFitness();
            }
        }
        return true;
    }
           
    protected boolean mutateChromosome(Chromosome c) {
        if(mutationType == MutationType.SWITCH_POINT) {
            return mutateSwitchPoint(c); 
        }
        
        if(mutationType == MutationType.MUTATE_POINT) {
            return checkUnique? mutateOnePointChecked(c) : mutateOnePointUnchecked(c);
        }
        
        throw new RuntimeException("Unsupported mutation type: " + mutationType);
    }
        
    private void printInfo() {
        
        System.out.println("Generation " + generation + " :");
        
        for(Chromosome c : population) {
            System.out.print("Chromosome : " + c.toString());
            System.out.print("Fitness:");
            System.out.println(String.format("%f ", c.getFitness()));
        }
        
        System.out.println();
    }
    
    private void nextGeneration() {
        Chromosome[] parents = selectParents();
        Chromosome[] children = new Chromosome[population.length - eliteSize];
        
        for(int i = 0; i < population.length - eliteSize; i++) {
            Chromosome parent1 = parents[(int)(rand.nextDouble() * crossOverPoolSize)];
            
            Chromosome parent2;
            do {
                parent2 = parents[(int)(rand.nextDouble() * crossOverPoolSize)];
            }while(parent2 == parent1);
            
            if(getNumberOfParents() == 2) {
                children[i] = crossOver(new Chromosome[] {parent1, parent2});
            }else {
                
                Chromosome parent3;
                do {
                    parent3 = parents[(int)(rand.nextDouble() * crossOverPoolSize)];
                }while(parent3 == parent1 || parent3 == parent2);
                children[i] = crossOver(new Chromosome[] {parent1, parent2, parent3});
            }
        }
        
        for(Chromosome c : children) {
            if(rand.nextDouble() < chromosomeMutationRate) {
                mutateChromosome(c);
            }
        }
        
        List<Chromosome> newPopulation = new ArrayList<>();
        newPopulation.addAll(Arrays.asList(population).subList(0, eliteSize));
        newPopulation.addAll(Arrays.asList(children));
        
        population = newPopulation.stream()
                .sorted((c1, c2) -> Double.compare(c2.getFitness(), c1.getFitness()))
                .collect(Collectors.toList())
                .toArray(new Chromosome[0]);
        
        generation += 1;
    }
            
    public void run(int numStep) {

        for(int i = 0; i < numStep; i++) {                
            nextGeneration();
            printInfo();
        }
    }
}
