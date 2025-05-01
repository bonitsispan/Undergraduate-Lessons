// Modiotis Athanasios    AM: 4736
// Bonitsis Pantelis      AM: 4742
// Sidiropoulos Georgios  AM: 4789

import java.util.Random;
import java.util.ArrayList;
import java.util.Scanner;


class Labyrinth{

  private int N;
  private double p;
  private Cell cells[][];
  private ArrayList<Cell> pathUcs = new ArrayList<Cell>();
  private ArrayList<Cell> pathAstar = new ArrayList<Cell>();
  private ArrayList<Cell> expandedCellsUcs = new ArrayList<Cell>();       // ArrayList which contains the states of the closed set for the Ucs algorithm
  private ArrayList<Cell> expandedCellsAstar = new ArrayList<Cell>();    // ArrayList which contains the states of the closed set for the A* algorithm
  private Random rd = new Random();
  private double random_blocked;
  private int random_val;
  private double finalCostUcs;
  private double finalCostAstar;
  private int ucsExpansions = 0;
  private int astarExpansions = 0;

  public Labyrinth(int N, double p){
    this.N = N;
    this.p = p;
    cells = new Cell[N][N];
  }

  /*public Cell[][] getCells(){                   //debugging
    return cells;
  }*/

  /*public ArrayList<Cell> getPathUcs(){         //debugging
    return pathUcs;
  }*/

  /*public ArrayList<Cell> getPathAstar(){       //debugging
    return pathAstar;
  }*/

  public double getfinalCostUcs(){
    return finalCostUcs;
  }

  public double getfinalCostAstar(){
    return finalCostAstar;
  }

  public double getUcsExpansions(){
    return ucsExpansions;
  }

  public double getAstarExpansions(){
    return astarExpansions;
  }

  public Cell getCell(int row, int column){
    for(int i = 0; i < N; i++){
      for(int j = 0; j < N; j++){
        if((cells[i][j].getRow() == row) && (cells[i][j].getColumn() == column)){
          return cells[i][j];
        }
      }
    }
    return null;
  }

