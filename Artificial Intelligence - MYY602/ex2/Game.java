// Modiotis Athanasios    AM: 4736
// Bonitsis Pantelis      AM: 4742
// Sidiropoulos Georgios  AM: 4789

import java.util.ArrayList;
import java.util.Scanner;
import java.lang.Math;

// Class Game is the executable class.
class Game{
  private Grid gameGrid;
  private int M;
  private int N;
  private int K;

// Constructor
  public Game(int M, int N, int K){
    this.M = M;
    this.N = N;
    this.K = K;
    gameGrid = new Grid(M, N, K);
  }

// Method which is executed when it is the user's turn to play.
  public void userPlay(){
    Scanner inputs = new Scanner(System.in);
    int newRow;
    int nextColumn;
    while (true){
      System.out.print("Please insert the number of column of your next move (1-4) : ");
      nextColumn = inputs.nextInt();
      System.out.println("");
      newRow = gameGrid.findRow(nextColumn - 1);
      if(newRow != -1){
        break;
      }
      System.out.println("Invalid column.");
    }
    gameGrid.addCell(nextColumn - 1, false);
  }

// Method that executes the flow of the entire game.
  public void playGame(String answer){
    boolean first = true;
    if(answer.equals("no")){
      first = false;
    }
    while (true){

      if(first){
        System.out.println(gameGrid);
        userPlay();

        boolean isWinUser = gameGrid.win(false);
        if(isWinUser){
          System.out.println(gameGrid);
          System.out.println("Game end: User wins !!!");
          System.out.println("");
          break;
        }

        boolean isDraw1 = gameGrid.isFull();
        if(isDraw1){
          System.out.println(gameGrid);
          System.out.println("Game end: Draw !!!");
          System.out.println("");
          break;
        }
        first = false;
      }

      if(!first){
        gameGrid.setFirstChilds(0);
        gameGrid.findAvailableColumns();
        gameGrid.setValue(miniMax(gameGrid, true));
        //gameGrid.setValue(alphaBeta(gameGrid, -1, 1, true));        // alphaBeta algorithm
        //System.out.println("\nValue: "+gameGrid.getValue());        // debugging
        gameGrid = gameGrid.nextGrid();
        boolean isWinPc = gameGrid.win(true);
        if(isWinPc){
          System.out.println(gameGrid);
          System.out.println("Game end: Pc wins !!!");
          System.out.println("");
          break;
        }

        boolean isDraw2 = gameGrid.isFull();
        if(isDraw2){
          System.out.println(gameGrid);
          System.out.println("Game end: Draw !!!");
          System.out.println("");
          break;
        }
        first = true;
      }
    }
  }

// Method that executes the steps of Minimax algorithm.
  public int miniMax(Grid grid, boolean player){
    if(grid.win(player) || grid.win(!player)){
      if(player){
        grid.setValue(-1);
        return -1;
      }else{
        grid.setValue(1);
        return 1;
      }
    }
    if(grid.isFull()){
      grid.setValue(0);
      return 0;
    }
    if(player){               // Computer's turn
      grid.setValue(-1);
      for(int i: grid.getAvailableColumns()){
        Grid childGrid = new Grid(M, N, K);
        grid.copy(childGrid);
        childGrid.addCell(i, true);
        if(grid.getFirstChilds() == 0){
          System.out.println("Loading....");
          grid.addChild(childGrid);
        }
        childGrid.setFirstChilds(grid.getFirstChilds() + 1);
        grid.setValue(Math.max(grid.getValue(), miniMax(childGrid, childGrid.getGridPlayer())));        // recursion
      }
      return grid.getValue();
    }
    else{                     // User's turn
      grid.setValue(1);
      for(int i: grid.getAvailableColumns()){
        Grid childGrid = new Grid(M, N, K);
        grid.copy(childGrid);
        childGrid.addCell(i, false);
        if(grid.getFirstChilds() == 0){
          System.out.println("Loading....");
          grid.addChild(childGrid);
        }
        childGrid.setFirstChilds(grid.getFirstChilds() + 1);
        grid.setValue(Math.min(grid.getValue(), miniMax(childGrid, childGrid.getGridPlayer())));        // recursion
      }
      return grid.getValue();
    }
  }

// Method that executes the steps of AlphaBeta algorithm.
  /*public int alphaBeta(Grid grid, int a, int b, boolean player){
    if(grid.win(player) || grid.win(!player)){
      if(player){
        grid.setValue(-1);
        return -1;
      }else{
        grid.setValue(1);
        return 1;
      }
    }
    if(grid.isFull()){
      grid.setValue(0);
      return 0;
    }
    if(player){               // Computer's turn
      grid.setValue(-1);
      for(int i: grid.getAvailableColumns()){
        Grid childGrid = new Grid(M, N, K);
        grid.copy(childGrid);
        childGrid.addCell(i, true);
        if(grid.getFirstChilds() == 0){
          System.out.println("Loading....");
          grid.addChild(childGrid);
        }
        childGrid.setFirstChilds(grid.getFirstChilds() + 1);
        grid.setValue(Math.max(grid.getValue(), alphaBeta(childGrid, a, b, childGrid.getGridPlayer())));        // recursion
        a = Math.max(a, grid.getValue());
        if(a >= b){
          break;
        }
      }
      return grid.getValue();
    }
    else{                     // User's turn
      grid.setValue(1);
      for(int i: grid.getAvailableColumns()){
        Grid childGrid = new Grid(M, N, K);
        grid.copy(childGrid);
        childGrid.addCell(i, false);
        if(grid.getFirstChilds() == 0){
          System.out.println("Loading....");
          grid.addChild(childGrid);
        }
        childGrid.setFirstChilds(grid.getFirstChilds() + 1);
        grid.setValue(Math.min(grid.getValue(), alphaBeta(childGrid, a, b, childGrid.getGridPlayer())));        // recursion
        b = Math.min(b, grid.getValue());
        if(b <= a){
          break;
        }
      }
      return grid.getValue();
    }
  }*/

  /*public Grid getGameGrid(){        // debugging
    return gameGrid;
  }*/

  public static void main(String args[]){
    System.out.println("\nCONNECT-4 (4x4 board)\n");
    System.out.println("(Please wait up to 90 seconds for the first move of computer)\n");
    Game myGame = new Game(4, 4, 4);
    Scanner inputs = new Scanner(System.in);
    System.out.print("Do you want to play first ? (yes/no)  ");
    String answer = inputs.nextLine();
    System.out.println("");
    myGame.playGame(answer);
  }
}
