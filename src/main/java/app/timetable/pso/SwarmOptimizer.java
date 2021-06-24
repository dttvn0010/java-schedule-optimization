package app.timetable.pso;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import app.timetable.model.DataSet;
import util.CombinationUtil;

/**
 * Swarm optimizer
 **/
public class SwarmOptimizer {

    private ParticleOptimizer optimizer;
    private Random rand;
    private Solution bestGlobalItem;
    
    /**
     * Constructor
     * @param optimizer
     * @param rand
     **/
    public SwarmOptimizer(ParticleOptimizer optimizer, Random rand) {
        this.optimizer = optimizer;
        this.rand = rand;
    }
    
    /**
     * Build a new swarm
     * @param psoAttributes
     * @param dataSet
     * @return a new swarm
     **/
    public Particle[] buildSwarm(PsoAttributes psoAttributes, DataSet dataSet) {
        Particle[] swarm = new Particle[psoAttributes.swarmSize];
        for(int i = 0; i < swarm.length; i++) {
            swarm[i] = new Particle(getNewSolution(dataSet), optimizer);
        }
        int[] particleIndex = initArray(psoAttributes.swarmSize);
        updateInformers(swarm, particleIndex, psoAttributes.maxInformers);
        return swarm;
    }
    
    /**
     * Generating a random solution
     * @param dataSet
     * @return a new generated solution
     **/
    public Solution getNewSolution(DataSet dataSet) {
        int N = dataSet.timeSlots.length * dataSet.rooms.length;
        int K = dataSet.classes.length;
        int[] encoded = CombinationUtil.genCombination(N, K);        
        return new Solution(dataSet, encoded);
    }
    
    /**
     * Update informers
     * @param swarm
     * @param particleIndex
     * @param maxInformers
     **/
    public void updateInformers(Particle[] swarm, int[] particleIndex, int maxInformers) {
        shuffle(particleIndex);
        List<Particle> informers = new ArrayList<Particle>();
        int informersCount = maxInformers + 1;
        
        for (int i = 1; i < particleIndex.length + 1; i++)
        {
            informers.add(swarm[particleIndex[i - 1]]);
            if (i % informersCount == 0)
            {
                addInformersToParticle(informers);
                informers.clear();
            }
        }
        
        addInformersToParticle(informers);
    }
    
    /**
     * Add informers to each particle in a group
     * @param particles
     **/
    public void addInformersToParticle(List<Particle> particles)
    {
        for (Particle particle : particles)
        {
            particle.informersList.clear();
            particle.informersList.addAll(particles);
            particle.informersList.remove(particle);
        }
    }
    
    /**
     * Optimize the swarm
     * @param swarm
     * @param psoAttributes 
     **/
    public int optimize(Particle[] swarm, PsoAttributes psoAttributes) {
        bestGlobalItem = swarm[0].currentRoute.clone();
        int[] particleIndex = initArray(psoAttributes.swarmSize);
        int epoch = 0;
        int staticEpochs = 0;
        
        while (epoch < psoAttributes.maxEpochs)
        {
            if(epoch % 100 == 0) {
                System.out.println(String.format("Epoch %d, fitness: %f", epoch, bestGlobalItem.fitness));
            }
            
            boolean isFitnessImproved = false;
            for(Particle particle : swarm) {
                double fitness = particle.optimize(psoAttributes);
                
                if (fitness > bestGlobalItem.fitness)
                {
                    particle.currentRoute.copyTo(bestGlobalItem);
                    isFitnessImproved = true;                   
                }
            }
            
            if(!isFitnessImproved) {
                staticEpochs++;
                if (staticEpochs == psoAttributes.maxStaticEpochs)
                {
                    this.updateInformers(swarm, particleIndex, psoAttributes.maxInformers);
                    staticEpochs = 0;
                }
            }
            
            epoch++;            
        }
        
        return (int) bestGlobalItem.fitness;
    }
    
    /**
     * Shuffle an array
     * @param arr
     **/
    private void shuffle(int[] arr) {
        int i = arr.length - 1;
        while(i > 0) {
            int k = rand.nextInt(i+1);
            int tmp = arr[i];
            arr[i] = arr[k];
            arr[k] = tmp;
            i--;
        }
    }
    
    /**
     * Create an incremental array
     * @param size
     **/
    private int[] initArray(int size)
    {
        int[] arr = new int[size];
        for (int i = 0; i < size; i++)
        {
            arr[i] = i;
        }
        return arr;
    }
}
