package app.timetable.pso;

import java.util.Random;

/**
 * Update solution after each epoch
 **/
public class SolutionUpdater {

    public boolean[] availabilityMask;
    private int[] encoded;
    private int Nmax;
    public boolean isinitialised;
    private int indexPointer;
    private Random rand = new Random();
    
    /**
     * Initialise the updater
     * @param nClass: Number of classes in the time table
     * @param Nmax: max indexes for item in the encoded array
     **/
    public void initialise(int nClass, int Nmax) {
        this.Nmax = Nmax;
        this.availabilityMask = new boolean[Nmax];
        for(int i = 0; i < Nmax; i++) this.availabilityMask[i] = true;
        
        this.encoded = new int[nClass];
        this.indexPointer = 0;
        isinitialised = true;        
    }
       
    /**
     * Add section values to the encoded array
     * @param pointer: start offset of the section's encoded array
     * @param section: section to be added
     **/
    public void addSection(int pointer, Solution section) {

        for(int i = 0; i < section.segmentSize; i++) {
            int index = section.encoded[pointer];
            if(availabilityMask[index]) {
                encoded[indexPointer++] = index;
                availabilityMask[index] = false;
            }
            pointer  = (pointer + 1) % section.encoded.length;
        }            
    }
    
    /**
     * Complete the encoded array with values from a solution
     * @param solution: solution
     * @return: the finalized indexes
     **/
    public int[] finalizeIndexes(Solution solution) {        
        int pointer = 0;
        
        if(this.encoded.length < this.Nmax) {
            pointer = rand.nextInt(this.Nmax - this.encoded.length);
        }
        
        for(int i = 0; i < solution.encoded.length; i++) {
            int index = solution.encoded[pointer % solution.encoded.length];
            
            if(availabilityMask[index]) {
                encoded[indexPointer++] = index;
                if(indexPointer >= encoded.length) break;
            }
            
            pointer++;
            
        }
        
        reset();
        
        return getIndexes();
        
    }
    
    /**
     * Get the encoded indexes
     **/
    public int[] getIndexes() {
        int[] copy = new int[encoded.length];
        System.arraycopy(encoded, 0, copy, 0, encoded.length);
        return copy;
    }
    

    /**
     * Reset the availability mask
     **/
    public void reset() {
        indexPointer = 0;
        for(int i = 0; i < availabilityMask.length; i++) availabilityMask[i] = true;
    }    
}
