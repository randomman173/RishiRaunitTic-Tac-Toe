package tictactoe;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import java.awt.Toolkit;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.BasicStroke;
import javax.sound.sampled.*;
import java.io.File;

public class SwingUI extends JFrame {
    private SwingGameController controller;
    private JButton[][] buttons;
    private JLabel titleLabel;
    private JLabel statusLabel;
    private JLabel scoreLabel;
    private Timer rainbowTimer;
    private ConfettiPanel confettiPanel;
    private int colorIndex = 0;
    private boolean waitingForComputer = false;
    private Clip moveClip;
    private Clip winClip;
    private Clip drawClip;
    private Clip backgroundClip;
    private Timer flashTimer;
    private int flashCount = 0;
    private boolean flashOn = false;


    
    private JPanel topPanel;
    private JPanel boardPanel;
    private JPanel bottomPanel;
    private SquarePanel centerPanel;
    private JButton resetButton;
    private JButton menuButton;

    private Color backgroundColor = new Color(18, 18, 28);
    private Color boardColor = new Color(40, 42, 65);
    private Color buttonColor = new Color(250, 250, 255);
    private Color xColor = new Color(255, 140, 0);
    private Color oColor = new Color(0, 102, 255);
    private Color hoverColor = new Color(230, 220, 255);
    private Color winningColor = new Color(255, 214, 10);
    private Color textColor = new Color(220, 240, 255);
    private String selectedTheme = "Neon";

    public SwingUI(String filename) {
        controller = new SwingGameController(filename);
        buttons = new JButton[3][3];
        prepareSounds();

        showLoginDialog();

        confettiPanel = new ConfettiPanel();
        confettiPanel.setOpaque(false);
        setGlassPane(confettiPanel);

        setTitle("Tic-Tac-Toe");
        setSize(500, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(backgroundColor);

        titleLabel = new JLabel("Tic-Tac-Toe", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 34));

        statusLabel = new JLabel("", JLabel.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 25));
        statusLabel.setForeground(textColor);

