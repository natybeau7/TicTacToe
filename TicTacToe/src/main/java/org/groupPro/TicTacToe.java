package org.groupPro;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

class TicTacToe extends Game implements ActionListener {

    JFrame frame = new JFrame();
    JPanel title_panel = new JPanel();
    JPanel button_panel = new JPanel();
    JPanel bottom_panel = new JPanel();

    JLabel textfield = new JLabel();
    JLabel timerLabel = new JLabel();
    JButton pauseButton = new JButton("Pause");
    JButton settingsButton = new JButton("Settings");
    JButton[] buttons = new JButton[9];

    ImageIcon icon = new ImageIcon(getClass().getResource("TicTacToe.png"));

    boolean player1_turn;
    boolean gameOver = false;
    boolean vsAI = false;
    boolean soundOn = true;

    String difficulty;
    String player1Symbol ;
    String player2Symbol;
    int timeLeft = 11;
    Timer timer;


    public TicTacToe() {
     
        String[] mainOptions = {"Play with Friend", "Play with AI"};
        int mainChoice = JOptionPane.showOptionDialog(frame, "Choose Game Mode", "TicTacToe",
                JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, icon, mainOptions, mainOptions[0]);

        if (mainChoice == 1) {
            vsAI = true;
            difficultySelection();
        }
        chooseSymbol();


        setupGUI();
        startTimer();
        firstTurn();
    }

    private void difficultySelection() {
        String[] diffOptions = {"Easy", "Medium"};
        int choice2 = JOptionPane.showOptionDialog(frame, "Select Difficulty", "AI",
                JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, icon, diffOptions, diffOptions[0]);
        difficulty = (choice2 == 0) ? "Easy" : "Medium";
    }

