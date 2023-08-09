package MonteCarloMini;
/* Serial  program to use Monte Carlo method to 
 * locate a minimum in a function
 * This is the reference sequential version (Do not modify this code)
 * Michelle Kuttel 2023, University of Cape Town
 * Adapted from "Hill Climbing with Montecarlo"
 * EduHPC'22 Peachy Assignment" 
 * developed by Arturo Gonzalez Escribano  (Universidad de Valladolid 2021/2022)
 */
import java.util.Random;
import java.io.FileWriter;
import java.io.IOException;

class MonteCarloMinimization{

	static final boolean DEBUG=false;
	
	static long startTime = 0;
	static long endTime = 0;

	//timers - note milliseconds
	private static void tick(){
		startTime = System.currentTimeMillis();
	}
	private static void tock(){
		endTime=System.currentTimeMillis(); 
	}
	
    public static void main(String[] args)  {

    	int rows, columns; //grid size
    	double xmin, xmax, ymin, ymax; //x and y terrain limits
    	TerrainArea terrain;  //object to store the heights and grid points visited by searches
    	double searches_density;	// Density - number of Monte Carlo  searches per grid position - usually less than 1!

     	int num_searches;		// Number of searches
    	Search [] searches;		// Array of searches
    	Random rand = new Random();  //the random number generator
    	
    	if (args.length!=7) {  
    		System.out.println("Incorrect number of command line arguments provided.");   	
    		System.exit(0);
    	}
    	/* Read argument values */
    	rows =Integer.parseInt( args[0] );
    	columns = Integer.parseInt( args[1] );
    	xmin = Double.parseDouble(args[2] );
    	xmax = Double.parseDouble(args[3] );
    	ymin = Double.parseDouble(args[4] );
    	ymax = Double.parseDouble(args[5] );
    	searches_density = Double.parseDouble(args[6] );
  
    	if(DEBUG) {
    		/* Print arguments */
    		System.out.printf("Arguments, Rows: %d, Columns: %d\n", rows, columns);
    		System.out.printf("Arguments, x_range: ( %f, %f ), y_range( %f, %f )\n", xmin, xmax, ymin, ymax );
    		System.out.printf("Arguments, searches_density: %f\n", searches_density );
    		System.out.printf("\n");
    	}
    	
    	// Initialize 
    	terrain = new TerrainArea(rows, columns, xmin,xmax,ymin,ymax);
    	num_searches = (int)( rows * columns * searches_density );
    	searches= new Search [num_searches];
    	for (int i=0;i<num_searches;i++) 
    		searches[i]=new Search(i+1, rand.nextInt(rows),rand.nextInt(columns),terrain);
    	
      	if(DEBUG) {
    		/* Print initial values */
    		System.out.printf("Number searches: %d\n", num_searches);
    		//terrain.print_heights();
    	}
    	
    	//start timer
    	tick();
    	
    	//all searches
    	int min=Integer.MAX_VALUE;
    	int local_min=Integer.MAX_VALUE;
    	int finder =-1;
    	for  (int i=0;i<num_searches;i++) {
    		local_min=searches[i].find_valleys();
    		if((!searches[i].isStopped())&&(local_min<min)) { //don't look at  those who stopped because hit exisiting path
    			min=local_min;
    			finder=i; //keep track of who found it
    		}
    		if(DEBUG) System.out.println("Search "+searches[i].getID()+" finished at  "+local_min + " in " +searches[i].getSteps());
    	}
   		//end timer
   		tock();
   		
    	if(DEBUG) {
    		/* print final state */
    		terrain.print_heights();
    		terrain.print_visited();
    	}
		
		
    	try {
			FileWriter fileWriter = new FileWriter("Parallel/data/varyingSearchesSerial.txt", true);
			String fstring = String.format("%d %d %f %f %f %f %f %d %d %d %f %f\n", rows, columns, xmin, xmax, ymin, ymax, searches_density, num_searches, endTime-startTime, min, terrain.getXcoord(searches[finder].getPos_row()), terrain.getYcoord(searches[finder].getPos_col()));
			fileWriter.append(fstring);
			fileWriter.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
    }
}