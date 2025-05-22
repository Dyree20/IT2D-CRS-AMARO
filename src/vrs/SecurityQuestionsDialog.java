package vrs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SecurityQuestionsDialog extends JDialog {
    private JComboBox<String> questionBox1;
    private JComboBox<String> questionBox2;
    private JTextField answerField1;
    private JTextField answerField2;
    private boolean confirmed = false;

    private static final String[] QUESTIONS = {
        "What is your favorite food?",
        "What is your favorite fruit?",
        "What is your favorite color?",
        "What is your favorite movie?",
        "What is your favorite book?",
        "What is your favorite place?",
        "What is your mother's maiden name?",
        "What is your first pet's name?",
        "What is your favorite subject in school?",
        "What is your dream job?"
    };

    public SecurityQuestionsDialog(Frame parent) {
        super(parent, "Security Questions", true);
        setLayout(new BorderLayout(10, 10));
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

        panel.add(new JLabel("Select Question 1:"));
        questionBox1 = new JComboBox<>(QUESTIONS);
        panel.add(questionBox1);

        panel.add(new JLabel("Answer 1:"));
        answerField1 = new JTextField();
        panel.add(answerField1);

        panel.add(new JLabel("Select Question 2:"));
        questionBox2 = new JComboBox<>(QUESTIONS);
        panel.add(questionBox2);

        panel.add(new JLabel("Answer 2:"));
        answerField2 = new JTextField();
        panel.add(answerField2);

        add(panel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        okButton.addActionListener(e -> {
            if (getSelectedQuestion1().equals(getSelectedQuestion2())) {
                JOptionPane.showMessageDialog(this, "Please select two different questions.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (getAnswer1().trim().isEmpty() || getAnswer2().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please provide answers to both questions.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            confirmed = true;
            setVisible(false);
        });
        cancelButton.addActionListener(e -> {
            confirmed = false;
            setVisible(false);
        });

        pack();
        setLocationRelativeTo(parent);
    }

    public boolean isConfirmed() {
        return confirmed;
    }
    public String getSelectedQuestion1() {
        return (String) questionBox1.getSelectedItem();
    }
    public String getSelectedQuestion2() {
        return (String) questionBox2.getSelectedItem();
    }
    public String getAnswer1() {
        return answerField1.getText();
    }
    public String getAnswer2() {
        return answerField2.getText();
    }
} 