package tictactoe;


public class GameLogic 
{  
	 
	public boolean checkWin(Board board, char player) 
	{
		for(int i = 0; i < 2; i++)
		{
			if(board.getCell(i,0) == player && board.getCell(i,1) == player && board.getCell(i,2) == player)
			{
				return true;
			}
		}
		for(int j = 0; j < 2; j++)
		{
			if(board.getCell(0,j) == player && board.getCell(1,j) == player && board.getCell(2,j) == player)
			{
				return true;
			}
		}
		if( (board.getCell(0,0) == player && board.getCell(1,1) == player && board.getCell(2,2) == player) ||
        (board.getCell(0,2) == player && board.getCell(1,1) == player && board.getCell(2,0) == player)) 
		{
			return true;
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
		return !checkWin(board, 'X') || !checkWin(board, 'O');
	}
	
	public boolean isGameOver(Board board)
	{
		GameLogic game = new GameLogic();
		return game.checkWin(board, 'X') || game.checkWin(board, 'O') || game.isDraw(board);
	}

}









