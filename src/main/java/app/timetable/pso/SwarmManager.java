package app.timetable.pso;

import app.timetable.model.DataSet;

/**
 * Manage the swarm
 **/
public class SwarmManager {

    private SwarmOptimizer swarmOptimizer;
    
    /**
     * Constructor
     * @param swarmOptimizer
     **/
    public SwarmManager(SwarmOptimizer swarmOptimizer) {
        this.swarmOptimizer = swarmOptimizer;
    }
    
    /**
     * Build a swarm
     * @param psoAttributes
     * @param dataSet
     * @return a swarm of particle
     **/
    public Particle[] buildSwarm(PsoAttributes psoAttributes, DataSet dataSet) {
        return swarmOptimizer.buildSwarm(psoAttributes, dataSet);
    }
    
    /**
     * Optimize the swarm
     * @param swarm
     * @param psoAttributes
     **/
    public int optimize(Particle[] swarm, PsoAttributes psoAttributes) {
        return swarmOptimizer.optimize(swarm, psoAttributes);
    }
}
