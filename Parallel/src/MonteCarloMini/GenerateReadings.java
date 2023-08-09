package MonteCarloMini;

import java.util.ArrayList;
import MonteCarloMini.MonteCarloMinimization;
import MonteCarloMini.MonteCarloMinimizationParallel;
import java.io.FileWriter;
import java.io.IOException;

public class GenerateReadings {

    public static void main(String[] args)
    {
        String[][] varyingSearches = 
        {
            {"100","100","-10","10","-10","10","0.3"},
            {"100","100","-10","10","-10","10","0.5"},
            {"100","100","-10","10","-10","10","0.7"},
            {"200","200","-10","10","-10","10","0.3"},
            {"200","200","-10","10","-10","10","0.5"},
            {"200","200","-10","10","-10","10","0.7"},
            {"400","400","-10","10","-10","10","0.3"},
            {"400","400","-10","10","-10","10","0.5"},
            {"400","400","-10","10","-10","10","0.7"},
            {"800","800","-10","10","-10","10","0.3"},
            {"800","800","-10","10","-10","10","0.5"},
            {"800","800","-10","10","-10","10","0.7"},
            {"1200","1200","-10","10","-10","10","0.3"},
            {"1200","1200","-10","10","-10","10","0.5"},
            {"1200","1200","-10","10","-10","10","0.7"},
            {"1600","1600","-10","10","-10","10","0.3"},
            {"1600","1600","-10","10","-10","10","0.5"},
            {"1600","1600","-10","10","-10","10","0.7"},
            {"2000","2000","-10","10","-10","10","0.3"},
            {"2000","2000","-10","10","-10","10","0.5"},
            {"2000","2000","-10","10","-10","10","0.7"},
            {"4000","4000","-10","10","-10","10","0.3"},
            {"4000","4000","-10","10","-10","10","0.5"},
            {"4000","4000","-10","10","-10","10","0.7"},
            {"6000","6000","-10","10","-10","10","0.3"},
            {"6000","6000","-10","10","-10","10","0.5"},
            {"6000","6000","-10","10","-10","10","0.7"},
            {"8000","8000","-10","10","-10","10","0.3"},
            {"8000","8000","-10","10","-10","10","0.5"},
            {"8000","8000","-10","10","-10","10","0.7"},
        };

        try{
            FileWriter fileWriterSerial1 = new FileWriter("Parallel/data/varyingSearchesSerial.txt");
            fileWriterSerial1.write("rows columns xmin xmax ymin ymax searches_density num_searches time min x y\n");
            fileWriterSerial1.close();

            FileWriter fileWriterParallel1 = new FileWriter("Parallel/data/varyingSearchesParallel.txt");
            fileWriterParallel1.write("rows columns xmin xmax ymin ymax searches_density num_searches time min x y\n");
            fileWriterParallel1.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        // Serial points
        System.out.println("Serial points");
        for (int c = 0; c<24; c++) // 6000 x 6000 crashes
        {
            System.out.println("On grid size of: " + varyingSearches[c][0] + "x" + varyingSearches[c][0]);
            for (int j = 0; j < 3; j++)
            {
                MonteCarloMinimization.main(varyingSearches[c]);
            }
        }

        // Parallel points
        System.out.println("Parallel points");
        for (int i = 0; i <24; i++)
        {
            System.out.println("On grid size of: " + varyingSearches[i][0] + "x" + varyingSearches[i][0]);
            for (int j = 0; j < 3; j++)
            {
                MonteCarloMinimizationParallel.main(varyingSearches[i]);
            }
        }
    }
}
