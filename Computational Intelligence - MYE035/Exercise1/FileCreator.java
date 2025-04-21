/**
 * 4674 CHARALAMPOS THEODORIDIS
 * 4742 PANTELIS MPONITSIS
 * 4789 GEORGIOS SIDIROPOULOS
 */

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * File Creator class . Responsible for creating the two datasets.
 */
public class FileCreator{
    Random rand;
    ArrayList<Double>visited;

    /**
     * Constractor for File Creator Class.
     */
    public FileCreator(){
        this.rand=new Random();
        this.visited=new ArrayList<>();
    }

    /**
     * This method checks the category that the vector belongs to .
     * @param x1 Double x1 .The x1 coordinate of the point x.
     * @param x2 Double x2 .The x2 coordinate of the point x.
     * @return String.The category that the point belongs to .
     */
    private String checkForCategor(Double x1,Double x2){

        if(((x1-0.5)*(x1-0.5)+(x2-0.5)*(x2-0.5))<0.2 && x2>0.5)return "C1";

        if(((x1-0.5)*(x1-0.5)+(x2-0.5)*(x2-0.5))<0.2 && x2<0.5)return "C2";

        if(((x1+0.5)*(x1+0.5)+(x2+0.5)*(x2+0.5))<0.2 && x2>-0.5)return "C1";

        if(((x1+0.5)*(x1+0.5)+(x2+0.5)*(x2+0.5))<0.2 && x2<-0.5)return "C2";

        if(((x1-0.5)*(x1-0.5)+(x2+0.5)*(x2+0.5))<0.2 && x2>-0.5)return "C1";

        if(((x1-0.5)*(x1-0.5)+(x2+0.5)*(x2+0.5))<0.2 && x2<-0.5)return "C2";

        if(((x1+0.5)*(x1+0.5)+(x2-0.5)*(x2-0.5))<0.2 && x2>0.5)return "C1";

        if(((x1+0.5)*(x1+0.5)+(x2-0.5)*(x2-0.5))<0.2 && x2<0.5)return "C2";

        return "C3";
    }

    /**
     * This method is used to generate a random number between [-1,1].
     * @return The random double number that was generated.
     */
    private double randomNumberX(){
        return 2*rand.nextDouble()-1;
    }

    /**
     * This method is used to constract the training set.
     * @throws IOException In case of a problem creating the file.
     */
    private void writerTrainingSet() throws IOException{
        FileWriter myWriter=new FileWriter("trainingSet.txt");

        for(int index=0;index<4000;index++){
            double tempX1=randomNumberX();
            double tempX2=randomNumberX();
            this.visited.add(tempX1);
            this.visited.add(tempX2);
            myWriter.write(tempX1+"  "+tempX2+" "+ checkForCategor(tempX1, tempX2));
            myWriter.write("\n");
        }
        myWriter.close();
    }

    /**
     * This method is used to constract the check set.
     * @throws IOException In case of a problem creating the file.
     */
    private void writerCheckSet() throws IOException{
        FileWriter myWriter=new FileWriter("checkSet.txt");

        for(int index=0;index<4000;index++){
            double tempX1=randomNumberX();
            double tempX2=randomNumberX();

            //The two randomNumbers must be different from any number in the training set.
            while(this.visited.contains(tempX1))tempX1=randomNumberX();

            while(this.visited.contains(tempX2))tempX2=randomNumberX();

            myWriter.write(tempX1+"  "+tempX2+"  "+ checkForCategor(tempX1, tempX2));
            myWriter.write("\n");
        }
        myWriter.close();  //Close the file.
    }

    /**
     * This method is used for creating the two dataSets.
     * @throws IOException In case that we can not create a file.
     */
    public void FileCreatorManager() throws IOException{
        writerTrainingSet();
        writerCheckSet();
    }

    /**
     * This method is used to print choices of the user. Is used in main.
     */
    public void printChoices(){
        System.out.println("Your choiches is : ");
        for(int index=0;index<4;index++){
         if(index==0)System.out.println("\u001B[31m"+index+")"+"\033[0m"+"Linear");
         if(index==1)System.out.println("\u001B[31m"+index+")"+"\033[0m"+"Logistic");
         if(index==2)System.out.println("\u001B[31m"+index+")"+"\033[0m"+"Hyperbolic Tangen");
         if(index==3)System.out.println("\u001B[31m"+index+")"+"\033[0m"+"Rectified Linear");
        }
    }
}
