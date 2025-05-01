// Modiotis Athanasios    AM: 4736
// Bonitsis Pantelis      AM: 4742
// Sidiropoulos Georgios  AM: 4789

import java.util.ArrayList;
import java.lang.Math;

class Cell{

  private ArrayList<Cell> neighboursHorVer = new ArrayList<Cell>();   // ArrayList which contains the horizontal and ertical neighboring cells.
  private ArrayList<Cell> neighboursDiag = new ArrayList<Cell>();     // ArrayList which contains the diagonal neighboring cells.
  private int row;
  private int column;
  private int rowParent;
  private int columnParent;
  private double sumDelta = 0.0;
  private int val;
  private boolean blocked = false;    // This variable takes value "true" if this cell contains an obstacle.
  private double h = 0.0;

  public Cell(){}

  public Cell(int row, int column){
    this.row = row;
    this. column = column;
  }

  public Cell(int row, int column, int val, double sumDelta, boolean blocked, ArrayList<Cell> neighboursHorVer, ArrayList<Cell> neighboursDiag){
    this.row = row;
    this. column = column;
    this.val = val;
    this.sumDelta = sumDelta;
    this.blocked = blocked;
    this.neighboursHorVer = neighboursHorVer;
    this.neighboursDiag = neighboursDiag;
  }

  public Cell(int row, int column, int val, double sumDelta, double h, boolean blocked, ArrayList<Cell> neighboursHorVer, ArrayList<Cell> neighboursDiag){
    this.row = row;
    this. column = column;
    this.val = val;
    this.sumDelta = sumDelta;
    this.h = h;
    this.blocked = blocked;
    this.neighboursHorVer = neighboursHorVer;
    this.neighboursDiag = neighboursDiag;
  }

  public int getRow(){
    return row;
  }

  public int getColumn(){
    return column;
  }

  /*public int getRowParent(){        //debugging
    return rowParent;
  }*/

  /*public int getColumnParent(){    //debugging
    return columnParent;
  }*/

  public double getH(){
    return h;
  }

  public boolean getBlocked(){
    return blocked;
  }

  public int getVal(){
    return val;
  }

  public double getSumDelta(){
    return sumDelta;
  }

  public ArrayList<Cell> getNeighboursHorVer(){
    return neighboursHorVer;
  }

  public ArrayList<Cell> getNeighboursDiag(){
    return neighboursDiag;
  }

  public void setBlocked(boolean b){
    blocked = b;
  }

  public void setVal(int v){
    val = v;
  }

  public void setH(double h){
    this.h = h;
  }

  public void setRow(int r){
    row = r;
  }

  public void setColumn(int c){
    column = c;
  }

  public void setRowParent(int r){
    rowParent = r;
  }

  public void setColumnParent(int c){
    columnParent = c;
  }

  public void setSumDelta(double sd){
    sumDelta = sd;
  }

  public void addNeighbourHorVer(Cell newCell){
    neighboursHorVer.add(newCell);
  }

  public void addNeighbourDiag(Cell newCell){
    neighboursDiag.add(newCell);
  }

  public String toString(){
    String free = "["+row+"]"+"["+column+"] "+"Val:"+val+"        ";
    String block = "   (Blocked)       ";
    if(blocked){
      return block;
    }
    return free;
  }

  /*public void printNeighbours(){                                 //debugging
    System.out.println("Horizontal and Vertical neighbours:");
    for (Cell c: neighboursHorVer){
      System.out.println(c);
    }
    System.out.println("Diagonal neighbours:");
    for (Cell c: neighboursDiag){
      System.out.println(c);
    }
  }*/