    private void setupGUI() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 650);
        frame.setLayout(new BorderLayout());
        frame.setIconImage(icon.getImage());
        frame.getContentPane().setBackground(new Color(40,40,40));

        textfield.setBackground(new Color(20,20,20));
        textfield.setForeground(new Color(0,255,120));
        textfield.setFont(new Font("Arial", Font.BOLD, 40));
        textfield.setHorizontalAlignment(JLabel.CENTER);
        textfield.setText("Tic-Tac-Toe");
        textfield.setOpaque(true);

        timerLabel.setText("Time: 10");
        timerLabel.setForeground(Color.WHITE);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 20));

        pauseButton.setFocusable(false);
        pauseButton.addActionListener(e -> showPauseMenu());
        settingsButton.setFocusable(false);
        settingsButton.addActionListener(e -> showSettingsMenu());

        title_panel.setLayout(new BorderLayout());
        button_panel.setLayout(new GridLayout(3,3,5,5));
        button_panel.setBackground(Color.BLACK);
        bottom_panel.setLayout(new FlowLayout(FlowLayout.CENTER, 50, 10));
        bottom_panel.setBackground(new Color(30,30,30));

        for(int i = 0; i < 9; i++) {
            buttons[i] = new JButton();
            button_panel.add(buttons[i]);
            buttons[i].setFocusable(false);
            buttons[i].setFont(new Font("Arial", Font.BOLD, 80));
            buttons[i].setBackground(Color.WHITE);
            buttons[i].addActionListener(this);
        }

        title_panel.add(textfield);
        bottom_panel.add(timerLabel);
        bottom_panel.add(pauseButton);
        bottom_panel.add(settingsButton);

        frame.add(title_panel, BorderLayout.NORTH);
        frame.add(button_panel, BorderLayout.CENTER);
        frame.add(bottom_panel, BorderLayout.SOUTH);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    private void chooseSymbol() {
        String[] options = {"X", "O"};
        int choice = JOptionPane.showOptionDialog(frame,
                "Player : Choose your symbol",
                "Symbol Selection",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
               icon,
                options,
                options[0]);

        if (choice == 1) {
            player1Symbol = "O";
            player2Symbol = "X";
        } else {
            player1Symbol = "X";
            player2Symbol = "O";
        }
    }
    private void showPauseMenu() {
        if (timer != null)
            timer.stop();

        String[] options = {"Restart Game", "Main Menu", "Exit", "Resume"};
        int choice = JOptionPane.showOptionDialog(frame, "Game Paused", "Pause",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, icon, options, options[3]);

        if (choice == 0)
            restartGame();
        else if (choice == 1)
            resetToMainMenu();
        else if (choice == 2)
            System.exit(0);
        else {
            if (!gameOver) timer.start();
        }
    }

    public void resetToMainMenu() {
        if(timer != null) timer.stop();
        frame.dispose();
        new TicTacToe();
    }

    public void startTimer() {
        if(timer != null)
            timer.stop();
        timeLeft = 10;
        timer = new Timer(1000, e -> {
            timeLeft--;
            timerLabel.setText("Time: " + timeLeft);
            if(timeLeft <= 0) {
                timer.stop();
                gameOver = true;
                if(soundOn)
                SoundPlayer.playSound("timeout.wav");
                textfield.setText("Time Out! Game Over");
                disableButtons();
                showRestart();
            }
        });
        timer.start();
    }


    @Override
    public void actionPerformed(ActionEvent e) {

        if(gameOver)
            return;
        if(vsAI && !player1_turn)
            return;

        for(int i = 0; i < 9; i++) {

            if(e.getSource() == buttons[i]) {

                if(buttons[i].getText().equals("")) {
                    if(soundOn)
                    SoundPlayer.playSound("click.wav");

         
                    if(player1_turn) {

                        buttons[i].setText(player1Symbol);

                        player1_turn = false;

                        textfield.setText(player2Symbol + " turn");

                        timeLeft = 10;

                        check();

                        
                        if(vsAI && !gameOver) {
                            Timer aiDelay = new Timer(2500, new ActionListener() {

                                @Override
                                public void actionPerformed(ActionEvent e) {

                                    if(!gameOver) {
                                        if(soundOn)
                                        SoundPlayer.playSound("click.wav");
                                        aiMove();
                                    }
                                }
                            });

                            aiDelay.setRepeats(false);
                            aiDelay.start();
                        }
                    }

                    else {

                        if(!vsAI) {

                            buttons[i].setText(player2Symbol);

                            player1_turn = true;

                            textfield.setText(player1Symbol + " turn");

                            timeLeft = 10;

                            check();
                        }
                    }
                }
            }
        }
    }
    public void showSettingsMenu() {
        if (timer != null)
            timer.stop();
        String soundStatus;

        if(soundOn) {
            soundStatus = "Sound: ON";
        }
        else {
            soundStatus = "Sound: OFF";
        }

        String[] options = {soundStatus, "Resume"};

        int choice = JOptionPane.showOptionDialog(
                frame,
                "Game Settings",
                "Settings",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                icon,
                options,
                options[0]
        );

        if(choice == 0) {

            soundOn = !soundOn;

            if(soundOn) {
                JOptionPane.showMessageDialog(frame, "Sound Enabled");
            }
            else {
                JOptionPane.showMessageDialog(frame, "Sound Disabled");
            }
        }
        timer.start();
    }
    @Override
    public void check() {
        int[][] comb = {
                {0,1,2},{3,4,5},{6,7,8},
                {0,3,6},{1,4,7},{2,5,8},
                {0,4,8},{2,4,6}};
        for(int i = 0; i < comb.length; i++) {
                int a = comb[i][0],
                    b = comb[i][1],
                    c = comb[i][2];
            if(!buttons[a].getText().equals("") &&
                    buttons[a].getText().equals(buttons[b].getText()) &&
                    buttons[a].getText().equals(buttons[c].getText()))
            {

                if(buttons[a].getText().equals("X"))
                    xWins(a, b, c);
                else
                    oWins(a, b, c);
                return;
            }
        }
        checkDraw();
    }

    public void xWins(int a, int b, int c) {
        if(soundOn)
        SoundPlayer.playSound("win.wav");
        setWinner(a, b, c, "X Wins!");
    }
    public void oWins(int a, int b, int c) {
        if(soundOn)
        SoundPlayer.playSound("win.wav");
        setWinner(a, b, c, "O Wins!");
    }

    private void setWinner(int a, int b, int c, String text) {
        buttons[a].setBackground(Color.GREEN);
        buttons[b].setBackground(Color.GREEN);
        buttons[c].setBackground(Color.GREEN);
        textfield.setText(text);
        gameOver = true;
        if(timer != null) timer.stop();
        disableButtons();
        showRestart();
    }

    public void checkDraw() {
        boolean full = true;
        for(int i = 0; i < 9; i++) {
            if(buttons[i].getText().equals(""))
                full = false;
        }
        if(full && !gameOver) {
            if(timer != null) timer.stop();
            textfield.setText("Draw!");
            gameOver = true;
            if(soundOn)
            SoundPlayer.playSound("draw.wav");
            showRestart();
        }
    }

    public void disableButtons() {
        for(int i = 0; i < 9; i++) {
            buttons[i].setEnabled(false);
        }
    }

    public void showRestart() {
        String[] options = {"Restart", "Main Menu", "Exit"};
        int choice = JOptionPane.showOptionDialog(frame, "Play Again?", "Game Over",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, icon, options, options[0]);
        if(choice == 0) restartGame();
        else if(choice == 1) resetToMainMenu();
        else System.exit(0);
    }

    @Override
    public void restartGame() {
        for(int i = 0; i < 9; i++) {
            buttons[i].setText("");
            buttons[i].setEnabled(true);
            buttons[i].setBackground(Color.WHITE);
        }
        gameOver = false;
        startTimer(); 
        firstTurn();
    }

    public void firstTurn() {
        player1_turn = true;
        textfield.setText(player1Symbol + " turn");
    }

    public void aiMove() {
        if(difficulty.equals("Easy")) randomMove();
        else {
            if(!tryWin(player2Symbol))
            if(!tryWin(player1Symbol))
                randomMove(); }
    }

    public int randomMove() {
        Random rand = new Random();
        int i;
        do { i = rand.nextInt(9); }
        while(!buttons[i].getText().equals(""));
        buttons[i].setText(player2Symbol);
        player1_turn = true;
        textfield.setText(player1Symbol + " turn");
        timeLeft = 10;
        check();
        return i;
    }

    public boolean tryWin(String symbol) {
        int[][] comb = {{0,1,2},{3,4,5},{6,7,8},
                {0,3,6},{1,4,7},{2,5,8},
                {0,4,8},{2,4,6}
        };
        for(int i = 0; i < comb.length; i++) {
            int count = 0, emptyIndex = -1;
            for(int j = 0; j < 3; j++) {
                int index = comb[i][j];
                if(buttons[index].getText().equals(symbol)) count++;
                else if(buttons[index].getText().equals("")) emptyIndex = index;
            }
            if(count == 2 && emptyIndex != -1) {
                buttons[emptyIndex].setText(player2Symbol);
                player1_turn = true;
                textfield.setText(player1Symbol + " turn");
                timeLeft = 10;
                check();
                return true;
            }
        }
        return false;
    }
}