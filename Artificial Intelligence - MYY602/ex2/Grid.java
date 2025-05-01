// Modiotis Athanasios    AM: 4736
// Bonitsis Pantelis      AM: 4742
// Sidiropoulos Georgios  AM: 4789

import java.util.ArrayList;

class Grid{       // Every state of Game Tree is a Grid.
  private int M;
  private int N;
  private int K;
  private int value;
  private int firstChilds;
  private String cells[][];
  private boolean gridPlayer;
  private ArrayList<Integer> availableColumns = new ArrayList<Integer>();
  private ArrayList<Grid> neighbouringGrids = new ArrayList<Grid>();

// Constructor
  public Grid(int M, int N, int K){
    this.M = M;
    this.N = N;
    this.K = K;
    cells = new String[M][N];
    createGrid();
  }

// Private method which creates the Grid of the game.
  private void createGrid(){
    for(int i = 0; i < M; i++){
      for(int j = 0; j < N; j++){
        cells[i][j] = "  ";
      }
    }
  }

// The User gives the number of column (newColumn) in which he wants to put "O" and this method finds the correct row.
  public int findRow(int newColumn){
    for(int i = M - 1; i >= 0; i--){
      if(cells[i][newColumn] == "  "){
        return i;
      }
    }
    return -1;
  }

// Method which adds a "X" or an "O" to the Grid of game.
  public void addCell(int newColumn, boolean player){
    int newRow = findRow(newColumn);
    gridPlayer = !player;
    if(player){
      cells[newRow][newColumn] = "X ";
    }else{
      cells[newRow][newColumn] = "O ";
    }
    findAvailableColumns();
  }

// Method which adds a child to a state of Game Tree
  public void addChild(Grid chlid){
    neighbouringGrids.add(chlid);
  }

// Method which finds the available positions (columns) in which players can put their symbol.
  public void findAvailableColumns(){
    ArrayList<Integer> newAvailableColumns = new ArrayList<Integer>();
    for(int i = 0; i <= N-1; i++){
      for(int j = M-1; j >=0; j--){
        if(cells[j][i] == "  "){
          newAvailableColumns.add(i);
          break;
        }
      }
    }
    availableColumns = newAvailableColumns;
  }

// Method which checks if the Grid of game is full and the game is finished.
  public boolean isFull(){
    if(availableColumns.size() == 0){
      return true;
    }
    return false;
  }

// Method which creates a copy of a state (Grid) in order to find its childs to the Game tree.
  public void copy(Grid first){
    for(int i = 0; i < M; i++){
      for(int j = 0; j < N; j++){
        first.setCell(i, j, getCell(i, j));
      }
    }
    first.setAvailableColumns(availableColumns);
    first.setGridPlayer(gridPlayer);
  }

// Method which finds the new grid (new state of Game Tree) after a move of Computer.
  public Grid nextGrid(){
    for (Grid g: neighbouringGrids){
      if(value == g.getValue()){
        ArrayList<Grid> newNeighbouringGrids = new ArrayList<Grid>();
        g.setNeighbouringGrids(newNeighbouringGrids);
        return g;
      }
    }
    return null;
  }

// Method which checks if a player has won the game.
  public boolean win(boolean player){
    String symbol;
    if(player){
      symbol = "X ";
    }else{
      symbol = "O ";
    }

    int columnCounter = 0;        // Checking columns
    for(int i = 0; i <= N-1; i++){
      for(int j = M-1; j >= 0; j--){
        if(cells[j][i] == "  "){
          columnCounter = 0;
          break;
        }
        if (cells[j][i] == symbol){
          columnCounter++;
          if(columnCounter == K){
            return true;
          }
        }else{
          columnCounter = 0;
        }
      }
      columnCounter = 0;
    }

    int rowCounter = 0;       // Checking rows
    for(int i = M-1; i >= 0; i--){
      for(int j = 0; j <= N - 1; j++){
        if(cells[i][j] == "  "){
          rowCounter = 0;
          continue;
        }
        if (cells[i][j] == symbol){
          rowCounter++;
          if(rowCounter == K){
            return true;
          }
        }else{
          rowCounter = 0;
        }
      }
      rowCounter = 0;
    }

    int diagCounterRc = 0;        // Checking diagonals
    for(int i = 0; i <= N-K; i++){
      for(int j = M-1 , g = i; j >= 0 && g <= N-1; j-- , g++){
        if(cells[j][g] == "  "){
          diagCounterRc = 0;
          continue;
        }
        if (cells[j][g] == symbol){
          diagCounterRc++;
          if(diagCounterRc == K){
            return true;
          }
        }else{
          diagCounterRc = 0;
        }
      }
      diagCounterRc = 0;
    }

    int diagCounterLc = 0;        // Checking diagonals
    for(int i = N-1; i >= K-1; i--){
      for(int j = M-1 , g = i; j >= 0 && g >= 0; j-- , g--){
        if(cells[j][g] == "  "){
          diagCounterLc = 0;
          continue;
        }
        if (cells[j][g] == symbol){
          diagCounterLc++;
          if(diagCounterLc == K){
            return true;
          }
        }else{
          diagCounterLc = 0;
        }
      }
      diagCounterLc = 0;
    }

    int diagCounterRr = 0;        // Checking diagonals
    for(int i = M-2; i >= K-1; i--){
      for(int j = 0, g = i; g >= 0; j++ , g--){
        if(cells[g][j] == "  "){
          diagCounterRr = 0;
          continue;
        }
        if (cells[g][j] == symbol){
          diagCounterRr++;
          if(diagCounterRr == K){
            return true;
          }
        }else{
          diagCounterRr = 0;
        }
      }
      diagCounterRr = 0;
    }

    int diagCounterLr = 0;        // Checking diagonals
    for(int i = M-2; i >= K-1; i--){
      for(int j = N-1 , g = i; g >= 0; j-- , g--){
        if(cells[g][j] == "  "){
          diagCounterLr = 0;
          continue;
        }
        if (cells[g][j] == symbol){
          diagCounterLr++;
          if(diagCounterLr == K){
            return true;
          }
        }else{
          diagCounterLr = 0;
        }
      }
      diagCounterLr = 0;
    }
    return false;
  }

  public ArrayList<Integer> getAvailableColumns(){
    return availableColumns;
  }

  public int getValue(){
    return value;
  }

  public int getFirstChilds(){
    return firstChilds;
  }

  private String getCell(int row, int column){
    return cells[row][column];
  }

  /*public ArrayList<Grid> getNeighbouringGrids(){        // debugging
    return neighbouringGrids;
  }*/

  public boolean getGridPlayer(){
    return gridPlayer;
  }

  private void setCell(int row, int column, String s){
    cells[row][column] = s;
  }

  private void setAvailableColumns(ArrayList<Integer> ac){
    availableColumns = ac;
  }

  public void setValue(int v){
    value = v;
  }

  public void setFirstChilds(int d){
    firstChilds = d;
  }

  private void setGridPlayer(boolean gp){
    gridPlayer = gp;
  }

  private void setNeighbouringGrids(ArrayList<Grid> ng){
    neighbouringGrids = ng;
  }

  public String toString(){
    String grid = "\n";
    grid += "  1   2   3   4";
    grid += "\n\n";
    for(int i = 0; i < M; i++){
      for(int j = 0; j < N; j++){
        grid += "| "+cells[i][j];
      }
      grid += "|\n";
      grid += "_________________\n\n";
    }
    return grid;
  }

}