        scoreLabel = new JLabel("", JLabel.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 20));
        scoreLabel.setForeground(textColor);

        topPanel = new JPanel(new GridLayout(3, 1, 0, 5));
        topPanel.setBackground(backgroundColor);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 5, 10));
        topPanel.add(titleLabel);
        topPanel.add(statusLabel);
        topPanel.add(scoreLabel);
        add(topPanel, BorderLayout.NORTH);

        boardPanel = new JPanel(new GridLayout(3, 3, 10, 10));
        boardPanel.setBackground(boardColor);
        boardPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
            	CheckerButton button = new CheckerButton();
                button.setFont(new Font("Arial", Font.BOLD, 48));
                button.setFocusPainted(false);
                button.setBackground(buttonColor);
                button.setOpaque(true);

                final int r = row;
                final int c = col;

                button.addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) {
                        if (controller.getCell(r, c) == 'E' && !controller.isGameOver()) {
                            button.setBackground(hoverColor);
                        }
                    }

                    public void mouseExited(MouseEvent e) {
                        if (controller.getCell(r, c) == 'E') {
                            button.setBackground(buttonColor);
                        }
                    }
                });

                button.addActionListener(e -> handleButtonClick(r, c));

                buttons[row][col] = button;
                boardPanel.add(button);
            }
        }

        startRainbowColors();

        boardPanel.setPreferredSize(new Dimension(380, 380));
        centerPanel = new SquarePanel(boardPanel, backgroundColor);
        add(centerPanel, BorderLayout.CENTER);

        resetButton = new JButton("Reset Match");
        resetButton.setFont(new Font("Arial", Font.BOLD, 20));
        resetButton.setFocusPainted(false);
        resetButton.setBackground(new Color(140, 90, 255));
        resetButton.setForeground(Color.WHITE);
        resetButton.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        resetButton.addActionListener(e -> {
            controller.resetMatch();
            showBigMessage(controller.randomizeStartingPlayer());
            refreshBoard();
            startComputerIfNeeded();
        });
        
        menuButton = new JButton("Main Menu");
        menuButton.setFont(new Font("Arial", Font.BOLD, 20));
        menuButton.setFocusPainted(false);
        menuButton.setBackground(new Color(255, 140, 0));
        menuButton.setForeground(Color.WHITE);
        menuButton.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        menuButton.addActionListener(e -> {
            returnToMainMenu();
        });

        bottomPanel = new JPanel();
        bottomPanel.setBackground(backgroundColor);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 20, 10));
        bottomPanel.add(resetButton);
        bottomPanel.add(menuButton);
        add(bottomPanel, BorderLayout.SOUTH);

        controller.newGame();
        refreshBoard();

        setVisible(true);
        playBackgroundMusic();
        startComputerIfNeeded();
    }

    private void showLoginDialog() {
        stopBackgroundMusic();

        while (true) {
            Color setupPanelColor = new Color(34, 35, 58);
            Color setupAccent = new Color(255, 140, 0);
            Color setupPurple = new Color(140, 90, 255);
            Color setupText = Color.WHITE;

            JTextField playerOneField = new JTextField();
            JTextField playerTwoField = new JTextField();

            playerOneField.setFont(new Font("Arial", Font.BOLD, 18));
            playerTwoField.setFont(new Font("Arial", Font.BOLD, 18));
            playerOneField.setBackground(new Color(250, 250, 255));
            playerTwoField.setBackground(new Color(250, 250, 255));
            playerOneField.setForeground(new Color(30, 30, 40));
            playerTwoField.setForeground(new Color(30, 30, 40));

            JRadioButton onePlayerButton = new JRadioButton("One Player vs Computer");
            JRadioButton twoPlayerButton = new JRadioButton("Two Players");
            twoPlayerButton.setSelected(true);

            onePlayerButton.setBackground(setupPanelColor);
            twoPlayerButton.setBackground(setupPanelColor);
            onePlayerButton.setForeground(setupText);
            twoPlayerButton.setForeground(setupText);
            onePlayerButton.setFocusPainted(false);
            twoPlayerButton.setFocusPainted(false);

            ButtonGroup playerModeGroup = new ButtonGroup();
            playerModeGroup.add(onePlayerButton);
            playerModeGroup.add(twoPlayerButton);

            String[] matchModes = {"Best of 3", "Best of 5", "Free Play"};
            JComboBox<String> matchModeBox = new JComboBox<>(matchModes);
            matchModeBox.setFont(new Font("Arial", Font.BOLD, 16));

            String[] themes = {"Classic", "Neon", "Ocean", "Sunset"};
            JComboBox<String> themeBox = new JComboBox<>(themes);
            themeBox.setFont(new Font("Arial", Font.BOLD, 16));

            String[] difficulties = {"Easy", "Medium", "Hard"};
            JComboBox<String> difficultyBox = new JComboBox<>(difficulties);
            difficultyBox.setFont(new Font("Arial", Font.BOLD, 16));

            JLabel title = new JLabel("Tic-Tac-Toe Setup", JLabel.CENTER);
            title.setFont(new Font("Arial", Font.BOLD, 28));
            title.setForeground(setupAccent);

            JLabel playerOneLabel = new JLabel("Player 1 Name:");
            JLabel playerTwoLabel = new JLabel("Player 2 Name:");
            JLabel matchModeLabel = new JLabel("Match Mode:");
            JLabel themeLabel = new JLabel("Theme:");
            JLabel difficultyLabel = new JLabel("AI Difficulty:");

            JLabel[] labels = {
                playerOneLabel, playerTwoLabel, matchModeLabel, themeLabel, difficultyLabel
            };

            for (JLabel label : labels) {
                label.setFont(new Font("Arial", Font.BOLD, 16));
                label.setForeground(setupText);
            }

            difficultyLabel.setVisible(false);
            difficultyBox.setVisible(false);

            JPanel panel = new JPanel(new GridLayout(0, 1, 8, 8));
            panel.setBackground(setupPanelColor);
            panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(setupPurple, 4),
                BorderFactory.createEmptyBorder(18, 28, 18, 28)
            ));

            panel.add(title);
            panel.add(playerOneLabel);
            panel.add(playerOneField);
            panel.add(playerTwoLabel);
            panel.add(playerTwoField);
            panel.add(twoPlayerButton);
            panel.add(onePlayerButton);
            panel.add(matchModeLabel);
            panel.add(matchModeBox);
            panel.add(themeLabel);
            panel.add(themeBox);
            panel.add(difficultyLabel);
            panel.add(difficultyBox);

            onePlayerButton.addActionListener(e -> {
                playerTwoLabel.setText("Computer Name:");
                playerTwoField.setText("Computer");
                playerTwoField.setEnabled(false);
                difficultyLabel.setVisible(true);
                difficultyBox.setVisible(true);
                panel.revalidate();
                panel.repaint();
            });

            twoPlayerButton.addActionListener(e -> {
                playerTwoLabel.setText("Player 2 Name:");
                playerTwoField.setText("");
                playerTwoField.setEnabled(true);
                difficultyLabel.setVisible(false);
                difficultyBox.setVisible(false);
                panel.revalidate();
                panel.repaint();
            });

            int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Game Setup",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );

            if (result != JOptionPane.OK_OPTION) {
                System.exit(0);
            }

            String playerOne = playerOneField.getText().trim();
            String playerTwo = playerTwoField.getText().trim();
            boolean onePlayerMode = onePlayerButton.isSelected();
            String matchMode = (String) matchModeBox.getSelectedItem();
            String aiDifficulty = (String) difficultyBox.getSelectedItem();

            selectedTheme = (String) themeBox.getSelectedItem();
            applyTheme(selectedTheme);
            controller.setThemeName(selectedTheme);
            controller.setAiDifficulty(aiDifficulty);

            if (playerOne.equals("") || playerTwo.equals("")) {
                showBigMessage("Both players must enter a name.");
            } else {
                controller.setGameSettings(onePlayerMode, matchMode);
                controller.setOriginalPlayerNames(playerOne, playerTwo);
                showBigMessage(controller.randomizeStartingPlayer());
                break;
            }
        }
    }

    private void handleButtonClick(int row, int col) {
        if (waitingForComputer) {
            return;
        }

        if (controller.isMatchOver()) {
            showBigMessage("The match is over. Reset the match.");
            return;
        }
        boolean moveMade = controller.makeMove(row, col);

        if (moveMade) {
        	playMoveSound();
            refreshBoard();

            if (finishGameIfNeeded()) {
                return;
            }

            startComputerIfNeeded();
        }
    }

    private boolean finishGameIfNeeded() {
        if (controller.isGameOver()) {
            controller.updateScore();

            if (!controller.isDraw()) {
                playWinSound();
                startFlashingLights();
                startConfetti();
            } else {
                showBigMessage(controller.getStatusMessage());

                if (controller.isMatchOver()) {
                    showCrownMessage(controller.getMatchWinnerMessage());
                    showBigMessage("Final Score: " + controller.getFinalScoreMessage());

                    controller.resetMatch();
                    showBigMessage(controller.randomizeStartingPlayer());
                } else {
                    startNextRound();
                }

                refreshBoard();
            }

            return true;
        }

        return false;
    }

    private void startNextRound() {
        controller.newGame();
        showBigMessage(controller.randomizeStartingPlayer());
        refreshBoard();
        playBackgroundMusic();
        startComputerIfNeeded();
    }

    private void startComputerIfNeeded() {
        if (controller.isComputerTurn()) {
            waitingForComputer = true;
            refreshBoard();

            Timer aiTimer = new Timer(500, e -> {
                controller.makeComputerMove();
                playMoveSound();
                waitingForComputer = false;
                refreshBoard();
                finishGameIfNeeded();
            });

            aiTimer.setRepeats(false);
            aiTimer.start();
        }
    }

    private void startConfetti() {
        confettiPanel.start();

        Timer resetTimer = new Timer(2000, e -> {
            confettiPanel.stop();

            if (controller.isMatchOver()) {
                showCrownMessage(controller.getMatchWinnerMessage());
                showBigMessage("Final Score: " + controller.getFinalScoreMessage());

                controller.resetMatch();
                showBigMessage(controller.randomizeStartingPlayer());
                refreshBoard();
                playBackgroundMusic();

            } else {
                showTrophyMessage(controller.getStatusMessage());
                startNextRound();
            }
        });

        resetTimer.setRepeats(false);
        resetTimer.start();
    }
    
    private void startFlashingLights() {
        stopFlashingLights();

        flashCount = 0;
        flashOn = false;

        flashTimer = new Timer(150, e -> {
            if (flashOn) {
                getContentPane().setBackground(backgroundColor);
                topPanel.setBackground(backgroundColor);
                centerPanel.setBackground(backgroundColor);
                bottomPanel.setBackground(backgroundColor);
                boardPanel.setBackground(boardColor);
            } else {
                Color flashColor = new Color(255, 255, 120);
                getContentPane().setBackground(flashColor);
                topPanel.setBackground(flashColor);
                centerPanel.setBackground(flashColor);
                bottomPanel.setBackground(flashColor);
                boardPanel.setBackground(new Color(255, 190, 60));
            }

            flashOn = !flashOn;
            flashCount++;

            repaint();

            if (flashCount >= 14) {
                stopFlashingLights();
                refreshTheme();
            }
        });

        flashTimer.start();
    }

    private void stopFlashingLights() {
        if (flashTimer != null) {
            flashTimer.stop();
            flashTimer = null;
        }
    }


    private void refreshBoard() {
        boolean gameOver = controller.isGameOver();
        int[][] winningCells = controller.getWinningCells();

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                char cell = controller.getCell(row, col);
                JButton button = buttons[row][col];

                if (cell == 'E') {
                    button.setText("");
                    ((CheckerButton) button).setPiece('E', xColor, oColor, "");
                    button.setEnabled(!gameOver && !waitingForComputer);
                    button.setBackground(buttonColor);
                } else {
                    button.setText("");
                    button.setEnabled(true);
                    ((CheckerButton) button).setPiece(
                    	    cell,
                    	    xColor,
                    	    oColor,
                    	    controller.getTokenLetter(cell)
                    	);

                    if (isWinningCell(row, col, winningCells)) {
                        button.setBackground(winningColor);
                    }
                }
            }
        }

        statusLabel.setText(controller.getStatusMessage());
        scoreLabel.setText(controller.getScoreMessage());
    }

    private void startRainbowColors() {
        Color[] rainbowColors = {
            Color.RED,
            Color.ORANGE,
            Color.YELLOW,
            Color.GREEN,
            Color.CYAN,
            Color.BLUE,
            new Color(180, 0, 255)
        };

        rainbowTimer = new Timer(500, e -> {
            Color currentColor = rainbowColors[colorIndex];
            titleLabel.setForeground(currentColor);

            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 3; col++) {
                    buttons[row][col].setBorder(
                        BorderFactory.createLineBorder(currentColor, 3)
                    );
                }
            }

            colorIndex++;

            if (colorIndex >= rainbowColors.length) {
                colorIndex = 0;
            }
        });

        rainbowTimer.start();
    }
    
    private void applyTheme(String theme) {
        if (theme.equals("Classic")) {
            backgroundColor = new Color(245, 247, 250);
            boardColor = new Color(220, 225, 232);
            buttonColor = Color.WHITE;
            xColor = new Color(220, 53, 69);
            oColor = new Color(13, 110, 253);
            hoverColor = new Color(235, 240, 255);
            winningColor = new Color(255, 214, 10);
            textColor = new Color(40, 45, 55);
        } else if (theme.equals("Ocean")) {
            backgroundColor = new Color(5, 30, 45);
            boardColor = new Color(0, 95, 115);
            buttonColor = new Color(230, 250, 255);
            xColor = new Color(255, 183, 3);
            oColor = new Color(33, 158, 188);
            hoverColor = new Color(180, 235, 245);
            winningColor = new Color(251, 133, 0);
            textColor = new Color(220, 240, 255);
        } else if (theme.equals("Sunset")) {
            backgroundColor = new Color(45, 18, 32);
            boardColor = new Color(120, 45, 70);
            buttonColor = new Color(255, 245, 235);
            xColor = new Color(255, 111, 97);
            oColor = new Color(88, 80, 141);
            hoverColor = new Color(255, 220, 200);
            winningColor = new Color(255, 202, 40);
            textColor = new Color(220, 240, 255);
        } else {
            backgroundColor = new Color(18, 18, 28);
            boardColor = new Color(40, 42, 65);
            buttonColor = new Color(250, 250, 255);
            xColor = new Color(255, 140, 0);
            oColor = new Color(0, 102, 255);
            hoverColor = new Color(230, 220, 255);
            winningColor = new Color(255, 214, 10);
            textColor = new Color(220, 240, 255);
        }
    }
    
    private void refreshTheme() {
        getContentPane().setBackground(backgroundColor);

        topPanel.setBackground(backgroundColor);
        centerPanel.setBackground(backgroundColor);
        bottomPanel.setBackground(backgroundColor);
        boardPanel.setBackground(boardColor);

        statusLabel.setForeground(textColor);
        scoreLabel.setForeground(textColor);

        resetButton.setBackground(new Color(140, 90, 255));
        resetButton.setForeground(Color.WHITE);

        if (menuButton != null) {
            menuButton.setBackground(new Color(255, 140, 0));
            menuButton.setForeground(Color.WHITE);
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (controller.getCell(row, col) == 'E') {
                    buttons[row][col].setBackground(buttonColor);
                }
            }
        }

        refreshBoard();
        repaint();
    }

    private boolean isWinningCell(int row, int col, int[][] winningCells) {
        if (winningCells == null) {
            return false;
        }

        for (int i = 0; i < winningCells.length; i++) {
            if (winningCells[i][0] == row && winningCells[i][1] == col) {
                return true;
            }
        }

        return false;
    }

    private void showBigMessage(String text) {
        JLabel message = new JLabel(text);
        message.setFont(new Font("Arial", Font.BOLD, 22));
        JOptionPane.showMessageDialog(this, message);
    }

    private void showTrophyMessage(String text) {
        TrophyPanel trophy = new TrophyPanel();

        JLabel winnerLabel = new JLabel(text, JLabel.CENTER);
        winnerLabel.setFont(new Font("Arial", Font.BOLD, 22));
        winnerLabel.setForeground(new Color(255, 140, 0));

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        trophy.setAlignmentX(Component.CENTER_ALIGNMENT);
        winnerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(trophy);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(winnerLabel);

        JOptionPane.showMessageDialog(this, panel, "Winner", JOptionPane.PLAIN_MESSAGE);
    }

    
    private void showCrownMessage(String text) {
    	CrownPanel crown = new CrownPanel();
    	
        JLabel winnerLabel = new JLabel(text, JLabel.CENTER);
        winnerLabel.setFont(new Font("Arial", Font.BOLD, 22));
        winnerLabel.setForeground(new Color(255, 214, 10));

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        crown.setAlignmentX(Component.CENTER_ALIGNMENT);
        winnerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(crown);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(winnerLabel);

        JOptionPane.showMessageDialog(this, panel, "Match Winner", JOptionPane.PLAIN_MESSAGE);
    }
    
    
    private void prepareSounds() {
        try {
        	File moveFile = new File("src/tictactoe/move.wav");
            if (moveFile.exists()) {
                AudioInputStream moveStream = AudioSystem.getAudioInputStream(moveFile);
                moveClip = AudioSystem.getClip();
                moveClip.open(moveStream);
            }

            // Load Win Sound (Optional but recommended!)
            File winFile = new File("src/tictactoe/win.wav");
            if (winFile.exists()) {
                AudioInputStream winStream = AudioSystem.getAudioInputStream(winFile);
                winClip = AudioSystem.getClip();
                winClip.open(winStream);
            }
            
            File drawFile = new File("src/tictactoe/draw.wav");
            if (drawFile.exists()) {
                AudioInputStream drawStream = AudioSystem.getAudioInputStream(drawFile);
                drawClip = AudioSystem.getClip();
                drawClip.open(drawStream);
            }
            
            File backgroundFile = new File("src/tictactoe/playing.wav");
            if (backgroundFile.exists()) {
                AudioInputStream backgroundStream = AudioSystem.getAudioInputStream(backgroundFile);
                backgroundClip = AudioSystem.getClip();
                backgroundClip.open(backgroundStream);
            }
        } catch (Exception e) {
            System.out.println("Sound loading failed: " + e.getMessage());
        }
    }

    private void playMoveSound() {
        if (moveClip != null) {
            moveClip.stop();
            moveClip.setFramePosition(0);
            moveClip.start();

            Timer stopTimer = new Timer(500, e -> {
                moveClip.stop();
            });

            stopTimer.setRepeats(false);
            stopTimer.start();
        }
    }
    
    private void playDrawSound() {
    	stopBackgroundMusic();

    	
    	if (drawClip != null) {
            drawClip.stop();
            drawClip.setFramePosition(0);
            drawClip.start();

            Timer stopTimer = new Timer(1500, e -> {
                drawClip.stop();
            });

            stopTimer.setRepeats(false);
            stopTimer.start();
        }
    }

    private void playWinSound() {
    	stopBackgroundMusic();
    	
    	if (winClip != null) {
    	        winClip.stop();
    	        winClip.setFramePosition(0);
    	        winClip.start();

    	        Timer stopTimer = new Timer(5000, e -> {
    	            winClip.stop();
    	        });

    	        stopTimer.setRepeats(false);
    	        stopTimer.start();
    	    }
    	}
    
    private void playBackgroundMusic() {
        if (backgroundClip != null) {
            backgroundClip.stop();
            backgroundClip.setFramePosition(0);
            backgroundClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    private void stopBackgroundMusic() {
        if (backgroundClip != null) {
            backgroundClip.stop();
        }
    }


    
    private void returnToMainMenu() {
        controller.resetMatch();
        showLoginDialog();
        controller.newGame();
        refreshTheme();
        playBackgroundMusic();
        startComputerIfNeeded();
    }

    public static void main(String[] args) {
        new SwingUI("board.csv");
    }
}

class SwingGameController {
    private Board board;
    private GameLogic logic;
    private String playerXName;
    private String playerOName;
    private String playerOneName;
    private String playerTwoName;
    private int playerOneScore;
    private int playerTwoScore;
    private int drawScore;
    private boolean onePlayerMode;
    private String matchMode;
    private boolean matchOver;
    private String themeName;
    private String aiDifficulty;

    public SwingGameController(String filename) {
        board = new Board(filename);
        logic = new GameLogic();
        playerXName = "Player X";
        playerOName = "Player O";
        playerOneName = "Player 1";
        playerTwoName = "Player 2";
        matchMode = "Free Play";
        themeName = "Classic";
        aiDifficulty = "Medium";
    }

    public void setGameSettings(boolean onePlayerMode, String matchMode) {
        this.onePlayerMode = onePlayerMode;
        this.matchMode = matchMode;
        this.matchOver = false;
    }

    public void setOriginalPlayerNames(String playerOneName, String playerTwoName) {
        this.playerOneName = playerOneName;
        this.playerTwoName = playerTwoName;
    }

    public String randomizeStartingPlayer() {
        if (Math.random() < 0.5) {
            playerXName = playerOneName;
            playerOName = playerTwoName;
        } else {
            playerXName = playerTwoName;
            playerOName = playerOneName;
        }

        return playerXName + " was randomly chosen to start as X!";
    }

    public boolean makeMove(int row, int col) {
        return logic.makeMove(board, row, col);
    }

    public char getCell(int row, int col) {
        return board.getCell(row, col);
    }

    public boolean isGameOver() {
        return logic.isGameOver(board);
    }

    public boolean isDraw() {
        return logic.isDraw(board);
    }

    public boolean isMatchOver() {
        return matchOver;
    }

    public void updateScore() {
        if (logic.checkWin(board, 'X')) {
            addScoreFor(playerXName);
        } else if (logic.checkWin(board, 'O')) {
            addScoreFor(playerOName);
        } else if (logic.isDraw(board)) {
            drawScore++;
        }

        checkMatchWinner();
    }

    private void addScoreFor(String name) {
        if (name.equals(playerOneName)) {
            playerOneScore++;
        } else if (name.equals(playerTwoName)) {
            playerTwoScore++;
        }
    }

    private void checkMatchWinner() {
        int winsNeeded = getWinsNeeded();

        if (winsNeeded == -1) {
            return;
        }

        if (playerOneScore >= winsNeeded || playerTwoScore >= winsNeeded) {
            matchOver = true;
        }
    }

    private int getWinsNeeded() {
        if (matchMode == null || matchMode.equals("Free Play")) {
            return -1;
        } else if (matchMode.equals("Best of 3")) {
            return 2;
        } else if (matchMode.equals("Best of 5")) {
            return 3;
        }

        return -1;
    }

    public String getStatusMessage() {
        if (logic.checkWin(board, 'X')) {
            return playerXName + " wins!";
        } else if (logic.checkWin(board, 'O')) {
            return playerOName + " wins!";
        } else if (logic.isDraw(board)) {
            return "Draw!";
        } else if (logic.getCurrentPlayer(board) == 'X') {
            return playerXName + "'s turn";
        } else {
            return playerOName + "'s turn";
        }
    }

    public String getScoreMessage() {
        return "<html><center>" +
               playerOneName + ": " + playerOneScore +
               "    " +
               playerTwoName + ": " + playerTwoScore +
               "    Draws: " + drawScore +
               "<br>" +
               "Mode: " + matchMode +
               "    Theme: " + themeName +
               "</center></html>";
    }

    public String getFinalScoreMessage() {
        return playerOneName + ": " + playerOneScore +
               " - " + playerTwoName + ": " + playerTwoScore +
               "  Draws: " + drawScore;
    }

    public String getMatchWinnerMessage() {
        if (playerOneScore > playerTwoScore) {
            return playerOneName + " wins the match!";
        } else if (playerTwoScore > playerOneScore) {
            return playerTwoName + " wins the match!";
        } else {
            return "The match is tied!";
        }
    }

    public void resetMatch() {
        playerOneScore = 0;
        playerTwoScore = 0;
        drawScore = 0;
        matchOver = false;
        board.clearBoard();
    }

    public void newGame() {
        board.clearBoard();
    }

    public void setThemeName(String themeName) {
        this.themeName = themeName;
    }

    public void setAiDifficulty(String aiDifficulty) {
        this.aiDifficulty = aiDifficulty;
    }

    public boolean isComputerTurn() {
        if (!onePlayerMode || logic.isGameOver(board)) {
            return false;
        }

        char currentPlayer = logic.getCurrentPlayer(board);

        if (currentPlayer == 'X') {
            return playerXName.equals("Computer");
        } else {
            return playerOName.equals("Computer");
        }
    }

    public void makeComputerMove() {
        if (!isComputerTurn()) {
            return;
        }

        int[] move;//e

        if (aiDifficulty.equals("Easy")) {
            move = findRandomOpenCell();
        } else if (aiDifficulty.equals("Hard")) {
            move = findBestMove();
        } else {
            move = findMediumMove();
        }

        if (move != null) {
            logic.makeMove(board, move[0], move[1]);
        }
    }

    private int[] findMediumMove() {
        char computer = logic.getCurrentPlayer(board);
        char opponent = computer == 'X' ? 'O' : 'X';

        int[] move = findWinningMove(computer);

        if (move == null) {
            move = findWinningMove(opponent);
        }

        if (move == null && board.getCell(1, 1) == 'E') {
            move = new int[]{1, 1};
        }

        if (move == null) {
            move = findFirstOpenCorner();
        }

        if (move == null) {
            move = findFirstOpenCell();
        }

        return move;
    }

    private int[] findRandomOpenCell() {
        int[][] openCells = new int[9][2];
        int count = 0;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (board.getCell(row, col) == 'E') {
                    openCells[count][0] = row;
                    openCells[count][1] = col;
                    count++;
                }
            }
        }

        if (count == 0) {
            return null;
        }

        int index = (int)(Math.random() * count);
        return new int[]{openCells[index][0], openCells[index][1]};
    }

    private int[] findBestMove() {
        char computer = logic.getCurrentPlayer(board);
        int bestScore = -1000;
        int[] bestMove = null;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (board.getCell(row, col) == 'E') {
                    board.getGrid()[row][col] = computer;

                    int score = minimax(false, computer);

                    board.getGrid()[row][col] = 'E';

                    if (score > bestScore) {
                        bestScore = score;
                        bestMove = new int[]{row, col};
                    }
                }
            }
        }

        return bestMove;
    }

    private int minimax(boolean isMaximizing, char computer) {
        char opponent = computer == 'X' ? 'O' : 'X';

        if (logic.checkWin(board, computer)) {
            return 10;
        }

        if (logic.checkWin(board, opponent)) {
            return -10;
        }

        if (logic.isDraw(board)) {
            return 0;
        }

        if (isMaximizing) {
            int bestScore = -1000;

            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 3; col++) {
                    if (board.getCell(row, col) == 'E') {
                        board.getGrid()[row][col] = computer;
                        int score = minimax(false, computer);
                        board.getGrid()[row][col] = 'E';

                        if (score > bestScore) {
                            bestScore = score;
                        }
                    }
                }
            }

            return bestScore;
        } else {
            int bestScore = 1000;

            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 3; col++) {
                    if (board.getCell(row, col) == 'E') {
                        board.getGrid()[row][col] = opponent;
                        int score = minimax(true, computer);
                        board.getGrid()[row][col] = 'E';

                        if (score < bestScore) {
                            bestScore = score;
                        }
                    }
                }
            }

            return bestScore;
        }
    }

    private int[] findWinningMove(char player) {
        char[][] grid = board.getGrid();

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (grid[row][col] == 'E') {
                    grid[row][col] = player;
                    boolean wins = logic.checkWin(board, player);
                    grid[row][col] = 'E';

                    if (wins) {
                        return new int[]{row, col};
                    }
                }
            }
        }

        return null;
    }

    private int[] findFirstOpenCorner() {
        int[][] corners = {
            {0, 0},
            {0, 2},
            {2, 0},
            {2, 2}
        };

        for (int i = 0; i < corners.length; i++) {
            int row = corners[i][0];
            int col = corners[i][1];

            if (board.getCell(row, col) == 'E') {
                return new int[]{row, col};
            }
        }

        return null;
    }
    
    public String getTokenLetter(char piece) {
        String name;

        if (piece == 'X') {
            name = playerXName;
        } else if (piece == 'O') {
            name = playerOName;
        } else {
            return "";
        }

        if (name.length() == 0) {
            return "";
        }

        if (name.length() == 1) {
            return name.substring(0, 1).toUpperCase();
        }

        return name.substring(0, 2).toUpperCase();
    }



    private int[] findFirstOpenCell() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (board.getCell(row, col) == 'E') {
                    return new int[]{row, col};
                }
            }
        }

        return null;
    }

    public int[][] getWinningCells() {
        char winner = 'E';

        if (logic.checkWin(board, 'X')) {
            winner = 'X';
        } else if (logic.checkWin(board, 'O')) {
            winner = 'O';
        } else {
            return null;
        }

        for (int row = 0; row < 3; row++) {
            if (board.getCell(row, 0) == winner &&
                board.getCell(row, 1) == winner &&
                board.getCell(row, 2) == winner) {
                return new int[][]{{row, 0}, {row, 1}, {row, 2}};
            }
        }

        for (int col = 0; col < 3; col++) {
            if (board.getCell(0, col) == winner &&
                board.getCell(1, col) == winner &&
                board.getCell(2, col) == winner) {
                return new int[][]{{0, col}, {1, col}, {2, col}};
            }
        }

        if (board.getCell(0, 0) == winner &&
            board.getCell(1, 1) == winner &&
            board.getCell(2, 2) == winner) {
            return new int[][]{{0, 0}, {1, 1}, {2, 2}};
        }

        if (board.getCell(0, 2) == winner &&
            board.getCell(1, 1) == winner &&
            board.getCell(2, 0) == winner) {
            return new int[][]{{0, 2}, {1, 1}, {2, 0}};
        }

        return null;
    }
}

