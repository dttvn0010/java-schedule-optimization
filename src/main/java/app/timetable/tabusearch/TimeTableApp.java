package app.timetable.tabusearch;

import java.io.IOException;
import app.timetable.model.DataSet;


public class TimeTableApp {

    private static DataSet dataSet;
    
    static int neigbourSize = 100;
    static int stoppingTurn = 500;
   
    /**
     * Running all benchmarks 
     * @throws IOException 
     */
    public static void run(String dataSetName, boolean soft) throws IOException {
        dataSet =  new DataSet(dataSetName);
                
        for(int k = 0; k < 10; k++) {
            TBSOptimizer optimizer = new TBSOptimizer(soft, dataSet, neigbourSize, stoppingTurn);
            System.out.println("Run: " + (k+1));
            optimizer.run();
            if(soft) {
                int nUnprefered = (int)( 1/(optimizer.getBestFitness()-1) - 1);
                System.out.println("Unprefered classes:" + nUnprefered);
            }else {
                System.out.println("Arranged classes:" + optimizer.getBestFitness());
            }
        }
    }
    
    /**
     * Program entry point
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {        
       
        run("timetable/hard/160_classes.json", false);
        run("timetable/hard/200_classes.json", false);
        
        run("timetable/soft/160_classes.json", true);
        run("timetable/soft/200_classes.json", true);
    }
}
