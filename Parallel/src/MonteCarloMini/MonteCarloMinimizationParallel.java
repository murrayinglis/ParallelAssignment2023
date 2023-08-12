package MonteCarloMini;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.Random;
import java.lang.Math;

// Class for returning the minimum value and the index of the Search object 
class LocalMin {
	int finder;
	int min;
	public LocalMin(int finder, int min)
	{
		this.finder=finder;
		this.min=min;
	}
} 

public class MonteCarloMinimizationParallel extends RecursiveTask<LocalMin>{

    static final boolean DEBUG=false;

	int min=Integer.MAX_VALUE;
    int local_min=Integer.MAX_VALUE;

    int lo;
    int hi;
    TerrainArea terrain;
	SearchParallel [] searches;

    static final int SEQUENTIAL_CUTOFF = 300;
	
	static long startTime = 0;
	static long endTime = 0;

	//timers - note milliseconds
	private static void tick(){
		startTime = System.currentTimeMillis();
	}
	private static void tock(){
		endTime=System.currentTimeMillis(); 
	}
    
    public MonteCarloMinimizationParallel(int lo, int hi, TerrainArea terrain, SearchParallel[] searches)
    {
        this.lo = lo;
        this.hi = hi;
        this.terrain = terrain;
		this.searches = searches;
    }

	@Override
    protected LocalMin compute() {
		int finder = 0;
        if ((hi-lo) < SEQUENTIAL_CUTOFF) // Sequential algorithm for when size is less than the cutoff
        {
			for  (int i=lo; i<hi; i++) {
				local_min=searches[i].find_valleys();
				if((!searches[i].isStopped())&&(local_min<min)) { //don't look at  those who stopped because hit exisiting path
					min=local_min;
					finder=i; //keep track of who found it
				}	
				if(DEBUG) System.out.println("Search "+searches[i].getID()+" finished at  "+local_min + " in " +searches[i].getSteps());
			}
			return new LocalMin(finder,min);
        }
        else // If size is greater than the cutoff, fork
        {
            MonteCarloMinimizationParallel left = new MonteCarloMinimizationParallel(lo,(hi+lo)/2,terrain,searches);
            MonteCarloMinimizationParallel right = new MonteCarloMinimizationParallel((hi+lo)/2,hi,terrain,searches);
            
            left.fork();
            LocalMin rightAns = right.compute();
            LocalMin leftAns = left.join();
			if (rightAns.min < leftAns.min) {return rightAns;}
			else {return leftAns;}
        }
    }

	// Main method
    public static void main(String[] args)  {

        int rows, columns; //grid size
        double xmin, xmax, ymin, ymax; //x and y terrain limits
        TerrainArea terrain;  //object to store the heights and grid points visited by searches
        double searches_density;	// Density - number of Monte Carlo  searches per grid position - usually less than 1!
    
        int num_searches;		// Number of searches
        SearchParallel [] searches;		// Array of searches
    
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

    	// Initialize 
    	terrain = new TerrainArea(rows, columns, xmin,xmax,ymin,ymax);
    	num_searches = (int)( rows * columns * searches_density );
    	searches= new SearchParallel [num_searches];
    	for (int i=0;i<num_searches;i++) 
    		searches[i]=new SearchParallel(i+1, rand.nextInt(rows),rand.nextInt(columns),terrain);

        if(DEBUG) {
    		/* Print arguments */
    		System.out.printf("Arguments, Rows: %d, Columns: %d\n", rows, columns);
    		System.out.printf("Arguments, x_range: ( %f, %f ), y_range( %f, %f )\n", xmin, xmax, ymin, ymax );
    		System.out.printf("Arguments, searches_density: %f\n", searches_density );
    		System.out.printf("\n");
    	}



        // Creating thread pool
        ForkJoinPool fjPool = new ForkJoinPool();

		//start timer
		tick();

        LocalMin localMin = fjPool.invoke(new MonteCarloMinimizationParallel(0,num_searches,terrain,searches));

		//end timer
		tock();

		int min = localMin.min;
		int finder = localMin.finder;

       

        if(DEBUG) {
    		/* print final state */
    		terrain.print_heights();
    		terrain.print_visited();
    	}
    	
		System.out.printf("Run parameters\n");
		System.out.printf("\t Rows: %d, Columns: %d\n", rows, columns);
		System.out.printf("\t x: [%f, %f], y: [%f, %f]\n", xmin, xmax, ymin, ymax );
		System.out.printf("\t Search density: %f (%d searches)\n", searches_density,num_searches );
		System.out.printf("\t Sequential cutoff: %d \n", SEQUENTIAL_CUTOFF );

		/*  Total computation time */
		System.out.printf("Time: %d ms\n",endTime - startTime );
		int tmp=terrain.getGrid_points_visited();
		System.out.printf("Grid points visited: %d  (%2.0f%s)\n",tmp,(tmp/(rows*columns*1.0))*100.0, "%");
		tmp=terrain.getGrid_points_evaluated();
		System.out.printf("Grid points evaluated: %d  (%2.0f%s)\n",tmp,(tmp/(rows*columns*1.0))*100.0, "%");
	
		/* Results*/
		System.out.printf("Global minimum: %d at x=%.1f y=%.1f\n\n", min, terrain.getXcoord(searches[finder].getPos_row()), terrain.getYcoord(searches[finder].getPos_col()) );
    }
}