  public String toString(){
    String labyrinth = "";
    for(int i = 0; i < N; i++){
      for(int j = 0; j < N; j++){
        labyrinth += cells[i][j].toString();
      }
      labyrinth += "\n\n\n";
    }
    return labyrinth;
  }

// Method which confirms if a cell belongs to the closed set for the Ucs algorithm.
  private boolean isExpandedUcs(Cell newCell){
    for(Cell c: expandedCellsUcs){
      if(c.equals(newCell)){
        return true;
      }
    }
    return false;
  }

// Method which confirms if a cell belongs to the closed set for the A* algorithm.
  private boolean isExpandedAstar(Cell newCell){
    for(Cell c: expandedCellsAstar){
      if(c.equals(newCell)){
        return true;
      }
    }
    return false;
  }

// Method which creates the labyrinth, decides randomnly which cells contain obstacles and gives a random value (val 1-4) to every cell.
  public void createLabyrinth(){
    for(int row = 0; row < N; row++){
      for(int column = 0; column < N; column++){
        cells[row][column] = new Cell();

        random_val = rd.nextInt(4) + 1;

        cells[row][column].setVal(random_val);
        random_blocked = rd.nextDouble();

        if(random_blocked < p){
          cells[row][column].setBlocked(true);
        }

        cells[row][column].setRow(row);
        cells[row][column].setColumn(column);

        if(row > 0){
          cells[row-1][column].setRow(row-1);
          cells[row-1][column].setColumn(column);
          cells[row-1][column].addNeighbourHorVer(cells[row][column]);
          cells[row][column].addNeighbourHorVer(cells[row-1][column]);

          if(column < N-1){
            cells[row-1][column+1].addNeighbourDiag(cells[row][column]);
            cells[row][column].addNeighbourDiag(cells[row-1][column+1]);
          }
        }
        if(column > 0){
          cells[row][column-1].setRow(row);
          cells[row][column-1].setColumn(column-1);
          cells[row][column-1].addNeighbourHorVer(cells[row][column]);
          cells[row][column].addNeighbourHorVer(cells[row][column-1]);

          if(row > 0){
            cells[row-1][column-1].addNeighbourDiag(cells[row][column]);
            cells[row][column].addNeighbourDiag(cells[row-1][column-1]);
          }
        }
      }
    }
  }

// Method which performs the steps of Ucs algorithm and A* algorithm.
  public ArrayList<Cell> algorithm(String nameAlgorithm, Cell S, Cell G1, Cell G2){

    MinPQ<Cell,Double> pq = new MinPQ<Cell,Double>(N*N*N*N);    // Search frontier.
    ArrayList<Cell> finalPath = new ArrayList<Cell>();
    Cell startCell = new Cell();

    if(nameAlgorithm.equals("Ucs")){
      pq.insert(S, 0.0);
    }else if(nameAlgorithm.equals("Astar")){
      S.setH(S.chooseH(G1, G2));
      pq.insert(S, S.getH());
    }

    while(pq.size() != 0){

      startCell = pq.minItem();

      if(nameAlgorithm.equals("Ucs")){
        startCell.setSumDelta(pq.minKey());
      }else if(nameAlgorithm.equals("Astar")){
        startCell.setSumDelta(pq.minKey() - startCell.getH());
      }

      pq.delMin();

      if( (nameAlgorithm.equals("Ucs")) && (isExpandedUcs(startCell)) ){
        continue;
      }else if( (nameAlgorithm.equals("Astar")) && (isExpandedAstar(startCell)) ){
        continue;
      }

      if(nameAlgorithm.equals("Ucs")){
        pathUcs.add(startCell);
        ucsExpansions++;
      }else if(nameAlgorithm.equals("Astar")){
        pathAstar.add(startCell);
        astarExpansions++;
      }

      if(startCell.equals(G1) || startCell.equals(G2)){
        /*for(Cell c: pathAstar){
          System.out.println(c+"  Parent:"+"["+c.getRowParent()+c.getColumnParent()+"]"+" sumDelta: "+c.getSumDelta());    // debugging
        }
        System.out.println("----------------------------");*/
        if(nameAlgorithm.equals("Ucs")){
          finalCostUcs = startCell.getSumDelta();
          finalPath = createPath(S ,pathUcs);
        }else if(nameAlgorithm.equals("Astar")){
          finalCostAstar = startCell.getSumDelta();
          finalPath = createPath(S ,pathAstar);
        }
        return finalPath;

      }
      if(nameAlgorithm.equals("Ucs")){
        startCell.insertNeighboursUCS(pq, N);
        expandedCellsUcs.add(startCell);
      }else if(nameAlgorithm.equals("Astar")){
        startCell.insertNeighboursAstar(pq, G1, G2, N);
        expandedCellsAstar.add(startCell);
      }
    }
    return null;
  }

// Method which creates the final path of closest distance to a final state.
  public ArrayList<Cell> createPath(Cell S, ArrayList<Cell> wrongPath){

    ArrayList<Cell> newPath = new ArrayList<Cell>();
    ArrayList<Cell> newReversePath = new ArrayList<Cell>();
    ArrayList<Cell> reversePath = new ArrayList<Cell>();

    Cell reverseC = new Cell();

    for(int i = wrongPath.size()-1; i >= 0; i--){
      reverseC = wrongPath.get(i);
      reversePath.add(reverseC);
    }

    Cell G = new Cell();
    G = reversePath.get(0);
    Cell tempCell = new Cell();

    newReversePath.add(G);
    for(int i = 1; i < reversePath.size(); i++){
      tempCell = reversePath.get(i);
      if(tempCell.isParent(G)){
        newReversePath.add(tempCell);
        G = tempCell;
        }
      }

      for(int i = newReversePath.size()-1; i >= 0; i--){
        reverseC = newReversePath.get(i);
        newPath.add(reverseC);
      }

      return newPath;
    }

