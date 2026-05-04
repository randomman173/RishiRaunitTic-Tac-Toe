package tictactoe;

import javax.swing.JFrame;

public class TicTocToeMain {

	public static void main(String[] args) {
		
		
		SwingUI run = new SwingUI("board.csv");
		run.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		ConsoleUI game = new ConsoleUI("board.csv");
        game.play();
		

	}

}
