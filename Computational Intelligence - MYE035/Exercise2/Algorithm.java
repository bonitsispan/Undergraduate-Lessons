/**
 * 4674 CHARALAMPOS THEODORIDIS
 * 4742 PANTELIS MPONITSIS
 * 4789 GEORGIOS SIDIROPOULOS
 */
 
import java.util.ArrayList;
import java.util.Random;

public class Algorithm {
    private int kCentroids;                        //The number of clusters.
    private double error;                          //The SSE.
    private ArrayList<Double>xCoordinate;          //List with all x Coordinates.
    private ArrayList<Double>yCoordinate;          //List with all y Coordinates.
    private ArrayList<Double>kCentroidsList;       //List with (x,y) centroids coordinates.
    private ArrayList<Double>listWithNewWeights;   //List with the new weights.
    private ArrayList<Integer>teamList;            //List with the teams.
    private ArrayList<Integer>counter;             //List with counters for each team.

    /**
     * Constractor that initializes all the variables of the class.
     * @param kCentroids INT number with k Clusters.
     */
    public Algorithm(int kCentroids){
        this.kCentroids=kCentroids;
        this.xCoordinate=new ArrayList<Double>();
        this.yCoordinate=new ArrayList<Double>();
        this.kCentroidsList=new ArrayList<Double>();
        this.listWithNewWeights=new ArrayList<Double>();
        this.teamList=new ArrayList<Integer>();
        this.counter=new ArrayList<Integer>();
        this.error=0.0;
    }

    /**
     * This method is responsible for loading the xCoordinates,yCoordinates list with points of the trainingSet.txt file.
     */
    public void loadFile(){
        FileReader fl=new FileReader();         //Creating a FileReader object.
        fl.readFile();                          //Read the trainingSet file.
        this.xCoordinate=fl.getxCoordinate();   //Get xCoordinate.
        this.yCoordinate=fl.getyCoordinate();   //Get yCoordinate.
    }

    /**
     * This method is responsible for choosing k random points of the training set.
     */
    public void randomCentroid(){
        Random rand=new Random();
        ArrayList<Integer>tempList=new ArrayList<Integer>();    //List with already choosen points.

        for(int index=0;index<this.kCentroids*2;index++){
            int tempPosition=rand.nextInt(xCoordinate.size());  //Random position

            while(tempList.contains(tempPosition))tempPosition=rand.nextInt(xCoordinate.size());    //Different points each time.

            this.kCentroidsList.set(index,xCoordinate.get(tempPosition));     //Set the x coordinate of Centroid equal to the random x coordinate.
            this.kCentroidsList.set(index+1,yCoordinate.get(tempPosition));   //Se the y coordinate of Centroid equal to the random y coordinate.
            tempList.add(tempPosition);   //Add the position to the visited list.
            index++;
        }
    }

