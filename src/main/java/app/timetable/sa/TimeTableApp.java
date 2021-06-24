package app.timetable.sa;

import java.io.IOException;
import app.timetable.model.DataSet;

/**
 * Solving time table with simulated annealing
 **/
public class TimeTableApp 
{
    private static DataSet dataSet;
        
    /**
     * Running all benchmarks 
     * @throws IOException 
     */
    public static void run(String dataSetName) throws IOException {
        dataSet =  new DataSet(dataSetName);

        double temperature = 10.0;
        double coolingRate = 0.00002;
                
        for(int k = 0; k < 10; k++) {
            SAOptimizer opt = new SAOptimizer(dataSet, temperature, coolingRate);
            System.out.println("Run: " + (k+1));
            double result = opt.run();
            System.out.println("Final fitness:" + result);
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