class ConfettiPanel extends JPanel {
    private int[] xPositions;
    private int[] yPositions;
    private int[] speeds;
    private Color[] colors;
    private Timer timer;
    private Random random;

    public ConfettiPanel() {
        random = new Random();

        int count = 80;
        xPositions = new int[count];
        yPositions = new int[count];
        speeds = new int[count];
        colors = new Color[count];

        Color[] possibleColors = {
            Color.RED,
            Color.ORANGE,
            Color.YELLOW,
            Color.GREEN,
            Color.CYAN,
            Color.BLUE,
            Color.MAGENTA
        };

        for (int i = 0; i < count; i++) {
            xPositions[i] = random.nextInt(460);
            yPositions[i] = random.nextInt(540) - 540;
            speeds[i] = random.nextInt(5) + 2;
            colors[i] = possibleColors[random.nextInt(possibleColors.length)];
        }

        timer = new Timer(30, e -> updateConfetti());
    }

    public void start() {
        setVisible(true);
        timer.start();
    }

    public void stop() {
        timer.stop();
        setVisible(false);
    }

    private void updateConfetti() {
        for (int i = 0; i < yPositions.length; i++) {
            yPositions[i] += speeds[i];

            if (yPositions[i] > getHeight()) {
                yPositions[i] = -10;
                xPositions[i] = random.nextInt(Math.max(getWidth(), 1));
            }
        }

        repaint();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (int i = 0; i < xPositions.length; i++) {
            g2.setColor(colors[i]);

            if (i % 2 == 0) {
                g2.fillRect(xPositions[i], yPositions[i], 8, 12);
            } else {
                g2.fillOval(xPositions[i], yPositions[i], 10, 10);
            }
        }
    }

}

