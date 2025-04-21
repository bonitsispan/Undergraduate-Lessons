/**
 * 4674 CHARALAMPOS THEODORIDIS
 * 4742 PANTELIS MPONITSIS
 * 4789 GEORGIOS SIDIROPOULOS
 */
 
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.io.FileWriter;

/**This class is responsible for creating two txt files . The first contains the coordinates of the points. The second the coordinates of the centroids*/
public class FileConstractor{
    File trainingSet;

    /**
     * This method is responsible for creating random point (x,y). With x in [lowBorder1,highBorder1] and y in [lowBorder2,highBorder2].
     * @param lowBorder1 The lowBorder of the coordinate x.
     * @param highBorder1 The upBorder (highBorder) of the coordinate x.
     * @param lowBorder2  The lowBorder of the coordinate y.
     * @param highBorder2 The upBorder (highBorder) of the coordinate y.
     * @return String Object. The return value contains the random x and y coordinates of the points
     */
    public String randomNumber(double lowBorder1,double highBorder1, double lowBorder2 , double highBorder2){
        Random rand =new Random();

        double xCoordinate=(highBorder1-lowBorder1)*rand.nextDouble()+lowBorder1;

        double yCoordinate=(highBorder2-lowBorder2)*rand.nextDouble()+lowBorder2;

        return xCoordinate+"  "+yCoordinate;
    }

    /**
     * This method writes the centroids points(x,y) to the centroids.txt.
     * @param centroids Is a ArrayList<Double> object that contains the (x,y) coordinates of the centroids.
     */
    public void centroidWriter(String Name,ArrayList<Double> centroids){
        try {
            FileWriter myWriter=new FileWriter(Name);    //Open a file writerObject.

            for(int index=0;index<centroids.size();index++){    //For every list element.
                myWriter.write(centroids.get(index)+"  "+centroids.get(index+1));   //Write to the txt files the index(x) and index+1(y) doubles .
                myWriter.write("\n");   //New line.
                index++;    //Skip y value.
            }
            myWriter.close();   //Close the file.
        } catch (IOException exception){
            exception.printStackTrace();
        }
    }

    /**
     * This method is responsible for writing the randomPoints in the trainingSet txt.
     */
    public void fileWriter(){
        try {
            FileWriter myWriter = new FileWriter("trainingSet.txt");    //Open a file writerObject.

            for(int index=0;index<150;index++){ //For 150 trainingValues
                myWriter.write(randomNumber(0.8, 1.2, 0.8, 1.2)); //Random point(x,y) where x in [0.8,1.2] and y in [0.8,1.2].
                myWriter.write("\n");

                myWriter.write(randomNumber(0.0, 0.5, 0.0, 0.5));//Random point(x,y) where x in [0.0,0.5] and y in [0.0,0.5].
                myWriter.write("\n");

                myWriter.write(randomNumber(0.0, 0.5, 1.5, 2.0));//Random point(x,y) where x in [0.0,0.5] and y in [1.5,2.0].
                myWriter.write("\n");

                myWriter.write(randomNumber(1.5, 2.0,0.0, 0.5));//Random point(x,y) where x in [1.5,2.0] and y in [0.0,0.5].
                myWriter.write("\n");

                myWriter.write(randomNumber(1.5, 2.0, 1.5, 2.0));//Random point(x,y) where x in [1.5,2.0] and y in [1.5,2.0].
                myWriter.write("\n");

                myWriter.write(randomNumber(0.0, 2.0, 0.0, 2.0));//Random point(x,y) where x in [0.0,2.0] and y in [0.0,2.0].
                myWriter.write("\n");

            }
            for(int index=0;index<75;index++){ //For 75 training values
                myWriter.write(randomNumber(0.8, 1.2, 0.0, 0.4));//Random point(x,y) where x in [0.8,1.2] and y in [0.0,0.4].
                myWriter.write("\n");

                myWriter.write(randomNumber(0.8, 1.2, 1.6, 2.0));//Random point(x,y) where x in [0.8,1.2] and y in [0.8,1.2].
                myWriter.write("\n");

                myWriter.write(randomNumber(0.3, 0.7, 0.8, 1.2));//Random point(x,y) where x in [0.3,0.7] and y in [0.8,1.2].
                myWriter.write("\n");

                myWriter.write(randomNumber(1.3, 1.7, 0.8, 1.2));//Random point(x,y) where x in [1.3,1.7] and y in [0.8,1.2].
                myWriter.write("\n");
            }
            myWriter.close(); //Close the file.
        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }
}
