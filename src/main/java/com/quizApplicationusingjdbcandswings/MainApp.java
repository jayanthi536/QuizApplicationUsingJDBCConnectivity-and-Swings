package com.quizApplicationusingjdbcandswings;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
public class MainApp extends JFrame {
    private static final long serialVersionUID = 1L;
    private Model m;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    public MainApp() {
        m = new Model();
        if (!m.isConnected()) {
            JOptionPane.showMessageDialog(
                null,
                "Database connection failed!\n"+ "Please check Credentials.java and MySQL."
            );
            return;
        }
        setTitle("JDBC Quiz Application");
        setSize(750, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(
            JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(
                java.awt.event.WindowEvent e
            ) {
                exitApplication();
            }
        });
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        // Add Main Menu
        mainPanel.add(
            createMenuScreen(),"MENU"
        );
        add(mainPanel);
        cardLayout.show(mainPanel, "MENU");
        setVisible(true);
    }
    private JPanel createMenuScreen() {
        JPanel panel = new JPanel(
            new BorderLayout(20, 20)
        );
        panel.setBackground(
            new Color(235, 245, 255)
        );
        panel.setBorder(
            new EmptyBorder(35, 100, 35, 100)
        );
        // Heading
        JLabel title = new JLabel(
            "JDBC QUIZ APPLICATION",
            JLabel.CENTER
        );
        title.setFont(
            new Font("Arial", Font.BOLD, 28)
        );
        title.setForeground(
            new Color(25, 55, 90)
        );
        panel.add(title, BorderLayout.NORTH);
        // Menu buttons
        JPanel buttonPanel = new JPanel(
            new GridLayout(6, 1, 15, 15)
        );
        buttonPanel.setBackground(
            new Color(235, 245, 255)
        );
        String[] menuOptions = {
            "1. Start Quiz",
            "2. Add New Question",
            "3. View Question By ID",
            "4. Update Question",
            "5. Delete Question",
            "6. Exit"
        };
        for (String option : menuOptions) {
            JButton button = new JButton(option);
            button.setFont(new Font("Arial", Font.BOLD, 18));
            button.setBackground(new Color(30, 50, 80));
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            buttonPanel.add(button);
            button.addActionListener(e -> {
                switch (option) {
                    case "1. Start Quiz":
                        startQuiz();
                        break;
                    case "2. Add New Question":
                        addQuestion();
                        break;
                    case "3. View Question By ID":
                        viewQuestion();
                        break;
                    case "4. Update Question":
                        updateQuestion();
                        break;
                    case "5. Delete Question":
                        deleteQuestion();
                        break;
                    case "6. Exit":
                        exitApplication();
                        break;
                }
            });
        }
        panel.add(buttonPanel,BorderLayout.CENTER);
        return panel;
    }
    
    private void startQuiz() {

        // Remove previous quiz screen if present
        Component[] components = mainPanel.getComponents();

        for (Component component : components) {

            if ("QUIZ".equals(component.getName())) {
                mainPanel.remove(component);
            }
        }
        // Create Quiz Panel
        QuizApplicationUsingJDBCWithGUI quiz =
            new QuizApplicationUsingJDBCWithGUI(
                m,
                () -> cardLayout.show(mainPanel, "MENU")
            );

        quiz.setName("QUIZ");

        mainPanel.add(quiz, "QUIZ");

        cardLayout.show(mainPanel, "QUIZ");

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void addQuestion() {

        JTextField question = new JTextField();
        JTextField op1 = new JTextField();
        JTextField op2 = new JTextField();
        JTextField op3 = new JTextField();
        JTextField op4 = new JTextField();
        JTextField answer = new JTextField();

        Object[] fields = {
            "Enter Question:", question,
            "Option 1:", op1,
            "Option 2:", op2,
            "Option 3:", op3,
            "Option 4:", op4,
            "Correct Answer (1-4):", answer
        };

        int result = JOptionPane.showConfirmDialog(
            this,
            fields, "Add New Question",
            JOptionPane.OK_CANCEL_OPTION
        );
        if (result != JOptionPane.OK_OPTION) {
            return;
        }
        try {
            String q = question.getText().trim();
            String o1 = op1.getText().trim();
            String o2 = op2.getText().trim();
            String o3 = op3.getText().trim();
            String o4 = op4.getText().trim();
            int correctAnswer =
                Integer.parseInt(answer.getText().trim());
            if (q.isEmpty()
                || o1.isEmpty()
                || o2.isEmpty()
                || o3.isEmpty()
                || o4.isEmpty()
                || correctAnswer < 1
                || correctAnswer > 4) {
                JOptionPane.showMessageDialog(
                    this,
                    "Please enter all details correctly!"
                );
                return;
            }
            boolean status = m.addQuestion(
                q, o1, o2, o3, o4, correctAnswer
            );
            JOptionPane.showMessageDialog(
                this,
                status
                ? "Question Added Successfully!"
                : "Question Not Added!"
            );
        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                this,
                "Correct Answer must be a number between 1 and 4!"
            );
        }}

