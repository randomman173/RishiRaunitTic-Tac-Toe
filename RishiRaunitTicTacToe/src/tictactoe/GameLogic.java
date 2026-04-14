package tictactoe;


public class GameLogic 
{  
	 
	public boolean checkWin(Board board, char player) 
	{
		char[][] g = board.getGrid();
		for (int i = 0; i < g.length; i++) 
		{
			for (int j = 0; j < g[0].length; j++) {
				if (g[i][j] == 'E')
					return false;
				else if (j == 0)
					if (g[i][j] == player && g[i][j + 1] == player && g[i][j + 2] == player) {
						return true;
				} else if (i == 0)
						if (g[i][j] == player && g[i + 1][j] == player && g[i + 2][j] == player) {
							return true;
				} else if (i == 0 && j == 0) {
							if (g[i][j] == player && g[i + 1][j + 1] == player && g[i + 2][j + 2] == player) {
								return true;
							}
				} else if (i == 0 && j == 2) {
							if (g[i][j] == player && g[i + 1][j - 1] == player && g[i + 2][j - 2] == player) {
								return true;
							}
						}
		

			}
		
			
		}
		return false;
	}
	
	public boolean isDraw(Board board)
	{
		char[][] g = board.getGrid();
		
		for (int i = 0; i < g.length; i++) 
	    {
	        for (int j = 0; j < g[0].length; j++) 
	        {
	           if(g[i][j] == 'E') 
	        	   return false;
	           
	        }
	    }
		return checkWin(board, 'X') || checkWin(board, 'O');
	}
	

}











