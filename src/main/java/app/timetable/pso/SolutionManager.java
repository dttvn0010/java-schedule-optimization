package app.timetable.pso;

import java.util.Random;

/**
 * Manage solution during search phase
 **/
public class SolutionManager {

    private Random rand;
    private SolutionUpdater routeUpdater;
    
    /**
     * Constructor
     * @param rand
     * @param routeUpdater
     */
    public SolutionManager(Random rand, SolutionUpdater routeUpdater) {
        this.rand = rand;
        this.routeUpdater = routeUpdater;
    }
    
    /**
     * Generate a new indexes array from a set of section  
     * @param sections: the set of section contains 3 item: local best item, personal best item & the current item
     * @return: new generated indexes
     **/
    public int[] addSections(Solution[] sections) {

        if(!routeUpdater.isinitialised) {
            routeUpdater.initialise(sections[0].encoded.length, sections[0].getNmax());
        }
        
        for(int i = 0; i < sections.length; i++) {
            routeUpdater.addSection(rand.nextInt(sections[i].encoded.length), sections[i]);
        }
        
        return routeUpdater.finalizeIndexes(sections[0]);
    }
    
    /**
     * Get the velocity component
     * @param: solution
     * @param: weighting
     * @param: randomDouble
     * @return: velocity component
     **/
    public double updateVelocity(Solution solution, double weighting, double randomDouble) {
        return solution.fitness * randomDouble * weighting;
    }
    
    /**
     * Get the section size of each component (local best/personal best/ current item)
     * @param: solution
     * @param: segmentVelocity
     * @param: totalVelocity
     * @reutrn: section size of the corresponding component 
     **/
    public int getSectionSize(Solution solution, double segmentVelocity, double totalVelocity) {
        int length = solution.encoded.length;
        return (int) Math.floor((segmentVelocity / totalVelocity) * length);
    }
}