class SquarePanel extends JPanel {
    private JPanel boardPanel;

    public SquarePanel(JPanel boardPanel, Color backgroundColor) {
        this.boardPanel = boardPanel;
        setBackground(backgroundColor);
        setLayout(new GridLayout(1, 1));
        setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        add(boardPanel);
    }

    public Dimension getPreferredSize() {
        return new Dimension(380, 380);
    }
}

class CrownPanel extends JPanel {
    public CrownPanel() {
        setPreferredSize(new Dimension(300, 160));
        setOpaque(false);
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        int crownWidth = 220;
        int crownHeight = 100;
        int x = (width - crownWidth) / 2;
        int y = 25;

        int[] xPoints = {
            x,
            x + 35,
            x + 70,
            x + 110,
            x + 150,
            x + 185,
            x + 220,
            x + 205,
            x + 15
        };

        int[] yPoints = {
            y + crownHeight,
            y + 35,
            y + crownHeight,
            y + 20,
            y + crownHeight,
            y + 35,
            y + crownHeight,
            y + crownHeight + 25,
            y + crownHeight + 25
        };

        g2.setColor(new Color(255, 214, 10));
        g2.fillPolygon(xPoints, yPoints, xPoints.length);

        g2.setColor(new Color(180, 120, 0));
        g2.drawPolygon(xPoints, yPoints, xPoints.length);

        g2.setColor(new Color(255, 80, 80));
        g2.fillOval(x + 27, y + 28, 16, 16);
        g2.fillOval(x + 102, y + 13, 16, 16);
        g2.fillOval(x + 177, y + 28, 16, 16);

        g2.setColor(new Color(255, 245, 170));
        g2.fillRect(x + 35, y + crownHeight + 2, 150, 12);
    }
}

