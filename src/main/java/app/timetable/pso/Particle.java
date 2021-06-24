package app.timetable.pso;

import java.util.ArrayList;
import java.util.List;

public class Particle {

    public List<Particle> informersList;
    public Solution currentRoute, localBestRoute, personalBestRoute;
    private ParticleOptimizer tspParticleOptimiser;
    
    public Particle(Solution route, ParticleOptimizer tspParticleOptimizer) {
        this.currentRoute = route;
        this.tspParticleOptimiser = tspParticleOptimizer;
        this.personalBestRoute = route.clone();
        this.informersList = new ArrayList<>();
    }
    
    private Solution getLocalBestRoute() {
        Solution tmp = personalBestRoute;
        
        for(Particle particle : informersList) {
            if(tmp.fitness > particle.personalBestRoute.fitness) {
                tmp = particle.personalBestRoute;
            }
        }
        return tmp.clone();
    }
    
    public double optimize(PsoAttributes psoAttributes) {
        
        localBestRoute = getLocalBestRoute();
        
        currentRoute.encoded = tspParticleOptimiser.getOptimizedDestinationIndex(
                currentRoute, personalBestRoute, localBestRoute, psoAttributes);
        
        double currentFitness = currentRoute.calcFitness();
        
        if(currentFitness > personalBestRoute.calcFitness()) {
            System.arraycopy(currentRoute.encoded, 0, 
                                personalBestRoute.encoded, 0, 
                                personalBestRoute.encoded.length);
        }
        return currentFitness;
    }
}