    /**
     * This method is responsible for calculating the euclidian distance between two points.
     * @param x1 The x coordinate of the fist point.
     * @param y1 The y coordinate of the first point.
     * @param x2 The x coordinate of the second point.
     * @param y2 The y coordinate of the second point.
     * @return Double number. The euclidian distance.
     */
    private double euclideanDistance(double x1,double y1,double x2, double y2){
        return Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));
    }

    /**
     * This method is responsible for copy the listWithNewWeights to the kCentroidsList.
     * @param listWithNewWeights ArrayList<Double> object . The list with the new weights.
     */
    private void copyList(ArrayList<Double> listWithNewWeights){
       for(int index=0;index<this.kCentroidsList.size();index++)this.kCentroidsList.set(index,listWithNewWeights.get(index));
    }

    /**
     * This method checks the similarity of kCentroids and listWithNewWeights lists.
     * @return True if the two lists is identical .
     */
    private boolean checkSimilarity(){
        for(int index=0;index<this.kCentroidsList.size();index++)if(!this.kCentroidsList.get(index).equals(this.listWithNewWeights.get(index)))return false;
        return true;
    }

    /**
     * This method adds the x and y coordinates of given point with the x and y coordinates in the minCoordinate position in list .
     * @param minCoordinatePosition Int.Position of x in the listWithNewWeights list.
     * @param xCoordinate Double. The x Coordinate of the point.
     * @param yCoordinate Double. The y Coordinate of the point.
     */
    private void listAddition(int minCoordinatePosition,double xCoordinate,double yCoordinate){
        this.listWithNewWeights.set(minCoordinatePosition,(this.listWithNewWeights.get(minCoordinatePosition)+xCoordinate));
        this.listWithNewWeights.set(minCoordinatePosition+1,(this.listWithNewWeights.get(minCoordinatePosition+1)+yCoordinate));
    }

    /**
     * This method sets the cluster for each point.
     */
    private void teamMinDistance(){
        for(int index=0;index<this.xCoordinate.size();index++)
            this.teamList.set(index,checkMinDistance(this.xCoordinate.get(index),this.yCoordinate.get(index)));
    }

    /**
     * This method initializes the weightList.
     */
    public void intialWeightList(){
        for(int i=0;i<this.kCentroidsList.size();i++)this.listWithNewWeights.add(0.0);
    }

    /**
     * This method initializes the TeamList.
     */
    public void intialTeamList(){
        for(int index=0;index<this.xCoordinate.size();index++)this.teamList.add(0);
    }

    /**
     * This method initalizes the Counter.
     */
    public void intialCounter(){
        for(int index=0;index<kCentroids;index++)this.counter.add(0);
    }

    /**
     * This method initializes the CentroidList.
     */
    public void intialRandom(){
        for(int index=0;index<kCentroids*2;index++)this.kCentroidsList.add(0.0);
    }

    /**
     * This method sets all the elements of counter to zero.
     */
    public void CounterRestart(){
        for(int index=0;index<counter.size();index++)counter.set(index, 0);
    }

    /**
     * This methods set all the weights of list with weights to zero.
     */
    public void listWithNewWeightsRestart(){
        for(int index=0;index<this.listWithNewWeights.size();index++)this.listWithNewWeights.set(index,0.0);
    }

    /**
     * This method calculates the average of all the points in a cluster.
     * @param counter ArrayList<Integer> counter. List with counters.
     */
    private void setAvg(ArrayList<Integer>counter){
        for(int index=0;index<this.listWithNewWeights.size();index++){
            this.listWithNewWeights.set(index,this.listWithNewWeights.get(index)/counter.get(index/2));
            this.listWithNewWeights.set(index+1,this.listWithNewWeights.get(index+1)/counter.get(index/2));
            index++;
        }
    }

    /**
     * This method finds the min distance between a given point and centroids.
     * @param x1 Double . The xCoordinate of the given point.
     * @param y1 Double . The yCoordinate of the given point.
     * @return Int . Returns the xCoordinate of the nearest centroid.
     */
    private int checkMinDistance(double x1, double y1){
        int minxCoordinatePosition=0;
        double minDistance=Double.POSITIVE_INFINITY;

        for(int index=0;index<this.kCentroidsList.size();index++){
            double tempDistance=euclideanDistance(x1, y1,this.kCentroidsList.get(index),this.kCentroidsList.get(index+1));
            //System.out.println("The mean distance :"+tempDistance+" from the points : " + " x1: "+this.kCentroidsList.get(index)+" y1: "+this.kCentroidsList.get(index+1));

            if(tempDistance<minDistance){
                minDistance=tempDistance;
                minxCoordinatePosition=index;
            }
            index++;
        }
        return minxCoordinatePosition;
    }

    /**
     * Calculates the SSE for all the points.
     */
    private void calculateError(){
        double groupError=0.0;

        for(int index=0;index<this.xCoordinate.size();index++){
            groupError+=euclideanDistance(this.xCoordinate.get(index),this.yCoordinate.get(index),this.kCentroidsList.get(this.teamList.get(index)),this.kCentroidsList.get(this.teamList.get(index)+1));
        }

        this.error=groupError;
    }


    /**
     * This method implements the k Means algorithm.
     */
    public void kMeans(){
        while(true){
            teamMinDistance();
            for(int index=0;index<this.xCoordinate.size();index++){
                //System.out.println(xCoordinate.get(index)+", "+yCoordinate.get(index)+" team : "+kCentroidsList.get(teamList.get(index))+","+kCentroidsList.get(teamList.get(index)+1));
                listAddition(teamList.get(index),xCoordinate.get(index),yCoordinate.get(index));
                counter.set(teamList.get(index)/2,counter.get(teamList.get(index)/2)+1);
            }

            setAvg(counter);

            if(checkSimilarity()){
                calculateError();
                copyList(listWithNewWeights);
                listWithNewWeightsRestart();
                CounterRestart();
                break;
            }
            copyList(listWithNewWeights);

            listWithNewWeightsRestart();
            CounterRestart();
        }
    }

    /**
     * Getter for the xCoordinate list.
     * @return ArrayList<Double> object.This list contains the xCoordinates.
     */
    public ArrayList<Double> getxCoordinate(){
        return this.xCoordinate;
    }


    /**
     * Getter for the yCoordinate list.
     * @return ArrayList<Double> object.This list contains the yCoordinates.
     */
    public ArrayList<Double> getyCoordinate(){
        return this.yCoordinate;
    }


    /**
     * Getter for the kCentoirdsList.
     * @return ArrayList<Double> object. This list contains the x,y coordinates for centroids
     */
    public ArrayList<Double> getkCentroidsList(){
        return this.kCentroidsList;
    }


    /**
     * Getter for the TeamList.
     * @return ArrayList<Integer> object. This list contains the team for every point of the plane.
    */
    public ArrayList<Integer> getTeamList(){
        return this.teamList;
    }


    /**
     * Getter for the list with Weights.
     * @return ArrayList<Double> object. This list contains the centroids after grouping.
     */
    public ArrayList<Double> getListWithWeights(){
        return this.listWithNewWeights;
    }


    /**
     * Getter for the list with counter.
     * @return ArrayList<Integer> object. This list contains the counters for every team.
     */
    public ArrayList<Integer> getCounter(){
        return this.counter;
    }

    /**
     * Getter for the error variable.
     * @return Double object. This object is the error of the grouping.
     */
    public double getError(){
        return this.error;
    }
}
