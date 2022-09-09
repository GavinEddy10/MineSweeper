package com.eddy;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;

public class GameBoard {

    private int[][] board;
    private boolean firstClick = true;

    private Texture emptyTile;
    private Texture questionTile;
    private Texture bombTile;
    private Texture emptyFloor;
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
            board[rowClicked][colClicked] = board[rowClicked][colClicked] % 10;
            if (firstClick) {
                firstClick = false;
                placeBombs(rowClicked,colClicked);
            }
            getNeighbors(rowClicked,colClicked);
        }
    }

    private void placeBombs(int rowClicked, int colClicked) {
        int bombCount = 0;

        while(bombCount < 99) {
            int randomRow = (int) (Math.random()*board.length);
            int randomCol = (int) (Math.random()*board[0].length);

            //random loc != first click
            if (randomRow != rowClicked && randomCol != colClicked) {
                if (board[randomRow][randomCol] == EMPTYTILE) {
                    board[randomRow][randomCol] = BOMB + 10;
                    bombCount++;
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
                    System.out.print("Current Neighbor " + currentNeighbor);
                    System.out.println();
                }
            }
        }
        System.out.println();
        System.out.println();

        return neighbors;
    }



    public void draw(SpriteBatch spriteBatch) {
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                //if we have an empty tile
                if (board[row][col] >= EMPTYTILE && board[row][col] < FLAGGEDTILE) {
                    spriteBatch.draw(emptyTile, (10) + (col * 25) ,  (600-35) - (row * 25));
                }
                else if (board[row][col] == BOMB) {
                    spriteBatch.draw(bomb, (10) + (col * 25) ,  (600-35) - (row * 25));
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
