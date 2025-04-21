/**
 * 4674 CHARALAMPOS THEODORIDIS
 * 4742 PANTELIS MPONITSIS
 * 4789 GEORGIOS SIDIROPOULOS
 */

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.time.Duration;
import java.time.Instant;

public class Engine {
    FileReader fileRD;
    NeuralNetwork nW;
    Double error;
    ArrayList<String>categoriesTS;
    ArrayList<Double>x1CoordinatesTS;
    ArrayList<Double>x2CoordinatesTS;
    ArrayList<String>categoriesCS;
    ArrayList<Double>x1CoordinatesCS;
    ArrayList<Double>x2CoordinatesCS;

    public Engine(int H1,int H2,int H3, int aF1,int aF2,int aF3,int aFO){
        this.fileRD=new FileReader();
        this.fileRD.readFile();
        this.nW=new NeuralNetwork(H1, H2, H3, aF1, aF2, aF3, aFO);
        this.categoriesTS=fileRD.getcategoryTS();
        this.x1CoordinatesTS=fileRD.getx1CoordinateTS();
        this.x2CoordinatesTS=fileRD.getx2CoordinateTS();
        this.categoriesCS=fileRD.getcategoryCS();
        this.x1CoordinatesCS=fileRD.getx1CoordinateCS();
        this.x2CoordinatesCS=fileRD.getx2CoordinateCS();
        this.error=0.0;
    }

    public ArrayList<Double>getOutPut(String category){
        ArrayList<Double> temp =new ArrayList<>();

        if(category.equals("C1")){
            temp.add(1.0);
            temp.add(0.0);
            temp.add(0.0);
            return temp;
        }

        if(category.equals("C2")){
            temp.add(0.0);
            temp.add(1.0);
            temp.add(0.0);
            return temp;
        }
        temp.add(0.0);
        temp.add(0.0);
        temp.add(1.0);
        return temp;
    }

    public ArrayList<Double>getInput(int index,int dataSet){
        ArrayList<Double> coordinates=new ArrayList<>();
        if(dataSet==0){
            coordinates.add(x1CoordinatesTS.get(index));
            coordinates.add(x2CoordinatesTS.get(index));
            return coordinates;
        }

        coordinates.add(x1CoordinatesCS.get(index));
        coordinates.add(x2CoordinatesCS.get(index));
        return coordinates;
    }

    public void NetworkTrain(int batches,double threshold) throws IOException{
        FileWriter errorWriter=new FileWriter("error.txt");
        int age=0;
        while(age<700||this.error>threshold){
            if(age == 10001)break;
            this.error=0.0;
            for(int index1=0;index1<this.categoriesTS.size();index1++){
                this.error+=nW.training(getInput(index1,0),getOutPut(this.categoriesTS.get(index1)));
                if(index1%batches==0 && index1!=0){
                    nW.updateWeights(0.01);
                    nW.restartPartialDerivatives();
                }
            }
            System.out.println("In the age : "+"\u001B[31m"+age+"\u001B[0m"+" the Error  is : "+"\u001B[31m"+error+"\u001B[0m");
            errorWriter.write(age +" "+this.error+" ");
            errorWriter.write("\n");
            age++;
        }
        this.error=0.0;
        errorWriter.close();

        FileWriter outputWriter=new FileWriter("output.txt");
        FileWriter outputWriterWrong=new FileWriter("wrong.txt");
        //System.out.println("Error");
        Double wrongAnswers = 0.0;

        for(int index1=0;index1<this.categoriesCS.size();index1++){
            ArrayList<Double> tempVector=new ArrayList<>();
            tempVector=getInput(index1,1);
            double singleExampleError=nW.generalizationError(tempVector,getOutPut(this.categoriesCS.get(index1)));
            this.error+=singleExampleError;
            if(singleExampleError<0.05){
                outputWriter.write(tempVector.get(0)+"  "+tempVector.get(1)+"  "+this.categoriesCS.get(index1));
                outputWriter.write("\n");
            }
            else{
                //System.out.println(singleExampleError);
                outputWriterWrong.write(tempVector.get(0)+"  "+tempVector.get(1)+"  "+this.categoriesCS.get(index1));
                outputWriterWrong.write("\n");
                wrongAnswers++;
            }
        }
        outputWriter.close();
        outputWriterWrong.close();
        System.out.printf("The Generalization Error is :  %.3f%%%n", 100 * this.error/this.categoriesCS.size());
        System.out.printf("The percentage of correct decisions :  %.3f%%%n", 100 * (1 - (wrongAnswers/this.categoriesCS.size())));
    }
    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner input=new Scanner(System.in);
        int counter=0;
        FileCreator nCreator=new FileCreator();
        nCreator.FileCreatorManager();

