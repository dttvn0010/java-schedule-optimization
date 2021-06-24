package app.timetable.pso;

import java.io.IOException;
import java.util.Random;
import app.timetable.model.DataSet;

/**
 * Solving time table with particle swarm optimization
 **/
public class TimeTableApp {
    
    
    /**
     * Running all benchmarks 
     */
    
    public static void run(String dataSetPath) throws IOException {
        DataSet dataSet =  new DataSet(dataSetPath);
        Random rand = new Random();
        PsoAttributes psoAttributes = new PsoAttributes();
        
        SolutionUpdater routeUpdater = new SolutionUpdater();
        SolutionManager routeManager = new SolutionManager(rand, routeUpdater);
        ParticleOptimizer optimizer = new ParticleOptimizer(rand, routeManager);
        SwarmOptimizer swarmOptimizer = new SwarmOptimizer(optimizer, rand);
        SwarmManager swarmManager = new SwarmManager(swarmOptimizer);
        
        for(int k = 0;  k < 10; k++) {
            System.out.println("Run " + (k+1));
            Particle[] swarm = swarmManager.buildSwarm(psoAttributes, dataSet);
            int fitness = swarmManager.optimize(swarm,psoAttributes);
            System.out.println("fitness:" + fitness);
        }
    }
    
    /**
     * Program entry point
     * @throws IOException 
     */
    
    public static void main(String[] args) throws IOException {
        run("timetable/hard/160_classes.json");
        run("timetable/hard/200_classes.json");
        
    }
}
