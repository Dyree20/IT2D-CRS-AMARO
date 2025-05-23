/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrs;
import admin.adminDashboard;
import config.dbConnector;
import employeeDashboard.employeeDashBoard;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import config.Logger;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class loginForm extends javax.swing.JFrame {

    public loginForm() {
        initComponents();
        forgot_pass.setFont(new java.awt.Font("Tahoma", 1, 11));
forgot_pass.setForeground(new java.awt.Color(0, 204, 204));
forgot_pass.setText("Forgot Password?");
forgot_pass.addMouseListener(new java.awt.event.MouseAdapter() {
    public void mouseClicked(java.awt.event.MouseEvent evt) {
        forgot_passMouseClicked(evt);
    }
    
});
        
        
    }

    public String loginAcc(String username, String password) {
    System.out.println("Login attempt for user: " + username);
    System.out.println("Plain text password entered: " + password);
    System.out.println("Hashed password calculated: " + hashPassword(password));
    String sql = "SELECT u_id, u_role, u_password, u_status FROM tbl_users WHERE LOWER(u_username) = LOWER(?)";
    String userIp = null;
    try {
        userIp = InetAddress.getLocalHost().getHostAddress();
    } catch (UnknownHostException e) {
        userIp = "Unknown";
    }
    try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/vrs", "root", "");
         PreparedStatement pst = con.prepareStatement(sql)) {
        pst.setString(1, username);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            int userId = rs.getInt("u_id");
            String role = rs.getString("u_role");
            String storedPassword = rs.getString("u_password");
            String status = rs.getString("u_status");
            System.out.println("🔹 User Found: " + username);
            System.out.println("🔹 User ID: " + userId);
            System.out.println("🔹 User Role: " + role);
            System.out.println("🔹 Stored Password (from DB): " + storedPassword);
            System.out.println("🔹 Password Length: " + storedPassword.length());
            System.out.println("🔹 User Status: " + status);
            if (status.equalsIgnoreCase("Pending")) {
                Logger.logLogin(username, false, userIp);
                JOptionPane.showMessageDialog(null, "Your account is pending approval. Please wait for admin approval.");
                return null;
            }
            if (checkPassword(password, storedPassword)) {
                System.out.println("✅ Password Matched! Login Successful.");
                Logger.logLogin(username, true, userIp);
                return role;
            } else {
                System.out.println("❌ Password Mismatch! Login Failed.");
                Logger.logLogin(username, false, userIp);
                JOptionPane.showMessageDialog(null, "Incorrect password. Please try again.");
            }
        } else {
            System.out.println("❌ No user found with username: " + username);
            Logger.logLogin(username, false, userIp);
            JOptionPane.showMessageDialog(null, "User not found.");
        }
    } catch (SQLException e) {
        System.out.println("❌ Database error: " + e.getMessage());
        Logger.logLogin(username, false, userIp);
        JOptionPane.showMessageDialog(null, "Database connection error.");
    }
    return null;
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

private boolean checkPassword(String enteredPassword, String storedPassword) {
    String hashedEntered = hashPassword(enteredPassword);
    System.out.println("Entered raw password: " + enteredPassword);
    System.out.println("Hashed entered password: " + hashedEntered);
    System.out.println("Stored password from DB: " + storedPassword);
    boolean matches = hashedEntered.equals(storedPassword);
    System.out.println("Password match: " + matches);
    return matches;
}
public void updateUserPassword(String username, String plainPassword) {
    String hashedPassword = hashPassword(plainPassword);
    String sql = "UPDATE tbl_users SET u_password = ? WHERE u_username = ?";
    
    try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/vrs", "root", "");
         PreparedStatement pst = con.prepareStatement(sql)) {
        
        pst.setString(1, hashedPassword);
        pst.setString(2, username);
        
        int result = pst.executeUpdate();
        if (result > 0) {
            System.out.println("Password updated successfully for " + username);
            System.out.println("New hash: " + hashedPassword);
        } else {
            System.out.println("Failed to update password. User not found.");
        }
    } catch (SQLException e) {
        System.out.println("Database error: " + e.getMessage());
    }
}


public void updateUserPasswordToHashed(String username) {
    try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/vrs", "root", "")) {
        // First get the current plain text password
        String selectSql = "SELECT u_password FROM tbl_users WHERE u_username = ?";
        try (PreparedStatement selectPst = con.prepareStatement(selectSql)) {
            selectPst.setString(1, username);
            ResultSet rs = selectPst.executeQuery();
            
            if (rs.next()) {
                String plainPassword = rs.getString("u_password");
                
                // Then update it with the hashed version
                String hashedPassword = hashPassword(plainPassword);
                String updateSql = "UPDATE tbl_users SET u_password = ? WHERE u_username = ?";
                
                try (PreparedStatement updatePst = con.prepareStatement(updateSql)) {
                    updatePst.setString(1, hashedPassword);
                    updatePst.setString(2, username);
                    
                    int result = updatePst.executeUpdate();
                    if (result > 0) {
                        System.out.println("Password updated to hash for user: " + username);
                    } else {
                        System.out.println("Failed to update password hash");
                    }
                }
            }
        }
    } catch (SQLException e) {
        System.out.println("Database error: " + e.getMessage());
    }
    
    
    
}