        while(true){
            System.out.println("\033[1;32m"+"Do you want to train the network with default parameters(1) or with your choices(2)?[1/2] :"+"\033[0m");
            if(input.nextInt()==2){
                if(counter!=0){
                    System.out.println("\033[1;32m" +"Do you want to create other dataset?[yes/no]" + "\033[0m");
                    if(input.next().equals("yes"))nCreator.FileCreatorManager();
                }

                System.out.println("\033[1;32m"+"Give me the number of neurons of the first hidden layer: "+"\033[0m");
                int H1=input.nextInt();
                System.out.println("\033[1;32m"+"Give me the activation function of the first hidden layer: "+"\033[0m");
                nCreator.printChoices();
                int aF1=input.nextInt();

                System.out.println("\033[1;32m"+"Give me the number of neurons in the second hidden layer: "+"\033[0m");
                int H2=input.nextInt();
                System.out.println("\033[1;32m"+"Give me the activation function of the second hidden layer : "+"\033[0m");
                nCreator.printChoices();
                int aF2=input.nextInt();

                System.out.println("\033[1;32m"+"Give me the number of neurons in the third hidden layer: " + "\033[0m");
                int H3=input.nextInt();
                System.out.println("\033[1;32m"+"Give me the activation function of the third hidden layer: " + "\033[0m");
                nCreator.printChoices();
                int aF3=input.nextInt();

                System.out.println("\u001B[31m"+"The ouput layer implements logistic activation function"+"\u001B[0m");
                Engine engine=new Engine(H1,H2,H3,aF1,aF2,aF3,1);

                System.out.println("\033[1;32m" +"Give me the number of batches: " +"\u001B[0m");
                int numberOfBatches=input.nextInt();
                System.out.println("\033[1;32m" +"Give me the threshold: " +"\u001B[0m");
                long start= System.currentTimeMillis();
                engine.NetworkTrain(numberOfBatches,input.nextDouble());
                System.out.println("The programm was running for : " + "\u001B[31m" + (System.currentTimeMillis()-start)+" milli seconds." +"\033[0m");
                System.out.println("\u001B[31m"+"Creating the plots." + "\u001B[0m");
                Runtime.getRuntime().exec("python3 PlotConstractor.py");
            }
            else{
                if(counter!=0){
                    System.out.println("\033[1;32m" +"Do you want to create other dataset?[yes/no]" + "\033[0m");
                    if(input.next().equals("yes"))nCreator.FileCreatorManager();
                }
                System.out.println("Default parameteres is "+"\u001B[31m"+"H1=H2=H3=20"+"\u001B[0m"+" neurons and in all layers we use "+"\u001B[31m"+"logistic function"+"\u001B[0m");
                System.out.println("Threshold is "+"\u001B[31m"+"50"+"\u001B[0m"+" and number of batches is "+"\u001B[31m"+"40"+"\u001B[0m");
                System.out.println();
                Thread.sleep(2000);
                Engine engine=new Engine(20,20,20,1,1,1,1);
                long start= System.currentTimeMillis();
                engine.NetworkTrain(40,50);
                System.out.println("The programm was running for : " + "\u001B[31m" + (System.currentTimeMillis()-start)+" milli seconds." +"\033[0m");
                System.out.println("\u001B[31m"+"Creating the plots." + "\u001B[0m");
                Runtime.getRuntime().exec("python3 PlotConstractor.py");
            }
            System.out.println("Do you want to test another neural Network ?[yes/no] : ");
            if(!input.next().equals("yes"))break;
            counter++;
        }

    }
}
