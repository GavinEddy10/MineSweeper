package com.eddy;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;

public class GameBoard { ////MAKE: If click on bomb tile reveal all bomb tiles + show game over

    private int[][] board;
    private boolean firstClick = true;

    private Texture emptyTile;
    private Texture questionTile;
    private Texture bombTile;
    private Texture emptyFloor;
    private Texture flaggedTile;

    private Texture bomb;
    private Texture oneTile, twoTile, threeTile, fourTile, fiveTile, sixTile, sevenTile, eightTile;

    private static final int BOMB = 9, EMPTYTILE = 10, FLAGGEDTILE = 20, QUESTIONTILE = 30;

    public GameBoard() {
        board = new int[16][30];
        initEmptyBoard();

        //load all textures
        emptyTile = new Texture("emptyTile.jpg");
        bomb = new Texture("bomb.jpg");
        oneTile = new Texture("oneTile.jpg");
        twoTile = new Texture("twoTile.jpg");
        threeTile = new Texture("threeTile.jpg");
        fourTile = new Texture("fourTile.jpg");
        fiveTile = new Texture("fiveTile.jpg");
        sixTile = new Texture("sixTile.jpg");
        sevenTile = new Texture("sevenTile.jpg");
        eightTile = new Texture("eightTile.jpg");
        flaggedTile = new Texture("flagTile.jpg");
        emptyFloor = new Texture("empty floor.jpg");
    }

    public boolean isValidLoc(int row, int col) {
        return row>= 0 && row<board.length &&
                col >= 0 && col < board[0].length;
    }
    public void handleClick(int x, int y) {
        //change windows (x,y) coordinate to 2D array loc
        int rowClicked = (y-10)/25;
        int colClicked = (x-10)/25;

        if (isValidLoc(rowClicked,colClicked)) {
            if (!firstClick) {//happens after first click //runs everytime
                clearOpenSpace(rowClicked,colClicked);
            }

            board[rowClicked][colClicked] = board[rowClicked][colClicked] % 10;
            if (board[rowClicked][colClicked] == BOMB) {
                unCoverAllBombs();
            }

            if (firstClick) {//first time run
                firstClick = false;
                placeBombs(rowClicked,colClicked);
                initBoardNumbers();
                clearOpenSpace(rowClicked,colClicked); //after bombs placed and initate board, bomb method has safety so you don't press on bomb
            }
            checkIfWon();

        }
    }
    public void handleRightClick(int x, int y) {
        int rowClicked = (y-10)/25;
        int colClicked = (x-10)/25;

        if (isValidLoc(rowClicked,colClicked)) {
            if (board[rowClicked][colClicked] == FLAGGEDTILE) {
                board[rowClicked][colClicked] = EMPTYTILE;
            }
            board[rowClicked][colClicked] = FLAGGEDTILE;
        }

    }

