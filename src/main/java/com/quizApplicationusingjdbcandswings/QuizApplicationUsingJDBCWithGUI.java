package com.quizApplicationusingjdbcandswings;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
public class QuizApplicationUsingJDBCWithGUI extends JPanel {
    private static final long serialVersionUID = 1L;
    private Model m;
    private Runnable onBack;
    private ArrayList<Model.QuizQuestion> questions;
    private int currentQuestion = 0;
    private int score = 0;
    private int[] rewards = {
        100, 200, 300, 500, 1000,
        2000, 4000, 8000, 16000, 32000};
    private boolean fiftyFiftyUsed = false;
    private boolean audienceUsed = false;
    private boolean lifelineUsedForCurrentQuestion = false;

    private JLabel questionLabel;
    private JLabel scoreLabel;
    private JLabel progressLabel;

    private JButton[] optionButtons = new JButton[4];

    private JButton btn5050;
    private JButton btnAudience;

    public QuizApplicationUsingJDBCWithGUI(
        Model m,
        Runnable onBack
    ) {
        this.m = m;
        this.onBack = onBack;
        questions = m.getQuestions();

        setLayout(new BorderLayout());
        setBackground(new Color(235, 245, 255));

        setBorder(new EmptyBorder(20, 30, 20, 30));
        createGUI();
        if (questions == null || questions.isEmpty()) {
            JOptionPane.showMessageDialog(
                this, "No questions available in database!"
            );
            SwingUtilities.invokeLater(onBack);

        } else {

            loadQuestion();
        }
    }
    // Create quiz GUI
    private void createGUI() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(
            new BoxLayout(mainPanel, BoxLayout.Y_AXIS)
        );
        mainPanel.setBackground(
            new Color(235, 245, 255)
        );
        // Top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground( new Color(235, 245, 255));

