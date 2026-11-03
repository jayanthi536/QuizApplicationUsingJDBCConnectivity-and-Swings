package com.quizApplicationusingjdbcandswings;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import programmingcom.utils.Credentials;

public class Model {
    private Connection con;
    public Model() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver loaded successfully");
            con = DriverManager.getConnection(Credentials.url, Credentials.user,Credentials.pwd);
            System.out.println("Database connection established");
            createTable();
            // Create results table
            createResultsTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public boolean isConnected() {
        try {
            return con != null && !con.isClosed();
        } catch (Exception e) {
            return false;
        }
    }

    // Create quiz table
    public void createTable() {

        String sql =
            "CREATE TABLE IF NOT EXISTS quiz ("
            + "question_id INT PRIMARY KEY AUTO_INCREMENT, "
            + "question_text VARCHAR(500) NOT NULL, "
            + "option1 VARCHAR(200) NOT NULL, "
            + "option2 VARCHAR(200) NOT NULL, "
            + "option3 VARCHAR(200) NOT NULL, "
            + "option4 VARCHAR(200) NOT NULL, "
            + "correct_answer INT NOT NULL"
            + ")";

        try (Statement stmt = con.createStatement()) {

            stmt.executeUpdate(sql);

            System.out.println("Starting Quiz...........");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Create results table
    public void createResultsTable() {

        String sql =
            "CREATE TABLE IF NOT EXISTS results ("
            + "result_id INT PRIMARY KEY AUTO_INCREMENT, "
            + "no_of_questions_attempted INT NOT NULL, "
            + "name VARCHAR(100) NOT NULL, "
            + "score INT NOT NULL"
            + ")";

        try (Statement stmt = con.createStatement()) {
            stmt.executeUpdate(sql);
           
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Save quiz result
    public boolean saveScore(
        String name,
        int noOfQuestions,
        int score
    ) {
        String sql =
            "INSERT INTO results "
            + "(no_of_questions_attempted, name, score) "
            + "VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, noOfQuestions);
            pstmt.setString(2, name);
            pstmt.setInt(3, score);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    // Quiz Question class
    public static class QuizQuestion {

        public int questionId;
        public String questionText;
        public String[] options;
        public int correctAnswer;

        public QuizQuestion(
            int questionId,
            String questionText,
            String option1,
            String option2,
            String option3,
            String option4,
            int correctAnswer
        ) {
            this.questionId = questionId;
            this.questionText = questionText;
            this.options = new String[] {
                option1, option2, option3, option4
            };
            this.correctAnswer = correctAnswer;
        }
    }
    // Add question
    public boolean addQuestion(
        String question,
        String op1,
        String op2,
        String op3,
        String op4,
        int answer
    ) {
        String sql = "INSERT INTO quiz " + "(question_text, option1, option2, option3, option4, correct_answer) "
        + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, question);
            pstmt.setString(2, op1);
            pstmt.setString(3, op2);
            pstmt.setString(4, op3);
            pstmt.setString(5, op4);
            pstmt.setInt(6, answer);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    // Get latest 10 questions in ascending ID order
 // Get all questions in ascending ID order
    public ArrayList<QuizQuestion> getQuestions() {

        ArrayList<QuizQuestion> list = new ArrayList<>();

        String sql = "SELECT * FROM quiz ORDER BY question_id ASC";

        try (PreparedStatement pstmt = con.prepareStatement(sql);
             ResultSet res = pstmt.executeQuery()) {

            while (res.next()) {

                QuizQuestion q = new QuizQuestion(
                    res.getInt("question_id"),
                    res.getString("question_text"),
                    res.getString("option1"),
                    res.getString("option2"),
                    res.getString("option3"),
                    res.getString("option4"),
                    res.getInt("correct_answer")
                );

                list.add(q);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    
    // View question by ID
    public QuizQuestion viewQuestionByID(int id) {
        String sql ="SELECT * FROM quiz WHERE question_id = ?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet res = pstmt.executeQuery()) {
                if (res.next()) {
                    return new QuizQuestion(
                        res.getInt("question_id"),
                        res.getString("question_text"),
                        res.getString("option1"),
                        res.getString("option2"),
                        res.getString("option3"),
                        res.getString("option4"),
                        res.getInt("correct_answer")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    // Update question
    public boolean updateQuestion(
        int id,
        String question,
        String op1,
        String op2,
        String op3,
        String op4,
        int answer
    ) {
        String sql =
            "UPDATE quiz SET "
            + "question_text = ?, "
            + "option1 = ?, "
            + "option2 = ?, "
            + "option3 = ?, "
            + "option4 = ?, "
            + "correct_answer = ? "
            + "WHERE question_id = ?";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, question);
            pstmt.setString(2, op1);
            pstmt.setString(3, op2);
            pstmt.setString(4, op3);
            pstmt.setString(5, op4);
            pstmt.setInt(6, answer);
            pstmt.setInt(7, id);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    // Delete question
    public boolean deleteQuestion(int id) {
        String sql = "DELETE FROM quiz WHERE question_id = ?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    // Close connection
    public void closeConnection() {
        try {
            if (con != null && !con.isClosed()) {
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
