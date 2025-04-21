/**
 * 4674 CHARALAMPOS THEODORIDIS
 * 4742 PANTELIS MPONITSIS
 * 4789 GEORGIOS SIDIROPOULOS
 */
 
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * File Reader Class responsible for reading the two files.
 */
public class FileReader{
    private ArrayList<Double>x1CoordinateTS;    //X1 coordinates of the Training Set.
    private ArrayList<Double>x2CoordinateTS;    //X2 coordinates of the Training Set.
    private ArrayList<String>categoryTS;        //ArrayList with categories of each point [x1,x2] from training set.
    private ArrayList<Double>x1CoordinateCS;    //X1 coordinates of the Check Set.
    private ArrayList<Double>x2CoordinateCS;    //X2 coordinates of the Check Set.
    private ArrayList<String>categoryCS;        //ArrayList with categories of each point [x1,x2] from check set.

    public FileReader(){
        this.x1CoordinateCS=new ArrayList<Double>();
        this.x2CoordinateCS=new ArrayList<Double>();
        this.categoryCS=new ArrayList<String>();
        this.x1CoordinateTS=new ArrayList<Double>();
        this.x2CoordinateTS=new ArrayList<Double>();
        this.categoryTS=new ArrayList<String>();
    }

    /**
     * This method is responsible for reading the two data sets from the files in the directory.
    */
    public void readFile(){
        File tsFile=new File("trainingSet.txt");    //Create new file of training Set.
        File csFile=new File("checkSet.txt");       //Create new file of check Set.

        try {
            Scanner inputTS = new Scanner(tsFile);  //New scanner for tsFile.
            Scanner inputCS=new Scanner(csFile);    //New scanner for csFile.
            while(inputTS.hasNext()){
                if(inputTS.hasNextDouble()){
                    x1CoordinateTS.add(inputTS.nextDouble());
                    x2CoordinateTS.add(inputTS.nextDouble());
                    x1CoordinateCS.add(inputCS.nextDouble());
                    x2CoordinateCS.add(inputCS.nextDouble());
                }
                if(inputTS.hasNextLine()){
                    categoryTS.add(inputTS.nextLine().trim());
                    categoryCS.add(inputCS.nextLine().trim());
                }
            }
            inputTS.close();    //Close the input scanner.
            inputCS.close();    //Close the inputCS scanner.
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Getter for the x1Coordinates of Training Set.
     * @return ArrayList<Double> containing all the x1 Coordinates of the training set.
     */
    public ArrayList<Double> getx1CoordinateTS(){
        return this.x1CoordinateTS;
    }

    /**
     * Getter for the x2Coordinates of Training Set.
     * @return ArrayList<Double> containing all the x2 Coordinates of the training set.
     */
    public ArrayList<Double> getx2CoordinateTS(){
        return this.x2CoordinateTS;
    }

    /**
     * Getter for the categories of Training Set.
     * @return ArrayList<Strings> cotaining all the Categories of the training set.
     */
    public ArrayList<String> getcategoryTS(){
        return this.categoryTS;
    }

    /**
     * Getter for the x1Coordinates of Check Set.
     * @return ArrayList<Double> containing all the x1Coordinates of the check set.
     */
    public ArrayList<Double> getx1CoordinateCS(){
        return this.x1CoordinateCS;
    }

    /**
     * Getter for the x2Coordinates of the Check Set
     * @return ArrayList<Double> containing all the x2Coordinates of the check set.
     */
    public ArrayList<Double> getx2CoordinateCS(){
        return this.x2CoordinateCS;
    }

    /**
     * Getter for the categories of the Check Set.
     * @return ArrayList<String> containing all the categorys of the check set.
     */
    public ArrayList<String> getcategoryCS(){
        return this.categoryCS;
    }

    /**
     * Getter for the size of data set(Training of Check).
     * @return Int number with the number of elements of each set.
     */
    public int getSize(){
        return this.x1CoordinateCS.size();
    }
}
