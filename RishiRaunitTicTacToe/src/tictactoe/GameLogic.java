package tictactoe;


public class GameLogic 
{  
	 
	public boolean checkWin(Board board, char player)
	{
		char[][] g = board.getGrid();
	    int n = g.length;

	    for (int i = 0; i < n; i++) 
	    {
	        boolean row = true, col = true;

	        for (int j = 0; j < n; j++) {
	            if (g[i][j] != player) row = false;
	            if (g[j][i] != player) col = false;
	        }

	        if (row || col) return true;
	    }

	    boolean d1 = true, d2 = true;
	    for (int i = 0; i < n; i++) {
	        if (g[i][i] != player) d1 = false;
	        if (g[i][n - 1 - i] != player) d2 = false;
	    }

	    return d1 || d2;
	}
	

	}