    private void viewQuestion() {

        String input = JOptionPane.showInputDialog(
            this,
            "Enter Question ID:"
        );

        if (input == null) {
            return;
        }
        try {
            int id = Integer.parseInt(input.trim());
            Model.QuizQuestion q =
                m.viewQuestionByID(id);
            if (q == null) {
                JOptionPane.showMessageDialog(
                    this,
                    "Question ID Not Found!"
                );
                return;
            }
            String details =
                "Question ID: " + q.questionId
                + "\n\nQuestion: " + q.questionText
                + "\n\n1. " + q.options[0]
                + "\n2. " + q.options[1]
                + "\n3. " + q.options[2]
                + "\n4. " + q.options[3];
//                + "\n\nCorrect Answer: " + q.correctAnswer;
            JOptionPane.showMessageDialog(
                this,
                details,
                "Question Details",
                JOptionPane.INFORMATION_MESSAGE
            );
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                this,
                "Enter a valid Question ID!"
            );
        }
    }

    private void updateQuestion() {

        String input = JOptionPane.showInputDialog(
            this,"Enter Question ID to Update:"
        );
        if (input == null) {
            return;
        }
        try {
            int id = Integer.parseInt(input.trim());
            Model.QuizQuestion q =
                m.viewQuestionByID(id);
            if (q == null) {
                JOptionPane.showMessageDialog(
                    this,
                    "Question ID Not Found!"
                );
                return;
            }
            JTextField question =
                new JTextField(q.questionText);

            JTextField op1 =
                new JTextField(q.options[0]);

            JTextField op2 =
                new JTextField(q.options[1]);

            JTextField op3 =
                new JTextField(q.options[2]);

            JTextField op4 =
                new JTextField(q.options[3]);

            JTextField answer =
                new JTextField(
                    String.valueOf(q.correctAnswer)
                );

            Object[] fields = {
                "Question:", question,
                "Option 1:", op1,
                "Option 2:", op2,
                "Option 3:", op3,
                "Option 4:", op4,
                "Correct Answer (1-4):", answer
            };

            int result = JOptionPane.showConfirmDialog(
                this,
                fields,
                "Update Question",
                JOptionPane.OK_CANCEL_OPTION
            );

            if (result != JOptionPane.OK_OPTION) {
                return;
            }

            String qText = question.getText().trim();
            String o1 = op1.getText().trim();
            String o2 = op2.getText().trim();
            String o3 = op3.getText().trim();
            String o4 = op4.getText().trim();

            int correctAnswer =
                Integer.parseInt(answer.getText().trim());

            if (qText.isEmpty()
                || o1.isEmpty()
                || o2.isEmpty()
                || o3.isEmpty()
                || o4.isEmpty()
                || correctAnswer < 1
                || correctAnswer > 4) {

                JOptionPane.showMessageDialog(
                    this,
                    "Please enter all details correctly!"
                );

                return;
            }
            boolean status = m.updateQuestion(id, qText,o1, o2, o3,o4,correctAnswer  );
            JOptionPane.showMessageDialog(
                this,
                status
                ? "Question Updated Successfully!"
                : "Question Not Updated!"
            );

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                this,
                "Enter valid ID and Correct Answer!"
            );
        }
    }

    private void deleteQuestion() {

        // Step 1: Enter Question ID
        String input = JOptionPane.showInputDialog(
            this, "Enter Question ID to Delete:");
        if (input == null) {
            return;
        }
        try {
            int id = Integer.parseInt(input.trim());
            // Step 2: Fetch question from database
            Model.QuizQuestion q = m.viewQuestionByID(id);
            if (q == null) {
                JOptionPane.showMessageDialog(
                    this,
                    "Question ID Not Found!"
                );
                return;
            }

            // Step 3: Display question and options
            String details =
                "Question ID: " + q.questionId
                + "\n\nQuestion: " + q.questionText
                + "\n\nOption 1: " + q.options[0]
                + "\nOption 2: " + q.options[1]
                + "\nOption 3: " + q.options[2]
                + "\nOption 4: " + q.options[3];

            // Step 4: Ask confirmation
            int choice = JOptionPane.showConfirmDialog(
                this,
                details + "\n\nAre you sure you want to delete this question?",
                "Delete Question Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );

            // Step 5: Delete if Yes is selected
            if (choice == JOptionPane.YES_OPTION) {

                boolean status = m.deleteQuestion(id);

                JOptionPane.showMessageDialog(
                    this,
                    status
                    ? "Question Deleted Successfully!"
                    : "Question Not Deleted!"
                );

            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Delete Operation Cancelled!"
                );
            }

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                this,
                "Enter a valid Question ID!"
            );
        }
    }
   

    private void exitApplication() {

        int choice = JOptionPane.showConfirmDialog(
            this,
            "Do you want to Exit?",
            "Exit Application",
            JOptionPane.YES_NO_OPTION
        );
        if (choice == JOptionPane.YES_OPTION) {
            m.closeConnection();
            dispose();
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainApp();
        });
    }
}
