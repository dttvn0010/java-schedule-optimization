package api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Genetic optimizer engine
 */
public class GAOptimizer {
    
	/**
	 * Type of selection
	 */
    public static enum SelectionType {
        ROULETTE,
        TOURNAMENT,
        REFINED,
    }
    
    /**
	 * Type of crossover
	 */
    public static enum CrossOverType {
        UNIFORM,
        ONE_POINT,
        UNI_THREE_PARENT,
        UNI_ONE_POINT,
    }
    
    /**
	 * Type of mutation
	 */
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
    
    /**
     * Constructor
     * @param initialPopulation: initial population
     * @param eliteSize: number of chromosome kept to the next generation
     * @param crossOverPoolSize: size of the mating pool
     * @param mutationRate: rate of mutation
     * @param selectionType: type of selection
     * @param crossOverType: type of crossover
     * @param checkUnique: whether the elements of a combination need to be unique
     * @param params: extra parameter for each cross-over/muation/selection type
     */
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
    
    /**
     * Constructor
     * @param initialPopulation: initial population
     * @param eliteSize: number of chromosome kept to the next generation
     * @param crossOverPoolSize: size of the mating pool
     * @param mutationRate: rate of mutation
     * @param selectionType: type of selection
     * @param crossOverType: type of crossover
     * @param checkUnique: whether the elements of a combination need to be unique
     */
    public GAOptimizer(List<Chromosome> initialPopulation, int eliteSize, int crossOverPoolSize, double mutationRate, 
                        SelectionType selectionType, CrossOverType crossOverType, MutationType mutationType,
                        boolean checkUnique) {
        this(initialPopulation, eliteSize, crossOverPoolSize, mutationRate, 
                selectionType, crossOverType, mutationType, checkUnique, new HashMap<>());
    }

    /**
     * Get the population of the engine
     * @return: population
     */
    public Chromosome[] getPopulations() {
        return population;
    }
    
    /**
     * Perform selection for mating
     * @return: mating pool containing of chromosomes
     */
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
        
    /**
     * Perform roulette selection
     * @param pool : the source for selection
     * @param nSelected: the size of mating pool 
     * @return: mating pool containing of chromosomes 
     */
    private Chromosome[] rouletteSelectParents(Chromosome[] pool, int nSelected) {

        double total_fitness = 0;
        for(Chromosome c : pool) total_fitness += c.getFitness();
        List<Chromosome> parents = new ArrayList<>();
                
        while(parents.size() < nSelected) {
            double r = rand.nextDouble();
            int k = 0;
            double acc_fitness = 0;
            
            while(acc_fitness <= r * total_fitness) {
                acc_fitness += pool[k].getFitness();
                k += 1;
            }
            
            parents.add(pool[k-1]);
        }
        return parents.toArray(new Chromosome[0]);
    }
    
    /**
     * Perform tournament selection
     * @param pool : the source for selection
     * @param nSelected: the size of mating pool 
     * @return: mating pool containing of chromosomes 
     */
    private Chromosome[] tournamentSelectParents(Chromosome[] pool, int nSelected, double tournamentThresh) {
        
        List<Chromosome> parents = new ArrayList<>();
        
        while(parents.size() < nSelected) {
            int index1 = (int) (rand.nextDouble() * pool.length);
            int index2 = (int) (rand.nextDouble() * pool.length - 1);
            index2 = (index1 + index2 + 1) % (pool.length);
            Chromosome c1 = pool[index1], c2 = pool[index2];
            double r = rand.nextDouble();
            
            Chromosome parent;
            if(r < tournamentThresh) {
                parent =  c1.getFitness() < c2.getFitness()? c2 : c1;
            }else {
                parent =  c1.getFitness() > c2.getFitness()? c2 : c1;
            }
            
            parents.add(parent);
        }
        return parents.toArray(new Chromosome[0]);
    }
    
    /**
     * Perform refined selection
     * @param pool : the source for selection
     * @param nSelected: the size of mating pool 
     * @return: mating pool containing of chromosomes 
     */
    private Chromosome[] refineSelectParents(Chromosome[] pool, int nSelected, double tournamentThresh) {
        int nPreselected = (int) (nSelected*1.5);
        if(nPreselected > pool.length) nPreselected = pool.length;
        Chromosome[] pool2 = tournamentSelectParents(pool, nPreselected , tournamentThresh);
        return rouletteSelectParents(pool2, nSelected);
    }
        
    /**
     * Perform uniform crossover without ensuring uniquity for elements in children encoded value
     * @param parents: parent chromosomes
     * @return: newly created child 
     */    
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
    
    /**
     * Perform uniform crossover with ensuring uniquity for elements in children encoded value
     * @param parents: parent chromosomes
     * @return: newly created child 
     */
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
        

    /**
     * Perform uniform onepoint without ensuring uniquity for elements in children encoded value
     * @param parents: parent chromosomes
     * @return: newly created child 
     */
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
    
    
    /**
     * Perform onepoint crossover with ensuring uniquity for elements in children encoded value
     * @param parents: parent chromosomes
     * @return: newly created child 
     */
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
    
    /**
     * Perform uni-three-parent crossover without ensuring uniquity for elements in children encoded value
     * @param parents: parent chromosomes
     * @return: newly created child 
     */
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
    