  public boolean equals(Cell other){
    if((this.row == other.row) && (this.column == other.column)){
      return true;
    }
    return false;
  }

// Method which confirms if this Cell has put the "other" cell into the search frontier (minimum priority queue).
  public boolean isParent(Cell other){
    if((this.row == other.rowParent) && (this.column == other.columnParent)){
      return true;
    }
    return false;
  }

// Method which returns the value of the shorter distance ( h(n) ) from a final state.
  public double chooseH(Cell G1, Cell G2){
    int rowG1 = G1.getRow();
    int columnG1 = G1.getColumn();
    int rowG2 = G2.getRow();
    int columnG2 = G2.getColumn();
    double h1;
    double h2;

    h1 = calculateH(rowG1, columnG1);
    h2 = calculateH(rowG2, columnG2);

    if(h1 <= h2){
      return h1;
    }else{
      return h2;
    }
  }

// Method which calculates the value of the heuristic function.
  public double calculateH(int rowG, int columnG){
    double h = 0.0;

    if(row == rowG){
      int rowSteps = Math.abs(column - columnG);
      int divTwo = rowSteps / 2;
      int remDiv = rowSteps % 2;
      return (divTwo + remDiv);
    }else if(column == columnG){
      int columnSteps = Math.abs(row - rowG);
      int divTwo = columnSteps / 2;
      int remDiv = columnSteps % 2;
      return (divTwo + remDiv);
    }else{
      if((row < rowG) && (column < columnG)){
        int tempColumn;
        int tempRow;
        int tempTimes;

        tempRow = rowG - row;
        tempColumn = tempRow + column;
        h = tempRow*0.5;
        if(tempColumn > columnG){
          tempTimes = Math.abs(tempColumn - columnG);
          h -= tempTimes*0.5;
          int divTwo = tempTimes / 2;
          int remDiv = tempTimes % 2;
          h += (divTwo + remDiv);
          return h;
        }else if(tempColumn < columnG){
          tempTimes = Math.abs(columnG - tempColumn);
          int divTwo = tempTimes / 2;
          int remDiv = tempTimes % 2;
          h += (divTwo + remDiv);
          return h;
        }else{
          return h;
        }
      }
      if((row < rowG) && (column > columnG)){
        int tempColumn;
        int tempRow;
        int tempTimes;

        tempRow = rowG - row;
        tempColumn = column - tempRow;
        h = tempRow*0.5;
        if(tempColumn > columnG){
          tempTimes = Math.abs(tempColumn - columnG);
          int divTwo = tempTimes / 2;
          int remDiv = tempTimes % 2;
          h += (divTwo + remDiv);
          return h;
        }else if(tempColumn < columnG){
          tempTimes = Math.abs(columnG - tempColumn);
          h -= tempTimes*0.5;
          int divTwo = tempTimes / 2;
          int remDiv = tempTimes % 2;
          h += (divTwo + remDiv);
          return h;
        }else{
          return h;
        }
      }
      if((row > rowG) && (column < columnG)){
        int tempColumn;
        int tempRow;
        int tempTimes;

        tempRow = row - rowG;
        tempColumn = column + tempRow;
        h = tempRow*0.5;
        if(tempColumn > columnG){
          tempTimes = Math.abs(tempColumn - columnG);
          h -= tempTimes*0.5;
          int divTwo = tempTimes / 2;
          int remDiv = tempTimes % 2;
          h += (divTwo + remDiv);
          return h;
        }else if(tempColumn < columnG){
          tempTimes = Math.abs(columnG - tempColumn);
          int divTwo = tempTimes / 2;
          int remDiv = tempTimes % 2;
          h += (divTwo + remDiv);
          return h;
        }else{
          return h;
        }
      }
      if((row > rowG) && (column > columnG)){
        int tempColumn;
        int tempRow;
        int tempTimes;

        tempRow = row - rowG;
        tempColumn = column - tempRow;
        h = tempRow*0.5;
        if(tempColumn > columnG){
          tempTimes = Math.abs(tempColumn - columnG);
          int divTwo = tempTimes / 2;
          int remDiv = tempTimes % 2;
          h += (divTwo + remDiv);
          return h;
        }else if(tempColumn < columnG){
          tempTimes = Math.abs(columnG - tempColumn);
          h -= tempTimes*0.5;
          int divTwo = tempTimes / 2;
          int remDiv = tempTimes % 2;
          h += (divTwo + remDiv);
          return h;
        }else{
          return h;
        }
      }
      return h;
    }
  }

// Method which puts the neighboring cells into the search frontier (minimum priority queue) for the Ucs algorithm.
  public void insertNeighboursUCS(MinPQ<Cell,Double> pq, int N){
    for (Cell c1: neighboursHorVer){
      Cell newC1 = new Cell(c1.getRow(), c1.getColumn(), c1.getVal(), c1.getSumDelta(), c1.getBlocked(), c1.getNeighboursHorVer(), c1.getNeighboursDiag());
      double delta1;
      delta1 = Math.abs(this.getVal() - newC1.getVal());
      delta1 += 1;
      newC1.setSumDelta(delta1 + sumDelta);
      if(!newC1.getBlocked()){
        newC1.setRowParent(row);
        newC1.setColumnParent(column);
        if(pq.size() == 0){
          pq.insert(newC1,newC1.getSumDelta());
        }else{
          pq = fixPQUcs(pq, newC1, N);
        }
      }
    }
    for (Cell c2: neighboursDiag){
      Cell newC2 = new Cell(c2.getRow(), c2.getColumn(), c2.getVal(), c2.getSumDelta(), c2.getBlocked(), c2.getNeighboursHorVer(), c2.getNeighboursDiag());
      double delta2;
      delta2 = Math.abs(this.getVal() - newC2.getVal());
      delta2 += 0.5;
      newC2.setSumDelta(delta2 + sumDelta);
      if(!newC2.getBlocked()){
        newC2.setRowParent(row);
        newC2.setColumnParent(column);
        if(pq.size() == 0){
          pq.insert(newC2,newC2.getSumDelta());
        }else{
          pq = fixPQUcs(pq, newC2, N);
        }
      }
    }
  }

// Method which puts the neighboring cells into the search frontier (minimum priority queue) for the A* algorithm.
  public void insertNeighboursAstar(MinPQ<Cell,Double> pq, Cell G1, Cell G2, int N){
    for (Cell c1: neighboursHorVer){
      Cell newC1 = new Cell(c1.getRow(), c1.getColumn(), c1.getVal(), c1.getSumDelta(), c1.getH(), c1.getBlocked(), c1.getNeighboursHorVer(), c1.getNeighboursDiag());
      double delta1;
      delta1 = Math.abs(this.getVal() - newC1.getVal());
      delta1 += 1;
      newC1.setH(newC1.chooseH(G1, G2));
      newC1.setSumDelta(delta1 + sumDelta);
      if(!newC1.getBlocked()){
        newC1.setRowParent(row);
        newC1.setColumnParent(column);
        if(pq.size() == 0){
          pq.insert(newC1,(newC1.getSumDelta()) + newC1.getH());
        }else{
          pq = fixPQAstar(pq, newC1, N);
        }
      }
    }
    for (Cell c2: neighboursDiag){
      Cell newC2 = new Cell(c2.getRow(), c2.getColumn(), c2.getVal(), c2.getSumDelta(), c2.getH(), c2.getBlocked(), c2.getNeighboursHorVer(), c2.getNeighboursDiag());
      double delta2;
      delta2 = Math.abs(this.getVal() - newC2.getVal());
      delta2 += 0.5;
      newC2.setH(newC2.chooseH(G1, G2));
      newC2.setSumDelta(delta2 + sumDelta);
      if(!newC2.getBlocked()){
        newC2.setRowParent(row);
        newC2.setColumnParent(column);
        if(pq.size() == 0){
          pq.insert(newC2,(newC2.getSumDelta()) + newC2.getH());
        }else{
          pq = fixPQAstar(pq, newC2, N);
        }
      }
    }
  }

// Method which between same states, puts into the search frontier this with the minimum cost ( g(n) ).
  public MinPQ<Cell,Double> fixPQUcs(MinPQ<Cell,Double> pq, Cell cellToInsert, int N){
    MinPQ<Cell,Double> tempPQ = new MinPQ<Cell,Double>(N*N*N*N);
    Cell cellPQ = new Cell();
    Cell restoreCellPQ = new Cell();
    boolean done = false;
    double minK;

    while(pq.size() != 0){
      cellPQ = pq.minItem();
      pq.delMin();
      if(cellToInsert.equals(cellPQ)){
        if(cellToInsert.getSumDelta() < cellPQ.getSumDelta()){
          tempPQ.insert(cellToInsert, cellToInsert.getSumDelta());
          done = true;
        }else{
          tempPQ.insert(cellPQ, cellPQ.getSumDelta());
          done = true;
        }
      }else{
        tempPQ.insert(cellPQ, cellPQ.getSumDelta());
      }
    }

    if(!done){
      tempPQ.insert(cellToInsert, cellToInsert.getSumDelta());
    }

    while(tempPQ.size() != 0){
      restoreCellPQ = tempPQ.minItem();
      restoreCellPQ.setSumDelta(tempPQ.minKey());
      tempPQ.delMin();
      pq.insert(restoreCellPQ, restoreCellPQ.getSumDelta());
    }
    return pq;
  }

// Method which between same states, puts into the search frontier this with the minimum cost ( e(n) = h(n) + g(n) ).
  public MinPQ<Cell,Double> fixPQAstar(MinPQ<Cell,Double> pq, Cell cellToInsert, int N){
    MinPQ<Cell,Double> tempPQ = new MinPQ<Cell,Double>(N*N*N*N);
    Cell cellPQ = new Cell();
    Cell restoreCellPQ = new Cell();
    boolean done = false;
    double minK;

    while(pq.size() != 0){
      cellPQ = pq.minItem();
      pq.delMin();
      if(cellToInsert.equals(cellPQ)){
        if(cellToInsert.getSumDelta() < cellPQ.getSumDelta()){
          tempPQ.insert(cellToInsert, (cellToInsert.getSumDelta()) + cellToInsert.getH());
          done = true;
        }else{
          tempPQ.insert(cellPQ, (cellPQ.getSumDelta()) + cellPQ.getH());
          done = true;
        }
      }else{
        tempPQ.insert(cellPQ, (cellPQ.getSumDelta()) + cellPQ.getH());
      }
    }

    if(!done){
      tempPQ.insert(cellToInsert, (cellToInsert.getSumDelta()) + cellToInsert.getH());
    }

    while(tempPQ.size() != 0){
      restoreCellPQ = tempPQ.minItem();
      restoreCellPQ.setSumDelta(tempPQ.minKey() - restoreCellPQ.getH());
      minK = tempPQ.minKey();
      tempPQ.delMin();
      pq.insert(restoreCellPQ, minK);
    }
    return pq;
  }

}