    public void unCoverAllBombs() {
        for(int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] %10 == BOMB)
                    board[i][j] = BOMB;
                //else if (board[i][j] == FLAGGEDTILE)

            }
        }
    }

    public void clearOpenSpace(int row, int col) {
        ArrayList<Location> neighbors = getNeighbors(row, col);

        if (board[row][col]/10 != 0) {// not an empty tile already revealed will run if
            //1,2,3... bomb near tiles //10 empty tile on top //0 tile on bottom that already revealed

            board[row][col] = board[row][col] % 10; //get remainder of tile, 1,2,3 if it is near bomb

            if (board[row][col] == 0) { //only runs if tile is bottom empty tile (=0)
                for (Location loc : neighbors) {
                    clearOpenSpace(loc.getRow(),loc.getColumn());
                }
            }

        }


        //doesn't do anyting if tile is > 0 (tile is a num)
    }


    private void placeBombs(int rowClicked, int colClicked) {
        int bombCount = 0;
        ArrayList<Location> firstClicNeighbors = getNeighbors(rowClicked, colClicked);//clear first click

        while(bombCount < 99) {
            int randomRow = (int) (Math.random()*board.length);
            int randomCol = (int) (Math.random()*board[0].length);

            //random loc != first click
            if (randomRow != rowClicked && randomCol != colClicked) {
                Location currentRandomLoc = new Location(randomRow,randomCol);//clear first click
                if (board[randomRow][randomCol] == EMPTYTILE && clickedNeighborsCheck(firstClicNeighbors, currentRandomLoc)) {//seccond clear first click
                    board[randomRow][randomCol] = BOMB + 10;
                    bombCount++;
                }
            }
        }
    }

    public boolean clickedNeighborsCheck(ArrayList<Location> neighbors, Location passedIn) {
        for (Location current : neighbors) {
            if (current.getRow() == passedIn.getRow() && current.getColumn() == passedIn.getColumn()) {
                return false;
            }
        }
        return true;
    }


    private void initBoardNumbers() {
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                if (board[row][col] % 10 != BOMB) {
                    int numOfBombs = bombsAroundLoc(row,col);
                    board[row][col] = numOfBombs + 10;
                }
            }
        }
    }

    //3-8 Location array use is validloc
    private ArrayList<Location> getNeighbors(int row, int col) {
        ArrayList<Location> neighbors = new ArrayList<>();

        for (int i = row-1; i < row+2; i++) {
            for (int j = col-1; j < col+2; j++) {
                Location currentNeighbor = new Location(i,j);
                if (isValidLoc(i,j)) {
                    neighbors.add(currentNeighbor);
                    // TEST //System.out.print("Current Neighbor " + currentNeighbor);
                    //System.out.println();
                }
            }
        }
        //System.out.println();
        //System.out.println();

        return neighbors;
    }

    private int bombsAroundLoc(int row, int col) {
        ArrayList<Location> locs = getNeighbors(row,col);
        //System.out.println("[ "+row+" ] " + "[ " + col + " ]");
        //System.out.println(locs);

        int count = 0;
        for (Location temp : locs) {
            if (board[temp.getRow()][temp.getColumn()] % 10 == BOMB) {
                count++;
            }
        }
        //System.out.println("bombs found: " + count);
        //ystem.out.println();
        return count;

    }

    public boolean checkIfWon() {
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; row++) {
                if (board[row][col] == EMPTYTILE)
                    return false;
            }
        }
        return true;
    }

   //recursion method
    //clearing out area at first click

    public void draw(SpriteBatch spriteBatch) {
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                //if we have an empty tile
                if (board[row][col] >= EMPTYTILE && board[row][col] < FLAGGEDTILE) {
                    spriteBatch.draw(emptyTile, (10) + (col * 25) ,  (600-35) - (row * 25));
                }
                else if (board[row][col] == FLAGGEDTILE) {
                    spriteBatch.draw(flaggedTile, (10) + (col*25), (600-35) - (row*25));
                }
                else if (board[row][col] == BOMB) {
                    spriteBatch.draw(bomb, (10) + (col * 25) ,  (600-35) - (row * 25));
                }
                else if (board[row][col] == 0) {
                    spriteBatch.draw(emptyFloor, (10) + (col * 25) ,  (600-35) - (row * 25));
                }
                else if (board[row][col] == 1) {
                    spriteBatch.draw(oneTile, (10) + (col * 25) ,  (600-35) - (row * 25));
                }
                else if (board[row][col] == 2) {
                    spriteBatch.draw(twoTile, (10) + (col * 25) ,  (600-35) - (row * 25));
                }
                else if (board[row][col] == 3) {
                    spriteBatch.draw(threeTile, (10) + (col * 25) ,  (600-35) - (row * 25));
                }
                else if (board[row][col] == 4) {
                    spriteBatch.draw(fourTile, (10) + (col * 25) ,  (600-35) - (row * 25));
                }
                else if (board[row][col] == 5) {
                    spriteBatch.draw(fiveTile, (10) + (col * 25) ,  (600-35) - (row * 25));
                }
                else if (board[row][col] == 6) {
                    spriteBatch.draw(sixTile, (10) + (col * 25) ,  (600-35) - (row * 25));
                }
                else if (board[row][col] == 7) {
                    spriteBatch.draw(sevenTile, (10) + (col * 25) ,  (600-35) - (row * 25));
                }
                else if (board[row][col] == 8) {
                    spriteBatch.draw(eightTile, (10) + (col * 25) ,  (600-35) - (row * 25));
                }
            }
        }
    }
    private void initEmptyBoard() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                board[i][j] = 10;
            }

        }
    }
}