    /**
     * Perform uni-three-parent crossover with ensuring uniquity for elements in children encoded value
     * @param parents: parent chromosomes
     * @return: newly created child 
     */
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
    
    /**
     * Perform uni-one-point crossover without ensuring uniquity for elements in children encoded value
     * @param parents: parent chromosomes
     * @return: newly created child 
     */
    private Chromosome uniOnePointCrossOverUnchecked(Chromosome[] parents) {
        Chromosome parent1 = parents[0], parent2 =  parents[1], parent3 = parents[2];
        
        return onePointCrossOverUnchecked(new Chromosome[] {
            uniformCrossOverUnchecked(new Chromosome[] {parent1, parent2}),
            parent3
        });        
    }
    
    /**
     * Perform uni-one-point crossover with ensuring uniquity for elements in children encoded value
     * @param parents: parent chromosomes
     * @return: newly created child 
     */
    private Chromosome uniOnePointCrossOverChecked(Chromosome[] parents) {
        Chromosome parent1 = parents[0], parent2 =  parents[1], parent3 = parents[2];
        
        return onePointCrossOverChecked(new Chromosome[] {
            uniformCrossOverChecked(new Chromosome[] {parent1, parent2}),
            parent3
        });        
    }

    /**
     * Get number of parent for crossover stage
     * @return: number of parent
     **/
    private int getNumberOfParents() {
        if(crossOverType == CrossOverType.UNI_THREE_PARENT || crossOverType == CrossOverType.UNI_ONE_POINT) {
            return 3;
        }
        else return 2;
    }
    
    /**
     * Perform crossover
     * @param parents: parent chromosomes
     * @return: newly created child
     */
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
    
    /**
     * Perform switch-2-point mutation
     * @param c: Chromosome to be mutated
     * @return: successful or not
     */
    private boolean mutateSwitchPoint(Chromosome c) {
        //double bitMutationRate = (Double) extraParams.get("bitMutationRate");
        int N = c.encoded.length;
        
        int i = (int)(N*rand.nextDouble());
        int j = (int)((N-1) * rand.nextDouble());
        j = (i + j + 1) % N;
        int tmp = c.encoded[i];
        c.encoded[i] = c.encoded[j];
        c.encoded[j] = tmp;
        c.clearFitness();
       
        return true;
    }
    
    /**
     * Perform single point mutation without ensuring uniquity for elements in the mutated encoded value
     * @param c: Chromosome to be mutated
     * @return: successful or not
     */
    private boolean mutateOnePointUnchecked(Chromosome c) {
        int N = (Integer) extraParams.getOrDefault("maxIndex", c.encoded.length);
        int i = (int)(rand.nextDouble() * c.encoded.length);
        c.encoded[i] = (int)(rand.nextDouble() * N);                
        c.clearFitness();
        
        return true;
    }
    
    /**
     * Perform single point mutation with ensuring uniquity for elements in the mutated encoded value
     * @param c: Chromosome to be mutated
     * @return: successful or not
     */
    private boolean mutateOnePointChecked(Chromosome c) {
        int N = (Integer) extraParams.getOrDefault("maxIndex", c.encoded.length);
        
        int i = (int)(rand.nextDouble() * c.encoded.length);
        int index = (int)(rand.nextDouble() * N); 
        int j = -1;
        
        for(int k = 0; k < c.encoded.length; k++) {
            if(c.encoded[k] == index) {
                j = k;
                break;
            }
        }
        
        int tmp = c.encoded[i];
        c.encoded[i] = index;
        if(j >= 0) c.encoded[j] = tmp;
        
        c.clearFitness();
        
        return true;
    }
           
    /**
     * Perform mutation
     * @param c: Chromosome to be mutated
     * @return: successful or not
     */
    protected boolean mutateChromosome(Chromosome c) {
        if(mutationType == MutationType.SWITCH_POINT) {
            return mutateSwitchPoint(c); 
        }
        
        if(mutationType == MutationType.MUTATE_POINT) {
            return checkUnique? mutateOnePointChecked(c) : mutateOnePointUnchecked(c);
        }
        
        throw new RuntimeException("Unsupported mutation type: " + mutationType);
    }
        
    /**
     * Print information after each generation
     */
    private void printInfo() {
        if(generation % 1000 == 0) {
            System.out.println("Generation " + generation + ", fitness:" + population[0].getFitness());
            
            /*
            for(int i = 0; i < 20; i ++) {
                Chromosome c = population[i];            
                System.out.print("Fitness:");
                System.out.print(String.format("%f ", c.getFitness()));
                System.out.println(", Chromosome : " + c.toString());
            }*/
        }
    }
    
    /**
     * Create new generation
     */
    private void nextGeneration(int numStep) {
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
        Collections.sort(newPopulation, (c1, c2) -> Double.compare(c2.getFitness(), c1.getFitness()));
        population = newPopulation.toArray(new Chromosome[0]);
        
        generation += 1;        
    }
            
    /**
     * Run the engine
     * @param numStep: number of step to run
     */
    public void run(int numStep) {

        for(int i = 0; i < numStep; i++) {                
            nextGeneration(numStep);
            printInfo();
        }
    }
}
