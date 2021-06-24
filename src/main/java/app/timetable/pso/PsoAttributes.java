package app.timetable.pso;

/**
 * Attribute parameters for particle swarm optimization
 **/
public class PsoAttributes {

    public double c1 = 1.4;
    public double c2 = 1.4;
    
    public int maxEpochs = 5000;
    
    public int maxInformers = 5;
    
    public int maxStaticEpochs = 250;
    
    
    public int swarmSize = 100;
    
    
    public double w = 0.5;
    
}
