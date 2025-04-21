/**
 * 4674 CHARALAMPOS THEODORIDIS
 * 4742 PANTELIS MPONITSIS
 * 4789 GEORGIOS SIDIROPOULOS
 */
 
import java.io.IOException;
import java.util.ArrayList;

public class Engine {
    /**
     * This method is responsible for running 15 times the K means algorithm with k clusters.
     * @param k Int object. Number(k) of clusters.
     * @param FC FileConstractor object. Used to write the results in txts.
     * @return  Double . The min Error.
     */
    public double AlgorithmEngine(int k,FileConstractor FC,String Name){
        double error=Double.POSITIVE_INFINITY;
        Algorithm algorithm=new Algorithm(k);
        ArrayList<Double>kCentroidsList=algorithm.getkCentroidsList();
        algorithm.loadFile();
        algorithm.intialRandom();
        algorithm.intialWeightList();
        algorithm.intialCounter();
        algorithm.intialTeamList();
        algorithm.randomCentroid();

        for(int index=0;index<15;index++){
            algorithm.randomCentroid();
            algorithm.kMeans();
            if(algorithm.getError()<error){
                kCentroidsList=algorithm.getkCentroidsList();
                error=algorithm.getError();
            }
        }

        System.out.println("The error is : "+"\u001B[31m"+error+"\u001B[0m");
        FC.centroidWriter(Name,kCentroidsList);
        return error;
    }

    /**
     * This method is use to constract the file with the errors.
     * @param erros ArrayList<Double> object .List that contains the errors for every cluster.
     * @param FC FileConstractor object. Used to write the erros in txt file.
     */
    public void errorFile(ArrayList<Double> erros,FileConstractor FC){
        FC.centroidWriter("errors.txt",erros);
    }

    /**
     * The engine of second Exercise solution
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        Engine engine=new Engine();
        ArrayList<Double>errorsList=new ArrayList<Double>();
        FileConstractor FC=new FileConstractor();
        FC.fileWriter();

        long start=System.currentTimeMillis();
        errorsList.add(engine.AlgorithmEngine(3, FC,"centroids3.txt"));
        errorsList.add(3.0);

        errorsList.add(engine.AlgorithmEngine(6, FC, "centroids6.txt"));
        errorsList.add(6.0);

        errorsList.add(engine.AlgorithmEngine(9, FC,"centroids9.txt"));
        errorsList.add(9.0);

        errorsList.add(engine.AlgorithmEngine(12, FC,"centroids12.txt"));
        errorsList.add(12.0);

        engine.errorFile(errorsList, FC);
        System.out.println("The programm was running for : " + "\u001B[31m" + (System.currentTimeMillis()-start)+" milli seconds." +"\033[0m");

        Runtime.getRuntime().exec("python3 PlotConstractor.py");
    }
}
