/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrs;

import config.dbConnector;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import javax.swing.table.DefaultTableModel;
import java.net.InetAddress;
import java.net.UnknownHostException;
import config.Logger;
import javax.swing.SwingUtilities;

/**
 *
 * @author ROCO
 */
public class passwordResetRequests extends javax.swing.JInternalFrame {
private int selectedRequestId = -1;
private String selectedEmail = null;
    /**
     * Creates new form passwordResetRequests
     */
    public passwordResetRequests() {
        initComponents();
        this.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
    BasicInternalFrameUI bi = (BasicInternalFrameUI) this.getUI();
    bi.setNorthPane(null);
    
    loadRequests(); // Load initial data
    
    requestsTable.getSelectionModel().addListSelectionListener(e -> {
    if (!e.getValueIsAdjusting() && requestsTable.getSelectedRow() != -1) {
        int row = requestsTable.getSelectedRow();
        selectedRequestId = Integer.parseInt(requestsTable.getModel().getValueAt(row, 0).toString());
        selectedEmail = requestsTable.getModel().getValueAt(row, 1).toString();
        System.out.println("Selected: " + selectedRequestId + ", " + selectedEmail);
    }
});
approveBtn.addActionListener(e -> {
    System.out.println("Approve clicked for ID: " + selectedRequestId);
    approveRequest();
});
denyBtn.addActionListener(e -> {
    System.out.println("Deny clicked for ID: " + selectedRequestId);
    denyRequest();
});
    
    }
private void loadRequests() {
    DefaultTableModel model = (DefaultTableModel) requestsTable.getModel();
    model.setRowCount(0);
    
    try {
        Connection conn = new dbConnector().getConnection();
        String query = "SELECT * FROM tbl_password_reset ORDER BY pr_date DESC";
        PreparedStatement pst = conn.prepareStatement(query);
        ResultSet rs = pst.executeQuery();
        
        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getInt("pr_id"),
                rs.getString("pr_email"),
                rs.getTimestamp("pr_date"),
                rs.getString("pr_status")
            });
        }
        
        rs.close();
        pst.close();
        conn.close();
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, 
                "Error loading requests: " + ex.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
    }
}

private void requestsTableMouseClicked(java.awt.event.MouseEvent evt) {
    int row = requestsTable.getSelectedRow();
    System.out.println("Selected row: " + row);
    
    if (row != -1) {
        try {
            Object idObj = requestsTable.getValueAt(row, 0);
            Object emailObj = requestsTable.getValueAt(row, 1);
            
            System.out.println("ID Object: " + idObj + ", Email Object: " + emailObj);
            
            if (idObj != null) {
                selectedRequestId = Integer.parseInt(idObj.toString());
                selectedEmail = (emailObj != null) ? emailObj.toString() : "";
                
                System.out.println("Set selected ID: " + selectedRequestId);
                System.out.println("Set selected email: " + selectedEmail);
            }
        } catch (Exception e) {
            System.out.println("Error in table selection: " + e.getMessage());
            e.printStackTrace();
        }
    }
}


private void approveBtnActionPerformed(java.awt.event.ActionEvent evt) {
    approveRequest();
}
private void denyBtnActionPerformed(java.awt.event.ActionEvent evt) {
    denyRequest();
}

private void approveRequest() {
    System.out.println("approveRequest method called, selectedRequestId=" + selectedRequestId);
    if (selectedRequestId == -1) {
        JOptionPane.showMessageDialog(this, "Please select a request first.");
        return;
    }
    
    try {
        Connection conn = new dbConnector().getConnection();
        String query = "UPDATE tbl_password_reset SET pr_status = 'Approved', pr_approved_date = NOW() WHERE pr_id = ?";
        PreparedStatement pst = conn.prepareStatement(query);
        pst.setInt(1, selectedRequestId);
        
        int result = pst.executeUpdate();
        if (result > 0) {
            JOptionPane.showMessageDialog(this, "Request approved. Please set a new password for the user.");
            // Logging
            String userIp = "Unknown";
            try { userIp = InetAddress.getLocalHost().getHostAddress(); } catch (UnknownHostException e) {}
            String username = System.getProperty("user.name");
            Logger.log("Approve Password Reset", "Request ID: " + selectedRequestId + ", User: " + selectedEmail, username, userIp);
            loadRequests();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to approve request.");
        }
        
        pst.close();
        conn.close();
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, 
                "Database error: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}

private void denyRequest() {
    if (selectedRequestId == -1) {
        JOptionPane.showMessageDialog(this, "Please select a request first.");
        return;
    }
    
    try {
        Connection conn = new dbConnector().getConnection();
        String query = "UPDATE tbl_password_reset SET pr_status = 'Denied', pr_approved_date = NOW() WHERE pr_id = ?";
        PreparedStatement pst = conn.prepareStatement(query);
        pst.setInt(1, selectedRequestId);
        
        int result = pst.executeUpdate();
        if (result > 0) {
            JOptionPane.showMessageDialog(this, "Request denied.");
            // Logging
            String userIp = "Unknown";
            try { userIp = InetAddress.getLocalHost().getHostAddress(); } catch (UnknownHostException e) {}
            String username = System.getProperty("user.name");
            Logger.log("Deny Password Reset", "Request ID: " + selectedRequestId + ", User: " + selectedEmail, username, userIp);
            loadRequests();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to deny request.");
        }
        
        pst.close();
        conn.close();
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, 
                "Database error: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}

private String getUsernameByEmail(String email) throws SQLException {
    Connection conn = new dbConnector().getConnection();
    String sql = "SELECT username FROM tbl_users WHERE email = ?";
    PreparedStatement stmt = conn.prepareStatement(sql);
    stmt.setString(1, email);
    ResultSet rs = stmt.executeQuery();
    
    if (rs.next()) {
        return rs.getString("username");
    }
    throw new SQLException("User not found");
}

private String getRequestStatus(int requestId) throws SQLException {
    Connection conn = new dbConnector().getConnection();
    String sql = "SELECT pr_status FROM tbl_password_reset WHERE pr_id = ?";
    PreparedStatement stmt = conn.prepareStatement(sql);
    stmt.setInt(1, requestId);
    ResultSet rs = stmt.executeQuery();
    
    if (rs.next()) {
        return rs.getString("pr_status");
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        requestsTable = new javax.swing.JTable();
        approveBtn = new javax.swing.JButton();
        denyBtn = new javax.swing.JButton();

        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(110, 0, 0));

        requestsTable.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(requestsTable);

        approveBtn.setFont(new java.awt.Font("Microsoft YaHei", 3, 11)); // NOI18N
        approveBtn.setText("APPROVE");

        denyBtn.setFont(new java.awt.Font("Microsoft YaHei", 3, 11)); // NOI18N
        denyBtn.setText("DENY");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 920, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addComponent(approveBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(denyBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(29, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 547, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(approveBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                    .addComponent(denyBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(-10, -30, 930, 630));

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton approveBtn;
    private javax.swing.JButton denyBtn;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable requestsTable;
    // End of variables declaration//GEN-END:variables
}