        JButton backButton = new JButton("Back to Menu");
        backButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                this,
                "Do you want to leave the quiz?",
                "Confirm",
                JOptionPane.YES_NO_OPTION
            );

            if (choice == JOptionPane.YES_OPTION) {
                onBack.run();
            }
        });

        progressLabel = new JLabel("Question");

        progressLabel.setFont(
            new Font("Arial", Font.BOLD, 16)
        );

        topPanel.add(backButton, BorderLayout.WEST);
        topPanel.add(progressLabel, BorderLayout.EAST);

        mainPanel.add(topPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Question section
        JPanel questionPanel = new JPanel(new BorderLayout());

        questionPanel.setBackground(Color.WHITE);

        questionPanel.setBorder(
            BorderFactory.createTitledBorder("Question")
        );

        questionLabel = new JLabel("Question appears here");

        questionLabel.setFont(
            new Font("Arial", Font.BOLD, 18)
        );
        questionLabel.setBorder(
            new EmptyBorder(15, 10, 15, 10)
        );
        questionPanel.add(
            questionLabel,
            BorderLayout.CENTER
        );
        questionPanel.setPreferredSize(
            new Dimension(600, 120)
        );
        questionPanel.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 140)
        );
        mainPanel.add(questionPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        // Options section
        JPanel optionsPanel = new JPanel(
            new GridLayout(4, 1, 10, 10)
        );
        optionsPanel.setBackground(
            new Color(235, 245, 255)
        );
        optionsPanel.setBorder(
            BorderFactory.createTitledBorder("Options")
        );
        for (int i = 0; i < 4; i++) {
            optionButtons[i] = new JButton();
            optionButtons[i].setFont(
                new Font("Arial", Font.PLAIN, 16)
            );
            optionButtons[i].setHorizontalAlignment(
                SwingConstants.LEFT
            );

            final int selectedOption = i + 1;

            optionButtons[i].addActionListener(
                e -> checkAnswer(selectedOption)
            );
            optionsPanel.add(optionButtons[i]);
        }
        mainPanel.add(optionsPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Bottom panel
        JPanel bottomPanel = new JPanel(
            new BorderLayout()
        );
        bottomPanel.setBackground(
            new Color(235, 245, 255)
        );
        scoreLabel = new JLabel("Current Score: Rs. 0");
        scoreLabel.setFont(
            new Font("Arial", Font.BOLD, 16)
        );
        bottomPanel.add(
            scoreLabel,
            BorderLayout.WEST
        );

        // Lifeline buttons
        JPanel lifelinePanel = new JPanel();

        lifelinePanel.setBackground(
            new Color(235, 245, 255)
        );
        btn5050 = new JButton("50:50");

        btnAudience = new JButton("Audience Poll");

        btn5050.addActionListener(e -> use5050());

        btnAudience.addActionListener(
            e -> useAudiencePoll()
        );

        lifelinePanel.add(btn5050);
        lifelinePanel.add(btnAudience);

        bottomPanel.add(
            lifelinePanel,
            BorderLayout.EAST
        );

        mainPanel.add(bottomPanel);

        add(mainPanel, BorderLayout.CENTER);
    }

    // Load question
    private void loadQuestion() {

        if (currentQuestion >= questions.size()) {

          finishQuiz();
            
            return;
        }

        lifelineUsedForCurrentQuestion = false;

        Model.QuizQuestion q =
            questions.get(currentQuestion);

        progressLabel.setText(
            "Question " + (currentQuestion + 1)
            + " / " + questions.size()
        );

        questionLabel.setText(
            "<html><body style='width:520px'>Q"
            + (currentQuestion + 1)
            + ". " + q.questionText
            + "</body></html>"
        );

        for (int i = 0; i < 4; i++) {

            optionButtons[i].setText(
                (i + 1) + ". " + q.options[i]
            );

            optionButtons[i].setVisible(true);
            optionButtons[i].setEnabled(true);
        }

        btn5050.setEnabled(
            !fiftyFiftyUsed && currentQuestion < 7
        );

        btnAudience.setEnabled(
            !audienceUsed && currentQuestion < 7
        );

        scoreLabel.setText(
            "Current Score: Rs. " + score
        );

        revalidate();
        repaint();
    }

    // Check answer
 // Check answer
    private void checkAnswer(int selectedAnswer) {

        if (currentQuestion >= questions.size()) {
            return;
        }

        int correctAnswer =
            questions.get(currentQuestion).correctAnswer;

        if (selectedAnswer == correctAnswer) {

            int reward;

            if (currentQuestion < rewards.length) {

                // Old questions: Previous rewards
                reward = rewards[currentQuestion];

            } else {

                // New questions: Add Rs. 1000
                reward = rewards[rewards.length - 1]
                         + ((currentQuestion - rewards.length + 1) * 1000);
            }

            score = reward;

            JOptionPane.showMessageDialog(
                this,
                "Correct Answer!\nYou won Rs. " + reward
            );

        } else {

            if (currentQuestion < 4) {
                score = 0;
            }

            JOptionPane.showMessageDialog(
                this,
                "Wrong Answer!\nCorrect Answer is Option "
                + correctAnswer
            );
        }

        currentQuestion++;

        loadQuestion();
    }
    // 50:50 lifeline
    private void use5050() {

        if (fiftyFiftyUsed ||
            lifelineUsedForCurrentQuestion) {
            return;
        }
        fiftyFiftyUsed = true;
        lifelineUsedForCurrentQuestion = true;
        int correctAnswer =
            questions.get(currentQuestion).correctAnswer;
        int hiddenCount = 0;
        for (int i = 0; i < 4; i++) {
            if (i + 1 != correctAnswer &&
                hiddenCount < 2) {
                optionButtons[i].setVisible(false);
                hiddenCount++;
            }
        }
        btn5050.setEnabled(false);
        btnAudience.setEnabled(false);
    }
    // Audience Poll
    private void useAudiencePoll() {

        if (audienceUsed ||
            lifelineUsedForCurrentQuestion) {
            return;
        }
        audienceUsed = true;
        lifelineUsedForCurrentQuestion = true;
        int correctAnswer =
            questions.get(currentQuestion).correctAnswer;
        StringBuilder poll = new StringBuilder();
        for (int i = 1; i <= 4; i++) {

            int percentage =
                (i == correctAnswer) ? 70 : 10;

            poll.append(
                "Option " + i + " : "
                + percentage + "%\n"
            );
        }
        JOptionPane.showMessageDialog(
            this,
            poll.toString(),
            "Audience Poll",
            JOptionPane.INFORMATION_MESSAGE
        );
        btn5050.setEnabled(false);
        btnAudience.setEnabled(false);
    }
    // Finish quiz

    // Finish quiz and save result
    private void finishQuiz() {
        String playerName = JOptionPane.showInputDialog(
            this,
            "Enter Your Name:"
        );
        // If user clicks Cancel
        if (playerName == null) {
            JOptionPane.showMessageDialog(
                this,
                "Result was not saved."
            );
            onBack.run();
            return;
        }
        // Check empty name
        if (playerName.trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                this,
                "Please enter your name!" );
            onBack.run();
            return;
        }
        // Save result into database
        boolean status = m.saveScore(
            playerName.trim(),
            currentQuestion,
            score
        );
        // Display final result
        JOptionPane.showMessageDialog(
            this,
            "Quiz Completed!\n\n"
            + "Name: " + playerName.trim()
            + "\nQuestions Attempted: " + currentQuestion
            + "\nFinal Score: Rs. " + score
            + "\n\n"
            + (status
                ? "Result Saved Successfully!"
                : "Unable to Save Result!"),
            "Quiz Result",
            JOptionPane.INFORMATION_MESSAGE
        );

        onBack.run();
    }
}
