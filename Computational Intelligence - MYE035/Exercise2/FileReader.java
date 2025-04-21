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
 * This class is reasponsible for reading the txt file with the coordinates of the points.
 */
public class FileReader{
    private ArrayList<Double>xCoordinate;   //List with the xCoordinates of points.
    private ArrayList<Double>yCoordinate;   //List with the yCoordinates of points.

    /**
     * Constractor for FileReader class.
     * Initialize the two list fields.
    */
    public FileReader(){
        this.xCoordinate=new ArrayList<Double>();   //Initialize the arrayList.
        this.yCoordinate=new ArrayList<Double>();   //Initialize the arrayList.
    }

    /**
     * This method reads the file with name trainingSet.txt.
     * Is responsible for adding the xCoordinates and yCoordinates to the two lists.
     */
    public void readFile(){
        File inputFile=new File("trainingSet.txt"); //Open the txt file.

        try {
            Scanner input = new Scanner(inputFile);
            while (input.hasNextDouble()){  //Read the file line by line.
                xCoordinate.add(input.nextDouble());
                yCoordinate.add(input.nextDouble());
            }
            input.close();
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }

    }

    /**
     * Getter for the xCoordinate list.
     * @return ArrayList<Double> object with xCoordinates.
     */
    public ArrayList<Double> getxCoordinate(){
        return this.xCoordinate;
    }

    /**
     * Getter for the yCoordinate list.
     * @return ArrayList<Double> object with yCoordinates.
     */
    public ArrayList<Double> getyCoordinate(){
        return this.yCoordinate;
    }
}
