package tictactoe;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

public class Board {
    private char[][] grid;
    private String filename;

    public Board(String filename) {
        this.filename = filename;
        this.grid = new char[3][3];

        if (isValidBoardFile()) {
            loadBoardFromFile();
        } else {
            clearBoard();
        }
    }

    public char getCell(int row, int col) {
        return grid[row][col];
    }

    public void setCell(int row, int col, char player) {
        grid[row][col] = player;
        saveBoardToFile();
    }

    public char[][] getGrid() {
        return grid;
    }

    public void setGrid(char[][] newGrid) {
        grid = newGrid;
        saveBoardToFile();
    }

    public void loadBoardFromFile() {
        try {
            File file = new File("src/tictactoe/" + this.filename);
            Scanner scanner = new Scanner(file);

            int row = 0;

            while (scanner.hasNextLine() && row < 3) {
                String line = scanner.nextLine().trim();
                String[] lineArray = line.split(",");

                for (int col = 0; col < lineArray.length && col < 3; col++) {
                    grid[row][col] = lineArray[col].charAt(0);
                }

                row++;
            }

            scanner.close();
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    public boolean isValidBoardFile() {
        try {
            File file = new File("src/tictactoe/" + this.filename);
            Scanner scanner = new Scanner(file);

            int xCount = 0;
            int oCount = 0;
            int rowCount = 0;

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();

                if (!line.matches("[EXO],[EXO],[EXO]")) {
                    scanner.close();
                    return false;
                }

                String[] lineArray = line.split(",");

                for (int i = 0; i < lineArray.length; i++) {
                    if (lineArray[i].charAt(0) == 'X') {
                        xCount++;
                    } else if (lineArray[i].charAt(0) == 'O') {
                        oCount++;
                    }
                }

                rowCount++;
            }

            scanner.close();

            return rowCount == 3 && (xCount == oCount || xCount == oCount + 1);
        } catch (Exception error) {
            error.printStackTrace();
            return false;
        }
    }

    public void saveBoardToFile() {
        try {
            File file = new File("src/tictactoe/" + this.filename);
            FileWriter writer = new FileWriter(file);

            String boardContents = "";

            for (int row = 0; row < grid.length; row++) {
                for (int col = 0; col < grid[0].length; col++) {
                    if (col < 2) {
                        boardContents += grid[row][col] + ",";
                    } else {
                        boardContents += grid[row][col];
                    }
                }

                if (row < 2) {
                    boardContents += "\n";
                }
            }

            writer.write(boardContents);
            writer.close();
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    public void printGrid() {
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[0].length; col++) {
                System.out.print(grid[row][col] + " ");
            }

            System.out.println();
        }
    }

    public void createRandomBoard() {
        char options[] = {'E', 'X', 'O'};
        char[][] randomBoard = new char[3][3];

        for (int row = 0; row < randomBoard.length; row++) {
            for (int col = 0; col < randomBoard[0].length; col++) {
                int index = (int)(Math.random() * 3);
                randomBoard[row][col] = options[index];
            }
        }

        this.grid = randomBoard;
        this.saveBoardToFile();
    }

    public void clearBoard() {
        char[][] clearedBoard = {
            {'E', 'E', 'E'},
            {'E', 'E', 'E'},
            {'E', 'E', 'E'}
        };

        this.grid = clearedBoard;
        this.saveBoardToFile();
    }
}