class TrophyPanel extends JPanel {
    public TrophyPanel() {
        setPreferredSize(new Dimension(260, 180));
        setOpaque(false);
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();

        int cupX = width / 2 - 45;
        int cupY = 35;

        g2.setColor(new Color(255, 214, 10));
        g2.fillRoundRect(cupX, cupY, 90, 70, 20, 20);

        g2.setColor(new Color(220, 160, 0));
        g2.drawRoundRect(cupX, cupY, 90, 70, 20, 20);
        
        g2.setColor(new Color(255, 214, 10));
        g2.setStroke(new BasicStroke(5));
        g2.drawArc(cupX - 45, cupY + 10, 50, 45, 90, 180);
        g2.drawArc(cupX + 85, cupY + 10, 50, 45, -90, 180);
        g2.setStroke(new BasicStroke(1));


        g2.setColor(new Color(255, 230, 120));
        g2.fillOval(cupX + 18, cupY + 15, 25, 12);

        g2.setColor(new Color(180, 120, 0));
        g2.fillRect(cupX + 35, cupY + 70, 20, 35);

        g2.setColor(new Color(80, 55, 45));
        g2.fillRoundRect(cupX + 15, cupY + 105, 60, 18, 8, 8);
    }
}

class CheckerButton extends JButton {
	private char piece = 'E';
	private String tokenText = "";
    private Color xColor = Color.ORANGE;
    private Color oColor = Color.BLUE;

    public void setPiece(char piece, Color xColor, Color oColor, String tokenText) {
        this.piece = piece;
        this.xColor = xColor;
        this.oColor = oColor;
        this.tokenText = tokenText;
        repaint();
    }


    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (piece == 'E') {
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int size = Math.min(getWidth(), getHeight()) - 24;
        int x = (getWidth() - size) / 2;
        int y = (getHeight() - size) / 2;

        if (piece == 'X') {
            g2.setColor(xColor);
        } else {
            g2.setColor(oColor);
        }

        g2.fillOval(x, y, size, size);

        g2.setColor(new Color(255, 255, 255, 120));
        g2.fillOval(x + size / 5, y + size / 6, size / 3, size / 5);

        g2.setColor(new Color(0, 0, 0, 80));
        g2.drawOval(x, y, size, size);
        g2.drawOval(x + 6, y + 6, size - 12, size - 12);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, size / 3));

        String text = tokenText;
        int textWidth = g2.getFontMetrics().stringWidth(text);
        int textHeight = g2.getFontMetrics().getAscent();

        g2.drawString(
            text,
            getWidth() / 2 - textWidth / 2,
            getHeight() / 2 + textHeight / 3
        );
    }
}

