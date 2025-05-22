package vrs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import config.dbConnector;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

public class SecurityQuestionsVerificationDialog extends JDialog {
    private JComboBox<String> questionBox1;
    private JComboBox<String> questionBox2;
    private JTextField answerField1;
    private JTextField answerField2;
    private boolean verified = false;
    private String userEmail;

    public SecurityQuestionsVerificationDialog(Window parent, String email) {
        super(parent, "Verify Security Questions", ModalityType.APPLICATION_MODAL);
        this.userEmail = email;
        setLayout(new BorderLayout(10, 10));
        
        // Load user's security questions
        String[] questions = loadUserQuestions(email);
        if (questions == null) {
            JOptionPane.showMessageDialog(this, "Error loading security questions.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Question 1:"));
        questionBox1 = new JComboBox<>(new String[]{questions[0]});
        questionBox1.setEnabled(false);
        panel.add(questionBox1);

        panel.add(new JLabel("Answer 1:"));
        answerField1 = new JTextField();
        panel.add(answerField1);

        panel.add(new JLabel("Question 2:"));
        questionBox2 = new JComboBox<>(new String[]{questions[1]});
        questionBox2.setEnabled(false);
        panel.add(questionBox2);

        panel.add(new JLabel("Answer 2:"));
        answerField2 = new JTextField();
        panel.add(answerField2);

        add(panel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton verifyButton = new JButton("Verify");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(verifyButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        verifyButton.addActionListener(e -> {
            if (verifyAnswers()) {
                verified = true;
                setVisible(false);
            }
        });

        cancelButton.addActionListener(e -> {
            verified = false;
            setVisible(false);
        });

        pack();
        setLocationRelativeTo(parent);
    }

    private String[] loadUserQuestions(String email) {
        try {
            Connection conn = new dbConnector().getConnection();
            String query = "SELECT question1, question2 FROM security_questions WHERE user_id = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, email);
            
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new String[]{rs.getString("question1"), rs.getString("question2")};
            }
            
            rs.close();
            pst.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean verifyAnswers() {
        try {
            Connection conn = new dbConnector().getConnection();
            String query = "SELECT answer1, answer2 FROM security_questions WHERE user_id = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, userEmail);
            
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                String storedAnswer1 = rs.getString("answer1");
                String storedAnswer2 = rs.getString("answer2");
                
                String enteredAnswer1 = hashPassword(answerField1.getText().trim());
                String enteredAnswer2 = hashPassword(answerField2.getText().trim());
                
                if (storedAnswer1.equals(enteredAnswer1) && storedAnswer2.equals(enteredAnswer2)) {
                    return true;
                } else {
                    JOptionPane.showMessageDialog(this, "Incorrect answers. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            
            rs.close();
            pst.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error verifying answers: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Password hashing not available", e);
        }
    }

    public boolean isVerified() {
        return verified;
    }
} 