  public static void main(String args[]){
    System.out.println("\n\n8x8 Labyrinth (p = 0.25):\n\n");
    Labyrinth myLabyrinth = new Labyrinth(8, 0.25);
    myLabyrinth.createLabyrinth();

    ArrayList<Cell> finalPathUcs;
    ArrayList<Cell> finalPathAstar;

    System.out.println(myLabyrinth);

    Scanner inputs = new Scanner(System.in);
    int row;
    int column;
    Cell S = new Cell();
    while (true){
      System.out.print("Please insert the number of row of S cell (one which is not blocked) : ");
      row = inputs.nextInt();
      System.out.print("Please insert he number of column of S cell (one which is not blocked) : ");
      column = inputs.nextInt();
      S = myLabyrinth.getCell(row,column);
      if(!S.getBlocked()){
        break;
      }
      System.out.println("");
    }
    System.out.println("\n");
    System.out.print("Please insert the number of row of G1 cell : ");
    row = inputs.nextInt();
    System.out.print("Please insert the number of column of G1 cell : ");
    column = inputs.nextInt();
    Cell G1 = myLabyrinth.getCell(row,column);
    System.out.println("\n");
    System.out.print("Please insert the number of row of G2 cell : ");
    row = inputs.nextInt();
    System.out.print("Please insert the number of column of G2 cell : ");
    column = inputs.nextInt();
    Cell G2 = myLabyrinth.getCell(row,column);

    System.out.println("\n\n --------- UCS algorithm --------- \n");
    System.out.println(" S cell: "+S);
    System.out.println("G1 cell: "+G1);
    System.out.println("G2 cell: "+G2);

    long startTimeUcs = System.currentTimeMillis();

    finalPathUcs = myLabyrinth.algorithm("Ucs", S, G1, G2);

    long endTimeUcs = System.currentTimeMillis();
    long totalTimeUcs = endTimeUcs - startTimeUcs;

    if(finalPathUcs == null){
      System.out.println("\nThere is no path");
    }else{
      System.out.println("\n----Path----");
      for (Cell c: finalPathUcs){
        //System.out.println(c+"  Parent:"+"["+c.getRowParent()+c.getColumnParent()+"]"+" sumDelta: "+c.getSumDelta());    // debugging
        System.out.println(c+"Cost: "+c.getSumDelta());
      }
      System.out.println("\nTotal cost of path: "+myLabyrinth.getfinalCostUcs());
      System.out.println("Number of expansions: "+myLabyrinth.getUcsExpansions());
      System.out.println("\nRunning time: " +totalTimeUcs+" ms");
    }

    System.out.println("\n\n -------- Astar algorithm -------- \n");
    System.out.println(" S cell: "+S);
    System.out.println("G1 cell: "+G1);
    System.out.println("G2 cell: "+G2);

    long startTimeAstar = System.currentTimeMillis();

    finalPathAstar = myLabyrinth.algorithm("Astar", S, G1, G2);

    long endTimeAstar = System.currentTimeMillis();
    long totalTimeAstar = endTimeAstar - startTimeAstar;

    if(finalPathAstar == null){
      System.out.println("\nThere is no path");
    }else{
      System.out.println("\n----Path----");
      for (Cell c: finalPathAstar){
        //System.out.println(c+"  Parent:"+"["+c.getRowParent()+c.getColumnParent()+"]"+" sumDelta: "+c.getSumDelta());    // debugging
        System.out.println(c+"Cost: "+c.getSumDelta());
      }
      System.out.println("\nTotal cost of path: "+myLabyrinth.getfinalCostAstar());
      System.out.println("Number of expansions: "+myLabyrinth.getAstarExpansions());//+"  ("+(myLabyrinth.getUcsExpansions() - myLabyrinth.getAstarExpansions())+" less than Ucs algorithm)");    // debugging
      System.out.println("\nRunning time: " +totalTimeAstar+" ms");
    }

    /*if((myLabyrinth.getfinalCostUcs() == myLabyrinth.getfinalCostAstar()) && (myLabyrinth.getAstarExpansions() < myLabyrinth.getUcsExpansions()) ){       // debugging
      System.out.println("\n---- Success ----");
    }else{
      System.out.println("\n---- Failure ----");
    }*/

  }
}