// Helper methods
private boolean userExists(String username) {
    try {
        Connection con = new dbConnector().getConnection();
        PreparedStatement pst = con.prepareStatement("SELECT u_id FROM tbl_users WHERE LOWER(u_username) = LOWER(?)");
        
        pst.setString(1, username);
        ResultSet rs = pst.executeQuery();
        boolean exists = rs.next(); // Returns true if user exists
        
        rs.close();
        pst.close();
        con.close();
        return exists;
    } catch (SQLException e) {
        System.out.println("Database error: " + e.getMessage());
        return false;
    }
}
private boolean userExistsByEmail(String email) {
    try {
        Connection con = new dbConnector().getConnection();
        PreparedStatement pst = con.prepareStatement("SELECT u_id FROM tbl_users WHERE LOWER(u_email) = LOWER(?)");
        
        pst.setString(1, email);
        ResultSet rs = pst.executeQuery();
        boolean exists = rs.next();
        
        rs.close();
        pst.close();
        con.close();
        return exists;
    } catch (SQLException e) {
        System.out.println("Database error: " + e.getMessage());
        return false;
    }
}
private boolean submitPasswordResetRequest(String email) {
    try {
        System.out.println("Attempting to submit password reset request for email: " + email);
        Connection con = new dbConnector().getConnection();
        if (con == null) {
            System.out.println("Connection is null!");
            return false;
        }
        
        String sql = "INSERT INTO tbl_password_reset (pr_email, pr_status, pr_date) VALUES (?, 'Pending', NOW())";
        System.out.println("SQL query: " + sql);
        
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setString(1, email);
        
        int result = pst.executeUpdate();
        System.out.println("Insert result: " + result);
        
        pst.close();
        con.close();
        return result > 0;
    } catch (SQLException e) {
        System.out.println("Database error: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}

private String getPasswordResetRequestStatus(String email) {
    try {
        Connection con = new dbConnector().getConnection();
        String sql = "SELECT pr_status FROM tbl_password_reset WHERE pr_email = ? ORDER BY pr_date DESC LIMIT 1";
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setString(1, email);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            String status = rs.getString("pr_status");
            rs.close();
            pst.close();
            con.close();
            return status;
        }
        rs.close();
        pst.close();
        con.close();
    } catch (SQLException e) {
        System.out.println("Database error: " + e.getMessage());
    }
    return null;
}

private void forgot_passMouseClicked(java.awt.event.MouseEvent evt) {
    String email = JOptionPane.showInputDialog(this, "Enter your email:", "Password Reset Request", JOptionPane.QUESTION_MESSAGE);
    if (email != null && !email.trim().isEmpty()) {
        // Check if user exists
        if (userExistsByEmail(email)) {
            String reqStatus = getPasswordResetRequestStatus(email);
            if (reqStatus != null) {
                if ("Approved".equalsIgnoreCase(reqStatus)) {
                    // Proceed to security questions and password reset
                    SecurityQuestionsVerificationDialog verificationDialog = new SecurityQuestionsVerificationDialog(this, email);
                    verificationDialog.setVisible(true);
                    if (verificationDialog.isVerified()) {
                        NewPasswordDialog passwordDialog = new NewPasswordDialog(this);
                        passwordDialog.setVisible(true);
                        String newPassword = passwordDialog.getNewPassword();
                        if (newPassword != null) {
                            // Update password in database
                            updateUserPasswordByEmail(email, newPassword);
                            // Mark request as completed
                            markPasswordResetRequestCompleted(email);
                            JOptionPane.showMessageDialog(this, "Password has been reset successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                    return;
                } else if ("Pending".equalsIgnoreCase(reqStatus)) {
                    JOptionPane.showMessageDialog(this, "A password reset request for this email is still pending admin approval.", "Request Pending", JOptionPane.INFORMATION_MESSAGE);
                    return;
                } else if ("Denied".equalsIgnoreCase(reqStatus)) {
                    JOptionPane.showMessageDialog(this, "Your previous password reset request was denied. Please contact admin or try again later.", "Request Denied", JOptionPane.INFORMATION_MESSAGE);
                    return;
                } else if ("Completed".equalsIgnoreCase(reqStatus)) {
                    // Allow new request if last one is completed
                } else {
                    // Unknown status, allow new request
                }
            }
            // No request or last one completed, allow new request
            if (submitPasswordResetRequest(email)) {
                JOptionPane.showMessageDialog(this, "Password reset request submitted.\nPlease wait for admin approval.", "Request Submitted", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to submit request. Try again later.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Email not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

private void updateUserPasswordByEmail(String email, String plainPassword) {
    String hashedPassword = hashPassword(plainPassword);
    String sql = "UPDATE tbl_users SET u_password = ? WHERE u_email = ?";
    try (Connection con = new dbConnector().getConnection();
         PreparedStatement pst = con.prepareStatement(sql)) {
        pst.setString(1, hashedPassword);
        pst.setString(2, email);
        int result = pst.executeUpdate();
        if (result > 0) {
            System.out.println("Password updated successfully for " + email);
        } else {
            System.out.println("Failed to update password. User not found.");
        }
    } catch (SQLException e) {
        System.out.println("Database error: " + e.getMessage());
    }
}

private void markPasswordResetRequestCompleted(String email) {
    String sql = "UPDATE tbl_password_reset SET pr_status = 'Completed' WHERE pr_email = ? AND pr_status = 'Approved'";
    try (Connection con = new dbConnector().getConnection();
         PreparedStatement pst = con.prepareStatement(sql)) {
        pst.setString(1, email);
        pst.executeUpdate();
    } catch (SQLException e) {
        System.out.println("Database error: " + e.getMessage());
    }
}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        u_user = new javax.swing.JTextField();
        u_pass = new javax.swing.JPasswordField();
        forgot_pass = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(0, 204, 0));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(255, 255, 0));

        jLabel1.setFont(new java.awt.Font("Rockwell Condensed", 3, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("WELCOME TO CAR RENTAL SYSTEM");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 780, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 840, -1));

        jLabel6.setFont(new java.awt.Font("Rockwell Condensed", 3, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Password");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 210, 190, 90));

        jLabel7.setFont(new java.awt.Font("Rockwell Condensed", 3, 48)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("LOG IN");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 80, 190, 90));

        jLabel8.setFont(new java.awt.Font("Rockwell Condensed", 3, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Username");
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 140, 190, 90));

        jButton1.setFont(new java.awt.Font("Rockwell Condensed", 0, 14)); // NOI18N
        jButton1.setText("LOGIN");
        jButton1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 310, 90, 30));

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Register");
        jLabel9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel9MouseClicked(evt);
            }
        });
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 330, 50, 20));

        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Dont have an Account?");
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 320, 140, -1));

        u_user.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        u_user.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                u_userActionPerformed(evt);
            }
        });
        jPanel1.add(u_user, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 200, 210, 30));

        u_pass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                u_passActionPerformed(evt);
            }
        });
        jPanel1.add(u_pass, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 270, 210, 30));

        forgot_pass.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        forgot_pass.setForeground(new java.awt.Color(255, 255, 255));
        forgot_pass.setText("Forgot Password?");
        jPanel1.add(forgot_pass, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 300, -1, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 779, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 506, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    
         String username = u_user.getText().trim();
    String password = new String(u_pass.getPassword()).trim();
    
    String role = loginAcc(username, password);
    if (role != null) {
        if (role.equalsIgnoreCase("admin")) {
            // Create admin dashboard and set username
            adminDashboard admin = new adminDashboard();
            admin.setUsername(username); // Call the new method
            admin.setVisible(true);
            this.dispose();
        } else if (role.equalsIgnoreCase("employee")) {
            // Create employee dashboard and set username
            employeeDashBoard employee = new employeeDashBoard();
            employee.setUsername(username); // Call the new method
            employee.setVisible(true);
            this.dispose();
        }
    } else {
        JOptionPane.showMessageDialog(null, "Login Failed. Invalid Username or Password.");
    }


    }//GEN-LAST:event_jButton1ActionPerformed

    private void jLabel9MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel9MouseClicked
       new registerForm().setVisible(true);
        this.dispose();
    

    }//GEN-LAST:event_jLabel9MouseClicked

    private void u_userActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_u_userActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_u_userActionPerformed

    private void u_passActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_u_passActionPerformed
        // Assuming u_pass is a JPasswordField
    char[] password = u_pass.getPassword(); // Get the password entered
    
    // Do something with the password (e.g., authentication)
    String passwordString = new String(password);
    System.out.println("Password entered: " + passwordString);
    }//GEN-LAST:event_u_passActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(loginForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(loginForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(loginForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(loginForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new loginForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel forgot_pass;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPasswordField u_pass;
    private javax.swing.JTextField u_user;
    // End of variables declaration//GEN-END:variables
}
