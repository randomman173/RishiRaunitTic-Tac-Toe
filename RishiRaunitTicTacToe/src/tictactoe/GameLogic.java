package tictactoe;

public class GameLogic {  

    public boolean checkWin(Board board, char player) {
        for (int i = 0; i < 3; i++) {
            if (board.getCell(i, 0) == player &&
                board.getCell(i, 1) == player &&
                board.getCell(i, 2) == player) {
                return true;
            }
        }

        for (int j = 0; j < 3; j++) {
            if (board.getCell(0, j) == player &&
                board.getCell(1, j) == player &&
                board.getCell(2, j) == player) {
                return true;
            }
        }

        return (board.getCell(0, 0) == player &&
                board.getCell(1, 1) == player &&
                board.getCell(2, 2) == player) ||
               (board.getCell(0, 2) == player &&
                board.getCell(1, 1) == player &&
                board.getCell(2, 0) == player);
    }

    public boolean isDraw(Board board) {
        char[][] g = board.getGrid();

        for (int i = 0; i < g.length; i++) {
            for (int j = 0; j < g[i].length; j++) {
                if (g[i][j] == 'E') {
                    return false;
                }
            }
        }

        return !checkWin(board, 'X') && !checkWin(board, 'O');
    }

    public boolean isGameOver(Board board) {
        return checkWin(board, 'X') || checkWin(board, 'O') || isDraw(board);
    }

    public char getCurrentPlayer(Board board) {
        int countX = 0;
        int countO = 0;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (board.getCell(row, col) == 'X') {
                    countX++;
                } else if (board.getCell(row, col) == 'O') {
                    countO++;
                }
            }
        }

        if (countX == countO) {
            return 'X';
        } else {
            return 'O';
        }
    }

    public boolean makeMove(Board board, int row, int col) {
        if (!board.isValidBoardFile()) {
            return false;
        }

        if (isGameOver(board)) {
            return false;
        }

        if (row < 0 || row > 2 || col < 0 || col > 2) {
            return false;
        }

        if (board.getCell(row, col) != 'E') {
            return false;
        }

        char player = getCurrentPlayer(board);
        board.setCell(row, col, player);
        return true;
    }
}
