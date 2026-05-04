package tictactoe;

import java.util.Scanner;

public class ConsoleUI
{
    private Board board;
    private GameLogic logic;
    private Scanner scanner;
    private String playerXName;
    private String playerOName;

    public ConsoleUI(String filename)
    {
        board = new Board(filename);
        logic = new GameLogic();
        scanner = new Scanner(System.in);
        playerXName = "Player X";
        playerOName = "Player O";
    }

    public void promptNames()
    {
        System.out.print("Enter Player X name: ");
        playerXName = scanner.nextLine().trim();

        System.out.print("Enter Player O name: ");
        playerOName = scanner.nextLine().trim();

        if (playerXName.equals(""))
            playerXName = "Player X";

        if (playerOName.equals(""))
            playerOName = "Player O";
    }

    public void printBoard()
    {
        System.out.println();
        System.out.println("    Col 0  Col 1  Col 2");
        System.out.println("  +------+------+------+");

        for (int row = 0; row < 3; row++)
        {
            System.out.print("Row " + row + " |");
            for (int col = 0; col < 3; col++)
            {
                char cell = board.getCell(row, col);
                String display;

                if (cell == 'E')
                    display = "  .  ";
                else if (cell == 'X')
                    display = "  X  ";
                else
                    display = "  O  ";

                System.out.print(display + "|");
            }
            System.out.println();
            System.out.println("  +------+------+------+");
        }
        System.out.println();
    }

    public int[] promptMove()
    {
        char currentPlayer = logic.getCurrentPlayer(board);

        if (currentPlayer == 'X')
            System.out.println(playerXName + "'s turn.");
        else
            System.out.println(playerOName + "'s turn.");

        int row = -1;
        int col = -1;

        while (true)
        {
            System.out.print("Enter row (0-2): ");
            if (scanner.hasNextInt())
            {
                row = scanner.nextInt();
            }
            else
            {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next();
                continue;
            }

            System.out.print("Enter col (0-2): ");
            if (scanner.hasNextInt())
            {
                col = scanner.nextInt();
            }
            else
            {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next();
                continue;
            }

            if (row < 0 || row > 2 || col < 0 || col > 2)
            {
                System.out.println("Out of range. Row and col must each be 0, 1, or 2.");
                continue;
            }

            if (board.getCell(row, col) != 'E')
            {
                System.out.println("That cell is already taken. Choose an empty cell.");
                continue;
            }

            break;
        }

        return new int[]{row, col};
    }

    public void showResult()
    {
        if (logic.checkWin(board, 'X'))
            System.out.println("*** " + playerXName + " wins! Congratulations! ***"
                    + "                     ___________\r\n"
                    + "                    '._==_==_=_.'\r\n"
                    + "                    .-\\\\:      /-.\r\n"
                    + "                   | (|:.     |) |\r\n"
                    + "                    '-|:.     |-'\r\n"
                    + "                      \\\\::.    /\r\n"
                    + "                       '::. .'\r\n"
                    + "                         ) (\r\n"
                    + "                       _.' '._\r\n"
                    + "                  `\\\"\\\"\\\"\\\"\\\"\\\"\\\"`" );
        else if (logic.checkWin(board, 'O'))
            System.out.println("*** " + playerOName + " wins! Congratulations! ***"
                    + "                     ___________\r\n"
                    + "                    '._==_==_=_.'\r\n"
                    + "                    .-\\\\:      /-.\r\n"
                    + "                   | (|:.     |) |\r\n"
                    + "                    '-|:.     |-'\r\n"
                    + "                      \\\\::.    /\r\n"
                    + "                       '::. .'\r\n"
                    + "                         ) (\r\n"
                    + "                       _.' '._\r\n"
                    + "                  `\\\"\\\"\\\"\\\"\\\"\\\"\\\"`" );
        else
            System.out.println("*** It's a draw! Well played by both! ***");
    }

    public void play()
    {
        System.out.println("=============================");
        System.out.println("   Welcome to Tic-Tac-Toe!  ");
        System.out.println("=============================");

        promptNames();

        System.out.println("X goes first. Enter row and col to place your mark.");

        board.clearBoard();

        while (!logic.isGameOver(board))
        {
            printBoard();
            int[] move = promptMove();
            logic.makeMove(board, move[0], move[1]);
        }

        printBoard();
        showResult();
        scanner.close();
    }

    public static void main(String[] args)
    {
        ConsoleUI game = new ConsoleUI("board.csv");
        game.play();
    }
}
