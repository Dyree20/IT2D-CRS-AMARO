/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrs;

import config.dbConnector;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import net.proteanit.sql.DbUtils;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import config.Logger;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 * @author SCC-COLLEGE
 */
public class Approval extends javax.swing.JInternalFrame {

    /**
     * Creates new form Approval
     */
    public Approval() {
        initComponents();
        displayData();
        this.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        javax.swing.plaf.basic.BasicInternalFrameUI bi = (javax.swing.plaf.basic.BasicInternalFrameUI) this.getUI();
        bi.setNorthPane(null);
        // Resize to parent desktop pane if available
        if (getParent() != null && getParent() instanceof javax.swing.JDesktopPane) {
            javax.swing.JDesktopPane parent = (javax.swing.JDesktopPane) getParent();
            this.setSize(parent.getSize());
            this.setLocation(0, 0);
        }
        // Log approval panel opened
        try {
            String userIp = "Unknown";
            try { userIp = InetAddress.getLocalHost().getHostAddress(); } catch (UnknownHostException e) {}
            String username = System.getProperty("user.name");
            Logger.log("VIEW APPROVAL", "Approval panel opened", username, userIp);
        } catch (Exception e) { e.printStackTrace(); }
    }
private String selectedUserId = null;
    // Load data from `tbl_approval` into the table
    private void displayData() {
    try {
        dbConnector db = new dbConnector();
        Connection conn = db.getConnection(); // Get connection
        String query = "SELECT * FROM tbl_approval";
        PreparedStatement stmt = conn.prepareStatement(query);
        ResultSet rs = stmt.executeQuery();

        employ.setModel(DbUtils.resultSetToTableModel(rs));

        // Close resources
        rs.close();
        stmt.close();
        conn.close();
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error loading data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

    
    

    // Approve selected user
private void approveUser() {
    if (selectedUserId == null) {
        JOptionPane.showMessageDialog(this, "Please select a user to approve.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    try (Connection conn = new dbConnector().getConnection()) {
        conn.setAutoCommit(false); // Start transaction

        // Fetch user details
        String selectQuery = "SELECT * FROM tbl_approval WHERE u_id = ?";
        try (PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {
            selectStmt.setString(1, selectedUserId);
            try (ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    // Insert into `tbl_users`
                    String insertQuery = "INSERT INTO tbl_users (u_name, u_username, u_email, u_password, u_phone, u_role, u_status) VALUES (?, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                        insertStmt.setString(1, rs.getString("u_name"));
                        insertStmt.setString(2, rs.getString("u_username"));
                        insertStmt.setString(3, rs.getString("u_email"));
                        insertStmt.setString(4, rs.getString("u_password"));
                        insertStmt.setString(5, rs.getString("u_phone"));
                        insertStmt.setString(6, rs.getString("u_role"));
                        insertStmt.setString(7, "Active"); // Set status to Active
                        
                        int rowsInserted = insertStmt.executeUpdate();
                        
                        if (rowsInserted > 0) {
                            // Delete from `tbl_approval` after successful insert
                            String deleteQuery = "DELETE FROM tbl_approval WHERE u_id = ?";
                            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                                deleteStmt.setString(1, selectedUserId);
                                deleteStmt.executeUpdate();
                                
                                conn.commit(); // Commit transaction
                                JOptionPane.showMessageDialog(this, "User approved successfully!");
                                // Log approval
                                try {
                                    String userIp = "Unknown";
                                    try { userIp = InetAddress.getLocalHost().getHostAddress(); } catch (UnknownHostException e) {}
                                    String username = System.getProperty("user.name");
                                    Logger.log("APPROVE USER", "Approved user ID: " + selectedUserId, username, userIp);
                                } catch (Exception e) { e.printStackTrace(); }
                                displayData(); // Refresh the table
                                selectedUserId = null; // Reset selection
                            }
                        } else {
                            conn.rollback(); // Rollback if insert failed
                            JOptionPane.showMessageDialog(this, "Failed to approve user.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "User not found!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

    // Edit selected user
    private void editUser() {
        if (selectedUserId == null) {
            JOptionPane.showMessageDialog(this, "Please select a user to edit.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String newEmail = JOptionPane.showInputDialog(this, "Enter new email:");
        String newPhone = JOptionPane.showInputDialog(this, "Enter new phone:");

        if (newEmail == null || newEmail.trim().isEmpty() || newPhone == null || newPhone.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Invalid input. No changes made.");
            return;
        }

        try (Connection conn = new dbConnector().getConnection();
             PreparedStatement updateStmt = conn.prepareStatement("UPDATE tbl_approval SET u_email = ?, u_phone = ? WHERE u_id = ?")) {
            updateStmt.setString(1, newEmail);
            updateStmt.setString(2, newPhone);
            updateStmt.setString(3, selectedUserId);

            if (updateStmt.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(this, "User updated successfully.");
                // Log edit
                try {
                    String userIp = "Unknown";
                    try { userIp = InetAddress.getLocalHost().getHostAddress(); } catch (UnknownHostException e) {}
                    String username = System.getProperty("user.name");
                    Logger.log("EDIT USER", "Edited user ID: " + selectedUserId, username, userIp);
                } catch (Exception e) { e.printStackTrace(); }
                displayData();
                selectedUserId = null;
            } else {
                JOptionPane.showMessageDialog(this, "Update failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Delete selected user
    private void deleteUser() {
        if (selectedUserId == null) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this user?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try (Connection conn = new dbConnector().getConnection();
             PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM tbl_approval WHERE u_id = ?")) {
            deleteStmt.setString(1, selectedUserId);

            if (deleteStmt.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(this, "User deleted successfully.");
                // Log delete
                try {
                    String userIp = "Unknown";
                    try { userIp = InetAddress.getLocalHost().getHostAddress(); } catch (UnknownHostException e) {}
                    String username = System.getProperty("user.name");
                    Logger.log("DELETE USER", "Deleted user ID: " + selectedUserId, username, userIp);
                } catch (Exception e) { e.printStackTrace(); }
                displayData();
                selectedUserId = null;
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete user.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

        jScrollPane1 = new javax.swing.JScrollPane();
        employ = new javax.swing.JTable();
        u_approve = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        edit = new javax.swing.JPanel();
        u_edit = new javax.swing.JLabel();
        u_delete = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();

        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        employ.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        employ.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                employMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(employ);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 960, 466));

        u_approve.setBackground(new java.awt.Color(255, 102, 51));
        u_approve.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        u_approve.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                u_approveMouseClicked(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Lucida Bright", 1, 14)); // NOI18N
        jLabel2.setText("APPROVE");
        jLabel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel2MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout u_approveLayout = new javax.swing.GroupLayout(u_approve);
        u_approve.setLayout(u_approveLayout);
        u_approveLayout.setHorizontalGroup(
            u_approveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(u_approveLayout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addComponent(jLabel2)
                .addContainerGap(52, Short.MAX_VALUE))
        );
        u_approveLayout.setVerticalGroup(
            u_approveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
        );

        getContentPane().add(u_approve, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 480, -1, 40));

        edit.setBackground(new java.awt.Color(255, 102, 51));
        edit.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        edit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                editMouseClicked(evt);
            }
        });

        u_edit.setFont(new java.awt.Font("Lucida Bright", 1, 14)); // NOI18N
        u_edit.setText("EDIT");

        javax.swing.GroupLayout editLayout = new javax.swing.GroupLayout(edit);
        edit.setLayout(editLayout);
        editLayout.setHorizontalGroup(
            editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(editLayout.createSequentialGroup()
                .addGap(60, 60, 60)
                .addComponent(u_edit)
                .addContainerGap(60, Short.MAX_VALUE))
        );
        editLayout.setVerticalGroup(
            editLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(u_edit, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
        );

        getContentPane().add(edit, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 480, 160, 40));

        u_delete.setBackground(new java.awt.Color(255, 102, 51));
        u_delete.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        u_delete.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                u_deleteMouseClicked(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Lucida Fax", 1, 14)); // NOI18N
        jLabel3.setText("DELETE");

        javax.swing.GroupLayout u_deleteLayout = new javax.swing.GroupLayout(u_delete);
        u_delete.setLayout(u_deleteLayout);
        u_deleteLayout.setHorizontalGroup(
            u_deleteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(u_deleteLayout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addComponent(jLabel3)
                .addContainerGap(54, Short.MAX_VALUE))
        );
        u_deleteLayout.setVerticalGroup(
            u_deleteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
        );

        getContentPane().add(u_delete, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 480, 170, 40));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MouseClicked
          approveUser();
    }//GEN-LAST:event_jLabel2MouseClicked

    private void u_approveMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_u_approveMouseClicked
        approveUser();
    }//GEN-LAST:event_u_approveMouseClicked

    private void editMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_editMouseClicked
         editUser();
    }//GEN-LAST:event_editMouseClicked

    private void u_deleteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_u_deleteMouseClicked
       deleteUser();
    }//GEN-LAST:event_u_deleteMouseClicked

    private void employMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_employMouseClicked
     int selectedRow = employ.getSelectedRow();
    if (selectedRow != -1) {
        // Assuming the u_id is in the first column (index 0)
        selectedUserId = employ.getValueAt(selectedRow, 0).toString();
        System.out.println("Selected user ID: " + selectedUserId);
    }

    
    }//GEN-LAST:event_employMouseClicked

    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel edit;
    private javax.swing.JTable employ;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel u_approve;
    private javax.swing.JPanel u_delete;
    private javax.swing.JLabel u_edit;
    // End of variables declaration//GEN-END:variables
}
