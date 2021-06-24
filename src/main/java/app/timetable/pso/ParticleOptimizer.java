package app.timetable.pso;

import java.util.Random;

public class ParticleOptimizer {

    private Random rand;
    private SolutionManager routeManager;
    
    public ParticleOptimizer(Random rand, SolutionManager routeManager) {
        this.rand = rand;
        this.routeManager = routeManager;
    }
    
    public int[] getOptimizedDestinationIndex(Solution currRoute, Solution pBRoute, Solution lBRoute, PsoAttributes psoAttribs) {
        double currV = routeManager.updateVelocity(currRoute, psoAttribs.w, 1);
        double pBV = routeManager.updateVelocity(pBRoute, psoAttribs.c1, rand.nextDouble());
        double lBV = routeManager.updateVelocity(lBRoute, psoAttribs.c2, rand.nextDouble());
        double totalVelocity = currV + pBV + lBV;
        
        currRoute.segmentSize = routeManager.getSectionSize(currRoute, currV, totalVelocity);
        pBRoute.segmentSize = routeManager.getSectionSize(pBRoute, pBV, totalVelocity);
        lBRoute.segmentSize = routeManager.getSectionSize(lBRoute, lBV, totalVelocity);
        return routeManager.addSections(new Solution[] { lBRoute, pBRoute, currRoute });
    }    
}
